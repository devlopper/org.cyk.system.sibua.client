package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitActivity;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.ReadListener;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.converter.ObjectConverter;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntity;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntityBuilder;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractActivityListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	protected LazyDataModel<Activity> activities;
	protected List<Activity> selectedActivities,__selectedActivities__;
	protected AutoCompleteEntity<AdministrativeUnit> administrativeUnit;
	protected AutoCompleteEntity<AdministrativeUnit> administrativeUnitBeneficiaire;
	protected Boolean overrideAdministrativeUnit,overrideAdministrativeUnitBeneficiaire;;
	protected Commandable saveCommandable;
	protected Map<String,Activity> activitiesMap = new HashMap<>();
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		administrativeUnit = AutoCompleteEntityBuilder.build(AdministrativeUnit.class);
		administrativeUnit.setIdentifier("ua01");
		administrativeUnit.setConverter(__inject__(ObjectConverter.class));
		administrativeUnit.setListener(new AutoCompleteEntity.Listener<AdministrativeUnit>() {
			@Override
			public Collection<AdministrativeUnit> listenComplete(AutoCompleteEntity<AdministrativeUnit> autoCompleteEntity,String queryString) {
				return __inject__(AdministrativeUnitController.class).read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_BY_FILTERS)
						.setFilters(new FilterDto()
								/*.addField(AdministrativeUnit.FIELD_CODE, queryString)*/.addField(AdministrativeUnit.FIELD_NAME, queryString))
						.setFields("identifier,code,name,section")
						);
			}
		});
		administrativeUnit.setReadItemLabelListener(new ReadListener() {
			@Override
			public Object read(Object object) {
				AdministrativeUnit administrativeUnit = (AdministrativeUnit) object;
				if(administrativeUnit == null)
					return null;
				String sectionCode = administrativeUnit.getSection() == null ? "???" : administrativeUnit.getSection().getCode();
				return administrativeUnit.getCode()+"-"+sectionCode+" "+administrativeUnit.getName();
			}
		});
		
		administrativeUnitBeneficiaire = AutoCompleteEntityBuilder.build(AdministrativeUnit.class);
		administrativeUnitBeneficiaire.setIdentifier("ua02");
		administrativeUnitBeneficiaire.setConverter(__inject__(ObjectConverter.class));
		
		administrativeUnitBeneficiaire.setListener(new AutoCompleteEntity.Listener<AdministrativeUnit>() {
			@Override
			public Collection<AdministrativeUnit> listenComplete(AutoCompleteEntity<AdministrativeUnit> autoCompleteEntity,String queryString) {
				if(defaultSection == null)
					return __inject__(AdministrativeUnitController.class).readByString(queryString);
				return __inject__(AdministrativeUnitController.class).read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_BY_FILTERS)
						.setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, List.of(defaultSection.getCode()))
								/*.addField(AdministrativeUnit.FIELD_CODE, queryString)*/.addField(AdministrativeUnit.FIELD_NAME, queryString))
						.setFields("identifier,code,name")
						);
			}
		});
		
		activities = new LazyDataModel<Activity>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				FilterDto filter = new FilterDto();
				filter.addField(Activity.FIELD_CODE, MapHelper.readByKey(filters, Activity.FIELD_CODE));
				filter.addField(Activity.FIELD_NAME, MapHelper.readByKey(filters, Activity.FIELD_NAME));				
				filter.addField(AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT, MapHelper.readByKey(filters, Activity.FIELD_ADMINISTRATIVE_UNIT));
				filter.addField(Activity.FIELD_ACTION, MapHelper.readByKey(filters, Activity.FIELD_ACTION));
				filter.addField(Activity.FIELD_PROGRAM, MapHelper.readByKey(filters, "action.program"));
				String sectionCode = (String) MapHelper.readByKey(filters, "action.program.section");
				if(StringHelper.isBlank(sectionCode))
					sectionCode = defaultSection == null ? null : defaultSection.getCode();		
				filter.addField(Activity.FIELD_SECTION, sectionCode);
				
				List<Activity> list = (List<Activity>) __inject__(ActivityController.class).read(__getProperties__(filter, first, pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(ActivityController.class).count(new Properties().setQueryIdentifier(ActivityPersistence.COUNT_BY_FILTERS_CODES_LIKE).setFilters(filter));
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
		
		CommandableBuilder saveCommandableBuilder = __inject__(CommandableBuilder.class);
		saveCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		saveCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),":form:dataTable");
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	protected Properties __getProperties__(Object filter,Object first,Object pageSize) {
		return new Properties().setQueryIdentifier(ActivityPersistence.READ_BY_FILTERS_CODES_LIKE)
		.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME+","+Activity.FIELD_ACTION+","+Activity.FIELD_ADMINISTRATIVE_UNIT)
		.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
	public void openEditDialog() {
		if(CollectionHelper.isEmpty(selectedActivities))
			return;
		if(__selectedActivities__ == null)
			__selectedActivities__ = new ArrayList<>();
		if(__selectedActivities__ != null)
			__selectedActivities__.clear();
		__selectedActivities__ = new ArrayList<Activity>();		
		__selectedActivities__.addAll(selectedActivities);
	}
	
	public void save() {
		if(CollectionHelper.isEmpty(__selectedActivities__))
			return;
		__selectedActivities__.forEach(new Consumer<Activity>() {
			@Override
			public void accept(Activity activity) {
				if(administrativeUnit.getValue() != null/* || Boolean.TRUE.equals(overrideAdministrativeUnit)*/)
					activity.setAdministrativeUnit((AdministrativeUnit) administrativeUnit.getValue());
				if(administrativeUnitBeneficiaire.getValue() != null /*Boolean.TRUE.equals(overrideAdministrativeUnitBeneficiaire)*/)
					activity.setAdministrativeUnitBeneficiaire((AdministrativeUnit) administrativeUnitBeneficiaire.getValue());
			}
		});
		__inject__(ActivityController.class).updateMany(__selectedActivities__,new Properties().setFields(Activity.FIELD_ADMINISTRATIVE_UNIT+","+Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE));
		selectedActivities.clear();
		selectedActivities = null;
		__selectedActivities__.clear();
		__selectedActivities__ = null;
	}
}
