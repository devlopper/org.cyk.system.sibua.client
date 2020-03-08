package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.properties.Properties;
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
		dataTable = DataTable.build(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.ConfiguratorImpl.FIELD_ENTIY_CLASS,Activity.class,DataTable.FIELD_SELECTION_MODE,"multiple");
		((LazyDataModel<?>)dataTable.getValue()).setListener(new LazyDataModel.Listener.AbstractImpl() {
			@Override
			public void processReadProperties(Properties properties) {
				super.processReadProperties(properties);
				properties.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME+","+Activity.FIELD_ACTION
						+","+Activity.FIELD_ADMINISTRATIVE_UNIT+","+Activity.FIELD_CAT_ATV_CODE+","+Activity.FIELD_NUMBER_OF_COST_UNITS);
			}
		});
		
		if(defaultSection == null)
			dataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_SECTION,Column.FIELD_WIDTH,"70",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE));
		
		dataTable.addColumnsAfterRowIndex(
				Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_NAME,Column.FIELD_HEADER_TEXT,"Activité","filterBy","activity",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_CAT_ATV_CODE,Column.FIELD_HEADER_TEXT,"Catégorie",Column.FIELD_WIDTH,"110",Column.FIELD_VISIBLE,Boolean.FALSE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_NUMBER_OF_COST_UNITS,Column.FIELD_HEADER_TEXT,"N.U.C.",Column.FIELD_WIDTH,"60")
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_PROGRAM,Column.FIELD_VISIBLE,Boolean.FALSE,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE,Column.FIELD_HEADER_TEXT,"Bénéficiaire",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE,Column.FIELD_HEADER_TEXT,"Gestionnaire",Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE)
				);
		
		dataTable.addHeaderToolbarLeftCommands(
				CommandButton.build(CommandButton.FIELD_VALUE,"Modification en masse",CommandButton.ConfiguratorImpl.FIELD_COLLECTION,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
					@Override
					protected void __showDialog__(Object argument) {
						super.__showDialog__(argument);
						dataTable.getDialog().setHeader("Modification en masse");
						
					}
				}.setMinimumSelectionSize(1),CommandButton.FIELD_ICON,"fa fa-pencil")
				,CommandButton.build(CommandButton.FIELD_VALUE,"Modification en détails",CommandButton.ConfiguratorImpl.FIELD_COLLECTION,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
					@Override
					protected void __showDialog__(Object argument) {
						super.__showDialog__(argument);
						dataTable.getDialog().setHeader("Modification en détails");
						
					}
				}.setMinimumSelectionSize(2),CommandButton.FIELD_ICON,"fa fa-pencil")
			);
		
		//dataTable.getMenuColumn().setRendered(Boolean.TRUE);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
