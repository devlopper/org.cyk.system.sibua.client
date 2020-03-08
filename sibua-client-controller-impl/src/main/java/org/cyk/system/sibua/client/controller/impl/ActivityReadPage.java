package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.ActivityCostUnitController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.ActivityCostUnit;
import org.cyk.system.sibua.server.persistence.api.ActivityCostUnitPersistence;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActivityReadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private Activity activity;
	protected Collection<ActivityCostUnit> activityCostUnits;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		activity = __inject__(ActivityController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
		activityCostUnits = __inject__(ActivityCostUnitController.class).read(new Properties()
				.setQueryIdentifier(ActivityCostUnitPersistence.READ_BY_ACTIVITIES_CODES)
				.setFilters(new FilterDto().addField(ActivityCostUnit.FIELD_ACTIVITY, List.of(activity.getCode()))						
				)
			);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return activity.toString();	
	}
	
}
