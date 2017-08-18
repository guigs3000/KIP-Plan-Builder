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
		XmlParser xmlParser = new XmlParser(xmlString);
		LOGGER.info("processo Escolhido: \n" + xmlString);
		xmlParser.extrairRequiredTasks();
		xmlParser.extrairSequenceFlow();
		if(!IdProcesso.isEmpty() && !NomePlano.isEmpty() && !cmmnFile.exists()){
			/*ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
			RepositoryService repositoryService = processEngine.getRepositoryService();
			InputStream BpmnInput = repositoryService.getProcessModel(IdProcesso);
			LOGGER.info(BpmnInput.toString());
			modelInstance = Bpmn.readModelFromStream(BpmnInput);
			List<Attribute<?>> atributosProcesso = modelInstance.getModel().getType(Process.class).getAttributes();
			ModelElementType start = modelInstance.getModel().getType(StartEvent.class);
			ModelElementType end = modelInstance.getModel().getType(EndEvent.class);
			
			
			CmmnModelInstance cmmn = Cmmn.createEmptyModel();
			org.camunda.bpm.model.cmmn.instance.Definitions definitions = cmmn.newInstance(Definitions.class);
			definitions.setTargetNamespace("http://camunda.org/examples");
			cmmn.setDefinitions(definitions);
			
			Case caseElement = modelInstance.newInstance(Case.class);
			LocalDate localDate = LocalDate.now();
			caseElement.setId(IdProcesso + localDate.toString());
			definitions.addChildElement(caseElement);
			
			File cmmnFile = new File(basePath + "\\" + NomePlano + ".bpmn");
			Cmmn.writeModelToFile(cmmnFile, cmmn);*/
			/*String xml = String.format(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					"<cmmn:definitions xmlns:dc=\"http://www.omg.org/spec/CMMN/20151109/DC\" xmlns:di=\"http://www.omg.org/spec/CMMN/20151109/DI\" xmlns:cmmndi=\"http://www.omg.org/spec/CMMN/20151109/CMMNDI\" xmlns:cmmn=\"http://www.omg.org/spec/CMMN/20151109/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:camunda=\"http://camunda.org/schema/1.0/cmmn\" id=\"Test\" targetNamespace=\"http://bpmn.io/schema/cmmn\" exporter=\"Camunda Modeler\" exporterVersion=\"1.6.0\">\n" + 
					"<cmmn:case id=\"%s\">\n"+
					"</cmmn:case>\n" + 
					"<cmmndi:CMMNDI>" + 
				    "<cmmndi:CMMNDiagram id=\"\">"+
				    "<cmmndi:Size xsi:type=\"dc:Dimension\" width=\"500\" height=\"500\" />"+
				    "</cmmndi:CMMNDiagram>"+
				    "</cmmndi:CMMNDI>"+
					"</cmmn:definitions>", IdProcesso);
			Files.write(cmmnFile.toPath(), Arrays.asList(xml), Charset.forName("UTF-8"));*/
			
			
			
			
		}
	}
}
