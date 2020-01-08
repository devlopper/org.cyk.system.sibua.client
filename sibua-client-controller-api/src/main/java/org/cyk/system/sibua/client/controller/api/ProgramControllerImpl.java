package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped @Named("programController")
public class ProgramControllerImpl extends AbstractControllerEntityImpl<Program> implements ProgramController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
