package org.cyk.system.sibua.client.controller.entities;
import org.cyk.system.sibua.server.representation.entities.ActivityDto;
import org.cyk.utility.__kernel__.representation.AbstractMapperSourceDestinationImpl;
import org.mapstruct.Mapper;

@Mapper
public abstract class ActivityMapper extends AbstractMapperSourceDestinationImpl<Activity, ActivityDto> {
	private static final long serialVersionUID = 1L;
    	
}