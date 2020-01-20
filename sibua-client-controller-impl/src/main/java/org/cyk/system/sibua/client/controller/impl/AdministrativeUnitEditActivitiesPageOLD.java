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
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.model.DualListModel;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditActivitiesPageOLD extends AbstractPageContainerManagedImpl implements Serializable {
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
		try {
		section = new SelectionOne<Section>(Section.class);
		section.setListener(new SelectionOne.Listener<Section>() {
			@Override
			public void processOnSelect(Section section) {
				administrativeUnit.setValue(null);
				activities.setTarget(new ArrayList<Activity>());				
				program.setValue(null);
				action.setValue(null);				
				if(section == null) {
					administrativeUnit.setChoices(null);
					program.setChoices(null);
					action.setChoices(null);
					activities.setSource(new ArrayList<Activity>());
				}else {					
					administrativeUnit.setChoices(__inject__(AdministrativeUnitController.class)
							.read(new Properties().setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
									.setIsPageable(Boolean.FALSE)));										
					program.setChoices( __inject__(ProgramController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Program.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
									.setIsPageable(Boolean.FALSE)));										
					action.setChoices( __inject__(ActionController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Action.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
									.setIsPageable(Boolean.FALSE)));
					activities.setSource((List<Activity>) __inject__(ActivityController.class)
							.read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_SECTIONS_CODES)
									.setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
									.setIsPageable(Boolean.FALSE)));
					if(activities.getSource() == null)
						activities.setSource(new ArrayList<>());
				}				
			}
		});
		if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
			
		}else {
			section.setValue(__inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section")));
		}
		
		program = new SelectionOne<Program>(Program.class);
		program.setAreChoicesGettable(Boolean.FALSE);
		program.setListener(new SelectionOne.Listener<Program>() {
			@Override
			public void processOnSelect(Program program) {
				action.setValue(null);
				if(program == null) {
					action.setChoices(null);
					if(section.getValue() == null) {
						activities.setSource(new ArrayList<>());
					}else {
						activities.setSource((List<Activity>) __inject__(ActivityController.class)
								.read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_SECTIONS_CODES)
										.setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
										.setIsPageable(Boolean.FALSE)));			
					}						
				}else {					
					action.setChoices(__inject__(ActionController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Action.FIELD_PROGRAM, CollectionHelper.listOf(program.getCode())))
									.setIsPageable(Boolean.FALSE)));						
					activities.setSource((List<Activity>) __inject__(ActivityController.class)
							.read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_PROGRAMS_CODES)
									.setFilters(new FilterDto().addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(program.getCode())))
									.setIsPageable(Boolean.FALSE)));
				}
				if(activities.getSource() == null)
					activities.setSource(new ArrayList<>());
			}
		});
		
		action = new SelectionOne<Action>(Action.class);
		action.setAreChoicesGettable(Boolean.FALSE);
		action.setListener(new SelectionOne.Listener<Action>() {
			@Override
			public void processOnSelect(Action action) {
				if(action == null) {
					if(program.getValue() == null) {
						
					}else {
						activities.setSource((List<Activity>) __inject__(ActivityController.class)
								.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(program.getValue().getCode())))
										.setIsPageable(Boolean.FALSE)));
					}
				}else {
					activities.setSource((List<Activity>) __inject__(ActivityController.class)
							.read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_ACTIONS_CODES)
									.setFilters(new FilterDto().addField(Activity.FIELD_ACTION, CollectionHelper.listOf(action.getCode())))
									.setIsPageable(Boolean.FALSE)));
				}
				if(activities.getSource() == null)
					activities.setSource(new ArrayList<>());
			}
		});
		
		administrativeUnit = new SelectionOne<AdministrativeUnit>(AdministrativeUnit.class);
		administrativeUnit.setAreChoicesGettable(Boolean.FALSE);
		administrativeUnit.setListener(new SelectionOne.Listener<AdministrativeUnit>() {
			@Override
			public void processOnSelect(AdministrativeUnit administrativeUnit) {
				activities.setTarget((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE, CollectionHelper.listOf(administrativeUnit.getCode())))
								.setIsPageable(Boolean.FALSE)));	
				if(activities.getTarget() == null)
					activities.setTarget(new ArrayList<Activity>());
			}
		});
		
		activities = new DualListModel<Activity>();
		
		if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
			
		}else {
			administrativeUnit.setValue(__inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier")));
			if(administrativeUnit.getValue() != null) {
				section.setValue(administrativeUnit.getValue().getSection());
				administrativeUnit.setChoices(__inject__(AdministrativeUnitController.class)
						.read(new Properties().setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
								.setIsPageable(Boolean.FALSE)));										
				program.setChoices( __inject__(ProgramController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Program.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
								.setIsPageable(Boolean.FALSE)));										
				action.setChoices( __inject__(ActionController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Action.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
								.setIsPageable(Boolean.FALSE)));
				activities.setSource((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_SECTIONS_CODES)
								.setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
								.setIsPageable(Boolean.FALSE)));
				if(activities.getSource() == null)
					activities.setSource(new ArrayList<>());
				
				activities.setTarget((List<Activity>) __inject__(ActivityController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE, CollectionHelper.listOf(administrativeUnit.getValue().getCode())))
								.setIsPageable(Boolean.FALSE)));	
				if(activities.getTarget() == null)
					activities.setTarget(new ArrayList<Activity>());
			}
		}
		
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		CommandableBuilder saveCommandableBuilder = __inject__(CommandableBuilder.class);
		saveCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		saveCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),"outputPanel");
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	public void save() {
		administrativeUnit.getValue().setActivities(activities.getTarget());
		__inject__(AdministrativeUnitController.class).update(administrativeUnit.getValue(),new Properties().setFields(AdministrativeUnit.FIELD_ACTIVITIES));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Rattachement des activités à l'unité administrative"+(administrativeUnit.getValue() == null ? ConstantEmpty.STRING : " "+administrativeUnit.getValue()
				+" , Section "+administrativeUnit.getValue().getSection());	
	}
	
}
