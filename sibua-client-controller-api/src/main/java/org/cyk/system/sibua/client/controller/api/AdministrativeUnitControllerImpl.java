package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped @Named(value = "administrativeUnitController")
public class AdministrativeUnitControllerImpl extends AbstractControllerEntityImpl<AdministrativeUnit> implements AdministrativeUnitController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
