
package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitFromDestinationController;
import org.cyk.system.sibua.client.controller.api.FunctionalClassificationController;
import org.cyk.system.sibua.client.controller.api.LocalisationController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.api.ServiceGroupController;
import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitFromDestination;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitFromDestinationCodifyPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private SelectionOne<Program> program;
	private SelectionOne<Action> action;
	private SelectionOne<AdministrativeUnit> administrativeUnit;
	
	private DualListModel<Activity> activities;	
	
	private List<AdministrativeUnitFromDestination> __selectedAdministrativeUnitFromDestinations__,selectedAdministrativeUnitFromDestinations;
	private LazyDataModel<AdministrativeUnitFromDestination> availableAdministrativeUnitFromDestinations;
	private Collection<Section> sections;
	private Collection<ServiceGroup> serviceGroups;
	private Collection<Localisation> localisations;
	private Collection<FunctionalClassification> functionalClassifications;
	
	private Commandable updateCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {
			sections = __inject__(SectionController.class).read(new Properties().setIsPageable(Boolean.FALSE));
			serviceGroups = __inject__(ServiceGroupController.class).read(new Properties().setIsPageable(Boolean.FALSE));
			localisations = __inject__(LocalisationController.class).read(new Properties().setIsPageable(Boolean.FALSE));
			functionalClassifications = __inject__(FunctionalClassificationController.class).read(new Properties().setIsPageable(Boolean.FALSE));
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		availableAdministrativeUnitFromDestinations = new LazyDataModel<AdministrativeUnitFromDestination>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<AdministrativeUnitFromDestination> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				List<AdministrativeUnitFromDestination> list = (List<AdministrativeUnitFromDestination>) __inject__(AdministrativeUnitFromDestinationController.class)
						.read(new Properties().setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
				setRowCount(__inject__(AdministrativeUnitFromDestinationController.class).count().intValue());
				return list;
			}
			
			@Override
			public Object getRowKey(AdministrativeUnitFromDestination administrativeUnitFromDestination) {
				if(administrativeUnitFromDestination == null)
					return null;
				return administrativeUnitFromDestination.getIdentifier();
			}
			
			@Override
			public AdministrativeUnitFromDestination getRowData(String identifier) {
				return __inject__(AdministrativeUnitFromDestinationController.class).readBySystemIdentifier(identifier);
			}			
		};
		
		CommandableBuilder updateCommandableBuilder = __inject__(CommandableBuilder.class);
		updateCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		updateCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),":form:selectedAdministrativeUnitFromDestinations");
		updateCommandable = updateCommandableBuilder.execute().getOutput();
	}
	
	public void openAvailableAdministrativeUnitFromDestinationsDialog() {
		
	}
	
	public void select() {
		if(CollectionHelper.isEmpty(__selectedAdministrativeUnitFromDestinations__))
			return;
		__select__(__selectedAdministrativeUnitFromDestinations__);
		__selectedAdministrativeUnitFromDestinations__.clear();
	}
	
	public void selectOne(AdministrativeUnitFromDestination destination) {
		if(destination == null)
			return;
		__select__(destination);
	}
	
	public void __select__(Collection<AdministrativeUnitFromDestination> administrativeUnitFromDestinations) {
		if(CollectionHelper.isEmpty(administrativeUnitFromDestinations))
			return;
		administrativeUnitFromDestinations.forEach(new Consumer<AdministrativeUnitFromDestination>() {
			@Override
			public void accept(AdministrativeUnitFromDestination administrativeUnitFromDestination) {
				administrativeUnitFromDestination.set__name__(administrativeUnitFromDestination.getName());
			}
		});
		if(selectedAdministrativeUnitFromDestinations == null)
			selectedAdministrativeUnitFromDestinations = new ArrayList<>();
		selectedAdministrativeUnitFromDestinations.addAll(administrativeUnitFromDestinations);
	}
	
	public void __select__(AdministrativeUnitFromDestination...administrativeUnitFromDestinations) {
		if(ArrayHelper.isEmpty(administrativeUnitFromDestinations))
			return;
		__select__(CollectionHelper.listOf(administrativeUnitFromDestinations));
	}
	
	public void save() {
		if(CollectionHelper.isEmpty(selectedAdministrativeUnitFromDestinations))
			return;
		selectedAdministrativeUnitFromDestinations.forEach(new Consumer<AdministrativeUnitFromDestination>() {
			@Override
			public void accept(AdministrativeUnitFromDestination administrativeUnitFromDestination) {
				administrativeUnitFromDestination.setName(administrativeUnitFromDestination.get__name__());
			}
		});
		__inject__(AdministrativeUnitFromDestinationController.class).updateMany(selectedAdministrativeUnitFromDestinations
				,new Properties().setFields(AdministrativeUnitFromDestination.FIELD_DESTINATION+","+AdministrativeUnitFromDestination.FIELD_FUNCTIONAL_CLASSIFICATION
						+","+AdministrativeUnitFromDestination.FIELD_LOCALISATION+","+AdministrativeUnitFromDestination.FIELD_SECTION
						+","+AdministrativeUnitFromDestination.FIELD_SERVICE_GROUP+","+AdministrativeUnitFromDestination.FIELD_NAME));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Codification d'unités administratives";
	}
	
}
