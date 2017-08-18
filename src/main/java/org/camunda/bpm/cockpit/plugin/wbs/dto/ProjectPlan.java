package org.camunda.bpm.cockpit.plugin.wbs.dto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

//Plano de projeto criado pelo usuario para ser validado
public class ProjectPlan {

	public String xml;
	public String name;
	public String filepath;
	public List<String> baseProcessIds;
	public List<Tarefa> tasks;
	public List<Tarefa> possibleTasks;
	
	public ProjectPlan(){
		tasks = new ArrayList<Tarefa>();
		possibleTasks = new ArrayList<Tarefa>();
		baseProcessIds = new ArrayList<String>();
	}
	
	public ProjectPlan(Document ProcessDocument){
		filepath = ProcessDocument.getDocumentURI();
		tasks = new ArrayList<Tarefa>();
		possibleTasks = new ArrayList<Tarefa>();
		baseProcessIds= new ArrayList<String>();
	}
	
}
