package com.team766.config;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import org.junit.Test;

public class ConfigFileReaderTest {
	@Test
	public void test() throws IOException {
		ConfigFileReader.instance = new ConfigFileReader(File.createTempFile("config_file_test", ".json").getPath());
		ConfigFileReader.getInstance().getDouble("test.sub.key");
		assertEquals("{\"test\": {\"sub\": {\"key\": null}}}", ConfigFileReader.getInstance().getJsonString());
	}
}