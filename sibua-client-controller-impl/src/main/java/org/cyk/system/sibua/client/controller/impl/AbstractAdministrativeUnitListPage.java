package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
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
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractAdministrativeUnitListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	protected SelectionOne<Section> section;
	protected Collection<Section> sections;
	protected Collection<FunctionalClassification> functionalClassifications;
	protected Collection<ServiceGroup> serviceGroups;
	protected Collection<Localisation> localisations;
	protected LazyDataModel<AdministrativeUnit> administrativeUnits;
	protected Boolean isShowAll,export;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
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
				filter.addField(AdministrativeUnit.FIELD_CODE, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_CODE));
				filter.addField(AdministrativeUnit.FIELD_NAME, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_NAME));
				String sectionCode = (String) MapHelper.readByKey(filters, AdministrativeUnit.FIELD_SECTION);
				if(StringHelper.isBlank(sectionCode))
					sectionCode = defaultSection == null ? section.getValue() == null ? null : section.getValue().getCode() : defaultSection.getCode();
				filter.addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(Boolean.TRUE,sectionCode));				
				filter.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, CollectionHelper.listOf(Boolean.TRUE,MapHelper.readByKey(filters, AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION)));
				filter.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, CollectionHelper.listOf(Boolean.TRUE,MapHelper.readByKey(filters, AdministrativeUnit.FIELD_SERVICE_GROUP)));
				filter.addField(AdministrativeUnit.FIELD_LOCALISATION, CollectionHelper.listOf(Boolean.TRUE,MapHelper.readByKey(filters, AdministrativeUnit.FIELD_LOCALISATION)));
				
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class).read(__getProperties__(filter, first, pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(AdministrativeUnitController.class).count(new Properties()
							.setQueryIdentifier(AdministrativeUnitPersistence.COUNT_BY_FILTERS).setFilters(filter));
					setRowCount(count == null ? 0 : count.intValue());	
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
	}
	
	protected Properties __getProperties__(Object filter,Object first,Object pageSize) {
		return new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_BY_FILTERS)
		.setFields(AdministrativeUnit.FIELD_IDENTIFIER+","+AdministrativeUnit.FIELD_CODE+","+AdministrativeUnit.FIELD_NAME
				+","+AdministrativeUnit.FIELD_SECTION+","+AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION
				+","+AdministrativeUnit.FIELD_SERVICE_GROUP+","+AdministrativeUnit.FIELD_LOCALISATION
				+","+AdministrativeUnit.FIELD_ACTIVITY_DESTINATIONS+","+AdministrativeUnit.FIELD_ACTIVITIES+","+AdministrativeUnit.FIELD_DESTINATIONS
				+","+AdministrativeUnit.FIELD_CHILDREN
				)
		.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
