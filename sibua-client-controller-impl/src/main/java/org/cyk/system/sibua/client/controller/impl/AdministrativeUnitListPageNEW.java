package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.server.persistence.api.query.AdministrativeUnitReadingQuerier;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.LazyDataModel;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitListPageNEW extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;
	private AdministrativeUnit administrativeUnit;
	private Collection<AdministrativeUnit> administrativeUnits;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		dataTable = DataTable.build(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.FIELD_ELEMENT_CLASS,AdministrativeUnit.class);
		
		@SuppressWarnings("unchecked")
		LazyDataModel<AdministrativeUnit> lazyDataModel = (LazyDataModel<AdministrativeUnit>) dataTable.getValue();
		lazyDataModel.setReaderUsable(Boolean.TRUE);
		lazyDataModel.setReadQueryIdentifier(AdministrativeUnitReadingQuerier.QUERY_IDENTIFIER_READ_VIEW_02);
		
		if(defaultSection == null) {
			dataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_SECTION+"AsString",Column.FIELD_FILTER_BY,AdministrativeUnit.FIELD_SECTION
					,Column.FIELD_HEADER_TEXT,"Section",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE));
		}else {			
			
		}
		
		lazyDataModel.setListener(new LazyDataModel.Listener.AbstractImpl<AdministrativeUnit>() {
			@Override
			public Filter.Dto instantiateFilter(LazyDataModel<AdministrativeUnit> lazyDataModel) {
				Filter.Dto filter = super.instantiateFilter(lazyDataModel);
				if(filter == null)
					filter = new Filter.Dto();
				if(defaultSection != null)
					filter.addField(AdministrativeUnit.FIELD_SECTION, defaultSection.getCode());
				return filter;
			}
		});
		
		dataTable.addColumnsAfterRowIndex(
				Column.build(Column.FIELD_FIELD_NAME,"asString",Column.FIELD_HEADER_TEXT,"Unité administrative",Column.FIELD_FILTER_BY,"administrativeUnit"
						,Column.FIELD_WIDTH,"60%",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES,Column.FIELD_HEADER_TEXT,"N.A.",Column.FIELD_WIDTH,"70")
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_SERVICE_GROUP+"AsString",Column.FIELD_HEADER_TEXT,"Groupe de service"
						,Column.FIELD_FILTER_BY,AdministrativeUnit.FIELD_SERVICE_GROUP,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION+"AsString",Column.FIELD_HEADER_TEXT,"CFAP"
						,Column.FIELD_FILTER_BY,AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION
						,Column.FIELD_VISIBLE,Boolean.FALSE,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_LOCALISATION+"AsString",Column.FIELD_HEADER_TEXT,"Localisation"
						,Column.FIELD_FILTER_BY,AdministrativeUnit.FIELD_LOCALISATION
						,Column.FIELD_VISIBLE,Boolean.FALSE,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				
				);
		
		dataTable.addHeaderToolbarLeftCommandsByArgumentsOpenViewInDialogCreate();
		
		dataTable.setListener(new AbstractDataTable.Listener.AbstractImpl() {
			@Override
			public String getStyleClassByRecord(Object record,Integer rowIndex) {
				/*if(record instanceof AdministrativeUnit) {
					if( ((AdministrativeUnit)record).getNumberOfActivities()==0 && ((AdministrativeUnit)record).getNumberOfActivitiesBeneficiaire()==0 )
						return "cyk-background-highlight";
				}*/						
				return "ui-state-default";
			}
			
			@Override
			public String getStyleClassByRecordByColumn(Object record,Integer rowIndex,Column column, Integer columnIndex) {
				return "ui-state-default";
			}
		});
		
		dataTable.addRecordMenuItemByArgumentsOpenViewInDialogRead();
		dataTable.addRecordMenuItemByArgumentsOpenViewInDialogUpdate();
		dataTable.addRecordMenuItemByArgumentsExecuteFunctionDelete();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}	
}