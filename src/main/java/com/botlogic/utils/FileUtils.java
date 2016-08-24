package com.botlogic.utils;

import java.io.File;

public class FileUtils {

	public static File loadFileFromClasspath(String fileName) {
		File file = new File(FileUtils.class.getClassLoader().getResource(fileName).getFile());
		return file;
	}

}
