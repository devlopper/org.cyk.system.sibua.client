package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.__kernel__.klass.PersistableClassesGetter;
import org.cyk.utility.client.controller.AbstractApplicationScopeLifeCycleListenerEntities;

@ApplicationScoped
public class ApplicationScopeLifeCycleListener extends AbstractApplicationScopeLifeCycleListenerEntities implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void __initialize__(Object object) {
		super.__initialize__(object);
		PersistableClassesGetter.COLLECTION.set(List.of(Activity.class,Section.class,AdministrativeUnit.class));
	}
	
	@Override
	public void __destroy__(Object object) {}
	
	/**/
	
}
