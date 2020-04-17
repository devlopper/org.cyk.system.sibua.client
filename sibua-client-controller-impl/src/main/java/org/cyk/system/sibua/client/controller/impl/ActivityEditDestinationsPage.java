package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActionController;
import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.DestinationController;
import org.cyk.system.sibua.client.controller.api.ProgramController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.Destination;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.server.persistence.api.DestinationPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.omnifaces.util.Faces;
import org.primefaces.model.DualListModel;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActivityEditDestinationsPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private SelectionOne<Program> program;
	private SelectionOne<Action> action;
	private SelectionOne<Activity> activity;
	
	private DualListModel<Destination> destinations = new DualListModel<>();	
	
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {
			section = new SelectionOne<Section>(Section.class);
			section.setListener(new SelectionOne.Listener<Section>() {
				@Override
				public void processOnSelect(Section section) {			
					program.setValue(null);
					action.setValue(null);	
					activity.setValue(null);
					destinations.setTarget(new ArrayList<>());
					if(section == null) {
						program.setChoices(null);
						action.setChoices(null);
						activity.setChoices(null);
						destinations.setSource(new ArrayList<>());
					}else {											
						program.setChoices( __inject__(ProgramController.class)
								.read(new Properties().setFilters(new Filter.Dto().addField(Program.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));										
						action.setChoices( __inject__(ActionController.class)
								.read(new Properties().setFilters(new Filter.Dto().addField(Action.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));
						activity.setChoices(__inject__(ActivityController.class)
								.read(new Properties().setFilters(new Filter.Dto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));				
						destinations.setSource((List<Destination>) __inject__(DestinationController.class)
								.read(new Properties().setQueryIdentifier(DestinationPersistence.READ_WHERE_ACTIVITY_DOES_NOT_EXIST_BY_SECTIONS_CODES)
										.setFilters(new Filter.Dto().addField(Destination.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));
						if(destinations.getSource() == null)
							destinations.setSource(new ArrayList<>());
					}					
				}
			});
			if(StringHelper.isNotBlank(Faces.getRequestParameter("section")))
				section.select(__inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section")));
			
			program = new SelectionOne<Program>(Program.class);
			program.setAreChoicesGettable(Boolean.FALSE);
			program.setListener(new SelectionOne.Listener<Program>() {
				@Override
				public void processOnSelect(Program program) {
					action.setValue(null);
					activity.setValue(null);
					if(program == null) {
						action.setChoices(null);
						if(section.getValue() == null) {
							
						}else {
							activity.setChoices(__inject__(ActivityController.class)
									.read(new Properties().setFilters(new Filter.Dto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
											.setIsPageable(Boolean.FALSE)));			
						}						
					}else {					
						action.setChoices(__inject__(ActionController.class)
								.read(new Properties().setFilters(new Filter.Dto().addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(program.getCode())))
										.setIsPageable(Boolean.FALSE)));						
						activity.setChoices(__inject__(ActivityController.class)
								.read(new Properties().setFilters(new Filter.Dto().addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(program.getCode())))
										.setIsPageable(Boolean.FALSE)));	
					}
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
							activity.setChoices(__inject__(ActivityController.class)
									.read(new Properties().setFilters(new Filter.Dto().addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(program.getValue().getCode())))
											.setIsPageable(Boolean.FALSE)));
						}
					}else {
						activity.setChoices(__inject__(ActivityController.class)
								.read(new Properties().setFilters(new Filter.Dto().addField(Activity.FIELD_ACTION, CollectionHelper.listOf(action.getCode())))
										.setIsPageable(Boolean.FALSE)));
					}
				}
			});
			
			activity = new SelectionOne<Activity>(Activity.class);
			activity.setAreChoicesGettable(Boolean.FALSE);
			activity.setListener(new SelectionOne.Listener<Activity>() {
				@Override
				public void processOnSelect(Activity activity) {
					destinations.setTarget((List<Destination>) __inject__(DestinationController.class)
							.read(new Properties().setFilters(new Filter.Dto().addField(Destination.FIELD_ACTIVITY, CollectionHelper.listOf(activity.getCode())))
									.setIsPageable(Boolean.FALSE)));
					if(destinations.getTarget() == null)
						destinations.setTarget(new ArrayList<>());
				}
			});
			
			/*
			destinations = new DualListModel<>();
			
			if(StringHelper.isBlank(Faces.getRequestParameter("activity"))) {
				
			}else {
				activity.setValue(__inject__(ActivityController.class).readBySystemIdentifier(Faces.getRequestParameter("activity")));
				if(activity.getValue() != null) {
					section.setValue(activity.getValue().getSection());
					activity.setChoices(__inject__(ActivityController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
									.setIsPageable(Boolean.FALSE)));										
					program.setChoices( __inject__(ProgramController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Program.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
									.setIsPageable(Boolean.FALSE)));										
					action.setChoices( __inject__(ActionController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Action.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
									.setIsPageable(Boolean.FALSE)));
					destinations.setSource((List<Destination>) __inject__(DestinationController.class)
							.read(new Properties().setQueryIdentifier(DestinationPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_SECTIONS_CODES)
									.setFilters(new FilterDto().addField(Destination.FIELD_SECTION, CollectionHelper.listOf(section.getValue().getCode())))
									.setIsPageable(Boolean.FALSE)));
					if(destinations.getSource() == null)
						destinations.setSource(new ArrayList<>());
					
					destinations.setTarget((List<Destination>) __inject__(DestinationController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Destination.FIELD_ADMINISTRATIVE_UNIT, CollectionHelper.listOf(activity.getValue().getCode())))
									.setIsPageable(Boolean.FALSE)));	
					if(destinations.getTarget() == null)
						destinations.setTarget(new ArrayList<>());
				}
			}
			*/
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
		activity.getValue().setDestinations(destinations.getTarget());
		__inject__(ActivityController.class).update(activity.getValue(),new Properties().setFields(Activity.FIELD_DESTINATIONS));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Rattachement des destinations aux activités";
	}
	
}
