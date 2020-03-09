package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActivityListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		dataTable = DataTable.build(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.FIELD_ELEMENT_CLASS,Activity.class,DataTable.FIELD_SELECTION_MODE,"multiple"
				,DataTable.ConfiguratorImpl.FIELD_ENTITY_FIELDS_NAMES,List.of(Activity.FIELD_IDENTIFIER,Activity.FIELD_CODE,Activity.FIELD_NAME,Activity.FIELD_ACTION
						,Activity.FIELD_ADMINISTRATIVE_UNIT,Activity.FIELD_CAT_ATV_CODE,Activity.FIELD_NUMBER_OF_COST_UNITS,Activity.FIELD_FUNCTION_TYPE));
		
		if(defaultSection == null)
			dataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_SECTION,Column.FIELD_WIDTH,"70",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE));
		
		dataTable.addColumnsAfterRowIndex(
				Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_NAME,Column.FIELD_HEADER_TEXT,"Activité","filterBy","activity",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_CAT_ATV_CODE,Column.FIELD_HEADER_TEXT,"Catégorie",Column.FIELD_WIDTH,"110",Column.FIELD_VISIBLE,Boolean.FALSE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_NUMBER_OF_COST_UNITS,Column.FIELD_HEADER_TEXT,"N.U.C.",Column.FIELD_WIDTH,"60")
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_PROGRAM,Column.FIELD_VISIBLE,Boolean.FALSE,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE,Column.FIELD_HEADER_TEXT,"Bénéficiaire",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ADMINISTRATIVE_UNIT,Column.FIELD_HEADER_TEXT,"Gestionnaire",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_FUNCTION_TYPE,Column.FIELD_HEADER_TEXT,"Type de fonction")
				);
		/*
		Map<String,Ajax> ajaxes = new HashMap<>();
		ajaxes.put("dialogReturn", Ajax.build(Ajax.FIELD_EVENT,"dialogReturn",Ajax.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			public void listenAction(Object argument) {
				MessageRenderer.getInstance().render("Activities has been updated!!! Datatable should be updated",RenderType.GROWL);
				PrimefacesHelper.updateOnComplete(":form:"+dataTable.getIdentifier());
			}
		},Ajax.FIELD_DISABLED,Boolean.FALSE));
		*/
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
				//,CommandButton.FIELD_AJAXES,ajaxes
			)
				/*,CommandButton.build(CommandButton.FIELD_VALUE,"Modification en détails",CommandButton.FIELD_ICON,"fa fa-pencil",CommandButton.ConfiguratorImpl.FIELD_COLLECTION,dataTable
						,CommandButton.ConfiguratorImpl.FIELD_OPEN_VIEW_IN_DIALOG_ARGUMENTS_GETTER,new AbstractAction.Listener.OpenViewInDialogArgumentsGetter.AbstractImpl() {										
					@Override
					public String getOutcome(Object argument, String outcome) {
						return "activityEditView";
					}					
					@Override
					public Map<String,List<String>> getParameters(Object argument, Map<String,List<String>> parameters) {
						SessionHelper.setAttributeValue(ActivityEditPage.SESSION_COLLECTION_ATTRIBUTE_NAME, dataTable.getSelection());
						return parameters;
					}
				}
				)*/
			);
		
		dataTable.addRecordMenuItemByArgumentsOpenViewInDialogRead();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
