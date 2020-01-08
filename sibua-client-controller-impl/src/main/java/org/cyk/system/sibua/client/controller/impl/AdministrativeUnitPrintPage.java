package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.properties.Properties;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitPrintPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private LazyDataModel<AdministrativeUnit> administrativeUnits;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		
		administrativeUnits = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class)
						.read(new Properties()
								.setFields(AdministrativeUnit.FIELD_NAME+","+AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION+","+AdministrativeUnit.FIELD_SERVICE_GROUP
										+","+AdministrativeUnit.FIELD_LOCALISATION)
								.setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				else {
					Long count = __inject__(AdministrativeUnitController.class).count();
					setRowCount(count == null ? 0 : count.intValue());	
				}
				return list;
			}	
		};
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Impression des unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
