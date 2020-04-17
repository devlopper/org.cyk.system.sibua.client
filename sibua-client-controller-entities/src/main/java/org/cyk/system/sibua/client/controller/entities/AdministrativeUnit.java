package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.object.__static__.controller.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.component.annotation.Input;
import org.cyk.utility.client.controller.component.annotation.InputChoice;
import org.cyk.utility.client.controller.component.annotation.InputChoiceOne;
import org.cyk.utility.client.controller.component.annotation.InputChoiceOneCombo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class AdministrativeUnit extends AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Input @InputChoice @InputChoiceOne @InputChoiceOneCombo
	private Section section;
	
	private ServiceGroup serviceGroup;
	private FunctionalClassification functionalClassification;
	private Localisation localisation;
	private Integer orderNumber;
	private AdministrativeUnit parent;
	private String __name__;
	private Integer numberOfActivities;
	private Integer numberOfActivitiesBeneficiaire;
	
	private List<AdministrativeUnit> parents;
	private List<AdministrativeUnit> children;
	private List<Activity> activities;
	private List<Activity> activitiesBeneficiaire;
	private List<Destination> destinations;
	private List<ActivityDestination> activityDestinations;
	
	/* As String */
	private String sectionAsString,serviceGroupAsString,functionalClassificationAsString,localisationAsString;
	
	public AdministrativeUnit addDestinations(Collection<Destination> destinations) {
		if(CollectionHelper.isEmpty(destinations))
			return this;
		if(this.destinations == null)
			this.destinations = new ArrayList<>();
		if(this.destinations.isEmpty())
			this.destinations.addAll(destinations);
		else
			for(Destination destination : destinations)
				if(!this.destinations.contains(destination))
					this.destinations.add(destination);
		return this;
	}
	
	public AdministrativeUnit addDestinations(Destination...destinations) {
		if(ArrayHelper.isEmpty(destinations))
			return this;
		addDestinations(CollectionHelper.listOf(Boolean.TRUE,destinations));
		return this;
	}
	
	public AdministrativeUnit addDestinationsByCodes(Collection<String> destinationsCodes) {
		if(CollectionHelper.isEmpty(destinationsCodes))
			return this;
		for(String destinationCode : destinationsCodes) {
			if(StringHelper.isNotBlank(destinationCode))
				addDestinations(new Destination().setCode(destinationCode));
		}
		return this;
	}
	
	public AdministrativeUnit addDestinationsByCodes(String...destinationsCodes) {
		if(ArrayHelper.isEmpty(destinationsCodes))
			return this;
		addDestinationsByCodes(CollectionHelper.listOf(Boolean.TRUE, destinationsCodes));
		return this;
	}
	
	public Destination getDestinationByCode(String code) {
		if(CollectionHelper.isEmpty(destinations))
			return null;
		for(Destination destination : destinations)
			if(destination.getCode().equals(code))
				return destination;
		return null;
	}
	
	/**/
	
	public AdministrativeUnit addActivityDestinations(Collection<ActivityDestination> activityDestinations) {
		if(CollectionHelper.isEmpty(activityDestinations))
			return this;
		if(this.activityDestinations == null)
			this.activityDestinations = new ArrayList<>();
		this.activityDestinations.addAll(activityDestinations);
		return this;
	}
	
	public AdministrativeUnit addActivityDestinations(ActivityDestination...activityDestinations) {
		if(ArrayHelper.isEmpty(activityDestinations))
			return this;
		addActivityDestinations(CollectionHelper.listOf(Boolean.TRUE,activityDestinations));
		return this;
	}
	
	public ActivityDestination getActivityDestinationByActivityCodeByDestinationCode(String activityCode,String destinationCode) {
		if(StringHelper.isBlank(activityCode) || StringHelper.isBlank(destinationCode))
			return null;
		for(ActivityDestination activityDestination : activityDestinations)
			if(activityDestination.getActivity().getCode().equals(activityCode) && activityDestination.getDestination().getCode().equals(destinationCode))
				return activityDestination;
		return null;
	}
	
	/**/
	
	public AdministrativeUnit addActivities(Collection<Activity> activities) {
		if(CollectionHelper.isEmpty(activities))
			return this;
		if(this.activities == null)
			this.activities = new ArrayList<>();
		if(this.activities.isEmpty())
			this.activities.addAll(activities);
		else
			for(Activity activity : activities)
				if(!this.activities.contains(activity))
					this.activities.add(activity);
		return this;
	}
	
	public AdministrativeUnit addActivities(Activity...activities) {
		if(ArrayHelper.isEmpty(activities))
			return this;
		addActivities(CollectionHelper.listOf(Boolean.TRUE,activities));
		return this;
	}
	
	public AdministrativeUnit addActivitiesByCodes(Collection<String> activitiesCodes) {
		if(CollectionHelper.isEmpty(activitiesCodes))
			return this;
		for(String activityCode : activitiesCodes) {
			if(StringHelper.isNotBlank(activityCode)) {
				Activity activity = new Activity();
				activity.setCode(activityCode);
				addActivities(activity);
			}
		}
		return this;
	}
	
	public AdministrativeUnit addActivitiesByCodes(String...activitiesCodes) {
		if(ArrayHelper.isEmpty(activitiesCodes))
			return this;
		addActivitiesByCodes(CollectionHelper.listOf(Boolean.TRUE, activitiesCodes));
		return this;
	}
	
	public Activity getActivityByCode(String code) {
		if(CollectionHelper.isEmpty(activities))
			return null;
		for(Activity activity : activities)
			if(activity.getCode().equals(code))
				return activity;
		return null;
	}
	
	/**/
	
	public Collection<Object[]> getActivitiesAndDestinations() {
		if(CollectionHelper.isEmpty(activities) || CollectionHelper.isEmpty(destinations))
			return null;		
		Collection<Object[]> collection = new ArrayList<>();
		for(Activity activity : activities)
			for(Destination destination : destinations)
				collection.add(new Object[] {activity,destination});
		return collection;
	}
	
	public String getNameAndCodeWithParenthesis() {
		if(StringHelper.isBlank(getName()))
			return null;
		return getName()+" ("+getCode()+")";
	}
	
	@Override
	public String toString() {
		return getCode()+" "+getName();
	}
	
	public static final String FIELD_PARENT = "parent";
	public static final String FIELD_SECTION = "section";
	public static final String FIELD_SERVICE_GROUP = "serviceGroup";
	public static final String FIELD_FUNCTIONAL_CLASSIFICATION = "functionalClassification";
	public static final String FIELD_LOCALISATION = "localisation";
	public static final String FIELD_ACTIVITIES = "activities";
	public static final String FIELD_PARENTS = "parents";
	public static final String FIELD_CHILDREN = "children";
	public static final String FIELD_DESTINATIONS = "destinations";
	public static final String FIELD_ACTIVITY_DESTINATIONS = "activityDestinations";
	public static final String FIELD_NUMBER_OF_ACTIVITIES = "numberOfActivities";
	public static final String FIELD_NUMBER_OF_ACTIVITIES_BENEFICIAIRE = "numberOfActivitiesBeneficiaire";
	
	/* As String*/
	public static final String FIELD_SECTION_AS_STRING = "sectionAsString";
	public static final String FIELD_SERVICE_GROUP_AS_STRING = "serviceGroupAsString";
	public static final String FIELD_FUNCTIONAL_CLASSIFICATION_AS_STRING = "functionalClassificationAsString";
	public static final String FIELD_LOCALISATION_AS_STRING = "localisationAsString";
}