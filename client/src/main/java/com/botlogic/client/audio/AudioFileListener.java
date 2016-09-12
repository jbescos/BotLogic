package com.botlogic.client.audio;

import java.io.File;
import java.util.function.Function;

@FunctionalInterface
public interface AudioFileListener extends Function<File, Boolean> {

	Boolean apply(File file);
	
}
