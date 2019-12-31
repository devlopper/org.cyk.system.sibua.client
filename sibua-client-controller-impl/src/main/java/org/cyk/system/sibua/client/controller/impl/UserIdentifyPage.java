package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.User;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.primefaces.model.UploadedFile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserIdentifyPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private SelectionOne<Section> section;
	private SelectionOne<AdministrativeUnit> administrativeUnit;
	private Commandable createCommandable;
	private Collection<UploadedFileImpl> uploadedFiles;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		user = new User();
		try{
			section = new SelectionOne<Section>(Section.class);		
			section.setListener(new SelectionOne.Listener<Section>() {
				@Override
				public void processOnSelect(Section section) {
					administrativeUnit.select(null);
					if(section == null) {
						administrativeUnit.setChoices(null);
					}else {					
						administrativeUnit.setChoices(__inject__(AdministrativeUnitController.class)
								.read(new Properties().setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setIsPageable(Boolean.FALSE)));
					}				
				}
			});
			
			administrativeUnit = new SelectionOne<AdministrativeUnit>(AdministrativeUnit.class);
			administrativeUnit.setAreChoicesGettable(Boolean.FALSE);
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
		
		createCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),"outputPanel");
		createCommandable = createCommandableBuilder.execute().getOutput();
		createCommandable.getProperties().setAjax(Boolean.FALSE);
	}
	
	public void addFile() {
		if(uploadedFiles == null)
			uploadedFiles = new ArrayList<>();
		uploadedFiles.add(new UploadedFileImpl());
	}
	
	public void removeFile(UploadedFileImpl uploadedFile) {
		if(CollectionHelper.isEmpty(uploadedFiles))
			return;
		uploadedFiles.remove(uploadedFile);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Identification";
	}
	
	public void create() {
		System.out.println("UserIdentifyPage.create() FILES : "+uploadedFiles);
		if(CollectionHelper.isNotEmpty(uploadedFiles)) {
			for(UploadedFileImpl index : uploadedFiles) {
				System.out.println("UserIdentifyPage.create() ::: "+index.getUploadedFile().getFileName());
			}
		}
	}
	
	/**/
	
	@Getter @Setter @EqualsAndHashCode
	public static class UploadedFileImpl implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private UploadedFile uploadedFile;
	}
}
