package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActionController;
import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.ProgramController;
import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.primefaces.model.DualListModel;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditActivitiesPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private SelectionOne<Program> program;
	private SelectionOne<Action> action;
	private SelectionOne<AdministrativeUnit> administrativeUnit;
	
	private DualListModel<Activity> activities;	
	
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		section = new SelectionOne<Section>(Section.class);
		section.setListener(new SelectionOne.Listener<Section>() {
			@Override
			public void processOnSelect(Section section) {
				administrativeUnit.setValue(null);
				administrativeUnit.setChoices(__inject__(AdministrativeUnitController.class)
						.read(new Properties().setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
								.setIsPageable(Boolean.FALSE)));
				
				program.setValue(null);
				program.setChoices( __inject__(ProgramController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Program.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
								.setIsPageable(Boolean.FALSE)));
				
				action.setValue(null);
				action.setChoices( __inject__(ActionController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Action.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
								.setIsPageable(Boolean.FALSE)));
				
				CollectionHelper.clear(activities.getSource());
				CollectionHelper.clear(activities.getTarget());				
				activities.setSource((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
								.setIsPageable(Boolean.FALSE)));
				activities.setTarget(new ArrayList<Activity>());	
				
			}
		});
		
		program = new SelectionOne<Program>(Program.class);
		program.setAreChoicesGettable(Boolean.FALSE);
		program.setListener(new SelectionOne.Listener<Program>() {
			@Override
			public void processOnSelect(Program program) {
				action.setValue(null);
				action.setChoices(__inject__(ActionController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Action.FIELD_PROGRAM, CollectionHelper.listOf(program.getCode())))
								.setIsPageable(Boolean.FALSE)));	
				
				CollectionHelper.clear(activities.getSource());
				CollectionHelper.clear(activities.getTarget());				
				activities.setSource((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(program.getCode())))
								.setIsPageable(Boolean.FALSE)));
				activities.setTarget(new ArrayList<Activity>());	
			}
		});
		
		action = new SelectionOne<Action>(Action.class);
		action.setAreChoicesGettable(Boolean.FALSE);
		action.setListener(new SelectionOne.Listener<Action>() {
			@Override
			public void processOnSelect(Action action) {
				CollectionHelper.clear(activities.getSource());
				CollectionHelper.clear(activities.getTarget());
				activities.setSource((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_ACTION, CollectionHelper.listOf(action.getCode())))
								.setIsPageable(Boolean.FALSE)));
				activities.setTarget(new ArrayList<Activity>());					
			}
		});
		
		administrativeUnit = new SelectionOne<AdministrativeUnit>(AdministrativeUnit.class);
		administrativeUnit.setAreChoicesGettable(Boolean.FALSE);
		administrativeUnit.setListener(new SelectionOne.Listener<AdministrativeUnit>() {
			@Override
			public void processOnSelect(AdministrativeUnit administrativeUnit) {
				//CollectionHelper.clear(activities.getSource());
				//CollectionHelper.clear(activities.getTarget());
				
				activities.setTarget((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_ADMINISTRATIVE_UNIT, CollectionHelper.listOf(administrativeUnit.getCode())))
								.setIsPageable(Boolean.FALSE)));				
			}
		});
		
		activities = new DualListModel<Activity>();
		
		CommandableBuilder saveCommandableBuilder = __inject__(CommandableBuilder.class);
		saveCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	public void save() {
		administrativeUnit.getValue().setActivities(activities.getTarget());
		__inject__(AdministrativeUnitController.class).update(administrativeUnit.getValue(),new Properties().setFields(AdministrativeUnit.FIELD_ACTIVITIES));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Rattachement des activités";
	}
	
}
