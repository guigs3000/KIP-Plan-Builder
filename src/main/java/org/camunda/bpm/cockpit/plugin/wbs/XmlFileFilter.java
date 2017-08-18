package org.camunda.bpm.cockpit.plugin.wbs;

import java.io.File;
import java.io.FilenameFilter;

public class XmlFileFilter implements FilenameFilter{

	@Override
	public boolean accept(File dir, String name){
		if(name.toLowerCase().endsWith(".cmmn")){
			return true;
		}
		return false;
	}
}
