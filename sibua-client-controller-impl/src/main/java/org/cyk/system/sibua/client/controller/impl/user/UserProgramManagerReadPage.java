package org.cyk.system.sibua.client.controller.impl.user;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class UserProgramManagerReadPage extends AbstractActorReadPage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Boolean getIsProgramManager() {
		return Boolean.TRUE;
	}
	
}
