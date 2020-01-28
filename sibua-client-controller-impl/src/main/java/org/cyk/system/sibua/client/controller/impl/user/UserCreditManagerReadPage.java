package org.cyk.system.sibua.client.controller.impl.user;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.user.UserController;
import org.cyk.system.sibua.client.controller.entities.user.User;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.QueryAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionCustom;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserCreditManagerReadPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private String severity,summary,detail;
	private Commandable deleteCommandable,sendCommandable;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		user = __inject__(UserController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));
		//UserFile userFile = CollectionHelper.getFirst(user.getUserFiles());
		//if(userFile != null)
		//	user.setCertificateReference(userFile.getReference());
		
		summary = "Notification";
		if(StringHelper.isBlank(user.getSendingDate())) {
			severity = "warn";
			detail = "Veuillez transmettre votre fiche d'identification.";
		}else {
			severity = "info";
			detail = "Votre fiche d'identification est en cours de traitement.";
		}
		
		CommandableBuilder deleteCommandableBuilder = __inject__(CommandableBuilder.class);
		deleteCommandableBuilder.setName("Supprimer").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					delete();
				}
			}
		);
		deleteCommandableBuilder.setIcon(Icon.REMOVE);
		deleteCommandable = deleteCommandableBuilder.execute().getOutput();
		
		CommandableBuilder sendCommandableBuilder = __inject__(CommandableBuilder.class);
		sendCommandableBuilder.setName("Transmettre").setCommandFunctionActionClass(SystemActionCustom.class).addCommandFunctionTryRunRunnable(
			new Runnable() {
				@Override
				public void run() {
					send();
				}
			}
		);
		sendCommandable = sendCommandableBuilder.execute().getOutput();
		sendCommandableBuilder.setIcon(Icon.SEND);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(user == null)
			return null;
		String string = ConstantEmpty.STRING;
		if(user.getCivility() != null)
			string = user.getCivility().getName()+" ";
		string += user.getFirstName();
		if(StringHelper.isNotBlank(user.getLastNames()))
			string += " "+user.getLastNames();
		return "Fiche d'identification de "+string;
	}
	
	public void delete() {
		__inject__(UserController.class).delete(user);
		UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
		p.setRequest(__getRequest__());
		p.setPath(new PathAsFunctionParameter());
		p.getPath().setIdentifier("userCreditManagerEditView");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send() {
		__inject__(UserController.class).update(user,new Properties().setFields("sendingDate"));
		UniformResourceIdentifierAsFunctionParameter p = new UniformResourceIdentifierAsFunctionParameter();
		p.setRequest(__getRequest__());
		p.setPath(new PathAsFunctionParameter());
		p.getPath().setIdentifier("userNotifySendView");
		p.setQuery(new QueryAsFunctionParameter());
		p.getQuery().setValue("entityidentifier="+user.getIdentifier());
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(UniformResourceIdentifierHelper.build(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
