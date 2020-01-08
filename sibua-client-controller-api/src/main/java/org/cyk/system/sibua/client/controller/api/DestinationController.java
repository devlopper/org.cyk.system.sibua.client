package org.cyk.system.sibua.client.controller.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.cyk.system.sibua.client.controller.entities.Destination;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.ControllerEntity;

public interface DestinationController extends ControllerEntity<Destination> {

	static String getCodeFromExcelString(String string) {
		if(StringHelper.isBlank(string))
			return null;
		string = RegExUtils.removeAll(string, " ");
		Matcher matcher = CODE_EXCEL_STRING_PATTERN.matcher(string);		
		if(!matcher.find())
			return null;		
		return matcher.group(1);
	}
	
	Pattern CODE_EXCEL_STRING_PATTERN = Pattern.compile(".*(\\d{9}).*");
	
}
