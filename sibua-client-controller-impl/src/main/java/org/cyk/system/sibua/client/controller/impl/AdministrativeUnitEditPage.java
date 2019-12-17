package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AdministrativeUnit administrativeUnit;
	private String name;
	private SelectionOne<Section> section;
	private Section __section__;
	private SelectionOne<ServiceGroup> serviceGroup;
	private SelectionOne<FunctionalClassification> functionalClassification;
	private SelectionOne<Localisation> localisation;
	
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try{
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				__section__ = __inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section"));
			}
		
			if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
				administrativeUnit = new AdministrativeUnit();	
				administrativeUnit.setSection(__section__);
			}else {
				administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
			}
			
			name = administrativeUnit.getName();
			section = new SelectionOne<Section>(Section.class);		
			section.setValue(administrativeUnit.getSection());
			serviceGroup = new SelectionOne<ServiceGroup>(ServiceGroup.class);
			serviceGroup.setValue(administrativeUnit.getServiceGroup());
			functionalClassification = new SelectionOne<FunctionalClassification>(FunctionalClassification.class);
			functionalClassification.setValue(administrativeUnit.getFunctionalClassification());
			localisation = new SelectionOne<Localisation>(Localisation.class);	
			localisation.setValue(administrativeUnit.getLocalisation());
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
		System.out.println("AdministrativeUnitEditPage.save() : "+section.getValue());
		if(StringHelper.isBlank(administrativeUnit.getIdentifier())) {
			__inject__(AdministrativeUnitController.class).create(administrativeUnit);
		}else {
			__inject__(AdministrativeUnitController.class).update(administrativeUnit,new Properties().setFields(AdministrativeUnit.FIELD_SECTION
					+","+AdministrativeUnit.FIELD_FUNCTIONAL_CLASSIFICATION+","+AdministrativeUnit.FIELD_LOCALISATION+","+AdministrativeUnit.FIELD_SERVICE_GROUP
					+","+AdministrativeUnit.FIELD_NAME));	
		}
	}
	
}
