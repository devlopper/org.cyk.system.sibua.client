package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.api.user.UserFunctionController;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.system.sibua.client.controller.entities.user.UserFunction;
import org.cyk.system.sibua.server.persistence.api.user.UserFunctionPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserNotifySendPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private String message;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
			user = new User();	
		}else {
			user = __inject__(UserController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
			user.setUserFunctions((List<UserFunction>) __inject__(UserFunctionController.class).read(new Properties().setQueryIdentifier(UserFunctionPersistence.READ_BY_USERS_IDENTIFIERS)
					.setFilters(new FilterDto().addField("user", List.of(user.getIdentifier())))));
			message = "Votre fiche d'identification a bien été transmise.\r\n" + 
					"    			Veuillez l'imprimer , la signée et la faire parvenir à ";
			
			if(CollectionHelper.isNotEmpty(user.getUserFunctions())) {
				Collection<String> codes = user.getUserFunctions().stream().map(userFunction -> userFunction.getFunction().getType().getCategory().getCode()).collect(Collectors.toList());
				if(codes.contains("1") || codes.contains("2")) {
					message = message + "la Direction Générale du Budget et des Finances (DGBF).";
				}else {
					message = message + "votre ordonnateur.";
				}
			}
		}
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Votre fiche d'identification a été bien transmise.";
	}
	
}
