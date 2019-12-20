package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.Title;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class TitleControllerImpl extends AbstractControllerEntityImpl<Title> implements TitleController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
