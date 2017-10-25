package org.camunda.bpm.cockpit.plugin.wbs.dto;

import java.util.Dictionary;
import java.util.HashMap;


public class Tarefa {

	public class Info{
		public String plannedStartDate;
		public String plannedEndDate;
		public String taskType;
		public String nickname;
	}
	private String id;

	private String name;

	public Info info;
	public HashMap<String, String> attributes;
	
	public Tarefa(){
		info = new Info();
		attributes = new HashMap<String, String>();
	}
	
	public String getId(){
		return attributes.get("id");
	};
	
	public String getName(){
		return attributes.get("name");
	};
	
	public void setName(String name){
		attributes.put("name", name);
		this.name = name;
	}
}
