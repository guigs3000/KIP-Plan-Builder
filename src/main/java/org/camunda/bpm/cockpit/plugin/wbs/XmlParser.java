package org.camunda.bpm.cockpit.plugin.wbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import javax.xml.transform.OutputKeys;
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
import org.xml.sax.Attributes;
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
	private File file;
	
	
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
		file = new File(filepath + "\\" + filename);
		Document document = db.parse(file);
		ProcessDocument = document;
	}
	
	public ProcessDefinition getPlanoDeProjeto() throws XPathExpressionException{
		ProcessDefinition plano = new ProcessDefinition();
		plano.name = filename;
		plano.tasks = getTaskList();
		
		return plano;
	}
	
	public List<String> getCorrelatedProcesses() throws XPathExpressionException{
		
		 
		List<String> novaLista = new ArrayList<String>();
		XPathFactory xpathFactory = XPathFactory.newInstance();		
		XPath xpath = xpathFactory.newXPath();
		
		XPathExpression expr = xpath.compile("//*[local-name()='case']");
		Node Case = (Node) expr.evaluate(ProcessDocument, XPathConstants.NODE);
		NamedNodeMap atributos = Case.getAttributes();
		HashMap<String, String> attrs = NodeParser.getTaskAttributes(atributos);
		LOGGER.info(attrs.get("wbsbuilder:id"));
		novaLista = Arrays.asList(attrs.get("wbsbuilder:id"));
		
		return novaLista;
	}
	
	public List<Tarefa> getTaskList() throws XPathExpressionException{
		NodeList allTasks = getAllTasks();
		List<Tarefa> tarefas = new ArrayList<Tarefa>();
		for(int i =0; i < allTasks.getLength(); i ++){
			NamedNodeMap taskAttributes = allTasks.item(i).getAttributes();
			Tarefa task = new Tarefa();
			task.attributes = NodeParser.getTaskAttributes(taskAttributes);
			task.info.plannedStartDate = task.attributes.get("wbsbuilder:dataPlanejadaInicio");
			task.info.plannedEndDate = task.attributes.get("wbsbuilder:dataPlanejadaFim");
			task.info.taskType = task.attributes.get("wbsbuilder:tipoTarefa");
			task.info.nickname = task.attributes.get("wbsbuilder:nickname");
			tarefas.add(task);
		}
		return tarefas;
	}
	
	public HashMap<String, Tarefa> getTaskMap() throws XPathExpressionException{
		NodeList allTasks = getAllTasks();
		HashMap<String, Tarefa> tarefas = new HashMap<String, Tarefa>();
		for(int i =0; i < allTasks.getLength(); i ++){
			NamedNodeMap taskAttributes = allTasks.item(i).getAttributes();
			Tarefa task = new Tarefa();
			task.attributes = NodeParser.getTaskAttributes(taskAttributes);
			task.info.plannedStartDate = task.attributes.get("wbsbuilder:dataPlanejadaInicio");
			task.info.plannedEndDate = task.attributes.get("wbsbuilder:dataPlanejadaFim");
			task.info.taskType = task.attributes.get("wbsbuilder:tipoTarefa");
			task.info.nickname = task.attributes.get("wbsbuilder:nickname");
			tarefas.put(task.info.taskType, task);
		}
		return tarefas;
	}

	private NodeList getAllTasks() throws XPathExpressionException{
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//*[contains(local-name(), 'Task') or contains(local-name(), 'task')]");
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
	
	public InputStream readFile(String path) 
			throws IOException 
	{
		File file = new File(Paths.get(path).toString());
		InputStream stream = new FileInputStream(file);
		return stream;
	}
	
	public void addTask(Tarefa task) throws TransformerException, FileNotFoundException, XPathExpressionException{
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//*[local-name()='casePlanModel']");
		Node casePlanModel = (Node) expr.evaluate(ProcessDocument, XPathConstants.NODE);
		
		XPathExpression expr2 = xpath.compile("(//*[local-name()='planItem'])[1]//@id");
		String lastId= (String) expr2.evaluate(ProcessDocument, XPathConstants.STRING);
		int Id = 1;
		if(lastId !=null && !lastId.isEmpty()){
			String[] idParts = lastId.split("_");
			if(idParts.length > 0){
				Id = Integer.parseInt(idParts[idParts.length - 1]) + 1;
			}
		}
		
		String taskId = task.getName().replaceAll("\\s+", "");
		Element element = ProcessDocument.createElement("cmmn:humanTask");
		element.setAttribute("id", taskId);
		element.setAttribute("name", task.getName());
		element.setAttribute("wbsbuilder:dataPlanejadaInicio", task.info.plannedStartDate);
		element.setAttribute("wbsbuilder:dataPlanejadaFim", task.info.plannedEndDate);
		element.setAttribute("wbsbuilder:tipoTarefa", task.info.taskType);
		element.setAttribute("wbsbuilder:nickname", task.info.nickname);
		casePlanModel.appendChild(element);
		
		Element element2 = ProcessDocument.createElement("cmmn:planItem");
		element2.setAttribute("id", "PlanItem_" + Id);
		element2.setAttribute("definitionRef", taskId);
		casePlanModel.insertBefore(element2, casePlanModel.getFirstChild());
		//casePlanModel.insertBefore(ProcessDocument.createTextNode("\n"), element2);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		StreamResult result = new StreamResult(new PrintWriter(
                new FileOutputStream(file, false)));
        DOMSource source = new DOMSource(ProcessDocument);
        transformer.transform(source, result);

	}
	
	public void removeTask(String taskId) throws TransformerException, XPathExpressionException, FileNotFoundException{
		LOGGER.info("task id :" + taskId);
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//*[contains(local-name(), 'Task')][@id='"+taskId+"']");
		Node task = (Node) expr.evaluate(ProcessDocument, XPathConstants.NODE);
		task.getParentNode().removeChild(task);
		
		XPathExpression expr2 = xpath.compile("//*[local-name()='planItem'][@definitionRef='"+taskId+"']");
		Node planItem = (Node) expr2.evaluate(ProcessDocument, XPathConstants.NODE);
		
		
		XPathExpression expr4 = xpath.compile("//*[local-name()='planItem'][@definitionRef='"+taskId+"']/@id");
		String planItemId = (String) expr4.evaluate(ProcessDocument, XPathConstants.STRING);
		planItem.getParentNode().removeChild(planItem);
		LOGGER.info("plan item id: " + planItemId);
		
		XPathExpression expr3 = xpath.compile("//*[@*='"+planItemId+"'][not(@id='"+planItemId+"')]");
		NodeList referencias = (NodeList) expr3.evaluate(ProcessDocument, XPathConstants.NODESET);
		for(int i = 0; i < referencias.getLength(); i ++){
			Node item = referencias.item(i);
			NodeParser.getTaskAttributes(item.getAttributes());
			item.getParentNode().removeChild(item);
		}
		
		
		//Element element =  ProcessDocument.getElementById(taskId);
		//element.getParentNode().removeChild(element);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		//Result output = new StreamResult(new File(filepath + "\\" + filename + "xml"));
		//Source input = new DOMSource(ProcessDocument);
		//transformer.transform(input, output);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		StreamResult result = new StreamResult(new PrintWriter(
                new FileOutputStream(file, false)));
        DOMSource source = new DOMSource(ProcessDocument);
        transformer.transform(source, result);
	}
	
	public HashMap<String,Tarefa> extrairRequiredTasks() throws XPathExpressionException{
		LOGGER.info("--------------------Extrair Tasks----------------------");
		
		HashMap<String, Tarefa> RequiredTasks = new HashMap<String, Tarefa>();
		
		XPathFactory xpathFactory = XPathFactory.newInstance();		
		XPath xpath = xpathFactory.newXPath();
		
		XPathExpression expr = xpath.compile("//*[local-name()='planItem']//*[local-name()='requiredRule']/../..");
		 
		NodeList list= (NodeList) expr.evaluate(ProcessDocument, XPathConstants.NODESET);
		
		for(int i = 0; i < list.getLength() ; i++){
			NamedNodeMap planItenAttributesNodeMap = list.item(i).getAttributes();
			HashMap<String, String> planItenAttributes = new HashMap<String, String>();
			planItenAttributes = NodeParser.getTaskAttributes(planItenAttributesNodeMap);
			String defReference = planItenAttributes.get("definitionRef");
			
			XPathExpression expr6 = xpath.compile("//*[contains(local-name(), 'task') or contains(local-name(), 'Task') ][@id='"+defReference+"']");
			Node tarefa= (Node) expr6.evaluate(ProcessDocument, XPathConstants.NODE);
			if(!defReference.isEmpty()){
				NamedNodeMap tarefaAttributes = tarefa.getAttributes();
				Tarefa task = new Tarefa();
				task.attributes = NodeParser.getTaskAttributes(tarefaAttributes);
				
				RequiredTasks.put(defReference, task);
				LOGGER.info("tarefas required: " + defReference + task.getName());
			}
				
		}
		return RequiredTasks;
	}
	
	public HashMap<String, List<Tarefa>> extrairSequenceFlow() throws XPathExpressionException{
		
		HashMap<String, List<Tarefa>> dependencias = new HashMap<String, List<Tarefa>>();
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		//busca todos itens com entryCriterion
		XPathExpression expr = xpath.compile("//*[local-name()='planItem']//*[local-name()='entryCriterion']/..");
		NodeList itensWithEntryCriterion= (NodeList) expr.evaluate(ProcessDocument, XPathConstants.NODESET);
		
		for(int i = 0; i < itensWithEntryCriterion.getLength() ; i++){
			Node node = itensWithEntryCriterion.item(i);
			PlanItem planItem = new PlanItem(node);
			
			//busca a referncia para sentry
			XPathExpression expr4 = xpath.compile("//*[local-name()='planItem'][@id='"+planItem.id+"']//*[local-name()='entryCriterion']/@sentryRef");
			planItem.sentryRef = (String) expr4.evaluate(ProcessDocument, XPathConstants.STRING);
			
			//Busca id do Elemento que plan item referencia
			XPathExpression expr5 = xpath.compile("//*[local-name()='planItem'][@id='"+planItem.id+"']/@definitionRef");
			String defReference = (String) expr5.evaluate(ProcessDocument, XPathConstants.STRING);
			LOGGER.info("defReference: " + defReference);
			XPathExpression expr10 = xpath.compile("//*[contains(local-name(), 'task') or contains(local-name(), 'Task') ][@id='"+defReference+"']");
			Node tarefaOriginal = (Node) expr10.evaluate(ProcessDocument, XPathConstants.NODE);
			
			if(tarefaOriginal == null){
				continue;
			}
						
			NamedNodeMap taskOriginalAttributes = tarefaOriginal.getAttributes();
			Tarefa taskOriginal = new Tarefa();
			taskOriginal.attributes = NodeParser.getTaskAttributes(taskOriginalAttributes);
			
			if(planItem.sentryRef != null){
				LOGGER.info("sentryRef:" + planItem.sentryRef);
				XPathExpression expr2 = xpath.compile("//*[local-name()='sentry'][@id='" + planItem.sentryRef + "']//*[local-name()='planItemOnPart']");
				NodeList tarefasPredecessoras = (NodeList) expr2.evaluate(ProcessDocument, XPathConstants.NODESET);
				List<Tarefa> listaTarefasPredecessoras = new ArrayList<Tarefa>();
				for(int j = 0 ; j < tarefasPredecessoras.getLength(); j++){
					NamedNodeMap taskAttributes = tarefasPredecessoras.item(j).getAttributes();
					HashMap<String, String> attributes = new HashMap<String, String>();
					attributes = NodeParser.getTaskAttributes(taskAttributes);
					String sourceRef = attributes.get("sourceRef");
					XPathExpression expr7 = xpath.compile("//*[@id='"+sourceRef+"']/@definitionRef");
					String idTarefaPredecessora= (String) expr7.evaluate(ProcessDocument, XPathConstants.STRING);
					
					XPathExpression expr8 = xpath.compile("//*[contains(local-name(), 'task') or contains(local-name(), 'Task')][@id='"+idTarefaPredecessora+"']");
					Node tarefaPredecessora= (Node) expr8.evaluate(ProcessDocument, XPathConstants.NODE);
					
					if(tarefaPredecessora!= null){
						Tarefa tarefaInfo = new Tarefa();
						NamedNodeMap tarefaAttributes = tarefaPredecessora.getAttributes();
						tarefaInfo.attributes = NodeParser.getTaskAttributes(tarefaAttributes);
						LOGGER.info("tarefa predecessora: " + tarefaInfo.getName());
						listaTarefasPredecessoras.add(tarefaInfo);
					}
						
				}
				
				dependencias.put(defReference, listaTarefasPredecessoras);
			}
		}
		
		return dependencias;
	}
	
}
