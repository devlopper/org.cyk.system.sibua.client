package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;

import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class Destination extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private Title title;
	private Section section;
	private Program program;
	private Action action;
	private Activity activity;
	private AdministrativeUnit administrativeUnit;
	
	@Override
	public String toString() {
		return getCode()+" "+getName();
	}

	public static final String FIELD_SECTION = "section";
	public static final String FIELD_PROGRAM = "program";
	public static final String FIELD_ACTION = "action";
	public static final String FIELD_ACTIVITY = "activity";
	public static final String FIELD_ADMINISTRATIVE_UNIT = "administrativeUnit";
	
}