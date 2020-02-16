package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitListPageNew extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		dataTable = Builder.build(DataTable.class,Map.of(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.ConfiguratorImpl.FIELD_ENTIY_CLASS,AdministrativeUnit.class
				,DataTable.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE
				,DataTable.FIELD_SELECTION_MODE,"multiple",DataTable.ConfiguratorImpl.FIELD_ENTITY_FIELDS_NAMES,List.of(AdministrativeUnit.FIELD_IDENTIFIER,AdministrativeUnit.FIELD_CODE
				,AdministrativeUnit.FIELD_NAME,AdministrativeUnit.FIELD_SECTION,AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION,AdministrativeUnit.FIELD_SERVICE_GROUP
				,AdministrativeUnit.FIELD_LOCALISATION,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES_BENEFICIAIRE)));
		
		if(defaultSection == null)
			dataTable.addColumnsAfterRowIndex(Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_SECTION)));
		
		dataTable.addColumnsAfterRowIndex(
				Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_NAME,Column.FIELD_HEADER
						,"Unité administrative","filterBy","administrativeUnit",Column.FIELD_WIDTH,"60%"))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES,Column.FIELD_HEADER,"N.A.",Column.FIELD_WIDTH,"70"
						,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.FALSE))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_SERVICE_GROUP))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION,Column.FIELD_HEADER,"CFAP"
						,Column.FIELD_VISIBLE,Boolean.FALSE))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_LOCALISATION,Column.FIELD_VISIBLE,Boolean.FALSE))
				);
		
		dataTable.addHeaderToolbarLeftCommandButtons(
				Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Créer",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(dataTable) {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Création d'unité administrative");
						super.__showDialog__();
					}
				}.setMinimumSelectionSize(0).setIsSelectionShowable(Boolean.FALSE),CommandButton.FIELD_ICON,"fa fa-plus"))
				,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Supprimer",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(dataTable) {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Suppression d'unité administrative");
						super.__showDialog__();
					}
				},CommandButton.FIELD_ICON,"fa fa-remove"))
				,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Fusionner",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(dataTable) {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Fusion d'unité administrative");
						super.__showDialog__();
					}
				}.setMinimumSelectionSize(2),CommandButton.FIELD_ICON,"fa fa-arrows"))
				,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Consulter",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(dataTable) {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Consultation d'unité administrative");
						super.__showDialog__();
					}
				},CommandButton.FIELD_ICON,"fa fa-eye"))
			);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
