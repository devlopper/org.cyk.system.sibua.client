package org.cyk.system.sibua.client.controller.api;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.cyk.system.sibua.client.controller.entities.AdministrativeUnit;
import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.system.sibua.server.representation.api.AdministrativeUnitRepresentation;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.identifier.resource.ProxyGetter;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.ControllerEntity;

public interface AdministrativeUnitController extends ControllerEntity<AdministrativeUnit> {

	default void generateCodesBySectionsCodes(List<String> sectionsCodes) {
		if(CollectionHelper.isEmpty(sectionsCodes))
			return;
		ProxyGetter.getInstance().get(AdministrativeUnitRepresentation.class).generateCodesBySectionsCodes(sectionsCodes);
	}
	
	default void generateCodesBySectionsCodes(String...sectionsCodes) {
		if(ArrayHelper.isEmpty(sectionsCodes))
			return;
		generateCodesBySectionsCodes(CollectionHelper.listOf(sectionsCodes));
	}
	
	default void generateCodesBySections(Collection<Section> sections) {
		if(CollectionHelper.isEmpty(sections))
			return;
		generateCodesBySectionsCodes(sections.stream().map(Section::getCode).collect(Collectors.toList()));
	}
	
	default void generateCodesBySections(Section...sections) {
		if(ArrayHelper.isEmpty(sections))
			return;
		generateCodesBySections(CollectionHelper.listOf(sections));
	}
	
	/**/
	
	default void mergeByCodes(List<String> administrativeUnitsSourcesCodes,String administrativeUnitDestinationCode) {
		if(CollectionHelper.isEmpty(administrativeUnitsSourcesCodes) || StringHelper.isBlank(administrativeUnitDestinationCode))
			return;
		ProxyGetter.getInstance().get(AdministrativeUnitRepresentation.class).mergeByCodes(administrativeUnitsSourcesCodes,administrativeUnitDestinationCode);
	}
	
	default void mergeByCodes(List<AdministrativeUnit> administrativeUnitsSources,AdministrativeUnit administrativeUnitDestination) {
		if(CollectionHelper.isEmpty(administrativeUnitsSources) || administrativeUnitDestination == null)
			return;
		mergeByCodes(administrativeUnitsSources.stream().map(AdministrativeUnit::getCode).collect(Collectors.toList()),administrativeUnitDestination.getCode());
	}
	
}
