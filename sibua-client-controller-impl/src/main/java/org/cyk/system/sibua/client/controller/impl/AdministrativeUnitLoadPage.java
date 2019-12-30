package org.cyk.system.sibua.client.controller.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.api.DestinationController;
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
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitPersistence;
import org.cyk.utility.__kernel__.array.ArrayTwoDimensionString;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.file.microsoft.excel.SheetGetter;
import org.cyk.utility.__kernel__.file.microsoft.excel.SheetReader;
import org.cyk.utility.__kernel__.file.microsoft.excel.WorkBookGetter;
import org.cyk.utility.__kernel__.number.Interval;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.message.MessageRender;
import org.cyk.utility.client.controller.message.MessageRenderTypeDialog;
import org.cyk.utility.client.controller.message.MessageRenderTypeInline;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.notification.NotificationBuilder;
import org.cyk.utility.notification.NotificationSeverityWarning;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitLoadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private List<AdministrativeUnit> administrativeUnits;
	private LazyDataModel<AdministrativeUnit> administrativeUnitsDataModel;
	private Map<String,Activity> activities;
	private Map<String,Destination> destinations;
	private Collection<String> unknownActivities,unknownDestinations;
	private Commandable loadCommandable;
	private Integer numberOfExistingAdministrativeUnits,numberOfActivityDestinations;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		section = new SelectionOne<Section>(Section.class);	
		try {
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				section.select(__inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section")));
			}
			activities = __inject__(ActivityController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(Activity::getCode, x-> x));
			destinations = __inject__(DestinationController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(Destination::getCode, x->x));
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
		
		administrativeUnitsDataModel = new LazyDataModel<AdministrativeUnit>() {
			private static final long serialVersionUID = 1L;
			@Override
			public List<AdministrativeUnit> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
				Integer size = CollectionHelper.getSize(administrativeUnits);
				if(size == 0)
					return null;
				Integer end = first + pageSize;
				if(end > size)
					end = size;
				return (List<AdministrativeUnit>) CollectionHelper.getElementsFromTo(administrativeUnits, first, end);
			}
		};
		
	}
	
	public void listenUpload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
	    byte[] contents = uploadedFile.getContents();
	    if(contents == null || contents.length == 0)
	    	return;
	    Workbook workbook = (Workbook) WorkBookGetter.getInstance().get(new ByteArrayInputStream(contents));
		Sheet sheet =  (Sheet) SheetGetter.getInstance().get(workbook, 0);
		ArrayTwoDimensionString array = SheetReader.getInstance().read(workbook, sheet,__inject__(Interval.class).setLow(1),__inject__(Interval.class).setLow(0).setHigh(4));
		String sectionCode = null;
		for(Integer index = 0; index < array.getFirstDimensionElementCount(); index = index + 1) {
			String sectionCell = StringUtils.trimToNull(array.get(index,0));
			if(StringUtils.startsWithIgnoreCase(sectionCell, "section")) {
				sectionCode = SectionController.getCodeFromExcelString(sectionCell);
				break;
			}
		}
		Integer firstRowIndex = 0;
		for(Integer index = 0; index < array.getFirstDimensionElementCount(); index = index + 1) {
			firstRowIndex = firstRowIndex + 1;
			if(StringHelper.isNotBlank(ActivityController.getCodeFromExcelString(StringUtils.trimToNull(array.get(index, 1))))) {
				break;
			}		
		}
		
		if(administrativeUnits == null)
			administrativeUnits = new ArrayList<>();
		else
			administrativeUnits.clear();
		numberOfActivityDestinations = 0;
		CollectionHelper.clear(unknownActivities);
		CollectionHelper.clear(unknownDestinations);
		if(StringHelper.isNotBlank(sectionCode))
			this.section.select(__inject__(SectionController.class).readByBusinessIdentifier(sectionCode));
		numberOfExistingAdministrativeUnits = __inject__(AdministrativeUnitController.class)
				.count(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.COUNT_BY_SECTIONS_CODES)
						.setFilters(new FilterDto().addField(AdministrativeUnit.FIELD_SECTION, List.of(section.getValue().getCode())))).intValue();
		ServiceGroup serviceGroup = __inject__(ServiceGroupController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET);
		Localisation localisation = __inject__(LocalisationController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.Localisation.CODE_NOT_SET);
		FunctionalClassification functionalClassification = __inject__(FunctionalClassificationController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET);
		Integer rowIndex = 1;
		for(String[] arrayIndex : (String[][])array.getValue()) {
			if(rowIndex++ < firstRowIndex)
				continue;			
			//build administrative unit
			String administrativeUnitName = arrayIndex[3];
			if(StringHelper.isBlank(administrativeUnitName) && CollectionHelper.isEmpty(administrativeUnits)) {
				if(StringHelper.isNotBlank(arrayIndex[1])) {
					MessageRender messageRender = __inject__(MessageRender.class);
					messageRender.addNotificationBuilders(__inject__(NotificationBuilder.class)
							.setSummary("L'activité "+arrayIndex[1]+" n'a pas son unité administrative renseignée.")
							//.setDetails(StringHelper.concatenate(collection, ","))
							.setSeverity(__inject__(NotificationSeverityWarning.class))
							);
					messageRender.addTypes(__inject__(MessageRenderTypeInline.class),__inject__(MessageRenderTypeDialog.class));
					messageRender.execute();
				}					
				continue;
			}
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
			Activity activity = null;
			if(StringHelper.isNotBlank(activityCode)) {
				activity = __getActivityByCode__(activityCode);
				if(activity == null) {
					if(unknownActivities == null)
						unknownActivities = new LinkedHashSet<>();
					unknownActivities.add(arrayIndex[1]);
				}
			}
			
			Destination destination = null;
			if(StringHelper.isBlank(arrayIndex[2]) && CollectionHelper.isNotEmpty(administrativeUnit.getActivityDestinations()))
				destination = CollectionHelper.getLast(administrativeUnit.getActivityDestinations()).getDestination();
			if(destination == null && StringHelper.isNotBlank(arrayIndex[2])) {
				destination = __getDestinationByCode__(StringUtils.substring(arrayIndex[2], 0, 9));
				if(destination == null) {
					if(unknownDestinations == null)
						unknownDestinations = new LinkedHashSet<>();
					unknownDestinations.add(arrayIndex[2]);
				}
			}
			
			if(activity != null && destination == null && StringHelper.isBlank(arrayIndex[2])) {
				MessageRender messageRender = __inject__(MessageRender.class);
				messageRender.addNotificationBuilders(__inject__(NotificationBuilder.class)
						.setSummary("L'activité "+arrayIndex[1]+" n'a pas sa destination renseignée.")
						//.setDetails(StringHelper.concatenate(collection, ","))
						.setSeverity(__inject__(NotificationSeverityWarning.class))
						);
				messageRender.addTypes(__inject__(MessageRenderTypeInline.class),__inject__(MessageRenderTypeDialog.class));
				messageRender.execute();
			}
				
			if(activity != null && destination != null) {
				administrativeUnit.addActivityDestinations(new ActivityDestination().setActivity(activity).setDestination(destination));
				numberOfActivityDestinations++;
			}
		}
		__renderMessageIfNotEmpty__(unknownActivities, "Activités inexistantes dans la base");
		__renderMessageIfNotEmpty__(unknownDestinations, "Destinations inexistantes dans la base");
		administrativeUnitsDataModel.setRowCount(CollectionHelper.getSize(administrativeUnits));
	}
	
	private void __renderMessageIfNotEmpty__(Collection<String> collection,String message) {
		if(CollectionHelper.isEmpty(collection))
			return;
		MessageRender messageRender = __inject__(MessageRender.class);
		messageRender.addNotificationBuilders(__inject__(NotificationBuilder.class)
				.setSummary(message+"<br/>"+StringHelper.concatenate(collection, "<br/>"))
				//.setDetails(StringHelper.concatenate(collection, ","))
				.setSeverity(__inject__(NotificationSeverityWarning.class))
				);
		messageRender.addTypes(__inject__(MessageRenderTypeInline.class),__inject__(MessageRenderTypeDialog.class));
		messageRender.execute();
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
		try {
			//return InstanceGetter.getInstance().getByBusinessIdentifier(Activity.class, code);
			return activities.get(code);
		} catch (Exception exception) {
			return null;
		}
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
		try {
			//return InstanceGetter.getInstance().getByBusinessIdentifier(Destination.class, code);
			return destinations.get(code);
		} catch (Exception exception) {
			return null;
		}
	}
	
	public void load() {
		if(numberOfExistingAdministrativeUnits > 0)
			throw new RuntimeException("Il existe "+numberOfExistingAdministrativeUnits+" unité(s) administrative(s) dans la base."
					+ " Afin d'éviter les risques de doublons, seulement un seul chargement est autorisé.");
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
