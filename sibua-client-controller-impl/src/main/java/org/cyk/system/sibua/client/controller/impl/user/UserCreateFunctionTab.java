package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;

import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.user.File;
import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntity;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntityBuilder;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Deprecated
public class UserCreateFunctionTab extends AbstractObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private AutoCompleteEntity<AdministrativeUnit> administrativeUnits;	
	private AutoCompleteEntity<Localisation> localisations;	
	private AutoCompleteEntity<Section> sections;
	private AutoCompleteEntity<Activity> activities;
	private File file;
	private UploadedFile uploaded;
	private String fileReference;
	
	public UserCreateFunctionTab(String name,UserFileType userFileType) {
		this.name = name;
		//file=new File().setType(userFileType);
		administrativeUnits = AutoCompleteEntityBuilder.build(AdministrativeUnit.class);
		localisations = AutoCompleteEntityBuilder.build(Localisation.class);	
		sections = AutoCompleteEntityBuilder.build(Section.class);
		activities = AutoCompleteEntityBuilder.build(Activity.class);	
	}
}
