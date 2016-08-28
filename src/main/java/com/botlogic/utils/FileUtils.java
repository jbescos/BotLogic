package com.botlogic.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class FileUtils {

	public static File loadFileFromClasspath(String fileName) throws IOException {
		File file = File.createTempFile("classpathFile", ".data");
		try(InputStream inputStream = FileUtils.class.getResourceAsStream(fileName); OutputStream outputStream = new FileOutputStream(file)){
			IOUtils.copy(inputStream, outputStream);
			return file;
		}
	}

}
