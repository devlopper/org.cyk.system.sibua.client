package org.cyk.system.sibua.client.controller.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.FunctionalClassificationController;
import org.cyk.system.sibua.client.controller.api.LocalisationController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.api.ServiceGroupController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.ActivityDestination;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Destination;
import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.utility.__kernel__.array.ArrayTwoDimensionString;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.file.microsoft.excel.SheetGetter;
import org.cyk.utility.__kernel__.file.microsoft.excel.SheetReader;
import org.cyk.utility.__kernel__.file.microsoft.excel.WorkBookGetter;
import org.cyk.utility.__kernel__.instance.InstanceGetter;
import org.cyk.utility.__kernel__.number.Interval;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitLoadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private Collection<AdministrativeUnit> administrativeUnits;
	private Commandable loadCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		section = new SelectionOne<Section>(Section.class);	
		try {
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				section.select(__inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section")));
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		CommandableBuilder loadCommandableBuilder = __inject__(CommandableBuilder.class);
		loadCommandableBuilder.setName("Charger").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					load();
				}
			}
		);
		loadCommandableBuilder.addUpdatables(__inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers(),"outputPanel");
		loadCommandable = loadCommandableBuilder.execute().getOutput();
	}
	
	public void listenUpload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
	    byte[] contents = uploadedFile.getContents();
	    if(contents == null || contents.length == 0)
	    	return;
	    Workbook workbook = (Workbook) WorkBookGetter.getInstance().get(new ByteArrayInputStream(contents));
		Sheet sheet =  (Sheet) SheetGetter.getInstance().get(workbook, 0);
		ArrayTwoDimensionString array = SheetReader.getInstance().read(workbook, sheet,__inject__(Interval.class).setLow(1),__inject__(Interval.class).setLow(0));
		String sectionCode = StringUtils.trimToNull(StringUtils.substringBetween(array.get(0, 0), "Section", ":"));
		if(administrativeUnits == null)
			administrativeUnits = new ArrayList<>();
		else
			administrativeUnits.clear();
		if(StringHelper.isNotBlank(sectionCode))
			this.section.select(__inject__(SectionController.class).readByBusinessIdentifier(sectionCode));	
		ServiceGroup serviceGroup = __inject__(ServiceGroupController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET);
		Localisation localisation = __inject__(LocalisationController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.Localisation.CODE_NOT_SET);
		FunctionalClassification functionalClassification = __inject__(FunctionalClassificationController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET);
		Integer rowIndex = 1;
		for(String[] arrayIndex : (String[][])array.getValue()) {
			if(rowIndex++ < 3)
				continue;			
			//build administrative unit
			String administrativeUnitName = arrayIndex[3];
			AdministrativeUnit administrativeUnit = __getAdministrativeUnitByName__(administrativeUnitName);
			if(administrativeUnit == null) {
				administrativeUnit = new AdministrativeUnit();
				administrativeUnit.setName(administrativeUnitName);
				administrativeUnit.setSection(section.getValue());
				administrativeUnit.setServiceGroup(serviceGroup);
				administrativeUnit.setFunctionalClassification(functionalClassification);
				administrativeUnit.setLocalisation(localisation);
				administrativeUnits.add(administrativeUnit);
			}
			//build activity and destination association			
			String activityCode = StringUtils.substring(arrayIndex[1], 0, 11);
			if(StringHelper.isNotBlank(activityCode)) {
				String destinationCode = StringUtils.substring(arrayIndex[2], 0, 9);
				if(StringHelper.isNotBlank(destinationCode)) {
					ActivityDestination activityDestination = new ActivityDestination();
					activityDestination.setActivity(__getActivityByCode__(activityCode));
					activityDestination.setDestination(__getDestinationByCode__(destinationCode));
					if(activityDestination.getActivity() != null && activityDestination.getDestination() != null)
						administrativeUnit.addActivityDestinations(activityDestination);
				}
			}
		}
	}
	
	private AdministrativeUnit __getAdministrativeUnitByName__(String name) {
		if(StringHelper.isBlank(name) && CollectionHelper.isNotEmpty(administrativeUnits))
			return CollectionHelper.getLast(administrativeUnits);
		for(AdministrativeUnit administrativeUnit : administrativeUnits)
			if(administrativeUnit.getName().equals(name))
				return administrativeUnit;
		return null;
	}
	
	private Activity __getActivityByCode__(String code) {
		if(StringHelper.isBlank(code))
			return null;
		if(CollectionHelper.isNotEmpty(administrativeUnits))
			for(AdministrativeUnit administrativeUnit : administrativeUnits)
				if(CollectionHelper.isNotEmpty(administrativeUnit.getActivityDestinations()))
					for(ActivityDestination activityDestination : administrativeUnit.getActivityDestinations())
						if(activityDestination.getActivity().getCode().equals(code))
							return activityDestination.getActivity();
		return InstanceGetter.getInstance().getByBusinessIdentifier(Activity.class, code);
	}
	
	private Destination __getDestinationByCode__(String code) {
		if(StringHelper.isBlank(code))
			return null;
		if(CollectionHelper.isNotEmpty(administrativeUnits))
			for(AdministrativeUnit administrativeUnit : administrativeUnits)
				if(CollectionHelper.isNotEmpty(administrativeUnit.getActivityDestinations()))
					for(ActivityDestination activityDestination : administrativeUnit.getActivityDestinations())
						if(activityDestination.getDestination().getCode().equals(code))
							return activityDestination.getDestination();
		return InstanceGetter.getInstance().getByBusinessIdentifier(Destination.class, code);
	}
	
	public void load() {
		if(CollectionHelper.isEmpty(administrativeUnits))
			return;
		administrativeUnits.forEach(new Consumer<AdministrativeUnit>() {
			@Override
			public void accept(AdministrativeUnit administrativeUnit) {
				administrativeUnit.setSection(section.getValue());
			}
		});
		__inject__(AdministrativeUnitController.class).createMany(administrativeUnits,new Properties().setIsBatchable(Boolean.TRUE).setBatchSize(1));
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Chargement des unités administratives";
	}
	
	/**/
	
}
