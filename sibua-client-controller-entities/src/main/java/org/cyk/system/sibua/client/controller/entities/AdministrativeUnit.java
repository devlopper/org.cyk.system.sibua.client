package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.component.annotation.Input;
import org.cyk.utility.client.controller.component.annotation.InputChoice;
import org.cyk.utility.client.controller.component.annotation.InputChoiceOne;
import org.cyk.utility.client.controller.component.annotation.InputChoiceOneCombo;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringNamableImpl;

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
	
	private List<AdministrativeUnit> parents;
	private List<AdministrativeUnit> children;
	private List<Activity> activities;
	private List<Destination> destinations;
	private List<ActivityDestination> activityDestinations;
	
	public AdministrativeUnit addDestinations(Collection<Destination> destinations) {
		if(CollectionHelper.isEmpty(destinations))
			return this;
		if(this.destinations == null)
			this.destinations = new ArrayList<>();
		this.destinations.addAll(destinations);
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
		this.activities.addAll(activities);
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
			if(StringHelper.isNotBlank(activityCode))
				addActivities(new Activity().setCode(activityCode));
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
		System.out.println("AdministrativeUnit.getActivitiesAndDestinations() : "+activities+" : "+destinations);
		if(CollectionHelper.isEmpty(activities) || CollectionHelper.isEmpty(destinations))
			return null;		
		Collection<Object[]> collection = new ArrayList<>();
		for(Activity activity : activities)
			for(Destination destination : destinations)
				collection.add(new Object[] {activity,destination});
		return collection;
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
}