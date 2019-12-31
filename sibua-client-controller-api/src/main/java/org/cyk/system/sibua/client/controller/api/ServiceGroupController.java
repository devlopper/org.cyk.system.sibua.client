package org.cyk.system.sibua.client.controller.api;

import java.util.Collection;

import org.cyk.system.sibua.client.controller.entities.ServiceGroup;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.client.controller.ControllerEntity;

public interface ServiceGroupController extends ControllerEntity<ServiceGroup> {

	@SuppressWarnings("unchecked")
	static Collection<ServiceGroup> readStatic() {
		if(!COLLECTION.isHasBeenSet())
			COLLECTION.set(DependencyInjection.inject(ServiceGroupController.class).read(new Properties().setIsPageable(Boolean.FALSE)));
		return (Collection<ServiceGroup>) COLLECTION.get();
	}
	
	Value COLLECTION = DependencyInjection.inject(Value.class);
	
}
