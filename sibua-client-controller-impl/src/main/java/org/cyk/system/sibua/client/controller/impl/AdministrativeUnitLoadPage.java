package org.cyk.system.sibua.client.controller.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
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
import org.cyk.utility.notification.NotificationSeverityError;
import org.cyk.utility.notification.NotificationSeverityWarning;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
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

	private static final ArrayTwoDimensionString RELATED_ARRAY;
	private static final Map<String,Activity> activities;
	private static final Map<String,Destination> destinations;
	private static final Map<String,FunctionalClassification> functionalClassifications;
	private static final Map<String,ServiceGroup> serviceGroups;
	private static final Map<String,Localisation> localisations;
	private static final ServiceGroup serviceGroupNotSet;
	private static final FunctionalClassification functionalClassificationNotSet;
	private static final Localisation localisationNotSet;
	
	static {
		Workbook workbook = (Workbook) WorkBookGetter.getInstance().get(AdministrativeUnitLoadPage.class.getResourceAsStream("ua02012020.xlsx"));
		Sheet sheet =  (Sheet) SheetGetter.getInstance().get(workbook, 0);
		RELATED_ARRAY = SheetReader.getInstance().read(workbook, sheet,__inject__(Interval.class).setLow(1),__inject__(Interval.class).setLow(0).setHigh(6));
		
		serviceGroupNotSet = __inject__(ServiceGroupController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.ServiceGroup.CODE_NOT_SET);
		localisationNotSet = __inject__(LocalisationController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.Localisation.CODE_NOT_SET);
		functionalClassificationNotSet = __inject__(FunctionalClassificationController.class).readByBusinessIdentifier(org.cyk.system.sibua.server.persistence.entities.FunctionalClassification.CODE_NOT_SET);
		
		activities = __inject__(ActivityController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(Activity::getCode, x-> x));
		destinations = __inject__(DestinationController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(Destination::getCode, x->x));
		functionalClassifications = __inject__(FunctionalClassificationController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(FunctionalClassification::getCode, x->x));
		serviceGroups = __inject__(ServiceGroupController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(ServiceGroup::getCode, x->x));
		localisations = __inject__(LocalisationController.class).read(new Properties().setFields("code,name").setIsPageable(Boolean.FALSE)).stream().collect(Collectors.toMap(Localisation::getCode, x->x));
	}
	
	private SelectionOne<Section> section;
	private List<AdministrativeUnit> administrativeUnits;
	private Set<Activity> __activities__;
	private Set<Destination> __destinations__;
	private LazyDataModel<AdministrativeUnit> administrativeUnitsDataModel;
	//private Collection<String> unknownActivities,unknownDestinations;
	private Commandable loadCommandable;
	private Integer numberOfExistingAdministrativeUnits,numberOfActivities,numberOfDestinations,numberOfActivityDestinations;
	
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
	    byte[] bytes = uploadedFile.getContents();
	    if(bytes == null || bytes.length == 0)
	    	return;
	    Workbook workbook = (Workbook) WorkBookGetter.getInstance().get(new ByteArrayInputStream(bytes));
		Sheet sheet =  (Sheet) SheetGetter.getInstance().get(workbook, 0);
		ArrayTwoDimensionString array = SheetReader.getInstance().read(workbook, sheet,__inject__(Interval.class).setLow(1),__inject__(Interval.class).setLow(0).setHigh(4));
		String sectionCode = null;
		for(Integer index = 0; index < array.getFirstDimensionElementCount(); index = index + 1) {
			String sectionCell = StringUtils.trimToNull(array.get(index,0));
			if(StringUtils.startsWithIgnoreCase(sectionCell, "section")) {
				sectionCode = SectionController.getCodeFromExcelString(sectionCell);
				if(StringHelper.isBlank(sectionCode)) {
					System.out.println("Section code cannot be read : "+sectionCell);
					MessageRender messageRender = __inject__(MessageRender.class);
					messageRender.addNotificationBuilders(__inject__(NotificationBuilder.class)
							.setSummary("Le code de la section n'a pas pu être déduit de la ligne <<"+StringEscapeUtils.escapeHtml4(sectionCell)+">>.")
							//.setDetails(StringHelper.concatenate(collection, ","))
							.setSeverity(__inject__(NotificationSeverityError.class))
							);
					messageRender.addTypes(__inject__(MessageRenderTypeInline.class),__inject__(MessageRenderTypeDialog.class));
					messageRender.execute();
					return;
				}
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
		numberOfActivities = 0;
		numberOfDestinations = 0;
		numberOfActivityDestinations = 0;
		/*
		CollectionHelper.clear(unknownActivities);
		CollectionHelper.clear(unknownDestinations);
		*/
		
		if(StringHelper.isNotBlank(sectionCode))
			try {
				this.section.select(__inject__(SectionController.class).readByBusinessIdentifier(sectionCode));
			} catch (Exception exception) {
				MessageRender messageRender = __inject__(MessageRender.class);
				messageRender.addNotificationBuilders(__inject__(NotificationBuilder.class)
						.setSummary("La section ayant pour code <<"+sectionCode+">> n'a pas pu être trouvé dans la base.")
						//.setDetails(StringHelper.concatenate(collection, ","))
						.setSeverity(__inject__(NotificationSeverityError.class))
						);
				messageRender.addTypes(__inject__(MessageRenderTypeInline.class),__inject__(MessageRenderTypeDialog.class));
				messageRender.execute();
				return;
			}
		numberOfExistingAdministrativeUnits = __inject__(AdministrativeUnitController.class)
				.count(new Properties().setQueryIdentifier(AdministrativeUnitPersistence.COUNT_BY_SECTIONS_CODES)
						.setFilters(new Filter.Dto().addField(AdministrativeUnit.FIELD_SECTION, List.of(section.getValue().getCode())))).intValue();
		
		
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
				String[] row = __getRowBySectionCodeByAdministrativeUnitName__(sectionCode, administrativeUnitName);
				if(row != null) {
					if(row.length >= 5)
						administrativeUnit.setServiceGroup(serviceGroups.get(row[4]));
					if(row.length >= 6)
						administrativeUnit.setLocalisation(localisations.get(row[5]));
					if(row.length >= 7)
						administrativeUnit.setFunctionalClassification(functionalClassifications.get(row[6]));
				}
				if(administrativeUnit.getServiceGroup() == null)
					administrativeUnit.setServiceGroup(serviceGroupNotSet);
				if(administrativeUnit.getLocalisation() == null)
					administrativeUnit.setLocalisation(localisationNotSet);
				if(administrativeUnit.getFunctionalClassification() == null)
					administrativeUnit.setFunctionalClassification(functionalClassificationNotSet);
				administrativeUnits.add(administrativeUnit);
			}
			//build activity and destination association			
			String activityCode = ActivityController.getCodeFromExcelString(arrayIndex[1]);
			Activity activity = null;
			if(StringHelper.isNotBlank(activityCode)) {
				activity = __getActivityByCode__(activityCode);
				/*if(activity == null) {
					if(unknownActivities == null)
						unknownActivities = new LinkedHashSet<>();
					unknownActivities.add(arrayIndex[1]);
				}
				*/
			}
			if(activity == null && StringHelper.isNotBlank(arrayIndex[1])) {
				activity = new Activity();
				activity.setName(arrayIndex[1]);
			}
			Destination destination = null;
			if(StringHelper.isBlank(arrayIndex[2]) && CollectionHelper.isNotEmpty(administrativeUnit.getActivityDestinations()))
				destination = CollectionHelper.getLast(administrativeUnit.getActivityDestinations()).getDestination();
			if(destination == null && StringHelper.isNotBlank(arrayIndex[2])) {
				destination = __getDestinationByCode__(DestinationController.getCodeFromExcelString(arrayIndex[2]));
				/*if(destination == null) {
					if(unknownDestinations == null)
						unknownDestinations = new LinkedHashSet<>();
					unknownDestinations.add(arrayIndex[2]);
				}*/
			}
			if(destination == null && StringHelper.isNotBlank(arrayIndex[2]))
				destination = new Destination().setName(arrayIndex[2]);
						
			if(activity != null && destination == null) {
				administrativeUnit.addActivities(activity);
				if(__activities__ == null)
					__activities__ = new HashSet<>();
				__activities__.add(activity);
				if(StringHelper.isBlank(arrayIndex[2])) {
					MessageRender messageRender = __inject__(MessageRender.class);
					messageRender.addNotificationBuilders(__inject__(NotificationBuilder.class)
							.setSummary("L'activité "+arrayIndex[1]+" n'a pas sa destination renseignée.")
							//.setDetails(StringHelper.concatenate(collection, ","))
							.setSeverity(__inject__(NotificationSeverityWarning.class))
							);
					messageRender.addTypes(__inject__(MessageRenderTypeInline.class),__inject__(MessageRenderTypeDialog.class));
					messageRender.execute();	
				}				
			}
			
			if(activity == null && destination != null) {
				administrativeUnit.addDestinations(destination);
				if(__destinations__ == null)
					__destinations__ = new HashSet<>();
				__destinations__.add(destination);
			}
				
			if(activity != null && destination != null) {
				administrativeUnit.addActivities(activity);
				administrativeUnit.addDestinations(destination);
				administrativeUnit.addActivityDestinations(new ActivityDestination().setActivity(activity).setDestination(destination));
				numberOfActivityDestinations++;
				if(__activities__ == null)
					__activities__ = new HashSet<>();
				__activities__.add(activity);
				if(__destinations__ == null)
					__destinations__ = new HashSet<>();
				__destinations__.add(destination);
			}			
		}
		/*
		__renderMessageIfNotEmpty__(unknownActivities, "Activités inexistantes dans la base");
		__renderMessageIfNotEmpty__(unknownDestinations, "Destinations inexistantes dans la base");
		*/
		administrativeUnitsDataModel.setRowCount(CollectionHelper.getSize(administrativeUnits));
		
		numberOfActivities = __activities__.size();
		numberOfDestinations = __destinations__.size();	
	}
	/*
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
	*/
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
						if(StringHelper.isNotBlank(activityDestination.getActivity().getCode()) && activityDestination.getActivity().getCode().equals(code))
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
						if(StringHelper.isNotBlank(activityDestination.getDestination().getCode()) && activityDestination.getDestination().getCode().equals(code))
							return activityDestination.getDestination();
		try {
			//return InstanceGetter.getInstance().getByBusinessIdentifier(Destination.class, code);
			return destinations.get(code);
		} catch (Exception exception) {
			return null;
		}
	}
	
	private String[] __getRowBySectionCodeByAdministrativeUnitName__(String sectionCode,String administrativeUnitName) {
		for(String[] arrayIndex : (String[][])RELATED_ARRAY.getValue())
			if(sectionCode.equals(StringUtils.trim(arrayIndex[0])) && Boolean.TRUE.equals(__isSameName__(arrayIndex[3], administrativeUnitName)))
				return arrayIndex;		
		return null;
	}
	
	private Boolean __isSameName__(String name1,String name2) {
		return name1.equals(name2);
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
