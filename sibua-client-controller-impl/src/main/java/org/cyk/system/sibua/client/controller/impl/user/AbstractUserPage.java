package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;

import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @Getter @Setter
public abstract class AbstractUserPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	protected User user;
	protected String message;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {

		}else {
			user = __inject__(UserController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
			if(user == null) {
				message = "Lien incorrect.";
			}else {
				if(StringHelper.isEmpty(user.getActivationDate())) {
					message = "Veuillez activer votre fiche d'identification afin qu'elle soit traitée.";
				}else {
					if(StringHelper.isEmpty(user.getValidationByOrdonnateurDate()))
						message = "Votre fiche d'identification en cours de traitement.";
					else
						message = "Votre fiche d'identification a été traitée.";
					
				}
			}
		}
	}
	
}
