package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;

import org.cyk.system.sibua.client.controller.entities.user.FunctionType;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @NoArgsConstructor @Accessors(chain=true)
public class AdministrativeUnitFunctionType extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AdministrativeUnit administrativeUnit;
	private FunctionType functionType;
	
	/**/
	
	public static final String FIELD_ADMINISTRATIVE_UNIT = "administrativeUnit";
	public static final String FIELD_FUNCTION_TYPE = "functionType";
}