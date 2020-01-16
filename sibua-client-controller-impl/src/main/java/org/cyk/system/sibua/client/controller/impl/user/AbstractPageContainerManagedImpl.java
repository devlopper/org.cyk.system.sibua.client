package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.cyk.system.sibua.client.controller.impl.MenuBuilderMapInstantiatorImpl;
import org.cyk.utility.client.controller.component.window.WindowBuilder;

import lombok.Getter;
import lombok.Setter;

@Named @Getter @Setter
public abstract class AbstractPageContainerManagedImpl extends org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected Object __getMenuBuilderMapKey__() {
		return MenuBuilderMapInstantiatorImpl.IDENTIFICATION;
	}
	
	@Override
	protected WindowBuilder __getWindowBuilder__(List<String> subDurations) {
		WindowBuilder windowBuilder = super.__getWindowBuilder__(subDurations);
		windowBuilder.getApplicationName(Boolean.TRUE).setValue("Module d'identification des acteurs du SIB");
		return windowBuilder;
	}
	
}
