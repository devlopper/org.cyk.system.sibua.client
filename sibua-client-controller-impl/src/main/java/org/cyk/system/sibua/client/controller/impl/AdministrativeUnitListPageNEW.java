package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.server.persistence.api.ActivityPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.Button;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;

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
		dataTable = Builder.build(DataTable.class,Map.of(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.ConfiguratorImpl.FIELD_ENTIY_CLASS,AdministrativeUnit.class
				,DataTable.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE
				,DataTable.FIELD_SELECTION_MODE,"multiple",DataTable.ConfiguratorImpl.FIELD_ENTITY_FIELDS_NAMES,List.of(AdministrativeUnit.FIELD_IDENTIFIER,AdministrativeUnit.FIELD_CODE
				,AdministrativeUnit.FIELD_NAME,AdministrativeUnit.FIELD_SECTION,AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION,AdministrativeUnit.FIELD_SERVICE_GROUP
				,AdministrativeUnit.FIELD_LOCALISATION,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES_BENEFICIAIRE)));
		
		if(defaultSection == null)
			dataTable.addColumnsAfterRowIndex(Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_SECTION)));
		
		dataTable.addColumnsAfterRowIndex(
				Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_NAME,Column.FIELD_HEADER_TEXT
						,"Unité administrative","filterBy","administrativeUnit",Column.FIELD_WIDTH,"60%"))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_NUMBER_OF_ACTIVITIES,Column.FIELD_HEADER_TEXT,"N.A.",Column.FIELD_WIDTH,"70"
						,Column.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.FALSE))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_SERVICE_GROUP))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION,Column.FIELD_HEADER_TEXT,"CFAP"
						,Column.FIELD_VISIBLE,Boolean.FALSE))
				,Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnit.FIELD_LOCALISATION,Column.FIELD_VISIBLE,Boolean.FALSE))
				);
		
		dataTable.addHeaderToolbarLeftCommands(
				Builder.build(Button.class,Map.of(Button.FIELD_VALUE,"Créer",Button.FIELD_ICON,"fa fa-plus",Button.FIELD_OUTCOME,"administrativeUnitEditView"))
				,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Supprimer",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Suppression d'unité administrative");
						dataTable.getDialog().getExecuteCommandButton().setRendered(Boolean.TRUE);
						dataTable.getDialog().getExecuteCommandButton().getConfirm().setDisabled(Boolean.FALSE);
						super.__showDialog__();
					}
				}.setCommandIdentifier("delete"),CommandButton.FIELD_ICON,"fa fa-remove"))
				,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Fusionner",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Fusion d'unité administrative");
						dataTable.getDialog().getExecuteCommandButton().setRendered(Boolean.TRUE);
						dataTable.getDialog().getExecuteCommandButton().getConfirm().setDisabled(Boolean.FALSE);
						super.__showDialog__();
					}
				}.setCommandIdentifier("merge").setMinimumSelectionSize(2),CommandButton.FIELD_ICON,"fa fa-arrows"))
				,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Consulter",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
						,CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
					@Override
					protected void __showDialog__() {
						dataTable.getDialog().setHeader("Consultation d'unité administrative");
						dataTable.getDialog().getExecuteCommandButton().setRendered(Boolean.FALSE);
						@SuppressWarnings("unchecked")
						Collection<AdministrativeUnit> selectedAdministrativeUnits = (Collection<AdministrativeUnit>) dataTable.getSelection();
						selectedAdministrativeUnits.forEach(new Consumer<AdministrativeUnit>() {
							@Override
							public void accept(AdministrativeUnit administrativeUnit) {
								if(administrativeUnit.getActivities() == null) {
									List<Activity> activities = (List<Activity>) __inject__(ActivityController.class).read(new Properties().setQueryIdentifier(ActivityPersistence.READ_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES)
											.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME)
											.setFilters(new FilterDto().addField(Activity.FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE_OR_BENEFICIAIRE, List.of(administrativeUnit.getCode())))
											.setIsPageable(Boolean.FALSE));
									if(CollectionHelper.isNotEmpty(activities)) {
										List<Activity> gestionnaires = (List<Activity>) __inject__(ActivityController.class).read(new Properties().setQueryIdentifier(ActivityPersistence.READ_BY_FILTERS_LIKE)
												.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME)
												.setFilters(new FilterDto().addField(Activity.FIELD_ADMINISTRATIVE_UNIT, administrativeUnit.getCode())).setIsPageable(Boolean.FALSE));
										
										List<Activity> beneficiaires = (List<Activity>) __inject__(ActivityController.class).read(new Properties().setQueryIdentifier(ActivityPersistence.READ_BY_FILTERS_LIKE)
												.setFields(Activity.FIELD_IDENTIFIER+","+Activity.FIELD_CODE+","+Activity.FIELD_NAME)
												.setFilters(new FilterDto().addField(Activity.FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE, administrativeUnit.getCode())).setIsPageable(Boolean.FALSE));
									
										for(Activity activity : activities) {
											activity.setIsGestionnaire(CollectionHelper.contains(gestionnaires, activity));
											activity.setIsBeneficiaire(CollectionHelper.contains(beneficiaires, activity));
										}
										
										administrativeUnit.setActivities(activities.stream().sorted(new Comparator<Activity>() {
											@Override
											public int compare(Activity o1, Activity o2) {
												return o1.getIsGestionnaire() ? -1 : 1;
											}							
										}).collect(Collectors.toList()));
									}
								}
							}
						});
						
						super.__showDialog__();
					}
				}.setCommandIdentifier("read"),CommandButton.FIELD_ICON,"fa fa-eye"))
			);
		
		dataTable.getMenuColumn().setRendered(Boolean.TRUE);
		
		dataTable.getDialog().getExecuteCommandButton().setListener(new AbstractAction.Listener.AbstractImpl() {			
			@Override
			public void listenAction(Object argument) {
				if(CollectionHelper.isEmpty(dataTable.getSelection()))
					return;
				@SuppressWarnings("unchecked")
				List<AdministrativeUnit> selectedAdministrativeUnits = (List<AdministrativeUnit>) dataTable.getSelection();
				
				if("create".equals(dataTable.getSelectedCommandIdentifier())) {
					__inject__(AdministrativeUnitController.class).createMany(administrativeUnits);
				}else if("delete".equals(dataTable.getSelectedCommandIdentifier())) {
					__inject__(AdministrativeUnitController.class).deleteMany(selectedAdministrativeUnits);
				}else if("merge".equals(dataTable.getSelectedCommandIdentifier())) {
					if(administrativeUnit == null)
						return;
					__inject__(AdministrativeUnitController.class).mergeByCodes(selectedAdministrativeUnits, administrativeUnit);
				}
				dataTable.getSelection().clear();
				dataTable.setSelection(null);
			}
		});
		
		dataTable.setListener(new AbstractDataTable.Listener() {
			@Override
			public String listenGetStyleClassByRecord(Object record,Integer rowIndex) {
				if(record instanceof AdministrativeUnit) {
					if( ((AdministrativeUnit)record).getNumberOfActivities()==0 && ((AdministrativeUnit)record).getNumberOfActivitiesBeneficiaire()==0 )
						return "cyk-background-highlight";
				}						
				return "ui-state-default";
			}
			
			@Override
			public String listenGetStyleClassByRecordByColumn(Object record,Integer rowIndex,Column column, Integer columnIndex) {
				return "ui-state-default";
			}
		});
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des unités administratives"+(defaultSection == null ? ConstantEmpty.STRING : " de la section "+defaultSection);
	}
	
}
