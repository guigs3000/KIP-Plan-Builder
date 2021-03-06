package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;
import org.xml.sax.SAXException;

public class CreateProjectPlanTaskResource extends AbstractPluginResource{

	public String filename;
	public Tarefa task;
	String filepath = System.getProperty("user.dir")+ "\\planosdeprojeto";

	public CreateProjectPlanTaskResource(String engineName, String filename, Tarefa task) {
		super(engineName);
		this.filename = filename;
		this.task = task;
	}
	
	@POST
	public void createProjectPlanTask() throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException{
		XmlParser xmlParser = new XmlParser(filepath, filename);
		xmlParser.addTask(task);
	}

}
