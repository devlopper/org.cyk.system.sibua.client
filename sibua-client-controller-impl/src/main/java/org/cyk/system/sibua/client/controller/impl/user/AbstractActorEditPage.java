package org.cyk.system.sibua.client.controller.impl.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.api.user.UserFileController;
import org.cyk.system.sibua.client.controller.api.user.UserProgramController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.user.Civility;
import org.cyk.system.sibua.client.controller.entities.user.File;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.system.sibua.client.controller.entities.user.UserProgram;
import org.cyk.system.sibua.client.controller.entities.user.UserType;
import org.cyk.system.sibua.server.persistence.api.user.UserProgramPersistence;
import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.file.FileHelper;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.QueryAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.omnifaces.util.Faces;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractActorEditPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private SelectionOne<Civility> civility;
	private SelectionOne<UserType> userType;
	private AutoComplete administrativeUnitAutoComplete;	
	private AutoComplete programAutoComplete;	
	private Collection<String> selectedFunctionCategoryCodes = new ArrayList<>();
	private Date administrativeUnitCertificateSignedDate;
	
	private List<UserCreateFunctionTab> functionTabs = new ArrayList<>();
	
	private File administrativeUnitCertificateFile/*,budgetaryCertificateFile=new File().setType(UserFileType.BUDGETARY_CERTIFICATE)*/;
	private UploadedFile administrativeUnitCertificateUploadedFile,budgetaryCertificateUploadedFile;
	private UploadedFile signatureFile;
	private String action;
	private Commandable saveCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		action = Faces.getRequestParameter("actionidentifier");
		super.__listenPostConstruct__();
		try {
			administrativeUnitAutoComplete = Builder.build(AutoComplete.class,Map.of(AutoComplete.FIELD_ENTITY_CLASS,AdministrativeUnit.class));
			programAutoComplete = Builder.build(AutoComplete.class,Map.of(AutoComplete.FIELD_ENTITY_CLASS,Program.class));
			civility = new SelectionOne<Civility>(Civility.class);
			civility.setMessage("Civilité");
			userType = new SelectionOne<UserType>(UserType.class);
			userType.setMessage("Je suis un");			
			functionTabs.add(new UserCreateFunctionTab("Contrôlleur Financier", UserFileType.BUDGETARY_CERTIFICATE));
			functionTabs.add(new UserCreateFunctionTab("Ordonnateur", UserFileType.BUDGETARY_CERTIFICATE));
			functionTabs.add(new UserCreateFunctionTab("Gestionnaire de Crédits", UserFileType.BUDGETARY_CERTIFICATE));
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
			user = new User();	
		}else {
			user = __inject__(UserController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
			civility.select(user.getCivility());
			userType.select(user.getType());
			if(user.getAdministrativeUnitCertificateSignedDateTimestamp() != null)
				administrativeUnitCertificateSignedDate = new Date(user.getAdministrativeUnitCertificateSignedDateTimestamp());
			administrativeUnitAutoComplete.setValue(user.getAdministrativeUnit());
			
			Collection<UserProgram> userPrograms = __inject__(UserProgramController.class).read(new Properties().setQueryIdentifier(UserProgramPersistence.READ_BY_USERS_IDENTIFIERS)
					.setFilters(new FilterDto().addField("user", List.of(user.getIdentifier()))));
			if(CollectionHelper.isNotEmpty(userPrograms)) {
				programAutoComplete.setValue(CollectionHelper.getFirst(userPrograms).getProgram());
			}
			
			if(CollectionHelper.isNotEmpty(user.getUserFiles())) {
				administrativeUnitCertificateFile = user.getUserFiles().get(0).getFile();
				if(administrativeUnitCertificateFile != null)
					administrativeUnitCertificateFile.setReference(user.getUserFiles().get(0).getReference());
			}
		}
		if(administrativeUnitCertificateFile == null) {
			administrativeUnitCertificateFile = new File();
			administrativeUnitCertificateFile.setType(UserFileType.ADMINISTRATIVE_CERTIFICATE);
		}
		
		CommandableBuilder saveCommandableBuilder = __inject__(CommandableBuilder.class);
		saveCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					save();
				}
			}
		);
		saveCommandableBuilder.addUpdatables(":form:outputPanel");
		saveCommandable = saveCommandableBuilder.execute().getOutput();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		String suffix = null;
		if(Boolean.TRUE.equals(getIsProgramManager()))
			suffix = "de responsable de programme";
		else if(Boolean.TRUE.equals(getIsFinancialController()))
			suffix = "de contrôlleur financier";
		else if(Boolean.TRUE.equals(getIsOrganizer()))
			suffix = "d'ordonnateur";
		else
			suffix = "de gestionnaire de crédits";
		if(StringHelper.isBlank(action))
			return "Création d'une fiche d'identification "+suffix;
		String string = ConstantEmpty.STRING;
		if(user.getCivility() != null)
			string = user.getCivility().getName()+" ";
		string += user.getFirstName();
		if(StringHelper.isNotBlank(user.getLastNames()))
			string += " "+user.getLastNames();
		if("update".equals(action))
			return "Modification de la fiche d'identification "+suffix+" de "+string;
		if("delete".equals(action))
			return "Suppression de la fiche d'identification "+suffix+" de "+string;
		if("send".equals(action))
			return "Transmission de la fiche d'identification "+suffix+" de "+string;
		return null;
	}
	
	public void save() {
		user.setType(userType.getValue());
		user.setCivility(civility.getValue());
		user.setAdministrativeUnit((AdministrativeUnit) administrativeUnitAutoComplete.getValue());
		if(programAutoComplete.getValue() == null)
			user.setPrograms(null);
		else
			user.setPrograms(CollectionHelper.listOf((Program)programAutoComplete.getValue()));
		user.setFiles(null);
		addFile(administrativeUnitCertificateUploadedFile, administrativeUnitCertificateFile);
		//addFile(budgetaryCertificateUploadedFile, budgetaryCertificateFile);
		if(StringHelper.isBlank(action)) {
			__inject__(UserController.class).create(user);
			
			UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
			p.setRequest(__getRequest__());
			p.setPath(new PathAsFunctionParameter());
			p.getPath().setIdentifier("userOpenView");
			p.setQuery(new QueryAsFunctionParameter());
			p.getQuery().setValue("entityidentifier="+user.getIdentifier());
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if("update".equals(action)) {
			if(administrativeUnitCertificateSignedDate == null) {
				//user.setAdministrativeUnitCertificateSignedDate(null);
				user.setAdministrativeUnitCertificateSignedDateTimestamp(null);
			}else {
				//user.setAdministrativeUnitCertificateSignedDate(LocalDateTime.ofInstant(administrativeUnitCertificateSignedDate.toInstant(),ZoneOffset.UTC));
				user.setAdministrativeUnitCertificateSignedDateTimestamp(administrativeUnitCertificateSignedDate.getTime());
			}
			__inject__(UserController.class).update(user,new Properties().setFields("type,civility,administrativeUnit,administrativeUnitFunction"
					+ ",administrativeUnitCertificateReference,administrativeUnitCertificateSignedBy,administrativeUnitCertificateSignedDateTimestamp,registrationNumber"
					+ ",firstName,lastNames,electronicMailAddress,mobilePhoneNumber,deskPhoneNumber,deskPost,postalAddress,programs"));
			
			UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
			p.setRequest(__getRequest__());
			p.setPath(new PathAsFunctionParameter());
			p.getPath().setIdentifier(Boolean.TRUE.equals(getIsProgramManager()) ? "userProgramManagerReadView" : "userCreditManagerReadView");
			p.setQuery(new QueryAsFunctionParameter());
			p.getQuery().setValue("entityidentifier="+user.getIdentifier());
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void addFile(UploadedFile uploadedFile,File file) {
		if(uploadedFile == null || uploadedFile.getContents().length == 0)
			return;
		if(user.getFiles() == null)
			user.setFiles(new ArrayList<>());
		file.setBytes(uploadedFile.getContents());
		file.setExtension(FileHelper.getExtension(uploadedFile.getFileName()));
		user.getFiles().add(file);
	}
	
	public void removeAdministrativeCertificateFile() {
		if(CollectionHelper.isNotEmpty(user.getUserFiles()))
			__inject__(UserFileController.class).delete(user.getUserFiles().get(0));
	}
	
	public Boolean isHasFunctionCategoryCode(String selectedFunctionCategoryCode) {
		return CollectionHelper.contains(selectedFunctionCategoryCodes, selectedFunctionCategoryCode);
	}
	
	public Boolean getIsProgramManager() {
		return null;
	}
	
	public Boolean getIsFinancialController() {
		return null;
	}
	
	public Boolean getIsOrganizer() {
		return null;
	}
}
