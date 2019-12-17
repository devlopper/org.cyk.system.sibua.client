package org.cyk.system.sibua.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;

import org.cyk.utility.__kernel__.system.action.SystemAction;
import org.cyk.utility.client.controller.AbstractSystemActionFieldsNamesGetterImpl;

@org.cyk.system.sibua.server.annotation.System
public class SystemActionFieldsNamesGetterImpl extends AbstractSystemActionFieldsNamesGetterImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected Collection<String> __get__(Class<? extends SystemAction> systemActionClass, Object systemActionIdentifier,Class<?> entityClass) {
		/*if(AdministrativeUnit.class.equals(entityClass)) {
			if(ClassHelper.isInstanceOf(systemActionClass, SystemActionList.class))
				return CollectionHelper.listOf(AdministrativeUnit.FIELD_CODE,AdministrativeUnit.FIELD_CUSTOMER,AdministrativeUnit.FIELD_DEPARTURE_PLACE);
		}
		*/
		return super.__get__(systemActionClass, systemActionIdentifier, entityClass);
	}
	
}
