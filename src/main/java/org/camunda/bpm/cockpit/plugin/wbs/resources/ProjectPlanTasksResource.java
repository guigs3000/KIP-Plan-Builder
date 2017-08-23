package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlFileFilter;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProcessDefinition;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProjectPlan;
import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.xml.sax.SAXException;

public class ProjectPlanTasksResource extends AbstractPluginResource{
	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	String planoProjeto;
	String basePath = System.getProperty("user.dir")+ "\\planosdeprojeto";
	String engineName;
	public ProjectPlanTasksResource(String engineName, String planoProjeto) {
		super(engineName);
		this.engineName = engineName;
		this.planoProjeto = planoProjeto;
	}
	
	@GET
	public ProjectPlan getTasks() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		LOGGER.info("chegou aqui no tarefas: "+planoProjeto );
		if(planoProjeto == null || planoProjeto.isEmpty()){
			return new ProjectPlan();
		}
		else{
			XmlParser myParser = new XmlParser(basePath, planoProjeto);
			ProjectPlan planoDeProjeto = new ProjectPlan();
			planoDeProjeto.name = myParser.filename;
			planoDeProjeto.xml = myParser.getXml();
			planoDeProjeto.tasks = myParser.getTaskList();
			planoDeProjeto.baseProcessIds = myParser.getCorrelatedProcesses();
			
			for(int i = 0; i < planoDeProjeto.baseProcessIds.size(); i++ ){
				List<Tarefa> tarefas = getTasksFromProcessDefinition(planoDeProjeto.baseProcessIds.get(i));
				planoDeProjeto.possibleTasks.addAll(tarefas);
			}
			return planoDeProjeto;
		}
	}
	
	public List<Tarefa> getTasksFromProcessDefinition(String idProcesso) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(this.engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		InputStream processo = repositoryService.getCaseModel(idProcesso);
		byte[] xmlByte = IoUtil.readInputStream(processo, "processo");
		String xml = new String(xmlByte, "UTF-8");
		
		XmlParser myParser = new XmlParser(xml);
		return myParser.getTaskList();
		
	}

}
