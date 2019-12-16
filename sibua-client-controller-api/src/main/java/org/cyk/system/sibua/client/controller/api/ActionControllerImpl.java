package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.Action;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class ActionControllerImpl extends AbstractControllerEntityImpl<Action> implements ActionController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
