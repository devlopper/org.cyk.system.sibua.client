package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitActivity;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitActivityPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.component.window.WindowBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.ajax.Ajax;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.omnifaces.util.Faces;
import org.primefaces.component.inplace.Inplace;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditActivitiesPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private DataTable dataTable;
	
	private AdministrativeUnit administrativeUnit;
	private LazyDataModel<AdministrativeUnitActivity> administrativeUnitActivities;
	private CommandButton deleteCommandButton;
	private AutoComplete serviceGestionnaireAutoComplete,serviceBeneficiaireAutoComplete,autoComplete;
	private Ajax saveAjax;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {		
			if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
				
			}else {
				administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
				/*
				dataTable = Builder.build(DataTable.class,Map.of(DataTable.FIELD_LAZY,Boolean.TRUE,DataTable.ConfiguratorImpl.FIELD_ENTIY_CLASS,AdministrativeUnitActivity.class
						,DataTable.ConfiguratorImpl.FIELD_FILTERABLE,Boolean.TRUE,DataTable.FIELD_SELECTION_MODE,"multiple"));				
				
				((org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.LazyDataModel<AdministrativeUnitActivity>) dataTable.getValue())
					.setReadQueryIdentifier(AdministrativeUnitActivityPersistence.READ_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES);
				
				((org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.LazyDataModel<AdministrativeUnitActivity>) dataTable.getValue())
					.setCountQueryIdentifier(AdministrativeUnitActivityPersistence.COUNT_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES);
				
				((org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.LazyDataModel<AdministrativeUnitActivity>) dataTable.getValue())
					.setFilter(new FilterDto().addField(AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT, List.of(administrativeUnit.getCode())));
				
				dataTable.addColumnsAfterRowIndex(
						Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,AdministrativeUnitActivity.FIELD_ACTIVITY,Column.FIELD_WIDTH,"50%"))
						,Builder.build(Column.class, Map.of(Column.FIELD_HEADER,"Programme",Column.FIELD_FILTER_BY,"program",Column.FIELD_VISIBLE,Boolean.FALSE))
						,Builder.build(Column.class, Map.of(Column.FIELD_HEADER,"Gestionnaire",Column.FIELD_FILTER_BY,"gestionnaire"))
						,Builder.build(Column.class, Map.of(Column.FIELD_HEADER,"Bénéficiaire",Column.FIELD_FILTER_BY,"beneficiaire"))
						);
				
				dataTable.addHeaderToolbarLeftCommands(
						Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Modifier en masse",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
								,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(dataTable) {
							@Override
							protected void __showDialog__() {
								dataTable.getDialog().setHeader("Modification en masse");
								dataTable.getDialog().getExecuteCommandButton().setRendered(Boolean.TRUE);
								super.__showDialog__();
							}
						}.setCommandIdentifier("editmass"),CommandButton.FIELD_ICON,"fa fa-pencil"))
						,Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Modifier en détails",CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,dataTable
								,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(dataTable) {
							@Override
							protected void __showDialog__() {
								dataTable.getDialog().setHeader("Modification en détails");
								dataTable.getDialog().getExecuteCommandButton().setRendered(Boolean.TRUE);
								super.__showDialog__();
							}
						}.setCommandIdentifier("editdetails"),CommandButton.FIELD_ICON,"fa fa-pencil"))
					);
				
				dataTable.setListener(new AbstractDataTable.Listener() {
					@Override
					public String listenGetStyleClassByRecord(Object record,Integer rowIndex) {
						return "ui-state-default";
					}
					
					@Override
					public String listenGetStyleClassByRecordByColumn(Object record,Integer rowIndex,Column column, Integer columnIndex) {
						if(record instanceof AdministrativeUnitActivity) {
							if((NumberHelper.compare(2, columnIndex, ComparisonOperator.EQ) && 	
									((AdministrativeUnitActivity)record).getAdministrativeUnit().getCode().equals(administrativeUnit.getCode()))
									||
									(NumberHelper.compare(3, columnIndex, ComparisonOperator.EQ) && 	
											((AdministrativeUnitActivity)record).getAdministrativeUnitBeneficiaire().getCode().equals(administrativeUnit.getCode()))
								)
								return "cyk-background-highlight";
						}						
						return "ui-state-default";
					}
				});
				*/
				administrativeUnitActivities = new LazyDataModel<AdministrativeUnitActivity>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<AdministrativeUnitActivity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
						FilterDto filter = null;				
						filter = new FilterDto();
						filter.addField(AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT, List.of(administrativeUnit.getCode()));
						List<AdministrativeUnitActivity> list = (List<AdministrativeUnitActivity>) __inject__(AdministrativeUnitActivityController.class)
								.read(new Properties().setQueryIdentifier(AdministrativeUnitActivityPersistence.READ_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES)
										.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
						if(CollectionHelper.isEmpty(list))
							setRowCount(0);
						else {
							Long count = __inject__(AdministrativeUnitActivityController.class).count(new Properties().setFilters(filter));
							setRowCount(count == null ? 0 : count.intValue());	
						}
						return list;
					}
				};
				
				autoComplete = Builder.build(AutoComplete.class,Map.of(AutoComplete.FIELD_ENTITY_CLASS,AdministrativeUnit.class));
				serviceGestionnaireAutoComplete = Builder.build(AutoComplete.class,Map.of(AutoComplete.FIELD_ENTITY_CLASS,AdministrativeUnit.class));
				serviceBeneficiaireAutoComplete = Builder.build(AutoComplete.class,Map.of(AutoComplete.FIELD_ENTITY_CLASS,AdministrativeUnit.class));
				
				saveAjax = Builder.build(Ajax.class,Map.of(Ajax.FIELD_EVENT,"save",Ajax.FIELD_LISTENER,new Ajax.Listener.AbstractImpl() {
					@Override
					public void listenAction(Object argument) {
						if(argument instanceof AjaxBehaviorEvent) {
							Inplace inplace = (Inplace) ((AjaxBehaviorEvent)argument).getSource();
							AdministrativeUnitActivity administrativeUnitActivity = (AdministrativeUnitActivity) inplace.getAttributes().get("administrativeUnitActivity");
							System.out.println(administrativeUnitActivity.getAdministrativeUnit()+" ::: "+administrativeUnitActivity.getAdministrativeUnitBeneficiaire());
						}
					}
				}));
				//saveAjax.getRunnerArguments().setSuccessMessageArguments(null);
				saveAjax.setDisabled(Boolean.FALSE);
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		deleteCommandButton = CommandButton.build(CommandButton.FIELD_TITLE,"Supprimer",CommandButton.FIELD_ICON,"fa fa-remove"
				,CommandButton.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE,CommandButton.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES
				,List.of(RenderType.GROWL));
		deleteCommandButton.addUpdates("administrativeUnitActivities");
		deleteCommandButton.setListener(new CommandButton.Listener.AbstractImpl() {
			@Override
			public void listenAction(Object argument) {
				if(!(argument instanceof AdministrativeUnitActivity))
					return;
				AdministrativeUnitActivity administrativeUnitActivity = (AdministrativeUnitActivity) argument;
				__inject__(AdministrativeUnitActivityController.class).delete(administrativeUnitActivity);
			}
		});
	}
	
	public void saveGestionnaire() {
		
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités de l'unité administrative"+(administrativeUnit == null ? ConstantEmpty.STRING : " "+administrativeUnit
				+" , Section "+administrativeUnit.getSection());	
	}
	
	@Override
	protected String __processWindowDialogOkCommandableGetUrl__(WindowBuilder window, CommandableBuilder commandable) {
		PathAsFunctionParameter pathAsFunctionParameter = new PathAsFunctionParameter();
		pathAsFunctionParameter.setIdentifier("administrativeUnitListView");
		String string =  UniformResourceIdentifierHelper.build(new UniformResourceIdentifierAsFunctionParameter().setPath(pathAsFunctionParameter));
		if(defaultSection != null)
			string = string  + "?section="+defaultSection.getCode();
		return string;
	}
}
