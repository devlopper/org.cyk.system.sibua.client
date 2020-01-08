package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped @Named(value = "functionalClassificationController")
public class FunctionalClassificationControllerImpl extends AbstractControllerEntityImpl<FunctionalClassification> implements FunctionalClassificationController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
