package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.IOException;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginRootResource;
import org.camunda.bpm.cockpit.plugin.wbs.WbsPlugin;

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
}
