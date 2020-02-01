package org.cyk.system.sibua.client.controller.impl.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.api.user.UserFileController;
import org.cyk.system.sibua.client.controller.entities.user.File;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.system.sibua.client.controller.entities.user.UserFile;
import org.cyk.system.sibua.server.persistence.api.user.UserFilePersistence;
import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.QueryAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserCreditManagerReadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private String severity,summary,detail;
	private UserFile administrativeUnitCertificateUserFile,photoUserFile;
	private String dialogAction,dialogTitle;
	private Commandable deleteCommandable,sendCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		user = __inject__(UserController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
		Collection<UserFile> userFiles = __inject__(UserFileController.class).read(new Properties().setQueryIdentifier(UserFilePersistence.READ_BY_USERS_IDENTIFIERS)
				.setFilters(new FilterDto().addField("user", List.of(user.getIdentifier()))));
		if(CollectionHelper.isNotEmpty(userFiles))
			for(UserFile index : userFiles)
				if(UserFileType.ADMINISTRATIVE_CERTIFICATE.equals(index.getType()))
					administrativeUnitCertificateUserFile = index;
				else if(UserFileType.PHOTO.equals(index.getType()))
					photoUserFile = index;
		summary = "Notification";
		if(StringHelper.isBlank(user.getSendingDate())) {
			severity = "warn";
			detail = "Veuillez transmettre votre fiche d'identification.";
		}else {
			severity = "info";
			detail = "Votre fiche d'identification est en cours de traitement.";
		}
		
		CommandableBuilder deleteCommandableBuilder = __inject__(CommandableBuilder.class);
		deleteCommandableBuilder.setName("Supprimer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					delete();
				}
			}
		);
		deleteCommandableBuilder.setIcon(Icon.REMOVE);
		deleteCommandable = deleteCommandableBuilder.execute().getOutput();
		
		CommandableBuilder sendCommandableBuilder = __inject__(CommandableBuilder.class);
		sendCommandableBuilder.setName("Transmettre").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					send();
				}
			}
		);
		sendCommandable = sendCommandableBuilder.execute().getOutput();
		sendCommandableBuilder.setIcon(Icon.SEND);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(user == null)
			return null;
		String string = ConstantEmpty.STRING;
		if(user.getCivility() != null)
			string = user.getCivility().getName()+" ";
		string += user.getFirstName();
		if(StringHelper.isNotBlank(user.getLastNames()))
			string += " "+user.getLastNames();
		return "Fiche d'identification de "+string;
	}
	
	public void openDialog(String action) {
		dialogAction = action;
		if("uploadAdministrativeUnitCertificateFile".equals(dialogAction)) {
			dialogTitle = "Acte de nomination";
		}else if("uploadPhotoFile".equals(dialogAction)) {
			dialogTitle = "Photo";
		}
	}
	
	public void listenAdministrativeUnitCertificateFileUpload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
	    byte[] bytes = uploadedFile.getContents();
	    if(bytes == null || bytes.length == 0)
	    	return;
	    if(administrativeUnitCertificateUserFile == null) {
	    	administrativeUnitCertificateUserFile = new UserFile();
	    	administrativeUnitCertificateUserFile.setUser(new User());
	    	administrativeUnitCertificateUserFile.getUser().setIdentifier(user.getIdentifier());
	    	administrativeUnitCertificateUserFile.setFile(new File());
	    	administrativeUnitCertificateUserFile.getFile().setBytes(bytes);
	    	administrativeUnitCertificateUserFile.getFile().setName(uploadedFile.getFileName());
	    	administrativeUnitCertificateUserFile.setType(UserFileType.ADMINISTRATIVE_CERTIFICATE);
	    	__inject__(UserFileController.class).create(administrativeUnitCertificateUserFile);
	    	PrimeFaces.current().ajax().update(":form:administrativeUnitCertificateFileOutputPanel");
	    	user.setAdministrativeCertificateUniformResourceIdentifier(String.format(user.getAdministrativeCertificateUniformResourceIdentifierFormat(), user.getIdentifier()));
	    }
	}
	
	public void deleteAdministrativeUnitCertificateFile() {
		if(administrativeUnitCertificateUserFile == null)
			return;
		__inject__(UserFileController.class).delete(administrativeUnitCertificateUserFile);
		administrativeUnitCertificateUserFile = null;
	}
	
	public void delete() {
		__inject__(UserController.class).delete(user);
		UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
		p.setRequest(__getRequest__());
		p.setPath(new PathAsFunctionParameter());
		p.getPath().setIdentifier("userCreditManagerEditView");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send() {
		__inject__(UserController.class).update(user,new Properties().setFields("sendingDate"));
		UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
		p.setRequest(__getRequest__());
		p.setPath(new PathAsFunctionParameter());
		p.getPath().setIdentifier("userNotifySendView");
		p.setQuery(new QueryAsFunctionParameter());
		p.getQuery().setValue("entityidentifier="+user.getIdentifier());
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
