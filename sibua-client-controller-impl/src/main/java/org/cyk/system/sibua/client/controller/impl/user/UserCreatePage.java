package org.cyk.system.sibua.client.controller.impl.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.user.Civility;
import org.cyk.system.sibua.client.controller.entities.user.File;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.system.sibua.client.controller.entities.user.UserType;
import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.QueryAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntity;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AutoCompleteEntityBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserCreatePage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private SelectionOne<Civility> civility;
	private SelectionOne<UserType> userType;
	private AutoCompleteEntity<AdministrativeUnit> administrativeUnit;	
	private Collection<String> selectedFunctionCategoryCodes = new ArrayList<>();
	
	private AutoCompleteEntity<Section> section;
	
	private List<UserCreateFunctionTab> functionTabs = new ArrayList<>();
	
	private File administrativeUnitCertificateFile
			,budgetaryCertificateFile;
	private UploadedFile administrativeUnitCertificateUploadedFile,budgetaryCertificateUploadedFile;
	private UploadedFile signatureFile;
	
	private Commandable createCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		user = new User();
		try {
			section = AutoCompleteEntityBuilder.build(Section.class);
			administrativeUnit = AutoCompleteEntityBuilder.build(AdministrativeUnit.class);		
			civility = new SelectionOne<Civility>(Civility.class);
			userType = new SelectionOne<UserType>(UserType.class);
			
			functionTabs.add(new UserCreateFunctionTab("Contrôlleur Financier", UserFileType.BUDGETARY_CERTIFICATE));
			
			functionTabs.add(new UserCreateFunctionTab("Ordonnateur", UserFileType.BUDGETARY_CERTIFICATE));
			functionTabs.add(new UserCreateFunctionTab("Gestionnaire de Crédits", UserFileType.BUDGETARY_CERTIFICATE));
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		CommandableBuilder createCommandableBuilder = __inject__(CommandableBuilder.class);
		createCommandableBuilder.setName("Enregistrer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					create();
				}
			}
		);
		
		createCommandable = createCommandableBuilder.execute().getOutput();
		createCommandable.getProperties().setAjax(Boolean.FALSE);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Création d'une fiche d'identification";
	}
	
	public void create() {
		user.setFiles(null);
		addFile(administrativeUnitCertificateUploadedFile, administrativeUnitCertificateFile);
		addFile(budgetaryCertificateUploadedFile, budgetaryCertificateFile);
		__inject__(UserController.class).create(user);
		
		UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
		p.setRequest(__getRequest__());
		p.setPath(new PathAsFunctionParameter());
		p.getPath().setIdentifier("userNotifyView");
		p.setQuery(new QueryAsFunctionParameter());
		p.getQuery().setValue("entityidentifier="+user.getIdentifier());
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addFile(UploadedFile uploadedFile,File file) {
		if(uploadedFile == null)
			return;
		if(user.getFiles() == null)
			user.setFiles(new ArrayList<>());
		//user.getFiles().add(file.setBytes(uploadedFile.getContents()).setExtension(FileHelper.getExtension(uploadedFile.getFileName())));		
	}
	
	public Boolean isHasFunctionCategoryCode(String selectedFunctionCategoryCode) {
		return CollectionHelper.contains(selectedFunctionCategoryCodes, selectedFunctionCategoryCode);
	}
}
