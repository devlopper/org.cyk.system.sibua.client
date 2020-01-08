package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped @Named(value = "localisationController")
public class LocalisationControllerImpl extends AbstractControllerEntityImpl<Localisation> implements LocalisationController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
