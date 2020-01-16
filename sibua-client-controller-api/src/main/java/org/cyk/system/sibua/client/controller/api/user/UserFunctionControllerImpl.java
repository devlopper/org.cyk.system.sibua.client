package org.cyk.system.sibua.client.controller.api.user;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.user.UserFunction;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class UserFunctionControllerImpl extends AbstractControllerEntityImpl<UserFunction> implements UserFunctionController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
