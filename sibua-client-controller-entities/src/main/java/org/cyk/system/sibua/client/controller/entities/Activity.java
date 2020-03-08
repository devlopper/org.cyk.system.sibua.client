package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;
import java.util.List;

import org.cyk.system.sibua.client.controller.entities.user.FunctionType;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class Activity extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private FunctionType functionType;
	private Section section;
	private AdministrativeUnit administrativeUnit;
	private AdministrativeUnit administrativeUnitGestionnaire;
	private AdministrativeUnit administrativeUnitBeneficiaire;
	private String natDepCode;
	private String catUsbCode;
	private String catAtvCode;
	private Integer numberOfCostUnits;
	private Boolean isGestionnaire;
	private Boolean isBeneficiaire;
	
	private Program program;
	private Action action;
	private List<Destination> destinations;
	private List<CostUnit> costUnits;
	private Destination destination;
	
	@Override
	public Activity setCode(String code) {
		return (Activity) super.setCode(code);
	}
	
	@Override
	public Activity setName(String name) {
		return (Activity) super.setName(name);
	}
	
	@Override
	public String toString() {
		return getCode()+" "+getName();
	}
	
	public static final String FIELD_FUNCTION_TYPE = "functionType";
	public static final String FIELD_SECTION = "section";
	public static final String FIELD_ADMINISTRATIVE_UNIT = "administrativeUnit";
	public static final String FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE = "administrativeUnitGestionnaire";
	public static final String FIELD_ADMINISTRATIVE_UNIT_BENEFICIAIRE = "administrativeUnitBeneficiaire";
	public static final String FIELD_ADMINISTRATIVE_UNIT_GESTIONNAIRE_OR_BENEFICIAIRE = "administrativeUnitGestionnaireOrBeneficiaire";
	public static final String FIELD_PROGRAM = "program";
	public static final String FIELD_ACTION = "action";
	public static final String FIELD_DESTINATIONS = "destinations";
	public static final String FIELD_DESTINATION = "destination";
	public static final String FIELD_COST_UNITS = "costUnits";
	public static final String FIELD_NUMBER_OF_COST_UNITS = "numberOfCostUnits";
	public static final String FIELD_IS_GESTIONNAIRE = "isGestionnaire";
	public static final String FIELD_IS_BENEFICIAIRE = "isBeneficiaire";
	
	public static final String FIELD_NAT_DEP_CODE = "natDepCode";
	public static final String FIELD_CAT_USB_CODE = "catUsbCode";
	public static final String FIELD_CAT_ATV_CODE = "catAtvCode";
	
}