package org.cyk.system.sibua.client.controller.entities.user;

import java.io.Serializable;
import java.util.List;

import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class User extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private UserType type;
	private Civility civility;
	private AdministrativeUnit administrativeUnit;
	private String administrativeUnitCertificateReference;
	private String administrativeUnitFunction;
	
	private Function function;
	private String registrationNumber;
	private String firstName;
	private String lastNames;
	private String electronicMailAddress;
	private String mobilePhoneNumber;
	private String deskPhoneNumber;
	private String deskPost;
	private String postalAddress;
	
	private String creationDate;
	private String activationDate;
	private String validationDate;
	private String sendingDate;
	
	private String accessToken;
	private String reportUniformResourceIdentifier;
	private String administrativeCertificateUniformResourceIdentifier;
	
	private List<Section> sections;
	private List<AdministrativeUnit> administrativeUnits;
	private List<File> files;
	private List<UserFile> userFiles;
	private List<Localisation> localisations;
	private List<Activity> activities;
	private List<Function> functions;
	
	public static final String FIELD_ACTIVATION_DATE = "activationDate";
	public static final String FIELD_ACCESS_TOKEN = "accessToken";
	public static final String FIELD_ELECTRONIC_MAIL_ADDRESS = "electronicMailAddress";
	
}