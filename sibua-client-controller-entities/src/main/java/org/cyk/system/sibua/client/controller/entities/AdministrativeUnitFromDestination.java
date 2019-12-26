package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;

import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class AdministrativeUnitFromDestination extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private Destination destination;
	private Section section;
	private ServiceGroup serviceGroup;
	private FunctionalClassification functionalClassification;
	private Localisation localisation;
	private String __name__;
	
	public AdministrativeUnitFromDestination(Destination destination) {
		this.destination = destination;
	}
	
	@Override
	public String toString() {
		return getCode()+" "+getName();
	}
	
	public static final String FIELD_DESTINATION = "destination";
	public static final String FIELD_SECTION = "section";
	public static final String FIELD_SERVICE_GROUP = "serviceGroup";
	public static final String FIELD_FUNCTIONAL_CLASSIFICATION = "functionalClassification";
	public static final String FIELD_LOCALISATION = "localisation";
}