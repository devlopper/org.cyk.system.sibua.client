package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.user.FunctionTypeController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.user.FunctionType;
import org.cyk.system.sibua.server.persistence.api.user.FunctionTypePersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActivityEditPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;
	private Boolean isOneToMany;
	private AutoComplete beneficiaryAutoComplete,managerAutoComplete,functionTypeAutoComplete;
	private CommandButton recordCommandButton;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		isOneToMany = ValueHelper.convertToBoolean(Faces.getRequestParameter("isonetomany"));
		Collection<Activity> activities = null;
		if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
			activities = (Collection<Activity>) SessionHelper.getAttributeValue(SESSION_COLLECTION_ATTRIBUTE_NAME);
		}else {
			Activity activity = __inject__(ActivityController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"),new Properties()
					.setFields(StringHelper.concatenate(List.of(Activity.FIELD_IDENTIFIER,Activity.FIELD_CODE,Activity.FIELD_NAME,Activity.FIELD_ACTION
						,Activity.FIELD_ADMINISTRATIVE_UNIT,Activity.FIELD_CAT_ATV_CODE,Activity.FIELD_NUMBER_OF_COST_UNITS,Activity.FIELD_FUNCTION_TYPE), ",")));
			if(activity != null)
				activities = CollectionHelper.listOf(activity);
		}
		/*
		activities =  CollectionHelper.getElementsFromTo(__inject__(ActivityController.class).read(new Properties().setFields(StringHelper.concatenate(List.of(Activity.FIELD_IDENTIFIER,Activity.FIELD_CODE,Activity.FIELD_NAME,Activity.FIELD_ACTION
						,Activity.FIELD_ADMINISTRATIVE_UNIT,Activity.FIELD_CAT_ATV_CODE,Activity.FIELD_NUMBER_OF_COST_UNITS,Activity.FIELD_FUNCTION_TYPE), ","))),0,2);
		*/
		dataTable = DataTable.build(DataTable.FIELD_ELEMENT_CLASS,Activity.class,DataTable.FIELD_VALUE,activities);
		
		if(defaultSection == null)
			dataTable.addColumnsAfterRowIndex(Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_SECTION,Column.FIELD_WIDTH,"70"));
		
		dataTable.addColumnsAfterRowIndex(
				Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_NAME,Column.FIELD_HEADER_TEXT,"Activité")
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE,Column.FIELD_HEADER_TEXT,"Bénéficiaire")
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE,Column.FIELD_HEADER_TEXT,"Gestionnaire")
				,Column.build(Column.FIELD_FIELD_NAME,Activity.FIELD_FUNCTION_TYPE)
				);	
		
		beneficiaryAutoComplete = AutoComplete.build(AutoComplete.FIELD_ENTITY_CLASS,AdministrativeUnit.class,AutoComplete.FIELD_DROPDOWN,Boolean.FALSE);
		managerAutoComplete = AutoComplete.build(AutoComplete.FIELD_ENTITY_CLASS,AdministrativeUnit.class,AutoComplete.FIELD_DROPDOWN,Boolean.FALSE);
		functionTypeAutoComplete = AutoComplete.build(AutoComplete.FIELD_ENTITY_CLASS,FunctionType.class,AutoComplete.FIELD_DROPDOWN,Boolean.TRUE);
		if(StringHelper.isNotBlank(Faces.getRequestParameter("entityidentifier"))) {
			Activity activity = CollectionHelper.getFirst(activities);
			if(activity.getAdministrativeUnit() != null) {
				functionTypeAutoComplete.setValue(__inject__(FunctionTypeController.class).read(new Properties()
					.setQueryIdentifier(FunctionTypePersistence.READ_BY_ADMINISTRATIVE_UNITS_CODES).setFilters(new Filter.Dto()
							.addField(FunctionType.FIELD_ADMINISTRATIVE_UNIT, List.of(activity.getAdministrativeUnit().getCode())))));
			}
		}
		
		recordCommandButton = CommandButton.build(CommandButton.ConfiguratorImpl.FIELD_OBJECT,this,CommandButton.ConfiguratorImpl.FIELD_METHOD_NAME,"record"
				,CommandButton.FIELD_ICON,"fa fa-floppy-o",CommandButton.ConfiguratorImpl.FIELD_LISTENER_IS_WINDOW_RENDERED_AS_DIALOG,getIsRenderTypeDialog());
		
		if(Boolean.TRUE.equals(isOneToMany)) {
			
		}else {
			beneficiaryAutoComplete.getAjaxes().get("query").setGlobal(Boolean.TRUE);
		}
	}
	
	public void record() {
		@SuppressWarnings("unchecked")
		Collection<Activity> activities = (Collection<Activity>) dataTable.getValue();
		if(CollectionHelper.isEmpty(activities))
			return;
		if(Boolean.TRUE.equals(isOneToMany)) {
			activities.forEach(new Consumer<Activity>() {
				@Override
				public void accept(Activity activity) {
					if(managerAutoComplete.getValue() != null)
						activity.setAdministrativeUnit((AdministrativeUnit) managerAutoComplete.getValue());
					if(beneficiaryAutoComplete.getValue() != null)
						activity.setAdministrativeUnitBeneficiaire((AdministrativeUnit) beneficiaryAutoComplete.getValue());
					if(functionTypeAutoComplete.getValue() != null)
						activity.setFunctionType((FunctionType) functionTypeAutoComplete.getValue());
				}
			});	
		}else {
			
		}
		__inject__(ActivityController.class).updateMany(activities,new Properties().setFields(Activity.FIELD_ADMINISTRATIVE_UNIT+","+Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE
				+","+Activity.FIELD_FUNCTION_TYPE));
		activities.clear();
		activities = null;
		SessionHelper.setAttributeValue(SESSION_COLLECTION_ATTRIBUTE_NAME, null);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Modification des activités"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
	/**/
	
	public static final String SESSION_COLLECTION_ATTRIBUTE_NAME = ActivityEditPage.class.getSimpleName()+"."+Activity.class.getSimpleName()+".collection";
}
