package org.camunda.bpm.cockpit.plugin.wbs;

import java.util.HashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

public class NodeParser {
	
	static public HashMap<String, String> getTaskAttributes(NamedNodeMap taskAttributes){
		HashMap<String, String> map = new HashMap<String, String>();
		int len = taskAttributes.getLength();
		for(int i =0; i < len; i++){
			Attr attr = (Attr) taskAttributes.item(i);
		    String name = attr.getName();
		    String value = attr.getValue();
		    map.put(name, value);
		}
		
		return map;
	}

}
