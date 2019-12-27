
package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.FunctionalClassificationController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.api.ServiceGroupController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitCodifyPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private Section section;
	private List<AdministrativeUnit> __selectedAdministrativeUnits__,selectedAdministrativeUnits;
	private LazyDataModel<AdministrativeUnit> availableAdministrativeUnits;
	private List<Section> sections;
	private List<ServiceGroup> serviceGroups;
	private List<FunctionalClassification> functionalClassifications;
	
	private Commandable updateCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {
			sections = (List<Section>) __inject__(SectionController.class).read(new Properties().setIsPageable(Boolean.FALSE));
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				section = __inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section"));
			}
			
			serviceGroups = (List<ServiceGroup>) __inject__(ServiceGroupController.class).read(new Properties().setIsPageable(Boolean.FALSE));
			for(Integer index = 0; index < serviceGroups.size();index = index + 1) {
				if(serviceGroups.get(index).getCode().equals(org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET)) {
					ServiceGroup serviceGroup = serviceGroups.remove(index.intValue());
					serviceGroups.add(0, serviceGroup);
					break;
				}
			}
			
			functionalClassifications = (List<FunctionalClassification>) __inject__(FunctionalClassificationController.class).read(new Properties().setIsPageable(Boolean.FALSE));
			for(Integer index = 0; index < functionalClassifications.size();index = index + 1) {
				if(functionalClassifications.get(index).getCode().equals(org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET)) {
					FunctionalClassification functionalClassification = functionalClassifications.remove(index.intValue());
					functionalClassifications.add(0, functionalClassification);
					break;
				}
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		availableAdministrativeUnits = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				FilterDto filterRead = null;
				FilterDto filterCount = null;
				if(MapHelper.isNotEmpty(filters)) {
					filterRead = new FilterDto();
					filterRead.addField(AdministrativeUnit.FIELD_NAME, filters.get(AdministrativeUnit.FIELD_NAME));
					filterRead.addField(AdministrativeUnit.FIELD_SECTION, List.of(filters.get(AdministrativeUnit.FIELD_SECTION)));
					filterRead.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, List.of(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP)));
					filterRead.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, List.of(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION)));
					
					filterCount = new FilterDto();
					filterCount.addField(AdministrativeUnit.FIELD_NAME, filters.get(AdministrativeUnit.FIELD_NAME));
					filterCount.addField(AdministrativeUnit.FIELD_SECTION, List.of(filters.get(AdministrativeUnit.FIELD_SECTION)));
					filterCount.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, List.of(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP)));
					filterCount.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, List.of(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION)));
				}
				
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class)
						.read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_BY_FILTERS).setFilters(filterRead).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Integer count = __inject__(AdministrativeUnitController.class)
							.count(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.COUNT_BY_FILTERS).setFilters(filterCount)).intValue();
					setRowCount(count);	
				}
				return list;
			}
			
			@Override
			public Object getRowKey(AdministrativeUnit administrativeUnit) {
				if(administrativeUnit == null)
					return null;
				return administrativeUnit.getIdentifier();
			}
			
			@Override
			public AdministrativeUnit getRowData(String identifier) {
				return __inject__(AdministrativeUnitController.class).readBySystemIdentifier(identifier);
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
		
	}
	
	public void select() {
		if(CollectionHelper.isEmpty(__selectedAdministrativeUnits__))
			return;
		__select__(__selectedAdministrativeUnits__);
		__selectedAdministrativeUnits__.clear();
	}
	
	public void selectOne(AdministrativeUnit destination) {
		if(destination == null)
			return;
		__select__(destination);
	}
	
	public void __select__(Collection<AdministrativeUnit> administrativeUnits) {
		if(CollectionHelper.isEmpty(administrativeUnits))
			return;
		administrativeUnits.forEach(new Consumer<AdministrativeUnit>() {
			@Override
			public void accept(AdministrativeUnit administrativeUnit) {
				administrativeUnit.set__name__(administrativeUnit.getName());
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
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Codification d'unités administratives";
	}
	
}
