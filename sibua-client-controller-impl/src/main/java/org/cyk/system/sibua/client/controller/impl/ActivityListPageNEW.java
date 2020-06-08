package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.server.persistence.api.ActivitySelectQuerier;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.LazyDataModel;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActivityListPageNEW extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		dataTable = DataTable.build(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.FIELD_ELEMENT_CLASS,Activity.class,DataTable.FIELD_SELECTION_MODE,"multiple");
		
		@SuppressWarnings("unchecked")
		LazyDataModel<Activity> lazyDataModel = (LazyDataModel<Activity>) dataTable.getValue();
		lazyDataModel.setReaderUsable(Boolean.TRUE);
		lazyDataModel.setReadQueryIdentifier(ActivitySelectQuerier.QUERY_IDENTIFIER_READ_VIEW_01);
		
		if(defaultSection == null) {
			dataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_SECTION+"AsString",Column.FIELD_FILTER_BY,AdministrativeUnit.FIELD_SECTION
					,Column.FIELD_HEADER_TEXT,"Section",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE));
		}else {			
			
		}
		
		lazyDataModel.setListener(new LazyDataModel.Listener.AbstractImpl<Activity>() {
			@Override
			public Filter.Dto instantiateFilter(LazyDataModel<Activity> lazyDataModel) {
				Filter.Dto filter = super.instantiateFilter(lazyDataModel);
				if(filter == null)
					filter = new Filter.Dto();
				if(defaultSection != null)
					filter.addField(AdministrativeUnit.FIELD_SECTION, defaultSection.getCode());
				return filter;
			}

		});
		
		dataTable.addColumnsAfterRowIndex(
			Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_AS_STRING,Column.FIELD_HEADER_TEXT,"Activité",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)		
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_CAT_ATV_CODE,Column.FIELD_HEADER_TEXT,"Catégorie",Column.FIELD_WIDTH,"110",Column.FIELD_VISIBLE,Boolean.FALSE)
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_NUMBER_OF_COST_UNITS,Column.FIELD_HEADER_TEXT,"N.U.C.",Column.FIELD_WIDTH,"60")
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_PROGRAM_AS_STRING,Column.FIELD_VISIBLE,Boolean.FALSE,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ACTION_AS_STRING,Column.FIELD_VISIBLE,Boolean.FALSE,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_BENEFICIARY_AS_STRING,Column.FIELD_HEADER_TEXT,"Bénéficiaire",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_MANAGER_AS_STRING,Column.FIELD_HEADER_TEXT,"Gestionnaire",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
			,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_FUNCTION_TYPE_AS_STRING,Column.FIELD_HEADER_TEXT,"Type de fonction")			
		);
		/*
		dataTable.addHeaderToolbarLeftCommands(
				CommandButton.build(CommandButton.FIELD_VALUE,"Modification en masse",CommandButton.FIELD_ICON,"fa fa-edit",CommandButton.ConfiguratorImpl.FIELD_COLLECTION,dataTable
						,CommandButton.ConfiguratorImpl.FIELD_OPEN_VIEW_IN_DIALOG_ARGUMENTS_GETTER,new AbstractAction.Listener.OpenViewInDialogArgumentsGetter.AbstractImpl() {										
					@Override
					public String getOutcome(Object argument, String outcome) {
						return "activityEditView";
					}					
					@Override
					public Map<String,List<String>> getParameters(Object argument, Map<String,List<String>> parameters) {
						SessionHelper.setAttributeValue(ActivityEditPage.SESSION_COLLECTION_ATTRIBUTE_NAME, dataTable.getSelection());
						if(parameters == null)
							parameters = new HashMap<String, List<String>>();
						parameters.put("isonetomany",CollectionHelper.listOf("true"));
						return parameters;
					}
				}
			)			
		);
		*/
		dataTable.addRecordMenuItemByArgumentsOpenViewInDialogRead();
		dataTable.addRecordMenuItemByArgumentsOpenViewInDialogUpdate();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}	
}