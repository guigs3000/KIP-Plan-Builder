package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.CaseDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinition;

public class ProcessDefinitionResource extends AbstractPluginResource{
	
	public ProcessDefinitionResource(String engineName) {
		super(engineName);
	}

	//Retorna lista com ids das definicoes de processos armazenadas na base do camunda
	@GET
	public List<String> getAllProcessDefinitions() {
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<CaseDefinition> caseDefinitions = repositoryService.createCaseDefinitionQuery()
				.orderByCaseDefinitionName().asc().list();
		List<String> ids = caseDefinitions.stream().map(CaseDefinition::getId).collect(Collectors.toList());
		return ids;
	}
}
