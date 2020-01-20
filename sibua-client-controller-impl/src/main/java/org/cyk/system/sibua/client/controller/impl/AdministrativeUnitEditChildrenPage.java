package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.component.window.WindowBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditChildrenPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<AdministrativeUnit> administrativeUnit;
	
	private List<AdministrativeUnit> __selectedAdministrativeUnits__,selectedAdministrativeUnits;
	private LazyDataModel<AdministrativeUnit> availableAdministrativeUnits;
	private final Map<String,AdministrativeUnit> administrativeUnits = new HashMap<>();
	
	private Commandable updateCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		administrativeUnit = new SelectionOne<AdministrativeUnit>(AdministrativeUnit.class);
		administrativeUnit.setAreChoicesGettable(Boolean.FALSE);
		try {		
			if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
				
			}else {
				administrativeUnit.select(__inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier")
						,new Properties().setFields(AdministrativeUnit.FIELD_IDENTIFIER+","+AdministrativeUnit.FIELD_CODE+","+AdministrativeUnit.FIELD_NAME
								+","+AdministrativeUnit.FIELD_CHILDREN)));
				if(administrativeUnit.getValue() != null)
					selectedAdministrativeUnits = administrativeUnit.getValue().getChildren();
			}	
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		availableAdministrativeUnits = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				FilterDto filter = new FilterDto();
				Collection<String> selectedAdministrativeUnitsCodes = new ArrayList<String>();
				filter.addField(AdministrativeUnit.FIELD_NAME, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_NAME));
				if(administrativeUnit != null && administrativeUnit.getValue() != null) {
					filter.addField(AdministrativeUnit.FIELD_SECTION, List.of(administrativeUnit.getValue().getSection().getCode()));
					selectedAdministrativeUnitsCodes.add(administrativeUnit.getValue().getCode());
				}
				filter.addField(AdministrativeUnit.FIELD_SERVICE_GROUP, CollectionHelper.listOf(Boolean.TRUE, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_SERVICE_GROUP)));
				filter.addField(AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION, CollectionHelper.listOf(Boolean.TRUE, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION)));
				filter.addField(AdministrativeUnit.FIELD_LOCALISATION, CollectionHelper.listOf(Boolean.TRUE, MapHelper.readByKey(filters, AdministrativeUnit.FIELD_LOCALISATION)));
				
				if(CollectionHelper.isNotEmpty(selectedAdministrativeUnits))
					selectedAdministrativeUnitsCodes.addAll(selectedAdministrativeUnits.stream().map(AdministrativeUnit::getCode).collect(Collectors.toSet()));
				
				if(CollectionHelper.isNotEmpty(selectedAdministrativeUnitsCodes))
					filter.addField(AdministrativeUnit.FIELD_CODE, selectedAdministrativeUnitsCodes);
				
				List<AdministrativeUnit> list = (List<AdministrativeUnit>) __inject__(AdministrativeUnitController.class)
						.read(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.READ_WHERE_CODE_NOT_IN_BY_FILTERS).setFilters(filter)
								.setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
				if(CollectionHelper.isEmpty(list))
					setRowCount(0);
				
				else {
					Long count = __inject__(AdministrativeUnitController.class)
							.count(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.COUNT_WHERE_CODE_NOT_IN_BY_FILTERS).setFilters(filter));
					setRowCount(count == null ? 0 : count.intValue());	
				}
				return list;
			}
			
			@Override
			public Object getRowKey(AdministrativeUnit administrativeUnit) {
				if(administrativeUnit == null)
					return null;
				administrativeUnits.put(administrativeUnit.getIdentifier(), administrativeUnit);
				return administrativeUnit.getIdentifier();
			}
			
			@Override
			public AdministrativeUnit getRowData(String identifier) {
				return administrativeUnits.get(identifier);
			}
		};
		
		CommandableBuilder updateCommandableBuilder = __inject__(CommandableBuilder.class);
		updateCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					update();
				}
			}
		);
		updateCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers());
		updateCommandable = updateCommandableBuilder.execute().getOutput();
	}
	
	public void openAvailableAdministrativeUnitsDialog() {
		
	}
	
	public void __select__(Collection<AdministrativeUnit> administrativeUnits) {
		if(CollectionHelper.isEmpty(administrativeUnits))
			return;
		if(selectedAdministrativeUnits == null)
			selectedAdministrativeUnits = new ArrayList<>();
		selectedAdministrativeUnits.addAll(administrativeUnits);
	}
	
	public void __select__(AdministrativeUnit...administrativeUnits) {
		if(ArrayHelper.isEmpty(administrativeUnits))
			return;
		__select__(CollectionHelper.listOf(administrativeUnits));
	}
	
	public void select() {
		if(CollectionHelper.isEmpty(__selectedAdministrativeUnits__))
			return;
		__select__(__selectedAdministrativeUnits__);
		__selectedAdministrativeUnits__.clear();
	}
	
	public void selectOne(AdministrativeUnit administrativeUnit) {
		if(administrativeUnit == null)
			return;
		__select__(administrativeUnit);
	}
	
	public void __unselect__(Collection<AdministrativeUnit> administrativeUnits) {
		if(CollectionHelper.isEmpty(administrativeUnits) || CollectionHelper.isEmpty(selectedAdministrativeUnits))
			return;
		selectedAdministrativeUnits.removeAll(administrativeUnits);
	}
	
	public void __unselect__(AdministrativeUnit...administrativeUnits) {
		if(ArrayHelper.isEmpty(administrativeUnits))
			return;
		__unselect__(CollectionHelper.listOf(administrativeUnits));
	}
	
	public void unselect() {
		if(CollectionHelper.isEmpty(selectedAdministrativeUnits))
			return;
		__unselect__(selectedAdministrativeUnits);
	}
	
	public void unselectOne(AdministrativeUnit administrativeUnit) {
		if(administrativeUnit == null)
			return;
		__unselect__(administrativeUnit);
	}
	
	public void update() {
		administrativeUnit.getValue().setChildren((List<AdministrativeUnit>) selectedAdministrativeUnits);
		__inject__(AdministrativeUnitController.class).update(administrativeUnit.getValue(),new Properties().setFields(AdministrativeUnit.FIELD_CHILDREN));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Arborescence de l'unité administrative"+(administrativeUnit.getValue() == null ? ConstantEmpty.STRING : " "+administrativeUnit.getValue()
				+" , Section "+administrativeUnit.getValue().getSection());	
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
