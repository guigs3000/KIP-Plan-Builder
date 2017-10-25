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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;

import javax.net.ssl.HttpsURLConnection;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.CmmnApi;
import org.camunda.bpm.cockpit.plugin.wbs.XmlFileFilter;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.engine.impl.util.json.JSONTokener;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.cmmn.Cmmn;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.camunda.bpm.model.cmmn.instance.Case;
import org.camunda.bpm.model.cmmn.instance.Definitions;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.xml.sax.SAXException;

public class CreateProjectPlanResource extends AbstractPluginResource{

	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	private String IdProcesso ="";
	private String NomePlano = "";
	private final String USER_AGENT = "Mozilla/5.0";
	private BpmnModelInstance modelInstance;
	String basePath = System.getProperty("user.dir")+ "\\planosdeprojeto";

	public CreateProjectPlanResource(String engineName, String IdProcesso, String NomePlano) throws IOException {
		super(engineName);
		this.IdProcesso = IdProcesso;
		this.NomePlano = NomePlano;
	}
	
	
	@GET
	public void createProjectPlan() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException{
		File cmmnFile = new File(basePath + "\\" + NomePlano + ".cmmn");
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		InputStream processo = repositoryService.getCaseModel(IdProcesso);
		byte[] xmlByte = IoUtil.readInputStream(processo, "processo");
		String xmlString = new String(xmlByte, "UTF-8");
		
		//String guid = NomePlano + java.util.UUID.randomUUID().toString();
		if(!IdProcesso.isEmpty() && !NomePlano.isEmpty() && !cmmnFile.exists()){
			String xml = String.format(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					"<cmmn:definitions xmlns:wbsbuilder=\"wbsplugin\" xmlns:dc=\"http://www.omg.org/spec/CMMN/20151109/DC\" xmlns:di=\"http://www.omg.org/spec/CMMN/20151109/DI\" xmlns:cmmndi=\"http://www.omg.org/spec/CMMN/20151109/CMMNDI\" xmlns:cmmn=\"http://www.omg.org/spec/CMMN/20151109/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:camunda=\"http://camunda.org/schema/1.0/cmmn\" id=\"Test\" targetNamespace=\"http://bpmn.io/schema/cmmn\" exporter=\"Camunda Modeler\" exporterVersion=\"1.6.0\">\n" + 
					"<cmmn:case id=\"%s\" wbsbuilder:id=\"%s\">\n"+
					"<cmmn:casePlanModel id=\"CasePlanModel_1\" name=\"%s\">"+      
					"</cmmn:casePlanModel>" +
					"</cmmn:case>\n" + 
					"</cmmn:definitions>", NomePlano, IdProcesso, NomePlano);
			Files.write(cmmnFile.toPath(), Arrays.asList(xml), Charset.forName("UTF-8"));
			
		}
	}
}
