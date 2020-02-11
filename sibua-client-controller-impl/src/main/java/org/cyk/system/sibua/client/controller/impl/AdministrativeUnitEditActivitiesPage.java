package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.system.sibua.client.controller.api.AdministrativeUnitActivityController;
import org.cyk.system.sibua.client.controller.api.AdministrativeUnitController;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.AdministrativeUnitActivity;
import org.cyk.system.sibua.server.persistence.api.AdministrativeUnitActivityPersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.__kernel__.identifier.resource.PathAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierAsFunctionParameter;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.__kernel__.persistence.query.filter.FilterDto;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.component.window.WindowBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class AdministrativeUnitEditActivitiesPage extends AbstractPageContainerManagedImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private AdministrativeUnit administrativeUnit;
	private LazyDataModel<AdministrativeUnitActivity> administrativeUnitActivities;
	private CommandButton deleteCommandButton;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		try {		
			if(StringHelper.isBlank(Faces.getRequestParameter("entityidentifier"))) {
				
			}else {
				administrativeUnit = __inject__(AdministrativeUnitController.class).readBySystemIdentifier(Faces.getRequestParameter("entityidentifier"));				
				administrativeUnitActivities = new LazyDataModel<AdministrativeUnitActivity>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<AdministrativeUnitActivity> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
						FilterDto filter = null;				
						filter = new FilterDto();
						filter.addField(AdministrativeUnitActivity.FIELD_ADMINISTRATIVE_UNIT, List.of(administrativeUnit.getCode()));
						List<AdministrativeUnitActivity> list = (List<AdministrativeUnitActivity>) __inject__(AdministrativeUnitActivityController.class)
								.read(new Properties().setQueryIdentifier(AdministrativeUnitActivityPersistence.READ_WHERE_IS_GESTIONNAIRE_OR_BENEFICIAIRE_BY_ADMINISTRATIVE_UNITS_CODES)
										.setFilters(filter).setIsPageable(Boolean.TRUE).setFrom(first).setCount(pageSize));
						if(CollectionHelper.isEmpty(list))
							setRowCount(0);
						else {
							Long count = __inject__(AdministrativeUnitActivityController.class).count(new Properties().setFilters(filter));
							setRowCount(count == null ? 0 : count.intValue());	
						}
						return list;
					}
				};
			}
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		deleteCommandButton = Builder.build(CommandButton.class,Map.of("title","Supprimer")).setIcon(Icon.REMOVE);
		deleteCommandButton.getConfirm().setDisabled(Boolean.FALSE);
		deleteCommandButton.setListener(new CommandButton.Listener() {
			@Override
			public void listenAction(Object argument) {
				if(!(argument instanceof AdministrativeUnitActivity))
					return;
				AdministrativeUnitActivity administrativeUnitActivity = (AdministrativeUnitActivity) argument;
				__inject__(AdministrativeUnitActivityController.class).delete(administrativeUnitActivity);
			}
		});
	}
	
	public void saveGestionnaire() {
		
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des activités de l'unité administrative"+(administrativeUnit == null ? ConstantEmpty.STRING : " "+administrativeUnit
				+" , Section "+administrativeUnit.getSection());	
	}
	
	@Override
	protected String __processWindowDialogOkCommandableGetUrl__(WindowBuilder window, CommandableBuilder commandable) {
		PathAsFunctionParameter pathAsFunctionParameter = new PathAsFunctionParameter();
		pathAsFunctionParameter.setIdentifier("administrativeUnitListView");
		String string =  UniformResourceIdentifierHelper.build(new UniformResourceIdentifierAsFunctionParameter().setPath(pathAsFunctionParameter));
		if(defaultSection != null)
			string = string  + "?section="+defaultSection.getCode();
		return string;
	}
}
