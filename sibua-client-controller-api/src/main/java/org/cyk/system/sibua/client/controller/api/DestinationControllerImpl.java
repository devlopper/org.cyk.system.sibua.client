package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.Destination;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class DestinationControllerImpl extends AbstractControllerEntityImpl<Destination> implements DestinationController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
