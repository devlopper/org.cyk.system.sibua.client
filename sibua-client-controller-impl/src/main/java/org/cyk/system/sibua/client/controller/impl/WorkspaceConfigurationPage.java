package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class WorkspaceConfigurationPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		section = new SelectionOne<Section>(Section.class);
		CommandableBuilder saveCommandableBuilder = __inject__(CommandableBuilder.class);
		saveCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					SessionHelper.setAttributeValue(Section.class, section.getValue());
				}
			}
		);
		saveCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),"outputPanel");
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Configuration de l'espace de travail";
	}
	
}
