package org.cyk.system.sibua.client.controller.api.user;

import java.util.Collection;

import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.client.controller.ControllerEntity;

public interface UserController extends ControllerEntity<User> {

	void notifyAccessToken(Collection<String> usersIdentifiers);
	
	default void notifyAccessToken(String...usersIdentifiers) {
		if(ArrayHelper.isEmpty(usersIdentifiers))
			return;
		notifyAccessToken(CollectionHelper.listOf(usersIdentifiers));
	}
	
}
