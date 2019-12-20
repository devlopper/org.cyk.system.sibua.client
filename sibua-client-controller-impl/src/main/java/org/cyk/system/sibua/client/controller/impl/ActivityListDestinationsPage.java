package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ActivityController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActivityListDestinationsPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private SelectionOne<AdministrativeUnit> administrativeUnit;
	private Collection<Activity> activities;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {
			section = new SelectionOne<Section>(Section.class);
			section.setListener(new SelectionOne.Listener<Section>() {
				@Override
				public void processOnSelect(Section section) {		
					if(section == null) {
						activities = null;
					}else {					
						activities = __inject__(ActivityController.class)
								.read(new Properties().setFilters(new FilterDto().addField(Activity.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
										.setFields(Activity.FIELD_CODE+","+Activity.FIELD_NAME+","+Activity.FIELD_DESTINATIONS)
										.setIsPageable(Boolean.FALSE));										
					}				
				}
			});
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				section.select(__inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section")));
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Correspondance des activités aux destinations";
	}
	
}
