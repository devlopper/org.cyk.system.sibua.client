package org.cyk.system.sibua.client.controller.api.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.system.sibua.server.representation.api.user.UserRepresentation;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.identifier.resource.ProxyGetter;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class UserControllerImpl extends AbstractControllerEntityImpl<User> implements UserController,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void notifyAccessToken(Collection<String> usersIdentifiers) {
		if(CollectionHelper.isEmpty(usersIdentifiers))
			return;
		ProxyGetter.getInstance().get(UserRepresentation.class).notifyAccessToken((List<String>) usersIdentifiers);
	}
	
}
