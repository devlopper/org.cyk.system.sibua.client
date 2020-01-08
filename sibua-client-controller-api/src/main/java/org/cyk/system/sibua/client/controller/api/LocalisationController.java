package org.cyk.system.sibua.client.controller.api;

import java.util.Collection;

import org.cyk.system.sibua.client.controller.entities.Localisation;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.client.controller.ControllerEntity;

public interface LocalisationController extends ControllerEntity<Localisation> {

	@SuppressWarnings("unchecked")
	static Collection<Localisation> readStatic() {
		if(!COLLECTION.isHasBeenSet())
			COLLECTION.set(DependencyInjection.inject(LocalisationController.class).read(new Properties().setIsPageable(Boolean.FALSE)));
		return (Collection<Localisation>) COLLECTION.get();
	}
	
	Value COLLECTION = DependencyInjection.inject(Value.class);
	
}
