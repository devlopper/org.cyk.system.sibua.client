package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private LazyDataModel<AdministrativeUnit> administrativeUnits;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
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
		
		try {
			
		}catch(Exception exception) {
			exception.printStackTrace();
		}

		administrativeUnits = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				FilterDto filter = null;				
				if(filters == null)
					filters = new HashMap<>();
				if(filters.isEmpty()) {
					filters.put(AdministrativeUnit.FIELD_SERVICE_GROUP, org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET);
					filters.put(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET);
				}
					
				filter = new FilterDto();
				filter.addField(AdministrativeUnit.FIELD_NAME, filters.get(AdministrativeUnit.FIELD_NAME));
				String sectionCode = defaultSection == null ? section.getValue() == null ? null : section.getValue().getCode() : defaultSection.getCode();
				System.out.println(sectionCode);
				if(StringHelper.isNotBlank(sectionCode))
					filter.addField(AdministrativeUnit.FIELD_SECTION, sectionCode);
				/*if(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP) != null)
					filter.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, List.of(filters.get(AdministrativeUnit.FIELD_SERVICE_GROUP)));
				if(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION) != null)
					filter.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, List.of(filters.get(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION)));
				*/
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class)
						.read(new Properties().setQueryIdentifier(StringHelper.isBlank(sectionCode) ? null : AdministrativeUnitPersistence.READ_BY_SECTIONS_CODES)
								.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(AdministrativeUnitController.class).count(new Properties()
							.setQueryIdentifier(StringHelper.isBlank(sectionCode) ? null : AdministrativeUnitPersistence.COUNT_BY_SECTIONS_CODES).setFilters(filter));
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
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des unités administratives"+(defaultSection == null ? " par section" : " de la section "+defaultSection);
	}
	
}
