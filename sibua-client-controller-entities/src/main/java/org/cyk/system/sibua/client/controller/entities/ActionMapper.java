package org.cyk.system.sibua.client.controller.entities;
import org.cyk.system.sibua.server.representation.entities.ActionDto;
import org.cyk.utility.__kernel__.representation.AbstractMapperSourceDestinationImpl;
import org.mapstruct.Mapper;

@Mapper
public abstract class ActionMapper extends AbstractMapperSourceDestinationImpl<Action, ActionDto> {
	private static final long serialVersionUID = 1L;
    	
}