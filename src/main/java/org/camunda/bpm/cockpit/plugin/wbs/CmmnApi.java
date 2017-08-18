package org.camunda.bpm.cockpit.plugin.wbs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.camunda.bpm.model.cmmn.Cmmn;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.camunda.bpm.model.cmmn.instance.Case;
import org.camunda.bpm.model.cmmn.instance.CmmnModelElementInstance;
import org.camunda.bpm.model.cmmn.instance.Definitions;
import org.camunda.bpm.model.cmmn.instance.Task;

public class CmmnApi {

	public void create(){
		CmmnModelInstance modelInstance = Cmmn.createEmptyModel();
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace("http://camunda.org/examples");
		modelInstance.setDefinitions(definitions);

		Case caseElement = modelInstance.newInstance(Case.class);
		caseElement.setId("a-case");
		definitions.addChildElement(caseElement);
		Task task = modelInstance.newInstance(Task.class);
		//task.
		//createElement();
		
		Cmmn.validateModel(modelInstance);

		// convert to string
		String xmlString = Cmmn.convertToString(modelInstance);

		// write to file
		File file = new File("D:\\Camunda\\server\\apache-tomcat-8.0.24\\bin\\planosdeprojeto\\testeNovo.cmmn");
		Cmmn.writeModelToFile(file, modelInstance);
	}

	protected <T extends CmmnModelElementInstance> T createElement(CmmnModelElementInstance parentElement, String id, Class<T> elementClass, CmmnModelInstance modelInstance) {
		T element = modelInstance.newInstance(elementClass);
		element.setAttributeValue("id", id, true);
		parentElement.addChildElement(element);
		return element;
	}
	
}
