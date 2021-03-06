package org.cyk.system.sibua.client.controller.entities.user;

import java.io.Serializable;
import java.util.Collection;

import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class Function extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private FunctionType type;
	private Collection<AdministrativeUnit> administrativeUnits;
	
	
	@Override
	public String toString() {
		return getCode()+" "+getName();
	}
		
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_ADMINISTRATIVE_UNITS = "administrativeUnits";
}