package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.user.interface_.message.Message;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserNotifyAccessTokenPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private String electronicMailAddress;
	
	public String open() {
		try {
			User user = CollectionHelper.getFirst(__inject__(UserController.class).read(new Properties().setFilters(new FilterDto().addField(User.FIELD_ELECTRONIC_MAIL_ADDRESS
					, List.of(electronicMailAddress)))));
			if(user == null) {
				MessageRenderer.getInstance().render(new Message().setSummary("Adresse mail inconnue").setSeverity(Severity.ERROR), RenderType.INLINE);
				return null;
			}
			__inject__(UserController.class).notifyAccessToken(user.getIdentifier());
		} catch (Exception exception) {
			MessageRenderer.getInstance().render(new Message().setSummary(exception.toString()).setSeverity(Severity.ERROR), RenderType.INLINE);
			return null;
		}
		return "open.jsf?faces-redirect=true";
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Notification de jeton d'accès";
	}
	
}
