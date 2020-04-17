package org.cyk.system.sibua.client.controller.entities.user;
import org.cyk.system.sibua.server.representation.entities.user.FileDto;
import org.cyk.utility.__kernel__.representation.AbstractMapperSourceDestinationImpl;
import org.mapstruct.Mapper;

@Mapper
public abstract class FileMapper extends AbstractMapperSourceDestinationImpl<File, FileDto> {
	private static final long serialVersionUID = 1L;
    	
}