package org.cyk.system.sibua.client.controller.entities;

import java.io.Serializable;

import org.cyk.utility.client.controller.data.AbstractDataIdentifiableSystemStringImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @NoArgsConstructor @Accessors(chain=true)
public class ActivityCostUnit extends AbstractDataIdentifiableSystemStringImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Activity activity;
	private CostUnit costUnit;
	private Long financementAE;
	private Long financementCP;
	private Long arbitrageAE;
	private Long arbitrageCP;
	private Long budgetAE;
	private Long budgetCP;
	private String procedure;
	private String exempted;
	
	public static final String FIELD_ACTIVITY = "activity";
	public static final String FIELD_COST_UNIT = "costUnit";
	
}