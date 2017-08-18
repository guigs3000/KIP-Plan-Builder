package org.camunda.bpm.cockpit.plugin.wbs.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.ws.rs.GET;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.cockpit.plugin.resource.AbstractPluginResource;
import org.camunda.bpm.cockpit.plugin.wbs.XmlFileFilter;
import org.camunda.bpm.cockpit.plugin.wbs.XmlParser;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProcessDefinition;
import org.camunda.bpm.cockpit.plugin.wbs.dto.ProjectPlan;
import org.camunda.bpm.cockpit.plugin.wbs.dto.Tarefa;
import org.camunda.bpm.engine.impl.util.IoUtil;
import org.xml.sax.SAXException;
import org.apache.commons.lang.StringEscapeUtils;


public class GetProjectPlanNamesResource extends AbstractPluginResource{
	private final static Logger LOGGER = Logger.getLogger("wbs-plugin");

	String basePath = System.getProperty("user.dir")+ "\\planosdeprojeto";
	
	public GetProjectPlanNamesResource(String engineName) {
		super(engineName);
		// TODO Auto-generated constructor stub
	}
	
	
	@GET
	public List<ProjectPlan> getProjectPlanNames() throws IOException, ParserConfigurationException, SAXException{
		File dir = new File(basePath);
		LOGGER.info("\n filepath: " + basePath);
		List<String> nomes = new ArrayList<String>();
		List<ProjectPlan> planosDeProjeto = new ArrayList<ProjectPlan>();
		XmlFileFilter filter = new XmlFileFilter();
		

		if(dir.exists()){
			nomes = Arrays.asList(dir.list(filter));
			for(int i =0 ; i < nomes.size(); i++){
				String fileName = nomes.get(i);
				LOGGER.info("\n filename: " + fileName);

				XmlParser myParser = new XmlParser(basePath, fileName);				
				ProjectPlan plano = new ProjectPlan();
				plano.name = fileName;

				plano.xml = myParser.getXml();
				LOGGER.info(plano.xml);
				planosDeProjeto.add(plano);
			}
		}else{
			dir.mkdir();
		}
		
		return planosDeProjeto;
	}
	
	
}
