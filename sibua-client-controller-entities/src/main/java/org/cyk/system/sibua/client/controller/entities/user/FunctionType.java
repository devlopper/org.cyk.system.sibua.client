package org.cyk.system.sibua.client.controller.entities.user;

import java.io.Serializable;

import org.cyk.system.sibua.server.persistence.entities.user.FunctionCategory;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class FunctionType extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private FunctionCategory category;
	
	@Override
	public String toString() {
		return getCode()+" "+getName();
	}
	
	public static final String FIELD_ADMINISTRATIVE_UNIT = "administrativeUnit";
}