package org.cyk.system.sibua.client.controller.api;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cyk.system.sibua.client.controller.entities.Section;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.client.controller.ControllerEntity;

public interface SectionController extends ControllerEntity<Section> {

	@SuppressWarnings("unchecked")
	static Collection<Section> readStatic() {
		if(!COLLECTION.isHasBeenSet())
			COLLECTION.set(DependencyInjection.inject(SectionController.class).read(new Properties().setIsPageable(Boolean.FALSE)));
		return (Collection<Section>) COLLECTION.get();
	}
	
	Value COLLECTION = DependencyInjection.inject(Value.class);
	
	static String getCodeFromExcelString(String string) {
		if(StringHelper.isBlank(string))
			return null;
		Matcher matcher = CODE_EXCEL_STRING_PATTERN.matcher(string);
		if(!matcher.find())
			return null;		
		return matcher.group(1);
	}
	
	Pattern CODE_EXCEL_STRING_PATTERN = Pattern.compile(".*(\\d{3}).*");
}
