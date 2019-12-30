package org.cyk.system.sibua.client.controller.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cyk.system.sibua.client.controller.entities.Activity;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.ControllerEntity;

public interface ActivityController extends ControllerEntity<Activity> {

	static String getCodeFromExcelString(String string) {
		if(StringHelper.isBlank(string))
			return null;
		Matcher matcher = CODE_EXCEL_STRING_PATTERN.matcher(string);		
		if(!matcher.find())
			return null;		
		return matcher.group(1);
	}
	
	Pattern CODE_EXCEL_STRING_PATTERN = Pattern.compile(".*(\\d{11}).*");
	
}
