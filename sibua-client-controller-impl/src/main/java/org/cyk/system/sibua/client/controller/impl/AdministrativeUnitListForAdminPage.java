package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.utility.__kernel__.properties.Properties;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitListForAdminPage extends AbstractAdministrativeUnitListPage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		isShowAll = Boolean.TRUE;
	}
	
	@Override
	protected Properties __getProperties__(Object filter, Object first, Object pageSize) {
		Properties properties = super.__getProperties__(filter, first, pageSize);
		properties.setFields(properties.getFields()+","+AdministrativeUnit.FIELD_ACTIVITY_DESTINATIONS+","+AdministrativeUnit.FIELD_ACTIVITIES
				+","+AdministrativeUnit.FIELD_DESTINATIONS);
		return properties;
	}
	
}
