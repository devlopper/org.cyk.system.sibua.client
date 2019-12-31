package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class User extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull private Function function;
	@NotNull private String registrationNumber;
	@NotNull private String firstName;
	@NotNull private String lastNames;
	@NotNull private String electronicMailAddress;
	@NotNull private String phoneNumber;
	private AdministrativeUnit administrativeUnit;
	private List<Section> sectionsManaged;
	private List<AdministrativeUnit> administrativeUnitsManaged;
	private List<UserFile> userFiles;
		
}