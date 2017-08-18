package org.camunda.bpm.cockpit.plugin.wbs;

import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.cockpit.plugin.spi.impl.AbstractCockpitPlugin;
import org.camunda.bpm.cockpit.plugin.wbs.resources.WbsPluginRootResource;

public class WbsPlugin extends AbstractCockpitPlugin{
	
	public static final String ID = "wbs-plugin";

	public String getId() {
		return ID;
	}

	@Override
	public Set<Class<?>> getResourceClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();

		classes.add(WbsPluginRootResource.class);

		return classes;
	}

}
