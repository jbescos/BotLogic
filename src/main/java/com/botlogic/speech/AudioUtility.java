package com.botlogic.speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class AudioUtility {

	public static String toString64(File file) throws FileNotFoundException,
			IOException {
		try (FileInputStream fin = new FileInputStream(file)) {
			byte fileContent[] = new byte[(int) file.length()];
			fin.read(fileContent);
			String serial = Base64.getEncoder().encodeToString(fileContent);
			return serial;
		}
	}

	public static File toFile(String base64, String toPath) throws IOException {
		byte[] content = Base64.getDecoder().decode(base64.getBytes());
		Path path = Paths.get(toPath);
		return Files.write(path, content).toFile();
	}

}
