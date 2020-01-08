
package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.instance.SelectionMany;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitGenerateCodePage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionMany<Section> sections;
	private Commandable generateCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {
			sections = new SelectionMany<Section>(Section.class);		
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		
		CommandableBuilder generateCommandableBuilder = __inject__(CommandableBuilder.class);
		generateCommandableBuilder.setName("Générer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					generate();
				}
			}
		);
		generateCommandable = generateCommandableBuilder.execute().getOutput();
	}
	
	public void generate() {
		if(sections != null && CollectionHelper.isEmpty(sections.getValue()))
			return;
		__inject__(AdministrativeUnitController.class).generateCodesBySections(sections.getValue());
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Génération des codes des unités administratives";
	}
	
}
