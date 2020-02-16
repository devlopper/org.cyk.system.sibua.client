package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.__kernel__.AbstractApplicationScopeLifeCycleListener;
import org.cyk.utility.client.controller.component.menu.MenuBuilderMapInstantiator;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;

@ApplicationScoped
public class ApplicationScopeLifeCycleListener extends AbstractApplicationScopeLifeCycleListener implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void __initialize__(Object object) {
		AutoComplete.INITIAL_NUMBER_OF_RESULTS = 20;
		AutoComplete.QUERY_DELAY = 2000;
		__inject__(org.cyk.system.sibua.client.controller.api.ApplicationScopeLifeCycleListener.class).initialize(null);
		__setQualifierClassTo__(org.cyk.system.sibua.server.annotation.System.class, MenuBuilderMapInstantiator.class);
	}
	
	@Override
	public void __destroy__(Object object) {}
	
}
