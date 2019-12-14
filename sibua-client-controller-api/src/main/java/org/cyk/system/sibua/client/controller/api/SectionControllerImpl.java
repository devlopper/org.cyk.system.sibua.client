package org.cyk.system.sibua.client.controller.api;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.client.controller.AbstractControllerEntityImpl;

@ApplicationScoped
public class SectionControllerImpl extends AbstractControllerEntityImpl<Section> implements SectionController,Serializable {
	private static final long serialVersionUID = 1L;
	
}
