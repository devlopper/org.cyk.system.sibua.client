package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.component.window.WindowBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditActivitiesPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private SelectionOne<Program> program;
	private SelectionOne<Action> action;
	private SelectionOne<AdministrativeUnit> administrativeUnit;
	
	private LazyDataModel<Activity> availableActivities;
	private Collection<Activity> selectedActivities;
	private List<Activity> __selectedActivities__;
	private final Map<String,Activity> activitiesMap = new HashMap<>();
	
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		administrativeUnit = new SelectionOne<AdministrativeUnit>(AdministrativeUnit.class);
		administrativeUnit.setAreChoicesGettable(Boolean.FALSE);
		try {		
			if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
				
			}else {
				administrativeUnit.select(__inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier")));
			}
			if(administrativeUnit.getValue() != null)
				selectedActivities = __inject__(ActivityController.class).read(new Properties().setIsPageable(Boolean.FALSE).setFilters(new FilterDto()
					.addField(Activity.FIELD_ADMINISTRATIVE_UNIT, administrativeUnit.getValue().getCode())));
			availableActivities = new LazyDataModel<Activity>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
					FilterDto filter = null;				
					filter = new FilterDto();
					filter.addField(Activity.FIELD_NAME, filters.get(Activity.FIELD_NAME));
					filter.addField(Activity.FIELD_SECTION, CollectionHelper.listOf(Boolean.TRUE, MapHelper.readByKey(filters, "action.program.section")));
					filter.addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(Boolean.TRUE, MapHelper.readByKey(filters, "action.program")));
					filter.addField(Activity.FIELD_ACTION, CollectionHelper.listOf(Boolean.TRUE, MapHelper.readByKey(filters, "action")));
					if(CollectionHelper.isNotEmpty(selectedActivities))
						filter.addField(Activity.FIELD_CODE, selectedActivities.stream().map(Activity::getCode).collect(Collectors.toSet()));
					List<Activity> list = (List<Activity>) __inject__(ActivityController.class)
							.read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_CODE_NOT_IN_AND_ADMINISTRATIVEUNIT_DOES_NOT_EXIST_BY_FILTERS)
									.setFilters(filter).setIsPageable(Boolean.FALSE).setFrom(first).setCount(pageSize));
					if(CollectionHelper.isEmpty(list))
						setRowCount(0);
					else {
						Long count = __inject__(ActivityController.class)
								.count(new Properties().setQueryIdentifier(ActivityPersistence.COUNT_WHERE_CODE_NOT_IN_AND_ADMINISTRATIVEUNIT_DOES_NOT_EXIST_BY_FILTERS).setFilters(filter));
						setRowCount(count == null ? 0 : count.intValue());	
					}
					return list;
				}
				
				@Override
				public Object getRowKey(Activity activity) {
					if(activity == null)
						return null;
					activitiesMap.put(activity.getIdentifier(), activity);
					return activity.getIdentifier();
				}
				
				@Override
				public Activity getRowData(String identifier) {
					return activitiesMap.get(identifier);
				}
			};
		
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
		saveCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers());
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	public void openAvailableActivitiesDialog() {
		
	}
	
	public void __select__(Collection<Activity> activities) {
		if(CollectionHelper.isEmpty(activities))
			return;
		if(selectedActivities == null)
			selectedActivities = new ArrayList<>();
		selectedActivities.addAll(activities);
	}
	
	public void __select__(Activity...activities) {
		if(ArrayHelper.isEmpty(activities))
			return;
		__select__(CollectionHelper.listOf(activities));
	}
	
	public void select() {
		if(CollectionHelper.isEmpty(__selectedActivities__))
			return;
		__select__(__selectedActivities__);
		__selectedActivities__.clear();
	}
	
	public void selectOne(Activity activity) {
		if(activity == null)
			return;
		__select__(activity);
	}
	
	public void __unselect__(Collection<Activity> activities) {
		if(CollectionHelper.isEmpty(activities) || CollectionHelper.isEmpty(selectedActivities))
			return;
		selectedActivities.removeAll(activities);
	}
	
	public void __unselect__(Activity...activities) {
		if(ArrayHelper.isEmpty(activities))
			return;
		__unselect__(CollectionHelper.listOf(activities));
	}
	
	public void unselect() {
		if(CollectionHelper.isEmpty(selectedActivities))
			return;
		__unselect__(selectedActivities);
	}
	
	public void unselectOne(Activity activity) {
		if(activity == null)
			return;
		__unselect__(activity);
	}
	
	public void save() {
		administrativeUnit.getValue().setActivities((List<Activity>) selectedActivities);
		__inject__(AdministrativeUnitController.class).update(administrativeUnit.getValue(),new Properties().setFields(AdministrativeUnit.FIELD_ACTIVITIES));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Rattachement des activités à l'unité administrative"+(administrativeUnit.getValue() == null ? ConstantEmpty.STRING : " "+administrativeUnit.getValue()
				+" , Section "+administrativeUnit.getValue().getSection());	
	}
	
	@Override
	protected String __processWindowDialogOkCommandableGetUrl__(WindowBuilder window, CommandableBuilder commandable) {
		PathAsFunctionParameter pathAsFunctionParameter = new PathAsFunctionParameter();
		pathAsFunctionParameter.setIdentifier("administrativeUnitListView");
		String string =  UniformResourceIdentifierHelper.build(new UniformResourceIdentifierAsFunctionParameter().setPath(pathAsFunctionParameter));
		if(defaultSection != null)
			string = string  + "?section="+defaultSection.getCode();
		return string;
	}
}
