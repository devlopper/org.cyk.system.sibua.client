package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitFunctionTypeController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitFunctionType;
import org.cyk.system.sibua.client.controller.entities.user.FunctionType;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitFunctionTypeCreatePage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AdministrativeUnit administrativeUnit;
	private Layout layout;
	private AutoComplete functionType;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("administrativeunit"));
		
		functionType = AutoComplete.build(AutoComplete.FIELD_ENTITY_CLASS,FunctionType.class,AutoComplete.FIELD_MULTIPLE,Boolean.TRUE);
		
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.FIELD_NUMBER_OF_COLUMNS,2
				,Layout.FIELD_ROW_CELL_MODEL,Map.of(0,new Cell().setWidth(3),1,new Cell().setWidth(9))
				,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,CollectionHelper.listOf(
					MapHelper.instantiate(Cell.FIELD_CONTROL,functionType.getOutputLabel()),MapHelper.instantiate(Cell.FIELD_CONTROL,functionType)
					
					,MapHelper.instantiate(Cell.ConfiguratorImpl.FIELD_CONTROL_COMMAND_BUTTON_ARGUMENTS,MapHelper.instantiate(CommandButton.ConfiguratorImpl.FIELD_OBJECT,this
							,CommandButton.ConfiguratorImpl.FIELD_METHOD_NAME,"record"))					
				));
	}
	
	public void record() {
		if(CollectionHelper.isEmpty((Collection<?>)functionType.getValue()))
			return;
		@SuppressWarnings("unchecked")
		Collection<FunctionType> functionTypes = (Collection<FunctionType>) functionType.getValue();
		__inject__(AdministrativeUnitFunctionTypeController.class).createMany(functionTypes.stream()
				.map(functionType -> new AdministrativeUnitFunctionType().setAdministrativeUnit(administrativeUnit).setFunctionType(functionType)).collect(Collectors.toList()));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Assignation de type de fonction budgétaire à "+administrativeUnit;
	}
	
}
