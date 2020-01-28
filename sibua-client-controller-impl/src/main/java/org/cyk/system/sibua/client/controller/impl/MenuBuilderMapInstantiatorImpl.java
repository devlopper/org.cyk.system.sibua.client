package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.configuration.ConfigurationHelper;
import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;
import org.cyk.utility.client.controller.session.SessionAttributeEnumeration;

@org.cyk.system.sibua.server.annotation.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String IDENTIFICATION = "identification";
	public static final String NONE = "none";
	
	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		Boolean isAdmin = ConfigurationHelper.is("cyk.variable.admin");
		if(SessionAttributeEnumeration.MENU_BUILDER_MAP.equals(key)) {
			MenuItemBuilder ua = __inject__(MenuItemBuilder.class).setCommandableName("Unité administrative").setCommandableIcon(Icon.BUILDING);
			ua.addChild(__inject__(MenuItemBuilder.class).setCommandableName("Liste").setCommandableNavigationIdentifier("administrativeUnitListView").setCommandableIcon(Icon.LIST));
			ua.addChild(__inject__(MenuItemBuilder.class).setCommandableName("Codification").setCommandableNavigationIdentifier("administrativeUnitCodifyView").setCommandableIcon(Icon.THUMB_TACK));
			if(isAdmin)
				ua.addChild(__inject__(MenuItemBuilder.class).setCommandableName("Génération des codes").setCommandableNavigationIdentifier("administrativeUnitGenerateCodeView").setCommandableIcon(Icon.FLASH));
			ua.addChild(__inject__(MenuItemBuilder.class).setCommandableName("Section").setCommandableNavigationIdentifier("workspaceConfigureView").setCommandableIcon(Icon.GEAR));
			sessionMenuBuilder.addItems(ua);
			
			MenuItemBuilder activity = __inject__(MenuItemBuilder.class).setCommandableName("Activité").setCommandableIcon(Icon.FLASH);
			activity.addChild(__inject__(MenuItemBuilder.class).setCommandableName("Liste").setCommandableNavigationIdentifier("activityListView").setCommandableIcon(Icon.LIST));
			activity.addChild(__inject__(MenuItemBuilder.class).setCommandableName("Rattachement").setCommandableNavigationIdentifier("administrativeUnitActivityCreateView").setCommandableIcon(Icon.LINK));
			sessionMenuBuilder.addItems(activity);
		}else if(IDENTIFICATION.equals(key)) {
			sessionMenuBuilder.addItems(
					__inject__(MenuItemBuilder.class).setCommandableName("Fiche identifiaction").setCommandableIcon(Icon.USERS)
					.addChild(
							__inject__(MenuItemBuilder.class).setCommandableName("Créer").setCommandableNavigationIdentifier("userCreditManagerEditView").setCommandableIcon(Icon.FILE)
							//,__inject__(MenuItemBuilder.class).setCommandableName("Activation").setCommandableNavigationIdentifier("userActivateView").setCommandableIcon(Icon.FLASH)
							,__inject__(MenuItemBuilder.class).setCommandableName("Ouvrir").setCommandableNavigationIdentifier("userOpenView").setCommandableIcon(Icon.FOLDER_OPEN)
							,__inject__(MenuItemBuilder.class).setCommandableName("Renvoyer jeton d'accès").setCommandableNavigationIdentifier("userNotifyAccessTokenView").setCommandableIcon(Icon.SEND)
							//,__inject__(MenuItemBuilder.class).setCommandableName("Edition").setCommandableNavigationIdentifier("userPrintView").setCommandableIcon(Icon.PRINT)
							//,__inject__(MenuItemBuilder.class).setCommandableName("Validation").setCommandableNavigationIdentifier("userValidateView").setCommandableIcon(Icon.EYE)
							//,__inject__(MenuItemBuilder.class).setCommandableName("Compte").setCommandableNavigationIdentifier("userEditAccountView").setCommandableIcon(Icon.SUITCASE)
							)
					);
		}else if(NONE.equals(key)) {
			
		}
	}
	
}
