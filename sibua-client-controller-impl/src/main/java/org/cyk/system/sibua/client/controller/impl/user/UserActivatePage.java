package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserActivatePage extends AbstractUserPage implements Serializable {
	private static final long serialVersionUID = 1L;

	private Commandable activateCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		CommandableBuilder createCommandableBuilder = __inject__(CommandableBuilder.class);
		createCommandableBuilder.setName("Activer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					create();
				}
			}
		);
		activateCommandable = createCommandableBuilder.execute().getOutput();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Activation de fiche d'identification";
	}
	
	public void create() {
		if(StringHelper.isBlank(user.getActivationDate()))
			__inject__(UserController.class).update(user,new Properties().setFields(User.FIELD_ACTIVATION_DATE));
	}

}
