package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProjectPlan;
import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.xml.sax.SAXException;

public class ValidateResource extends AbstractPluginResource{

	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");
	
	private String NomePlano = "";
	String basePath = System.getProperty("user.dir")+ "\\planosdeprojeto";

	public ValidateResource(String engineName, String NomePlano) throws IOException {
		super(engineName);
		this.NomePlano = NomePlano;
	}
	
	
	@GET
	public List<String> validate() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, ParseException{
		
		List<String> inconformidades = new ArrayList<String>();
		
		//Lista de tarefas que devem ser executadas de acordo com definicao do modelo cmmn
		HashMap<String, Tarefa> requiredTasks = new HashMap<String,Tarefa>();
		//dict de tarefas que possuem dependencias de acordo com definicao do modelo cmmn
		HashMap<String, List<Tarefa>> dependencias = new HashMap<String, List<Tarefa>>();
		
		//Busca o id dos processos correlacionados com plano de projeto.
		XmlParser planoProjetoXml = new XmlParser(basePath, NomePlano);
		ProjectPlan planoDeProjeto = new ProjectPlan();
		planoDeProjeto.baseProcessIds = planoProjetoXml.getCorrelatedProcesses();
		
		ProcessEngine processEngine = ProcessEngines.getProcessEngine(engineName);
		RepositoryService repositoryService = processEngine.getRepositoryService();
		
		//Para cada processo correlacionado, busca o modelo e extrai tarefas obrigatorias e 
		//as dependencias
		for(int i = 0; i < planoDeProjeto.baseProcessIds.size(); i++ ){
			String IdProcesso = planoDeProjeto.baseProcessIds.get(i);
			InputStream processo = repositoryService.getCaseModel(IdProcesso);
			byte[] xmlByte = IoUtil.readInputStream(processo, "processo");
			String xmlString = new String(xmlByte, "UTF-8");
			XmlParser xmlParser = new XmlParser(xmlString);
			requiredTasks.putAll(xmlParser.extrairRequiredTasks());
			dependencias = xmlParser.extrairSequenceFlow();	
		}
		
		//Lista de tarefas que serao executadas de acordo com plano de projeto 
		List<String> executedTasks = new ArrayList<String>();
		//Dict com tipo da tarefa e suas informacoes
		HashMap<String, Tarefa> tarefasPlanejadas = planoProjetoXml.getTaskMap();
		
		//verifica se dependencias foram obedecidas
		for(Map.Entry<String, Tarefa> entry: tarefasPlanejadas.entrySet()){
			Tarefa tarefa = entry.getValue();
			executedTasks.add(tarefa.info.taskType);
			if(dependencias.containsKey(tarefa.info.taskType)){
				List<Tarefa> tarefasPredecessoras = dependencias.get(tarefa.info.taskType);
				for(int j = 0; j < tarefasPredecessoras.size(); j++){
					Tarefa tarefaPlanejada = tarefasPlanejadas.get(tarefa.info.taskType);
					Tarefa tarefaPredecessora = tarefasPlanejadas.get(tarefasPredecessoras.get(j).getId());
					if(tarefaPredecessora == null){
						inconformidades.add(String.format( "'%s' depende de '%s'! Inconformidade: '%s' não foi planejada.", tarefaPlanejada.getName(),tarefasPredecessoras.get(j).getName(), tarefasPredecessoras.get(j).getName()));
					}else{
						SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");    
						Date dataFimTarefaPredecessora = fmt.parse(tarefaPredecessora.info.plannedEndDate); 
						Date dataInicioTarefaPlanejada = fmt.parse(tarefaPlanejada.info.plannedStartDate);
						if(dataInicioTarefaPlanejada.before(dataFimTarefaPredecessora)){
							inconformidades.add(String.format("Inconformidade: tarefa '%s' foi planejada para execução antes de sua dependencia '%s'", tarefaPlanejada.getName(),tarefaPredecessora.getName()));
						}
					}
				}
			}
		}
		
		//Verifica que tarefa obrigatoria nao foi executada
		for(Map.Entry<String, Tarefa> entry: requiredTasks.entrySet()){
			if(!executedTasks.contains(entry.getKey())){
				inconformidades.add(String.format("tarefa obrigatoria não planejada: '%s'", entry.getValue().getName()));	
			}
		}
		
		return inconformidades;
	}
	
}
