package org.cyk.system.sibua.client.controller.entities;
import org.cyk.system.sibua.server.representation.entities.UserFileDto;
import org.cyk.utility.__kernel__.mapping.AbstractMapperSourceDestinationImpl;
import org.mapstruct.Mapper;

@Mapper
public abstract class UserFileMapper extends AbstractMapperSourceDestinationImpl<UserFile, UserFileDto> {
	private static final long serialVersionUID = 1L;
    	
}