package org.thuannt.waze_hcm_scraper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.thuannt.waze_hcm_scraper.utils.FileHelpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SpringBootTest
class WazeHcmScraperApplicationTests {

	@Test
	void testRegex() {
		var test = "LYTHUONGKIET_1740002408273.json";
		var part1 = FileHelpers.getPartFromFileName(test, 1);
		var part2 = FileHelpers.getPartFromFileName(test, 2);
		Assertions.assertEquals("LYTHUONGKIET", part1);
		Assertions.assertEquals("1740002408273", part2);
	}

}
