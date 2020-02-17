package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;

import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class AdministrativeUnitActivity extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AdministrativeUnit administrativeUnit;
	private AdministrativeUnit administrativeUnitBeneficiaire;
	private Activity activity;
	
	public AdministrativeUnitActivity(AdministrativeUnit administrativeUnit,Activity activity) {
		this.administrativeUnit = administrativeUnit;
		this.activity = activity;
	}
	
	/**/

	public static final String FIELD_ADMINISTRATIVE_UNIT = "administrativeUnit";
	public static final String FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE = "administrativeUnitBeneficiaire";
	public static final String FIELD_ACTIVITY = "activity";
	
}