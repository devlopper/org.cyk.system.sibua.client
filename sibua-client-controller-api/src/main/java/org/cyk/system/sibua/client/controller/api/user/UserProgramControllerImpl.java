package org.cyk.system.sibua.client.controller.api.user;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.user.UserProgram;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class UserProgramControllerImpl extends AbstractControllerEntityImpl<UserProgram> implements UserProgramController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
