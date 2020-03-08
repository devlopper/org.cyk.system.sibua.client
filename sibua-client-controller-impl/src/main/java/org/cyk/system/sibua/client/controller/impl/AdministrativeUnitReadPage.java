package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitActivity;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitActivityPersistence;
import org.cyk.utility.__kernel__.computation.ComparisonOperator;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.LazyDataModel;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitReadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AdministrativeUnit administrativeUnit;
	private DataTable administrativeUnitActivitiesDataTable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
		administrativeUnitActivitiesDataTable = DataTable.build(DataTable.FIELD_ELEMENT_CLASS,AdministrativeUnitActivity.class,DataTable.FIELD_LAZY,Boolean.TRUE);
		
		administrativeUnitActivitiesDataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ACTIVITY)
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE,Column.FIELD_HEADER_TEXT,"Bénéficiaire")
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT,Column.FIELD_HEADER_TEXT,"Gestionnaire"));
		
		administrativeUnitActivitiesDataTable.addRecordMenuItemByArgumentsExecuteFunctionDelete();
		
		((LazyDataModel<?>)administrativeUnitActivitiesDataTable.getValue()).setReadQueryIdentifier(AdministrativeUnitActivityPersistence.READ_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES);
		((LazyDataModel<?>)administrativeUnitActivitiesDataTable.getValue()).setCountQueryIdentifier(AdministrativeUnitActivityPersistence.COUNT_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES);
		((LazyDataModel<?>)administrativeUnitActivitiesDataTable.getValue()).setListener(new LazyDataModel.Listener.AbstractImpl() {
			@Override
			public void processFilter(FilterDto filter) {
				filter.addField(AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT, List.of(administrativeUnit.getCode()));
			}
		});
		
		administrativeUnitActivitiesDataTable.setListener(new DataTable.Listener.AbstractImpl() {			
			@Override
			public String listenGetStyleClassByRecordByColumn(Object record,Integer recordIndex,Column column,Integer columnIndex) {
				if(record == null)
					return null;
				AdministrativeUnitActivity administrativeUnitActivity = (AdministrativeUnitActivity) record;
				if(
					(NumberHelper.compare(columnIndex, 1, ComparisonOperator.EQ) && administrativeUnitActivity.getAdministrativeUnitBeneficiaire().equals(administrativeUnit))
					|| (NumberHelper.compare(columnIndex, 2, ComparisonOperator.EQ) && administrativeUnitActivity.getAdministrativeUnit().equals(administrativeUnit))
					)
					return "cyk-background-highlight";
				return null;
			}
		});
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return administrativeUnit.toString();	
	}
	
}
