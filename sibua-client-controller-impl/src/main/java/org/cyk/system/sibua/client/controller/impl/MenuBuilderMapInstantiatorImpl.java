package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;

@org.cyk.system.sibua.server.annotation.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		sessionMenuBuilder.addItems(
				__inject__(MenuItemBuilder.class).setCommandableName("Unité administrative").setCommandableIcon(Icon.BUILDING)
					.addChild(
							__inject__(MenuItemBuilder.class).setCommandableName("Rattacher les activités").setCommandableNavigationIdentifier("administrativeUnitEditActivitiesView")
							,__inject__(MenuItemBuilder.class).setCommandableName("Liste des unités administratives").setCommandableNavigationIdentifier("administrativeUnitListView")
							)
				/*,__inject__(MenuItemBuilder.class).setCommandableName("Parametrage").setCommandableIcon(Icon.GEARS)
					.list(PersistableClassesGetter.getInstance().get())
					*/
				);	
	}
	
}
