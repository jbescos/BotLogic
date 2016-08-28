package com.botlogic.audio;

import java.io.File;

@FunctionalInterface
public interface AudioFileListener {

	void notify(File file);
	
}
