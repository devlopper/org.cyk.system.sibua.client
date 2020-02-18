package org.cyk.system.sibua.client.controller.entities.user;

import java.io.Serializable;

import org.cyk.system.sibua.client.controller.entities.Program;
import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @NoArgsConstructor @Accessors(chain=true)
public class UserProgram extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private User user;
	private Program program;
	
}