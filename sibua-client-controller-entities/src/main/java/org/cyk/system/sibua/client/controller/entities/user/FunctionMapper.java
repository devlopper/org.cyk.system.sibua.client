package org.cyk.system.sibua.client.controller.entities.user;
import org.cyk.system.sibua.server.representation.entities.user.FunctionDto;
import org.cyk.utility.__kernel__.mapping.AbstractMapperSourceDestinationImpl;
import org.mapstruct.Mapper;

@Mapper
public abstract class FunctionMapper extends AbstractMapperSourceDestinationImpl<Function, FunctionDto> {
	private static final long serialVersionUID = 1L;
    	
}