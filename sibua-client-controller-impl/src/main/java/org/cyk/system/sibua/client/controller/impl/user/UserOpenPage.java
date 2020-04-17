package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.Filter;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.message.Message;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserOpenPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private String accessToken;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		if(StringHelper.isNotBlank(Faces.getRequestParameter("entityidentifier")))
			user = __inject__(UserController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
		identifier = Faces.getRequestParameter("entityidentifier");
	}
	
	public String open() {
		User user = null;
		try {
			//user = __inject__(UserController.class).readBySystemIdentifier(accessToken);
			user = CollectionHelper.getFirst(__inject__(UserController.class).read(new Properties()
					.setFields("identifier,programs")
					.setFilters(new Filter.Dto().addField(User.FIELD_ACCESS_TOKEN, List.of(accessToken)))));
		} catch (Exception exception) {}
		if(user == null) {
			MessageRenderer.getInstance().render(new Message().setSummary("Jeton d'accès inconnu").setSeverity(Severity.ERROR), RenderType.INLINE);
			return null;
		}
		return "read/"+(CollectionHelper.isEmpty(user.getPrograms()) ? "program" : "credit")+"manager.jsf?faces-redirect=true&entityidentifier="+user.getIdentifier();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Ouvrir une fiche d'identification";
	}
	
}
