package org.cyk.system.sibua.client.controller.impl.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.api.user.UserFileController;
import org.cyk.system.sibua.client.controller.api.user.UserFunctionController;
import org.cyk.system.sibua.client.controller.entities.user.File;
import org.cyk.system.sibua.client.controller.entities.user.Function;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.system.sibua.client.controller.entities.user.UserFile;
import org.cyk.system.sibua.client.controller.entities.user.UserFunction;
import org.cyk.system.sibua.server.persistence.api.user.UserFilePersistence;
import org.cyk.system.sibua.server.persistence.api.user.UserFunctionPersistence;
import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.QueryAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AbstractActorReadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private String severity,summary,detail;
	private UserFile administrativeUnitCertificateUserFile,photoUserFile;
	private String dialogAction,dialogTitle;
	private Commandable deleteCommandable,sendCommandable;
	private CommandButton deleteCommandButton,sendCommandButton;
	private DataTable functionsDataTable;
	private Layout functionsDataTableBodyLayout;
	private AutoComplete functionAutoComplete;
	
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
		
		user.setUserFunctions((List<UserFunction>) __inject__(UserFunctionController.class).read(new Properties().setQueryIdentifier(UserFunctionPersistence.READ_BY_USERS_IDENTIFIERS)
				.setFilters(new FilterDto().addField("user", List.of(user.getIdentifier())))));
		
		summary = "Notification";
		if(StringHelper.isBlank(user.getSendingDate())) {
			severity = "warn";
			detail = "Veuillez transmettre votre fiche d'identification.";
		}else {
			severity = "info";
			//detail = "Votre fiche d'identification sera traitée lorsqu'elle sera validée par votre ordonnateur au vu de vos specimens de signature.";
			if(CollectionHelper.isEmpty(user.getUserFunctions())) {
				
			}else {
				Collection<String> codes = user.getUserFunctions().stream().map(userFunction -> userFunction.getFunction().getType().getCategory().getCode()).collect(Collectors.toList());
				if(codes.contains("1") || codes.contains("2")) {
					detail = "Votre fiche d'identification est en cours de traitement.";
				}else {
					detail = "Votre fiche d'identification sera traitée lorsqu'elle sera validée par votre ordonnateur au vu de vos specimens de signature.";
				}
			}
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
		sendCommandableBuilder.setIcon(Icon.SEND);
		sendCommandable = sendCommandableBuilder.execute().getOutput();
		
		deleteCommandButton = Builder.build(CommandButton.class,Map.of("value","Supprimer")).setIcon(Icon.REMOVE);
		deleteCommandButton.getConfirm().setDisabled(Boolean.FALSE);
		deleteCommandButton.setListener(new CommandButton.Listener() {
			@Override
			public void listenAction(Object argument) {
				delete();
			}
		});
		
		sendCommandButton = Builder.build(CommandButton.class,Map.of("value","Transmettre")).setIcon(Icon.SEND);
		sendCommandButton.getConfirm().setDisabled(Boolean.FALSE);
		sendCommandButton.setListener(new CommandButton.Listener() {
			@Override
			public void listenAction(Object argument) {
				send();
			}
		});
		
		functionAutoComplete = Builder.build(AutoComplete.class,Map.of(AutoComplete.FIELD_ENTITY_CLASS,Function.class));
		
		Map<Object,Object> map = new HashMap<>();
		map.put(DataTable.ConfiguratorImpl.FIELD_ENTIY_CLASS,Function.class);
		if(CollectionHelper.isNotEmpty(user.getFunctions()))
			map.put(DataTable.FIELD_VALUE,user.getFunctions());
		functionsDataTable = Builder.build(DataTable.class,map);		
		functionsDataTable.addColumnsAfterRowIndex(Builder.build(Column.class, Map.of(Column.FIELD_FIELD_NAME,Function.FIELD_NAME)));
		functionsDataTable.setAreColumnsChoosable(Boolean.FALSE);		
		functionsDataTable.setIsExportable(Boolean.FALSE);
		
		functionsDataTable.addHeaderToolbarLeftCommands(
				Builder.build(CommandButton.class,Map.of(CommandButton.FIELD_VALUE,"Ajouter des fonctions budgétaires",CommandButton.FIELD_ICON,"fa fa-plus"
						,CommandButton.ConfiguratorImpl.FIELD_DATA_TABLE,functionsDataTable
						,CommandButton.FIELD_LISTENER,new AbstractCollection.AbstractActionListenerImpl(functionsDataTable) {
					@Override
					protected void __showDialog__() {
						functionsDataTable.getDialog().setHeader("Ajout de fonctions budgétaires");
						super.__showDialog__();
					}
				}.setMinimumSelectionSize(0).setIsSelectionShowable(Boolean.FALSE)))
			);
		
		functionsDataTable.setListener(new AbstractDataTable.Listener() {
			
			@Override
			public String listenGetStyleClassByRecord(Object record,Integer recordIndex) {
				/*if(record instanceof Function) {
					return "cyk-default";
				}*/					
				return null;
			}
			
			@Override
			public String listenGetStyleClassByRecordByColumn(Object record,Integer recordIndex,Column column,Integer columnIndex) {
				return null;
			}
		});
		
		functionsDataTableBodyLayout = Builder.build(Layout.class,Map.of(Layout.FIELD_IDENTIFIER,"layoutTwoColumns",Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.FIELD_NUMBER_OF_COLUMNS,2
				,Layout.FIELD_ROW_CELL_MODEL,Map.of(0,new Cell().setWidth(3),1,new Cell().setWidth(9))
				,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,CollectionHelper.listOf(
					MapHelper.instantiate(Cell.FIELD_IDENTIFIER,"type_label"),MapHelper.instantiate(Cell.FIELD_IDENTIFIER,"type_input")
					,MapHelper.instantiate(Cell.FIELD_IDENTIFIER,"cat_label"),MapHelper.instantiate(Cell.FIELD_IDENTIFIER,"cat_input")
					,MapHelper.instantiate(Cell.FIELD_IDENTIFIER,"func_label"),MapHelper.instantiate(Cell.FIELD_IDENTIFIER,"func_input")
					)));
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
	
	public void listenPhotoFileUpload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
	    byte[] bytes = uploadedFile.getContents();
	    if(bytes == null || bytes.length == 0)
	    	return;
	    if(photoUserFile == null) {
	    	photoUserFile = new UserFile();
	    	photoUserFile.setUser(new User());
	    	photoUserFile.getUser().setIdentifier(user.getIdentifier());
	    	photoUserFile.setFile(new File());
	    	photoUserFile.getFile().setBytes(bytes);
	    	photoUserFile.getFile().setName(uploadedFile.getFileName());
	    	photoUserFile.setType(UserFileType.PHOTO);
	    	__inject__(UserFileController.class).create(photoUserFile);
	    	PrimeFaces.current().ajax().update(":form:photoFileOutputPanel");
	    	user.setPhotoUniformResourceIdentifier(String.format(user.getPhotoUniformResourceIdentifierFormat(), user.getIdentifier()));
	    }
	}
	
	public void deleteAdministrativeUnitCertificateFile() {
		if(administrativeUnitCertificateUserFile == null)
			return;
		__inject__(UserFileController.class).delete(administrativeUnitCertificateUserFile);
		administrativeUnitCertificateUserFile = null;
	}
	
	public void deletePhotoFile() {
		if(photoUserFile == null)
			return;
		__inject__(UserFileController.class).delete(photoUserFile);
		photoUserFile = null;
	}
	
	public void delete() {
		__inject__(UserController.class).delete(user);
		UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
		p.setRequest(__getRequest__());
		p.setPath(new PathAsFunctionParameter());
		p.getPath().setIdentifier("userEditIndexView");
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
