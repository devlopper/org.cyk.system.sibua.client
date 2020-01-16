package org.cyk.system.sibua.client.controller.entities.user;

import java.io.Serializable;

import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @NoArgsConstructor @Accessors(chain=true)
public class File extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private byte[] bytes;	
	private String extension;
	private String name;
	private String sha1;
	private UserFileType type;
	
}