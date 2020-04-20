package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitActivity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitFunctionType;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitActivityPersistence;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitFunctionTypePersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.computation.ComparisonOperator;
import org.cyk.utility.__kernel__.identifier.resource.ParameterName;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
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
	private DataTable administrativeUnitFunctionTypesDataTable;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		if(StringHelper.isNotBlank(Faces.getRequestParameter("entityidentifier")))
			administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
		
		if(administrativeUnit == null)
			administrativeUnit =CollectionHelper.getFirst((Collection<AdministrativeUnit>) SessionHelper.getAttributeValue(Faces.getRequestParameter(ParameterName.SESSION_IDENTIFIER.getValue())));
		
		createAdministrativeUnitActivitiesDataTable();
		createAdministrativeUnitFunctionTypesDataTable();
	}
	
	@SuppressWarnings("unchecked")
	private void createAdministrativeUnitActivitiesDataTable() {
		administrativeUnitActivitiesDataTable = DataTable.build(DataTable.FIELD_ELEMENT_CLASS,AdministrativeUnitActivity.class,DataTable.FIELD_LAZY,Boolean.TRUE
				,DataTable.FIELD___PARENT_ELEMENT__,administrativeUnit);
		
		administrativeUnitActivitiesDataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ACTIVITY)
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE,Column.FIELD_HEADER_TEXT,"Bénéficiaire")
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT,Column.FIELD_HEADER_TEXT,"Gestionnaire"));
		
		administrativeUnitActivitiesDataTable.addRecordMenuItemByArgumentsExecuteFunctionDelete();
		
		LazyDataModel<AdministrativeUnitActivity> lazyDataModel = (LazyDataModel<AdministrativeUnitActivity>) administrativeUnitActivitiesDataTable.getValue();
		lazyDataModel.setReaderUsable(Boolean.TRUE);
		lazyDataModel.setReadQueryIdentifier(AdministrativeUnitActivityPersistence.READ_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES);
		lazyDataModel.setCountQueryIdentifier(AdministrativeUnitActivityPersistence.COUNT_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES);
		lazyDataModel.setListener(new LazyDataModel.Listener.AbstractImpl<AdministrativeUnitActivity>() {
			@Override
			public Filter.Dto instantiateFilter(LazyDataModel<AdministrativeUnitActivity> lazyDataModel) {
				Filter.Dto filter = super.instantiateFilter(lazyDataModel);
				if(filter == null)
					filter = new Filter.Dto();
				//return filter.addField(AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT, List.of(administrativeUnit.getCode()));
				return filter.addField("administrativeUnitsCodes", CollectionHelper.listOf(administrativeUnit.getCode()));
			}
		});
		
		administrativeUnitActivitiesDataTable.setListener(new DataTable.Listener.AbstractImpl() {			
			@Override
			public String getStyleClassByRecordByColumn(Object record,Integer recordIndex,Column column,Integer columnIndex) {
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
	
	@SuppressWarnings("unchecked")
	private void createAdministrativeUnitFunctionTypesDataTable() {
		administrativeUnitFunctionTypesDataTable = DataTable.build(DataTable.FIELD_ELEMENT_CLASS,AdministrativeUnitFunctionType.class,DataTable.FIELD_LAZY,Boolean.TRUE
				,DataTable.FIELD___PARENT_ELEMENT__,administrativeUnit);		
		administrativeUnitFunctionTypesDataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnitFunctionType.FIELD_FUNCTION_TYPE));
		administrativeUnitFunctionTypesDataTable.addHeaderToolbarLeftCommandsByArgumentsOpenViewInDialogCreate();
		administrativeUnitFunctionTypesDataTable.addRecordMenuItemByArgumentsExecuteFunctionDelete();
		
		LazyDataModel<AdministrativeUnit> lazyDataModel = (LazyDataModel<AdministrativeUnit>) administrativeUnitFunctionTypesDataTable.getValue();
		lazyDataModel.setReaderUsable(Boolean.TRUE);
		lazyDataModel.setReadQueryIdentifier(AdministrativeUnitFunctionTypePersistence.READ_BY_ADMINISTRATIVE_UNITS_CODES);
		lazyDataModel.setCountQueryIdentifier(AdministrativeUnitFunctionTypePersistence.COUNT_BY_ADMINISTRATIVE_UNITS_CODES);
		lazyDataModel.setListener(new LazyDataModel.Listener.AbstractImpl<AdministrativeUnit>() {
			@Override
			public Filter.Dto instantiateFilter(LazyDataModel<AdministrativeUnit> lazyDataModel) {
				Filter.Dto filter = super.instantiateFilter(lazyDataModel);
				if(filter == null)
					filter = new Filter.Dto();
				//return filter.addField("administrativeUnit", List.of(administrativeUnit.getCode()));
				return filter.addField("administrativeUnitsCodes", List.of(administrativeUnit.getCode()));
			}
		});
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return administrativeUnit.toString();	
	}	
}