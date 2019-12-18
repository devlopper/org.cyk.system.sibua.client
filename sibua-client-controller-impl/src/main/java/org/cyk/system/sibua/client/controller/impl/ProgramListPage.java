package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.ProgramController;
import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.SelectionOne;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ProgramListPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private SelectionOne<Section> section;
	private Collection<Program> programs;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {
			section = new SelectionOne<Section>(Section.class);
			section.setListener(new SelectionOne.Listener<Section>() {
				@Override
				public void processOnSelect(Section section) {
					programs = __inject__(ProgramController.class)
							.read(new Properties().setFilters(new FilterDto().addField(Program.FIELD_SECTION, CollectionHelper.listOf(section.getCode())))
									.setFields(Program.FIELD_CODE+","+Program.FIELD_NAME+","+Program.FIELD_ADMINISTRATIVE_UNITS)
									.setIsPageable(Boolean.FALSE));
				}
			});
			if(StringHelper.isBlank(Faces.getRequestParameter("section"))) {
				
			}else {
				section.select(__inject__(SectionController.class).readBySystemIdentifier(Faces.getRequestParameter("section")));
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Cartographie administrative par section";
	}
	
}
