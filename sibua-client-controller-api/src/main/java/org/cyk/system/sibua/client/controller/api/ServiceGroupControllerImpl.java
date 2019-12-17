package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class ServiceGroupControllerImpl extends AbstractControllerEntityImpl<ServiceGroup> implements ServiceGroupController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
