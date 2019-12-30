package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;

import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractPageContainerManagedImpl extends org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Section defaultSection;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		String sectionCode = Faces.getRequestParameter("section");
		try {
			if(StringHelper.isBlank(sectionCode))
				defaultSection = (Section) SessionHelper.getAttributeValue(Section.class);
			else
				defaultSection = __inject__(SectionController.class).readBySystemIdentifier(sectionCode);
		}catch(Exception exception) {
			exception.printStackTrace();
		}
	}
		
}
