package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;
import java.util.List;

import org.cyk.utility.client.controller.component.annotation.Input;
import org.cyk.utility.client.controller.component.annotation.InputChoice;
import org.cyk.utility.client.controller.component.annotation.InputChoiceOne;
import org.cyk.utility.client.controller.component.annotation.InputChoiceOneCombo;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class AdministrativeUnit extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Input @InputChoice @InputChoiceOne @InputChoiceOneCombo
	private Section section;
	
	private ServiceGroup serviceGroup;
	private FunctionalClassification functionalClassification;
	private Localisation localisation;
	private Integer orderNumber;
	private AdministrativeUnit parent;
	
	private List<AdministrativeUnit> parents;
	private List<AdministrativeUnit> children;
	private List<Activity> activities;
	private List<Destination> destinations;

	@Override
	public String toString() {
		return getCode()+" "+getName();
	}
	
	public static final String FIELD_PARENT = "parent";
	public static final String FIELD_SECTION = "section";
	public static final String FIELD_SERVICE_GROUP = "serviceGroup";
	public static final String FIELD_FUNCTIONAL_CLASSIFICATION = "functionalClassification";
	public static final String FIELD_LOCALISATION = "localisation";
	public static final String FIELD_ACTIVITIES = "activities";
	public static final String FIELD_PARENTS = "parents";
	public static final String FIELD_CHILDREN = "children";
	public static final String FIELD_DESTINATIONS = "destinations";
}