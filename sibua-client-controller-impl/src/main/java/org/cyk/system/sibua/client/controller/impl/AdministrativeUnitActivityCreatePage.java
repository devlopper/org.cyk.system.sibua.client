package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitActivity;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitActivityCreatePage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<AdministrativeUnitActivity> __administrativeUnitActivities__;
	private LazyDataModel<AdministrativeUnitActivity> administrativeUnitActivities;	
	private List<AdministrativeUnitActivity> selectedAdministrativeUnitActivities;
	private Map<String,AdministrativeUnitActivity> administrativeUnitActivityMap = new HashMap<>();
	
	private Commandable createCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();		
		Collection<Activity> activities = defaultSection == null ? null : __inject__(ActivityController.class).read(new Properties()
				.setQueryIdentifier(ActivityPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_FILTERS)
				.setFilters(new FilterDto().addField(Activity.FIELD_SECTION, defaultSection == null ? null : List.of(defaultSection.getCode())))
				.setFields("identifier,code,name,action")
				.setIsPageable(Boolean.FALSE)
				);
		if(CollectionHelper.isNotEmpty(activities))
			__administrativeUnitActivities__ = activities.stream().map(activity -> new AdministrativeUnitActivity(null,activity)).collect(Collectors.toList());
		
		administrativeUnitActivities = new LazyDataModel<AdministrativeUnitActivity>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnitActivity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				/*
				FilterDto filter = new FilterDto();
				filter.addField(Activity.FIELD_CODE, MapHelper.readByKey(filters, Activity.FIELD_CODE));
				filter.addField(Activity.FIELD_NAME, MapHelper.readByKey(filters, Activity.FIELD_NAME));				
				filter.addField(Activity.FIELD_ADMINISTRATIVE_UNIT, CollectionHelper.listOf(Boolean.TRUE,MapHelper.readByKey(filters, Activity.FIELD_ADMINISTRATIVE_UNIT)));
				filter.addField(Activity.FIELD_ACTION, CollectionHelper.listOf(Boolean.TRUE,MapHelper.readByKey(filters, Activity.FIELD_ACTION)));
				filter.addField(Activity.FIELD_PROGRAM, CollectionHelper.listOf(Boolean.TRUE,MapHelper.readByKey(filters, "action.program")));
				String sectionCode = (String) MapHelper.readByKey(filters, "action.program.section");
				if(StringHelper.isBlank(sectionCode))
					sectionCode = defaultSection == null ? null : defaultSection.getCode();		
				filter.addField(Activity.FIELD_SECTION, CollectionHelper.listOf(Boolean.TRUE,sectionCode));
				*/
				List<AdministrativeUnitActivity> list = __administrativeUnitActivities__;
				setRowCount(CollectionHelper.getSize(__administrativeUnitActivities__));	
				return list;
			}
			
			@Override
			public Object getRowKey(AdministrativeUnitActivity administrativeUnitActivity) {
				if(administrativeUnitActivity == null)
					return null;
				administrativeUnitActivityMap.put(administrativeUnitActivity.getIdentifier(), administrativeUnitActivity);
				return administrativeUnitActivity.getIdentifier();
			}
			
			@Override
			public AdministrativeUnitActivity getRowData(String identifier) {
				return administrativeUnitActivityMap.get(identifier);
			}
		};
		
		CommandableBuilder createCommandableBuilder = __inject__(CommandableBuilder.class);
		createCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					create();
				}
			}
		);
		createCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers());
		createCommandable = createCommandableBuilder.execute().getOutput();
	}
	
	public Collection<AdministrativeUnit> readAdministrativeUnitByString(String string) {
		if(defaultSection == null)
			return __inject__(AdministrativeUnitController.class).readByString(string);
		return __inject__(AdministrativeUnitController.class).read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_BY_FILTERS)
				.setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, defaultSection == null ? null : List.of(defaultSection.getCode()))
						.addField(AdministrativeUnit.FIELD_NAME, string))
				.setFields("identifier,code,name")
				);
	}
	
	public void create() {
		if(CollectionHelper.isNotEmpty(selectedAdministrativeUnitActivities))
			__inject__(AdministrativeUnitActivityController.class).createMany(selectedAdministrativeUnitActivities);
		
		if(CollectionHelper.isNotEmpty(__administrativeUnitActivities__)) {
			Collection<AdministrativeUnitActivity> administrativeUnitActivities = __administrativeUnitActivities__.stream().filter(x -> x.getAdministrativeUnit() != null).collect(Collectors.toList());
			if(CollectionHelper.isNotEmpty(administrativeUnitActivities))
				__inject__(AdministrativeUnitActivityController.class).createMany(administrativeUnitActivities);
		}
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Rattachement des activités aux unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
