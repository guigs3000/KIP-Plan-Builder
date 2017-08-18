package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProcessDefinition;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.xml.sax.SAXException;

public class ProcessDefinitionTasksResource extends AbstractPluginResource{
	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	String idProcesso;
	
	public ProcessDefinitionTasksResource(String engineName, String idProcesso) {
		super(engineName);
		this.idProcesso = idProcesso;
	}
	
	@GET
	public ProcessDefinition getTasks() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		InputStream processo = repositoryService.getProcessModel(idProcesso);
		byte[] xmlByte = IoUtil.readInputStream(processo, "processo");
		String xml = new String(xmlByte, "UTF-8");
		LOGGER.info(xml);
		return new ProcessDefinition();
	}
}