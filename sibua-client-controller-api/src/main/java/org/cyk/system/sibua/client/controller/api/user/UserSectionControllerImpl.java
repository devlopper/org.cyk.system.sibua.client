package org.cyk.system.sibua.client.controller.api.user;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.user.UserSection;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class UserSectionControllerImpl extends AbstractControllerEntityImpl<UserSection> implements UserSectionController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
