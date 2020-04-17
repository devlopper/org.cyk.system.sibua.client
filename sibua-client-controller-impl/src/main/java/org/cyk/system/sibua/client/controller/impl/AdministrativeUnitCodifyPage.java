
package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitCodifyPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private Section section;
	private ServiceGroup serviceGroup;
	private Boolean overrideServiceGroup;
	private FunctionalClassification functionalClassification;
	private Boolean overrideFunctionalClassification;
	private Localisation localisation;
	private Boolean overrideLocalisation;
	
	private List<AdministrativeUnit> __selectedAdministrativeUnits__,selectedAdministrativeUnits;
	private LazyDataModel<AdministrativeUnit> availableAdministrativeUnits;
	
	private Commandable updateCommandable;
	
	private final Map<String,AdministrativeUnit> administrativeUnits = new HashMap<>();
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		section = defaultSection;	
		availableAdministrativeUnits = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				Filter.Dto filter = null;				
				if(filters == null)
					filters = new HashMap<>();
				/*if(filters.isEmpty()) {
					filters.put(AdministrativeUnit.FIELD_SERVICE_GROUP, org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET);
					filters.put(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET);
				}
				*/	
				filter = new Filter.Dto();
				/*
				filter.addField(AdministrativeUnit.FIELD_NAME, filters.get(AdministrativeUnit.FIELD_NAME));
				String sectionCode = section == null ? (String) filters.get(AdministrativeUnit.FIELD_SECTION) : section.getCode();
				if(StringHelper.isNotBlank(sectionCode))
					filter.addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(Boolean.TRUE, sectionCode));
				if(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP) != null)
					filter.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, CollectionHelper.listOf(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP)));
				if(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION) != null)
					filter.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, CollectionHelper.listOf(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION)));
				if(filters.get(AdministrativeUnit.FIELD_LOCALISATION) != null)
					filter.addField(AdministrativeUnit.FIELD_LOCALISATION, CollectionHelper.listOf(filters.get(AdministrativeUnit.FIELD_LOCALISATION)));
				if(CollectionHelper.isNotEmpty(selectedAdministrativeUnits))
					filter.addField(AdministrativeUnit.FIELD_CODE, selectedAdministrativeUnits.stream().map(AdministrativeUnit::getCode).collect(Collectors.toSet()));
				*/
				
				filter.addField(AdministrativeUnit.FIELD_NAME, filters.get(AdministrativeUnit.FIELD_NAME));
				String sectionCode = section == null ? (String) filters.get(AdministrativeUnit.FIELD_SECTION) : section.getCode();
				if(StringHelper.isNotBlank(sectionCode))
					filter.addField(AdministrativeUnit.FIELD_SECTION, sectionCode);
				if(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP) != null)
					filter.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP));
				if(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION) != null)
					filter.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION));
				if(filters.get(AdministrativeUnit.FIELD_LOCALISATION) != null)
					filter.addField(AdministrativeUnit.FIELD_LOCALISATION, filters.get(AdministrativeUnit.FIELD_LOCALISATION));
				if(CollectionHelper.isNotEmpty(selectedAdministrativeUnits))
					filter.addField(AdministrativeUnit.FIELD_CODE, selectedAdministrativeUnits.stream().map(AdministrativeUnit::getCode).collect(Collectors.toSet()));
				
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class)
						.read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_WHERE_CODE_NOT_IN_BY_FILTERS_CODES_LIKE).setFilters(filter)
								.setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
				
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(AdministrativeUnitController.class)
							.count(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.COUNT_WHERE_CODE_NOT_IN_BY_FILTERS_CODES_LIKE).setFilters(filter));
					setRowCount(count == null ? 0 : count.intValue());	
				}
				return list;
			}
			
			@Override
			public Object getRowKey(AdministrativeUnit administrativeUnit) {
				if(administrativeUnit == null)
					return null;
				administrativeUnits.put(administrativeUnit.getIdentifier(), administrativeUnit);
				return administrativeUnit.getIdentifier();
			}
			
			@Override
			public AdministrativeUnit getRowData(String identifier) {
				return administrativeUnits.get(identifier); //__inject__(AdministrativeUnitController.class).readBySystemIdentifier(identifier);
			}
		};
		
		CommandableBuilder updateCommandableBuilder = __inject__(CommandableBuilder.class);
		updateCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		updateCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),":form:selectedAdministrativeUnits");
		updateCommandable = updateCommandableBuilder.execute().getOutput();
	}
	
	public void openAvailableAdministrativeUnitsDialog() {
		serviceGroup = null;
		localisation = null;
		functionalClassification = null;
	}
	
	public void __select__(Collection<AdministrativeUnit> administrativeUnits) {
		if(CollectionHelper.isEmpty(administrativeUnits))
			return;
		administrativeUnits.forEach(new Consumer<AdministrativeUnit>() {
			@Override
			public void accept(AdministrativeUnit administrativeUnit) {
				administrativeUnit.set__name__(administrativeUnit.getName());
				if(Boolean.TRUE.equals(overrideFunctionalClassification) || administrativeUnit.getFunctionalClassification().getCode().equals(org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET) && functionalClassification != null)
					administrativeUnit.setFunctionalClassification(functionalClassification);
				if(Boolean.TRUE.equals(overrideServiceGroup) || administrativeUnit.getServiceGroup().getCode().equals(org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET) && serviceGroup != null)
					administrativeUnit.setServiceGroup(serviceGroup);
				if(Boolean.TRUE.equals(overrideLocalisation) || administrativeUnit.getLocalisation().getCode().equals(org.cyk.system.sibua.server.persistence.entities.Localisation.CODE_NOT_SET) && localisation != null)
					administrativeUnit.setLocalisation(localisation);
			}
		});
		if(selectedAdministrativeUnits == null)
			selectedAdministrativeUnits = new ArrayList<>();
		selectedAdministrativeUnits.addAll(administrativeUnits);
	}
	
	public void __select__(AdministrativeUnit...administrativeUnits) {
		if(ArrayHelper.isEmpty(administrativeUnits))
			return;
		__select__(CollectionHelper.listOf(administrativeUnits));
	}
	
	public void select() {
		if(CollectionHelper.isEmpty(__selectedAdministrativeUnits__))
			return;
		__select__(__selectedAdministrativeUnits__);
		__selectedAdministrativeUnits__.clear();
	}
	
	public void selectOne(AdministrativeUnit administrativeUnit) {
		if(administrativeUnit == null)
			return;
		__select__(administrativeUnit);
	}
	
	public void __unselect__(Collection<AdministrativeUnit> administrativeUnits) {
		if(CollectionHelper.isEmpty(administrativeUnits) || CollectionHelper.isEmpty(selectedAdministrativeUnits))
			return;
		selectedAdministrativeUnits.removeAll(administrativeUnits);
	}
	
	public void __unselect__(AdministrativeUnit...administrativeUnits) {
		if(ArrayHelper.isEmpty(administrativeUnits))
			return;
		__unselect__(CollectionHelper.listOf(administrativeUnits));
	}
	
	public void unselect() {
		if(CollectionHelper.isEmpty(selectedAdministrativeUnits))
			return;
		__unselect__(selectedAdministrativeUnits);
	}
	
	public void unselectOne(AdministrativeUnit administrativeUnit) {
		if(administrativeUnit == null)
			return;
		__unselect__(administrativeUnit);
	}
	
	public void save() {
		if(CollectionHelper.isEmpty(selectedAdministrativeUnits))
			return;
		selectedAdministrativeUnits.forEach(new Consumer<AdministrativeUnit>() {
			@Override
			public void accept(AdministrativeUnit administrativeUnit) {
				administrativeUnit.setName(administrativeUnit.get__name__());
			}
		});
		__inject__(AdministrativeUnitController.class).updateMany(selectedAdministrativeUnits
				,new Properties().setFields(
						AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION
						+","+AdministrativeUnit.FIELD_LOCALISATION
						+","+AdministrativeUnit.FIELD_SERVICE_GROUP
						+","+AdministrativeUnit.FIELD_NAME));
		selectedAdministrativeUnits.clear();
		selectedAdministrativeUnits = null;
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Codification " + (section == null ? "d'unités administratives" : "des unités administratives de la section "+section);
	}
	
}
