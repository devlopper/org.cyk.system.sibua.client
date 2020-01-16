package org.cyk.system.sibua.client.controller.entities.user;

import java.io.Serializable;

import org.cyk.system.sibua.server.persistence.entities.user.UserFileType;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor //@Accessors(chain=true)
public class UserFile extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private User user;
	private UserFileType type;
	private byte[] bytes;
		
}