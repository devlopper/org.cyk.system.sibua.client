package org.cyk.system.sibua.client.deployment;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

import org.cyk.system.sibua.client.controller.impl.ApplicationScopeLifeCycleListener;
import org.cyk.user.interface_.theme.web.jsf.primefaces.atlantis.dgbf.DesktopDefault;
import org.cyk.utility.client.deployment.AbstractServletContextListener;

@WebListener
public class ServletContextListener extends AbstractServletContextListener implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void __initialize__(ServletContext context) {
		DesktopDefault.DYNAMIC_MENU = Boolean.FALSE;
		DesktopDefault.IS_SHOW_USER_MENU = Boolean.FALSE;
		DesktopDefault.SYSTEM_NAME = "SIB";
		DesktopDefault.SYSTEM_LINK = "#";
		//VariableHelper.write(VariableName.SYSTEM_LOGGING_THROWABLE_PRINT_STACK_TRACE, Boolean.TRUE);
		super.__initialize__(context);
		__inject__(ApplicationScopeLifeCycleListener.class).initialize(null);
	}
	
}
