package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.FunctionalClassificationController;
import org.cyk.system.sibua.client.controller.api.LocalisationController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.api.ServiceGroupController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntity;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntityBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractAdministrativeUnitListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AutoCompleteEntity<Section> sectionAutoComplete;
	private AutoCompleteEntity<ServiceGroup> serviceGroupAutoComplete;
	private AutoCompleteEntity<FunctionalClassification> functionalClassificationAutoComplete;
	private AutoCompleteEntity<Localisation> localisationAutoComplete;
	
	protected SelectionOne<Section> section;
	protected Collection<Section> sections;
	protected Collection<FunctionalClassification> functionalClassifications;
	protected Collection<ServiceGroup> serviceGroups;
	protected Collection<Localisation> localisations;
	
	protected LazyDataModel<AdministrativeUnit> administrativeUnits;
	protected List<AdministrativeUnit> selectedAdministrativeUnits,__selectedAdministrativeUnits__;
	protected Map<String,AdministrativeUnit> administrativeUnitsMap = new HashMap<>();
	protected Boolean isShowAll,export;
	protected Commandable dialogActionCommandable;
	
	protected String dialogAction;
	protected String dialogTitle;
	protected String dialogMessage;
	protected AdministrativeUnit administrativeUnit;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		sectionAutoComplete = AutoCompleteEntityBuilder.build(Section.class, "administrativeUnitsDataTable");		
		serviceGroupAutoComplete = AutoCompleteEntityBuilder.build(ServiceGroup.class, "administrativeUnitsDataTable");
		functionalClassificationAutoComplete = AutoCompleteEntityBuilder.build(FunctionalClassification.class, "administrativeUnitsDataTable");
		localisationAutoComplete = AutoCompleteEntityBuilder.build(Localisation.class, "administrativeUnitsDataTable");
		
		export = ValueHelper.convertToBoolean(Faces.getRequestParameter("export"));
		section = new SelectionOne<Section>(Section.class);		
		section.setAreChoicesGettable(defaultSection == null);
		section.setListener(new SelectionOne.Listener<Section>() {
			@Override
			public void processOnSelect(Section section) {
				if(section == null) {
					
				}else {					
				
				}				
			}
		});
		section.select(defaultSection);
		section.setMessage("-- Toutes les sections --");
		
		try {
			sections = SectionController.readStatic();
			functionalClassifications = FunctionalClassificationController.readStatic();
			serviceGroups = ServiceGroupController.readStatic();
			localisations = LocalisationController.readStatic();
		}catch(Exception exception) {
			exception.printStackTrace();
		}

		administrativeUnits = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				FilterDto filter = new FilterDto();
				filter.addField("administrativeUnit", MapHelper.readByKey(filters, "administrativeUnit"));
				String sectionCode = (String) MapHelper.readByKey(filters, AdministrativeUnit.FIELD_SECTION);
				if(StringHelper.isBlank(sectionCode))
					sectionCode = defaultSection == null ? section.getValue() == null ? null : section.getValue().getCode() : defaultSection.getCode();
				filter.addField(AdministrativeUnit.FIELD_SECTION, sectionCode);				
				filter.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION));
				filter.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_SERVICE_GROUP));
				filter.addField(AdministrativeUnit.FIELD_LOCALISATION, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_LOCALISATION));
				
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class).read(__getProperties__(filter, first, pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(AdministrativeUnitController.class).count(new Properties()
							.setQueryIdentifier(AdministrativeUnitPersistence.COUNT_BY_FILTERS_LIKE).setFilters(filter));
					setRowCount(count == null ? 0 : count.intValue());	
				}
				return list;
			}
			
			@Override
			public Object getRowKey(AdministrativeUnit administrativeUnit) {
				if(administrativeUnit == null)
					return null;
				administrativeUnitsMap.put(administrativeUnit.getIdentifier(), administrativeUnit);
				return administrativeUnit.getIdentifier();
			}
			
			@Override
			public AdministrativeUnit getRowData(String identifier) {
				return administrativeUnitsMap.get(identifier);
			}
		};
		
		CommandableBuilder dialogActionCommandableBuilder = __inject__(CommandableBuilder.class);
		dialogActionCommandableBuilder.setName("Oui").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					act();
				}
			}
		);
		dialogActionCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers());
		dialogActionCommandable = dialogActionCommandableBuilder.execute().getOutput();
	}
	
	protected Properties __getProperties__(Object filter,Object first,Object pageSize) {
		return new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_BY_FILTERS_LIKE)
		.setFields(AdministrativeUnit.FIELD_IDENTIFIER+","+AdministrativeUnit.FIELD_CODE+","+AdministrativeUnit.FIELD_NAME
				+","+AdministrativeUnit.FIELD_SECTION+","+AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION
				+","+AdministrativeUnit.FIELD_SERVICE_GROUP+","+AdministrativeUnit.FIELD_LOCALISATION
				/*+","+AdministrativeUnit.FIELD_ACTIVITY_DESTINATIONS+","+AdministrativeUnit.FIELD_ACTIVITIES+","+AdministrativeUnit.FIELD_DESTINATIONS
				+","+AdministrativeUnit.FIELD_CHILDREN
				*/
				)
		.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
	public void openDialog(String type) {
		if(CollectionHelper.isEmpty(selectedAdministrativeUnits))
			return;
		dialogAction = type;
		if("delete".equals(type)) {
			dialogTitle = "Suppression d'unité administrative";
			dialogMessage = "Voulez vous supprimer les unités administratives suivantes ?";
		}else if("merge".equals(type)) {
			dialogTitle = "Fusion d'unité administrative";
			dialogMessage = "Voulez vous fusionner les unités administratives suivantes ?";
		}
		if(__selectedAdministrativeUnits__ == null)
			__selectedAdministrativeUnits__ = new ArrayList<>();
		if(__selectedAdministrativeUnits__ != null)
			__selectedAdministrativeUnits__.clear();
		__selectedAdministrativeUnits__ = new ArrayList<AdministrativeUnit>();		
		__selectedAdministrativeUnits__.addAll(selectedAdministrativeUnits);
		Ajax.oncomplete("PF('dialog').show();");
	}
	
	public void act() {
		if(CollectionHelper.isEmpty(__selectedAdministrativeUnits__))
			return;
		if("delete".equals(dialogAction)) {
			__inject__(AdministrativeUnitController.class).deleteMany(__selectedAdministrativeUnits__);
		}else if("merge".equals(dialogAction)) {
			if(administrativeUnit == null)
				return;
			__inject__(AdministrativeUnitController.class).mergeByCodes(__selectedAdministrativeUnits__, administrativeUnit);
		}
		dialogAction = null;		
		selectedAdministrativeUnits.clear();
		selectedAdministrativeUnits = null;
		__selectedAdministrativeUnits__.clear();
		__selectedAdministrativeUnits__ = null;
	}
}
