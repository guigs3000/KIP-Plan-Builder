package org.camunda.bpm.cockpit.plugin.wbs.dto;

import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.camunda.bpm.cockpit.plugin.wbs.NodeParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PlanItem {
	
	public String id;
	public String defReference;
	public boolean required;
	public String sentryRef;
	
	public PlanItem(Node node){
		NamedNodeMap taskAttributes = node.getAttributes();
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes = NodeParser.getTaskAttributes(taskAttributes);
		
		id = attributes.get("id");
		defReference = attributes.get("definitionRef");
		required = false;
		NodeList list = node.getChildNodes();
		for(int i=0; i < list.getLength(); i++){
			Node filho = list.item(i);
			if(filho.getNodeName()== "cmmn:entryCriterion"){
				NamedNodeMap filhoAttributes = filho.getAttributes();
				HashMap<String, String> filhoAttributesDict = new HashMap<String, String>();
				filhoAttributesDict = NodeParser.getTaskAttributes(taskAttributes);
				sentryRef = filhoAttributesDict.get("sentryRef");
			}else if(filho.getNodeName() == "cmmn:requiredRule"){
				required = true;
			}
			
		}
	}

}
