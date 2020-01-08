package org.cyk.system.sibua.client.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.cyk.system.sibua.client.controller.api.SectionController;
import org.cyk.utility.test.weld.AbstractWeldUnitTest;
import org.junit.jupiter.api.Test;

public class UnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void section_code_from_section_coma_code_text_101() {
		__assertSectionCodeFromString__("Section: 101 Représentation Nationale","101");
	}
	
	@Test
	public void section_code_from_section_code_coma_text_101() {
		__assertSectionCodeFromString__("Section 101: Représentation Nationale","101");
	}
	
	@Test
	public void section_code_from_section_coma_code_text_1_0_1() {
		__assertSectionCodeFromString__("Section: 1 0 1 Représentation Nationale","101");
	}
	
	public void __assertSectionCodeFromString__(String string,String expectedCode) {
		assertThat(SectionController.getCodeFromExcelString(string)).isEqualTo(expectedCode);
	}
}
