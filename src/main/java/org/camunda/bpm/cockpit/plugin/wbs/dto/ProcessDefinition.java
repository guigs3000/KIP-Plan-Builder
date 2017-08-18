package org.camunda.bpm.cockpit.plugin.wbs.dto;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

//Processo base usado pelo usuario para criar seus planos de projeto
public class ProcessDefinition {

	public String name;
	public String id;
	public List<Tarefa> tasks;
	public String xml;
	
	public ProcessDefinition(){
		tasks = new ArrayList<Tarefa>();
	}
}
