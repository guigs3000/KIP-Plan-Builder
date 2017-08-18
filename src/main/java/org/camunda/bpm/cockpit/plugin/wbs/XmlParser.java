package org.camunda.bpm.cockpit.plugin.wbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import org.camunda.bpm.cockpit.plugin.wbs.dto.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.springframework.util.xml.SimpleNamespaceContext;

public class XmlParser {
	
	public Document ProcessDocument;
	public String filepath;
	public String filename;
	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	  
	public XmlParser(String xmlString) throws ParserConfigurationException, SAXException, IOException{
		InputSource source = new InputSource(new StringReader(xmlString));
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		Document document = db.parse(source);
		ProcessDocument = document;
	}
	
	public XmlParser(String filepath, String filename) throws ParserConfigurationException, SAXException, IOException{
		LOGGER.info("filepath is " + filepath);
		this.filepath = filepath;
		this.filename = filename;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		Document document = db.parse(new File(filepath + "\\" + filename));
		ProcessDocument = document;
	}
	
	public ProcessDefinition getPlanoDeProjeto() throws XPathExpressionException{
		ProcessDefinition plano = new ProcessDefinition();
		plano.name = filename;
		plano.tasks = getTaskList();
		
		return plano;
	}
	
	public List<String> getCorrelatedProcesses() throws XPathExpressionException{
		
		NodeList list = ProcessDocument.getElementsByTagName("cmmn:case"); 
		List<String> novaLista = new ArrayList<String>();
		
		if(list.getLength() > 0){
			NamedNodeMap taskAttributes = list.item(0).getAttributes();
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes = NodeParser.getTaskAttributes(taskAttributes);
			String id = attributes.get("id");
			novaLista.add(id);
		}
		
		return novaLista;
	}
	
	public List<Tarefa> getTaskList() throws XPathExpressionException{
		NodeList allTasks = getAllTasks();
		List<Tarefa> tarefas = new ArrayList<Tarefa>();
		for(int i =0; i < allTasks.getLength(); i ++){
			NamedNodeMap taskAttributes = allTasks.item(i).getAttributes();
			Tarefa task = new Tarefa();
			task.attributes = NodeParser.getTaskAttributes(taskAttributes);
			task.info.plannedStartDate = task.attributes.get("dataPlanejadaInicio");
			task.info.plannedEndDate = task.attributes.get("dataPlanejadaFim");
			tarefas.add(task);
		}
		return tarefas;
	}

	private NodeList getAllTasks() throws XPathExpressionException{
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//*[contains(local-name(), 'Task')]");
		NodeList list= (NodeList) expr.evaluate(ProcessDocument, XPathConstants.NODESET);
		
		return list;
	}
	
	public String getXml() throws IOException
	{
		InputStream xmlInputStream = readFile(filepath + "\\" + filename);
		byte[] xmlByte = IoUtil.readInputStream(xmlInputStream, filename);
		String xml = new String(xmlByte, "UTF-8");
		return xml;
	}
	
	private InputStream readFile(String path) 
			throws IOException 
	{
		File file = new File(Paths.get(path).toString());
		InputStream stream = new FileInputStream(file);
		return stream;
	}
	
	public void addTask(Tarefa task) throws TransformerException{
		NodeList list = ProcessDocument.getElementsByTagName("cmmn:casePlanModel");
		Node node = list.item(0);
		Element element = ProcessDocument.createElement("cmmn:humanTask");
		element.setAttribute("id", String.join(" ", task.getName()));
		element.setAttribute("name", task.getName());
		element.setAttribute("dataPlanejadaInicio", task.info.plannedStartDate);
		element.setAttribute("dataPlanejadaFim", task.info.plannedEndDate);
		element.setAttribute("tipoTarefa", task.info.taskType);
		node.appendChild(element);
		
		NodeList list2 = ProcessDocument.getElementsByTagName("cmmn:casePlanModel");
		Node node2 = list.item(0);
		Element element2 = ProcessDocument.createElement("cmmn:humanTask");
		element2.setAttribute("id", String.join(" ", task.getName()));
		element2.setAttribute("name", task.getName());
		element2.setAttribute("dataPlanejadaInicio", task.info.plannedStartDate);
		element2.setAttribute("dataPlanejadaFim", task.info.plannedEndDate);
		element2.setAttribute("tipoTarefa", task.info.taskType);
		node2.appendChild(element2);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File(filepath + "\\" + filename + "xml"));
		Source input = new DOMSource(ProcessDocument);

		transformer.transform(input, output);

	}
	
	public void removeTask(String taskId) throws TransformerException{
		Element element =  ProcessDocument.getElementById(taskId);
		element.getParentNode().removeChild(element);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File(filepath + "\\" + filename + "xml"));
		Source input = new DOMSource(ProcessDocument);

		transformer.transform(input, output);
		
	}
	
	public List<String> extrairRequiredTasks() throws XPathExpressionException{
		LOGGER.info("--------------------Extrair Tasks----------------------");
		XPathFactory xpathFactory = XPathFactory.newInstance();		
		XPath xpath = xpathFactory.newXPath();
		
		XPathExpression expr = xpath.compile("//*[local-name()='planItem']//*[local-name()='requiredRule']/../..");
		NodeList list= (NodeList) expr.evaluate(ProcessDocument, XPathConstants.NODESET);
		LOGGER.info("resultado de xpath: " + list.getLength());
		List<String> RequiredTasks = new ArrayList();
		for(int i = 0; i < list.getLength() ; i++){
			LOGGER.info("iteracao");
			NamedNodeMap taskAttributes = list.item(i).getAttributes();
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes = NodeParser.getTaskAttributes(taskAttributes);
			String defReference = attributes.get("definitionRef");
			if(!defReference.isEmpty()){
				RequiredTasks.add(defReference);
				LOGGER.info("tarefas required: " + defReference);
			}
				
		}
		return RequiredTasks;
	}
	
	public HashMap<String, List<String>> extrairSequenceFlow() throws XPathExpressionException{
		HashMap<String, List<String>> Dependencias = new HashMap<String, List<String>>();
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//cmmn:planItem//cmmn:entryCriterion/..");
		NodeList list= (NodeList) expr.evaluate(ProcessDocument, XPathConstants.NODESET);
		
		for(int i = 0; i < list.getLength() ; i++){
			Node node = list.item(i);
			PlanItem planItem = new PlanItem(node);
			if(planItem.sentryRef != null){
				XPathExpression expr2 = xpath.compile("//cmmn:sentry[@id='" + planItem.sentryRef + "']/@sourceRef");
				String id = (String) expr2.evaluate(ProcessDocument, XPathConstants.STRING);
				List<String> novasDependencias = Arrays.asList(id);
				LOGGER.info(planItem.sentryRef + " : " + id);
				List<String> antigasDependencias = Dependencias.get(planItem.sentryRef);
				if(antigasDependencias == null){
					Dependencias.put(planItem.sentryRef, novasDependencias );
				}else{
					antigasDependencias.add(id);
				}
			}
		}
		return Dependencias;
	}
	
}
