package org.cyk.system.sibua.client.controller.api;

import java.util.Collection;

import org.cyk.system.sibua.client.controller.entities.FunctionalClassification;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.client.controller.ControllerEntity;

public interface FunctionalClassificationController extends ControllerEntity<FunctionalClassification> {

	@SuppressWarnings("unchecked")
	static Collection<FunctionalClassification> readStatic() {
		if(!COLLECTION.isHasBeenSet())
			COLLECTION.set(DependencyInjection.inject(FunctionalClassificationController.class).read(new Properties().setIsPageable(Boolean.FALSE)));
		return (Collection<FunctionalClassification>) COLLECTION.get();
	}
	
	Value COLLECTION = DependencyInjection.inject(Value.class);
	
}
