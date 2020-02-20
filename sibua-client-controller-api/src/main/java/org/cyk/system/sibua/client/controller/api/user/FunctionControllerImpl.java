package org.cyk.system.sibua.client.controller.api.user;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.user.Function;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class FunctionControllerImpl extends AbstractControllerEntityImpl<Function> implements FunctionController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
