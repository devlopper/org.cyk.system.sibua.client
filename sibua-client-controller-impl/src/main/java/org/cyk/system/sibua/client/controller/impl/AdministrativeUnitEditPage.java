package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.DestinationController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Destination;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.system.sibua.server.persistence.api.DestinationPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.model.DualListModel;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AdministrativeUnit administrativeUnit;
	private String name;
	private SelectionOne<AdministrativeUnit> parent;
	private SelectionOne<Section> section;
	private Section __section__;
	private SelectionOne<ServiceGroup> serviceGroup;
	private SelectionOne<FunctionalClassification> functionalClassification;
	private SelectionOne<Localisation> localisation;
	private DualListModel<Destination> destinations;	
	
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		destinations = new DualListModel<>();
		try{
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				__section__ = __inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section"));
			}
		
			if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
				administrativeUnit = new AdministrativeUnit();	
				administrativeUnit.setSection(__section__);
			}else {
				administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"),
						new Properties().setFields(AdministrativeUnit.FIELD_IDENTIFIER+","+AdministrativeUnit.FIELD_CODE+","+AdministrativeUnit.FIELD_NAME
								+","+AdministrativeUnit.FIELD_SECTION+","+AdministrativeUnit.FIELD_PARENT+","+AdministrativeUnit.FIELD_SERVICE_GROUP+","
								+AdministrativeUnit.FIELD_LOCALISATION+","+AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION));
				destinations.setTarget((List<Destination>) __inject__(DestinationController.class)
						.read(new Properties().setFilters(new FilterDto().addField(Destination.FIELD_ADMINISTRATIVE_UNIT, CollectionHelper.listOf(administrativeUnit.getCode())))
								.setIsPageable(Boolean.FALSE)));	
				if(destinations.getTarget() == null)
					destinations.setTarget(new ArrayList<>());
			}
			
			name = administrativeUnit.getName();
			parent = new SelectionOne<AdministrativeUnit>(AdministrativeUnit.class);		
			parent.setValue(administrativeUnit.getParent());
			parent.setAreChoicesGettable(Boolean.FALSE);
			
			section = new SelectionOne<Section>(Section.class);		
			section.setListener(new SelectionOne.Listener<Section>() {
				@Override
				public void processOnSelect(Section section) {
					if(section == null) {
						parent.setValue(null);
						destinations.setSource(new ArrayList<Destination>());
					}else {					
						parent.setChoices(__inject__(AdministrativeUnitController.class)
								.read(new Properties().setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));
						
						destinations.setSource((List<Destination>) __inject__(DestinationController.class)
								.read(new Properties().setQueryIdentifier(DestinationPersistence.READ_WHERE_ADMINISTRATIVE_UNIT_DOES_NOT_EXIST_BY_SECTIONS_CODES)
										.setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));
						if(destinations.getSource() == null)
							destinations.setSource(new ArrayList<>());
					}				
				}
			});
			section.select(administrativeUnit.getSection());
			
			serviceGroup = new SelectionOne<ServiceGroup>(ServiceGroup.class);
			serviceGroup.setValue(administrativeUnit.getServiceGroup());
			functionalClassification = new SelectionOne<FunctionalClassification>(FunctionalClassification.class);
			functionalClassification.setValue(administrativeUnit.getFunctionalClassification());
			localisation = new SelectionOne<Localisation>(Localisation.class);	
			localisation.setValue(administrativeUnit.getLocalisation());
			parent.setValue(administrativeUnit.getParent());
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		CommandableBuilder saveCommandableBuilder = __inject__(CommandableBuilder.class);
		saveCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		
		saveCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),"outputPanel");
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return StringHelper.isBlank(administrativeUnit.getIdentifier()) ? "Création d'unité administrative" : "Modification d'unité administrative";
	}
	
	public void save() {
		administrativeUnit.setName(name);
		administrativeUnit.setServiceGroup(serviceGroup.getValue());
		administrativeUnit.setFunctionalClassification(functionalClassification.getValue());
		administrativeUnit.setLocalisation(localisation.getValue());
		administrativeUnit.setSection(section.getValue());
		administrativeUnit.setDestinations(destinations.getTarget());
		administrativeUnit.setParent(parent.getValue());
		if(StringHelper.isBlank(administrativeUnit.getIdentifier())) {
			__inject__(AdministrativeUnitController.class).create(administrativeUnit);
		}else {
			__inject__(AdministrativeUnitController.class).update(administrativeUnit,new Properties().setFields(AdministrativeUnit.FIELD_SECTION
					+","+AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION+","+AdministrativeUnit.FIELD_LOCALISATION+","+AdministrativeUnit.FIELD_SERVICE_GROUP
					+","+AdministrativeUnit.FIELD_NAME+","+AdministrativeUnit.FIELD_DESTINATIONS));	
		}
	}
	
}
