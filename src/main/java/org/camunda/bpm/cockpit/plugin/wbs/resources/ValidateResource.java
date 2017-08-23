package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProjectPlan;
import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.xml.sax.SAXException;

public class ValidateResource extends AbstractPluginResource{

	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	
	private String NomePlano = "";
	String basePath = System.getProperty("user.dir")+ "\\planosdeprojeto";

	public ValidateResource(String engineName, String NomePlano) throws IOException {
		super(engineName);
		this.NomePlano = NomePlano;
	}
	
	
	@GET
	public void validate() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, ParseException{
		List<String> requiredTasks = new ArrayList<String>();
		HashMap<String, List<String>> Dependencias = new HashMap<String, List<String>>();
		
		XmlParser planoProjetoXml = new XmlParser(basePath, NomePlano);
		ProjectPlan planoDeProjeto = new ProjectPlan();
		planoDeProjeto.baseProcessIds = planoProjetoXml.getCorrelatedProcesses();
		
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		
		for(int i = 0; i < planoDeProjeto.baseProcessIds.size(); i++ ){
			String IdProcesso = planoDeProjeto.baseProcessIds.get(i);
			InputStream processo = repositoryService.getCaseModel(IdProcesso);
			byte[] xmlByte = IoUtil.readInputStream(processo, "processo");
			String xmlString = new String(xmlByte, "UTF-8");
			XmlParser xmlParser = new XmlParser(xmlString);
			requiredTasks.addAll(xmlParser.extrairRequiredTasks());
			Dependencias = xmlParser.extrairSequenceFlow();	
		}
		
		List<String> executedTasks = new ArrayList<String>();
		HashMap<String, Tarefa> tarefasPlanejadas = planoProjetoXml.getTaskMap();
		
		for(int i =0; i < tarefasPlanejadas.size(); i++){
			Tarefa tarefa = tarefasPlanejadas.get(i);
			executedTasks.add(tarefa.info.taskType);
			if(Dependencias.containsKey(tarefa.info.taskType)){
				List<String> tarefasDependentes = Dependencias.get(tarefa.info.taskType);
				for(int j = 0; j < tarefasDependentes.size(); j++){
					Tarefa tarefaPlanejada = tarefasPlanejadas.get(tarefa.info.taskType);
					Tarefa tarefaDependente = tarefasPlanejadas.get(tarefasDependentes.get(j));
					if(tarefaDependente == null){
						LOGGER.info(tarefaPlanejada.getName() + " depende de " + tarefasDependentes.get(j) + ". \n Inconformidade: " +  tarefasDependentes.get(j) + " nao executada");
					}else{
						SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");    
						Date dataDependente = fmt.parse(tarefaDependente.info.plannedEndDate); 
						Date dataPlanejada = fmt.parse(tarefaPlanejada.info.plannedStartDate);
						if(dataPlanejada.before(dataDependente)){
							LOGGER.info("Inconformidade: tarefa " + tarefaPlanejada.getName() +" foi executada antes de sua dependencia " + tarefaDependente.getName() );
						}
					}
				}
			}
		}
		
		requiredTasks.removeAll(executedTasks);
		for(int i =0; i < requiredTasks.size(); i++){
			LOGGER.info("tarefa obrigatoria nao executada: " + requiredTasks.get(i));
		}
	}
	
}
