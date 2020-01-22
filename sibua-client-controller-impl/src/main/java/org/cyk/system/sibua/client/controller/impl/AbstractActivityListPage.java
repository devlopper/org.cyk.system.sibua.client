package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.ActivityCostUnitController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.ActivityCostUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.server.persistence.api.ActivityCostUnitPersistence;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.ReadListener;
import org.cyk.utility.__kernel__.persistence.query.QueryHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntity;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntityBuilder;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter
public abstract class AbstractActivityListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	protected LazyDataModel<Activity> activities;
	
	protected AutoCompleteEntity<Program> programFilter;
	protected AutoCompleteEntity<AdministrativeUnit> administrativeUnitFilter;
	protected AutoCompleteEntity<AdministrativeUnit> administrativeUnitBeneficiaireFilter;
	
	protected List<Activity> selectedActivities,__selectedActivities__;
	protected AutoCompleteEntity<AdministrativeUnit> administrativeUnit;
	protected AutoCompleteEntity<AdministrativeUnit> administrativeUnitBeneficiaire;
	
	protected Collection<ActivityCostUnit> activityCostUnits;
	
	protected Boolean overrideAdministrativeUnit,overrideAdministrativeUnitBeneficiaire;;
	protected Commandable saveCommandable;
	protected Map<String,Activity> activitiesMap = new HashMap<>();
	
	protected Boolean groupModification;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		/* Filters */
		programFilter = AutoCompleteEntityBuilder.build(Program.class,"activitiesDataTable");
		programFilter.setIdentifier("programFilter");
		
		administrativeUnitFilter = AutoCompleteEntityBuilder.build(AdministrativeUnit.class,"activitiesDataTable");
		administrativeUnitFilter.setIdentifier("ua01Filter");
		administrativeUnitFilter.setListener(new AdministrativeUnitAutoCompleteEntityListener(defaultSection, null));
		administrativeUnitFilter.setReadItemLabelListener(new ReadListenerImpl(defaultSection,null));
		//administrativeUnitFilter.setRendered(groupModification);
		
		administrativeUnitBeneficiaireFilter = AutoCompleteEntityBuilder.build(AdministrativeUnit.class,"activitiesDataTable");
		administrativeUnitBeneficiaireFilter.setIdentifier("ua02Filter");
		administrativeUnitBeneficiaireFilter.setListener(new AdministrativeUnitAutoCompleteEntityListener(defaultSection, Boolean.TRUE));
		administrativeUnitBeneficiaireFilter.setReadItemLabelListener(new ReadListenerImpl(defaultSection,Boolean.TRUE));
		//administrativeUnitBeneficiaireFilter.setRendered(groupModification);
		
		/**/
		administrativeUnit = AutoCompleteEntityBuilder.build(AdministrativeUnit.class);
		administrativeUnit.setIdentifier("ua01");
		administrativeUnit.setListener(new AdministrativeUnitAutoCompleteEntityListener(defaultSection, null));
		administrativeUnit.setReadItemLabelListener(new ReadListenerImpl(defaultSection,null));
		
		administrativeUnitBeneficiaire = AutoCompleteEntityBuilder.build(AdministrativeUnit.class);
		administrativeUnitBeneficiaire.setIdentifier("ua02");
		administrativeUnitBeneficiaire.setListener(new AdministrativeUnitAutoCompleteEntityListener(defaultSection, null));
		administrativeUnitBeneficiaire.setReadItemLabelListener(new ReadListenerImpl(defaultSection,Boolean.TRUE));
		
		activities = new LazyDataModel<Activity>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				FilterDto filter = new FilterDto();
				filter.addField("activity", MapHelper.readByKey(filters, "activity"));				
				filter.addField(Activity.FIELD_ADMINISTRATIVE_UNIT, MapHelper.readByKey(filters, Activity.FIELD_ADMINISTRATIVE_UNIT));
				filter.addField(Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE, MapHelper.readByKey(filters, Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE));
				filter.addField(Activity.FIELD_ACTION, MapHelper.readByKey(filters, Activity.FIELD_ACTION));
				filter.addField(Activity.FIELD_PROGRAM, MapHelper.readByKey(filters, "action.program"));
				filter.addField("catAtvCode", MapHelper.readByKey(filters, "catAtvCode"));
				String sectionCode = (String) MapHelper.readByKey(filters, "action.program.section");
				if(StringHelper.isBlank(sectionCode))
					sectionCode = defaultSection == null ? null : defaultSection.getCode();		
				filter.addField(Activity.FIELD_SECTION, sectionCode);
				
				List<Activity> list = (List<Activity>) __inject__(ActivityController.class).read(__getProperties__(filter, first, pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(ActivityController.class).count(new Properties().setQueryIdentifier(ActivityPersistence.COUNT_BY_FILTERS_LIKE).setFilters(filter));
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
		return new Properties().setQueryIdentifier(ActivityPersistence.READ_BY_FILTERS_LIKE)
		.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME+","+Activity.FIELD_ACTION
				+","+Activity.FIELD_ADMINISTRATIVE_UNIT+","+Activity.FIELD_CAT_ATV_CODE+","+Activity.FIELD_NUMBER_OF_COST_UNITS)
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
		groupModification = ValueHelper.convertToBoolean(Faces.getRequestParameter("group"));
	}
	
	public void save() {
		if(CollectionHelper.isEmpty(__selectedActivities__))
			return;
		if(Boolean.TRUE.equals(groupModification)) {
			__selectedActivities__.forEach(new Consumer<Activity>() {
				@Override
				public void accept(Activity activity) {
					if(administrativeUnit.getValue() != null/* || Boolean.TRUE.equals(overrideAdministrativeUnit)*/)
						activity.setAdministrativeUnit((AdministrativeUnit) administrativeUnit.getValue());
					if(administrativeUnitBeneficiaire.getValue() != null /*Boolean.TRUE.equals(overrideAdministrativeUnitBeneficiaire)*/)
						activity.setAdministrativeUnitBeneficiaire((AdministrativeUnit) administrativeUnitBeneficiaire.getValue());
				}
			});	
		}else {
			__selectedActivities__.forEach(new Consumer<Activity>() {
				@Override
				public void accept(Activity activity) {
					//System.out.println(activity.getAdministrativeUnit()+" : "+activity.getAdministrativeUnitBeneficiaire());
				}
			});	
		}
		__inject__(ActivityController.class).updateMany(__selectedActivities__,new Properties().setFields(Activity.FIELD_ADMINISTRATIVE_UNIT+","+Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE));
		selectedActivities.clear();
		selectedActivities = null;
		__selectedActivities__.clear();
		__selectedActivities__ = null;
	}
	
	public void openDialog(Activity activity) {
		if(activity == null || StringHelper.isBlank(activity.getCode()))
			return;
		activityCostUnits = __inject__(ActivityCostUnitController.class).read(new Properties()
				.setQueryIdentifier(ActivityCostUnitPersistence.READ_BY_ACTIVITIES_CODES)
				.setFilters(new FilterDto().addField(ActivityCostUnit.FIELD_ACTIVITY, List.of(activity.getCode()))						
				)
			);
		Ajax.oncomplete("PF('dialog').show();");
	}
	
	/**/
	
	@Getter @Setter @Accessors(chain=true) @AllArgsConstructor
	public static class AdministrativeUnitAutoCompleteEntityListener implements AutoCompleteEntity.Listener<AdministrativeUnit> {
		
		private Section section;
		private Boolean beneficiaire;
		
		@Override
		public Collection<AdministrativeUnit> listenComplete(AutoCompleteEntity<AdministrativeUnit> autoCompleteEntity,String queryString) {
			if(Boolean.TRUE.equals(beneficiaire)) {
				if(section == null)
					return __inject__(AdministrativeUnitController.class).readByString(queryString);
				return __inject__(AdministrativeUnitController.class).read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_WHERE_CODE_OR_NAME_CONTAINS_AND_SECTION_CODE_LIKES)
						.setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, section.getCode())
								.addField(AdministrativeUnit.FIELD_CODE, queryString).addField(AdministrativeUnit.FIELD_NAME, queryString))
						.setFields("identifier,code,name")
						);
			}else
				return __inject__(AdministrativeUnitController.class).read(new Properties().setQueryIdentifier(
					QueryHelper.getIdentifierReadWhereBusinessIdentifierOrNameContains(AdministrativeUnit.class))
					.setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_CODE, queryString).addField(AdministrativeUnit.FIELD_NAME, queryString))
					.setFields("identifier,code,name,section")
					);
		}		
	}
	
	@Getter @Setter @Accessors(chain=true) @AllArgsConstructor
	public static class ReadListenerImpl implements ReadListener,Serializable {
		private static final long serialVersionUID = 1L;
		
		private Section section;
		private Boolean beneficiaire;
		
		@Override
		public Object read(Object object) {
			if(object == null)
				return null;
			AdministrativeUnit administrativeUnit = (AdministrativeUnit) object;
			if(section != null && Boolean.TRUE.equals(beneficiaire))
				return administrativeUnit;
			String sectionCode = administrativeUnit.getSection() == null ? "???" : administrativeUnit.getSection().getCode();
			return administrativeUnit.getCode()+"-"+sectionCode+" "+administrativeUnit.getName();
		}	
	}
}
