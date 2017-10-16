package org.camunda.bpm.cockpit.plugin.wbs;

import java.util.HashMap;
import java.util.logging.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

public class NodeParser {
	
	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	
	static public HashMap<String, String> getTaskAttributes(NamedNodeMap taskAttributes){
		HashMap<String, String> map = new HashMap<String, String>();
		int len = taskAttributes.getLength();
		for(int i =0; i < len; i++){
			Attr attr = (Attr) taskAttributes.item(i);
		    String name = attr.getName();
		    String value = attr.getValue();
		    map.put(name, value);
		    //LOGGER.info(name + ":" + value);
		}
		
		return map;
	}

}
