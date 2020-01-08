package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractActivityListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	protected LazyDataModel<Activity> activities;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		activities = new LazyDataModel<Activity>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Activity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
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
				
				List<Activity> list = (List<Activity>) __inject__(ActivityController.class).read(__getProperties__(filter, first, pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(ActivityController.class).count(new Properties()
							.setQueryIdentifier(ActivityPersistence.COUNT_BY_FILTERS).setFilters(filter));
					setRowCount(count == null ? 0 : count.intValue());	
				}
				return list;
			}
			
			@Override
			public Object getRowKey(Activity administrativeUnit) {
				if(administrativeUnit == null)
					return null;
				return administrativeUnit.getIdentifier();
			}
			
			@Override
			public Activity getRowData(String identifier) {
				return __inject__(ActivityController.class).readBySystemIdentifier(identifier);
			}
		};		
	}
	
	protected Properties __getProperties__(Object filter,Object first,Object pageSize) {
		return new Properties().setQueryIdentifier(ActivityPersistence.READ_BY_FILTERS)
		.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME+","+Activity.FIELD_ACTION+","+Activity.FIELD_ADMINISTRATIVE_UNIT)
		.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
