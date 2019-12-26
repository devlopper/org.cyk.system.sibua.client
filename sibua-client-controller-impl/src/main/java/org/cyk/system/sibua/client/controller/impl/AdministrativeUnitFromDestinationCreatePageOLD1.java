package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.DestinationController;
import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Destination;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitFromDestinationCreatePageOLD1 extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private SelectionOne<Program> program;
	private SelectionOne<Action> action;
	private SelectionOne<AdministrativeUnit> administrativeUnit;
	
	private DualListModel<Activity> activities;	
	
	private List<Destination> __selectedDestinations__,selectedDestinations;
	private LazyDataModel<Destination> availableDestinations;
	private Destination[] selectableDestinations;
	
	private Commandable createCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		
		availableDestinations = new LazyDataModel<Destination>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Destination> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				List<Destination> list = (List<Destination>) __inject__(DestinationController.class).read(new Properties().setIsPageable(Boolean.TRUE).setFrom(first)
						.setCount(pageSize));
				setRowCount(__inject__(DestinationController.class).count().intValue());
				System.out.println(
						"AdministrativeUnitFromDestinationCreatePage.__listenPostConstruct__().new LazyDataModel() {...}.load() : "+getRowCount()+" : "+list.size());
				return list;
			}
			
			@Override
			public Destination getRowData(String rowKey) {
				return __inject__(DestinationController.class).readByBusinessIdentifier(rowKey);
			}
			
		};
		
		CommandableBuilder createCommandableBuilder = __inject__(CommandableBuilder.class);
		createCommandableBuilder.setName("Créer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		//createCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),"outputPanel");
		createCommandable = createCommandableBuilder.execute().getOutput();
	}
	
	public void select() {
		System.out.println("AdministrativeUnitFromDestinationCreatePage.select() : "+__selectedDestinations__);
		if(CollectionHelper.isEmpty(__selectedDestinations__))
			return;
		if(selectedDestinations == null)
			selectedDestinations = new ArrayList<>();
		selectedDestinations = new ArrayList<>();
	}
	
	public void save() {
		administrativeUnit.getValue().setActivities(activities.getTarget());
		__inject__(AdministrativeUnitController.class).update(administrativeUnit.getValue(),new Properties().setFields(AdministrativeUnit.FIELD_ACTIVITIES));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Création d'unités administratives à partir des destinations";
	}
	
}
