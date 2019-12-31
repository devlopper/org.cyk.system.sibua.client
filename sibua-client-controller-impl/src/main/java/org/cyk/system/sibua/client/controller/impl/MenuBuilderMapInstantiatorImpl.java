package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;
import org.cyk.utility.client.controller.session.SessionAttributeEnumeration;

@org.cyk.system.sibua.server.annotation.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		if(SessionAttributeEnumeration.MENU_BUILDER_MAP.equals(key)) {
			sessionMenuBuilder.addItems(
					__inject__(MenuItemBuilder.class).setCommandableName("Unité administrative").setCommandableIcon(Icon.BUILDING)
						.addChild(
								__inject__(MenuItemBuilder.class).setCommandableName("Liste").setCommandableNavigationIdentifier("administrativeUnitListView").setCommandableIcon(Icon.LIST)
								,__inject__(MenuItemBuilder.class).setCommandableName("Chargement").setCommandableNavigationIdentifier("administrativeUnitLoadView").setCommandableIcon(Icon.UPLOAD)
								,__inject__(MenuItemBuilder.class).setCommandableName("Codification").setCommandableNavigationIdentifier("administrativeUnitCodifyView").setCommandableIcon(Icon.THUMB_TACK)
								,__inject__(MenuItemBuilder.class).setCommandableName("Localisation").setCommandableNavigationIdentifier("administrativeUnitLocaliseView")
								/*
								,__inject__(MenuItemBuilder.class).setCommandableName("Rattachements aux destinations").setCommandableNavigationIdentifier("administrativeUnitListDestinationView")
								,__inject__(MenuItemBuilder.class).setCommandableName("Rattachements aux activités").setCommandableNavigationIdentifier("administrativeUnitListActivityView")
								,__inject__(MenuItemBuilder.class).setCommandableName("Rattachements des activités aux destinations").setCommandableNavigationIdentifier("activityListDestinationView")
								*/
								,__inject__(MenuItemBuilder.class).setCommandableName("Section").setCommandableNavigationIdentifier("workspaceConfigureView").setCommandableIcon(Icon.GEAR)
								)
					);	
		}else if(ApplicationScopeLifeCycleListener.MENU_USER.equals(key)) {
			sessionMenuBuilder.addItems(
					__inject__(MenuItemBuilder.class).setCommandableName("Utilisateurs").setCommandableIcon(Icon.USERS)
					.addChild(
							__inject__(MenuItemBuilder.class).setCommandableName("Identification").setCommandableNavigationIdentifier("userIdentifyView").setCommandableIcon(Icon.USER)
							,__inject__(MenuItemBuilder.class).setCommandableName("Validation").setCommandableNavigationIdentifier("userValidateView").setCommandableIcon(Icon.EYE)
							,__inject__(MenuItemBuilder.class).setCommandableName("Compte").setCommandableNavigationIdentifier("userEditAccountView").setCommandableIcon(Icon.SUITCASE)
							)
					);	
		}
	}
	
}
