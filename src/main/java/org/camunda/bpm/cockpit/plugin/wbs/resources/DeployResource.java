package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.engine.repository.CaseDefinition;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.xml.sax.SAXException;

public class DeployResource extends AbstractPluginResource{

	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	
	private String nomePlano = "";
	private final String USER_AGENT = "Mozilla/5.0";
	String basePath = System.getProperty("user.dir")+ "\\planosdeprojeto";

	public DeployResource(String engineName, String NomePlano) throws IOException {
		super(engineName);
		this.nomePlano = NomePlano;
	}
	
	
	@GET
	public String deploy() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException{
		String nome = nomePlano.substring(0, nomePlano.lastIndexOf(".")); 
		
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		DeploymentBuilder deployBuilder = repositoryService.createDeployment();
		
		XmlParser xmlParser = new XmlParser(basePath, nomePlano);
		deployBuilder.name(nome);
		deployBuilder.addInputStream(nomePlano, xmlParser.readFile(basePath + "\\" + nomePlano));
		deployBuilder.deploy();
		nome = nome.replaceAll("\\s+","_");
		LOGGER.info(nome);
		List<CaseDefinition> casos = repositoryService.createCaseDefinitionQuery().caseDefinitionKey(nome).orderByCaseDefinitionVersion().asc().list();
		return casos.get(0).getId();

		
		
	}
}
