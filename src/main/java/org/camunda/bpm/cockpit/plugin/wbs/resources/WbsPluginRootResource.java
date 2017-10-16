package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.IOException;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginRootResource;
import org.camunda.bpm.cockpit.plugin.wbs.WbsPlugin;
import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;

@Path("plugin/" + WbsPlugin.ID)
public class WbsPluginRootResource extends AbstractPluginRootResource{

	public WbsPluginRootResource() {
	    super(WbsPlugin.ID);
	}

	@Path("{engineName}/process-definition")
	public ProcessDefinitionResource getAllProcessDefinitions(@PathParam("engineName") String engineName) {
		return subResource(new ProcessDefinitionResource(engineName), engineName);
	}
	
	@Path("{engineName}/getProjectPlanNames")
	public GetProjectPlanNamesResource getProjectPlanNames(@PathParam("engineName") String engineName) {
		return subResource(new GetProjectPlanNamesResource(engineName), engineName);
	}
	
	@Path("{engineName}/tasks")
	public ProjectPlanTasksResource getTasks(@PathParam("engineName") String engineName, @QueryParam("planoProjeto") String planoProjeto) {
		return subResource(new ProjectPlanTasksResource(engineName, planoProjeto), engineName);
	}
	
	@Path("{engineName}/createProjectPlan")
	public CreateProjectPlanResource createProjectPlan(@PathParam("engineName") String engineName, @QueryParam("IdProcesso") String IdProcesso, @QueryParam("NomePlano") String NomePlano) throws IOException {
		return subResource(new CreateProjectPlanResource(engineName, IdProcesso, NomePlano), engineName);
	}
	
	@Path("{engineName}/createTask/{NomePlano}")
	public CreateProjectPlanTaskResource createTask (@PathParam("engineName") String engineName, @PathParam("NomePlano") String NomePlano, 	@QueryParam("taskName") String taskName, @QueryParam("taskInicio") String taskInicio, @QueryParam("taskFim") String taskFim, @QueryParam("taskTipo") String taskTipo) throws IOException {
		Tarefa task = new Tarefa();
		task.setName(taskName);
		task.info.plannedStartDate = taskInicio;
		task.info.plannedEndDate = taskFim;
		task.info.taskType = taskTipo;
		return subResource(new CreateProjectPlanTaskResource(engineName, NomePlano, task), engineName);
	}
	
	@Path("{engineName}/deleteTask/{NomePlano}")
	public DeleteProjectPlanTaskResource deleteTask (@PathParam("engineName") String engineName, @PathParam("NomePlano") String NomePlano, @QueryParam("taskId") String taskId) throws IOException {
		return subResource(new DeleteProjectPlanTaskResource(engineName, NomePlano, taskId), engineName);
	}
	
	@Path("{engineName}/validate/{NomePlano}")
	public ValidateResource validate(@PathParam("engineName") String engineName, @PathParam("NomePlano") String NomePlano) throws IOException {
		return subResource(new ValidateResource(engineName, NomePlano), engineName);
	}
}
