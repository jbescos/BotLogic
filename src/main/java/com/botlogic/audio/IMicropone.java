package com.botlogic.audio;

import java.io.File;
import java.util.function.Supplier;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public interface IMicropone extends Supplier<File>, AutoCloseable{

	public final static float SAMPLE_RATE = 16000;
	public final static int SAMPLE_SIZE_BITS = 16;
	public final static int CHANNELS = 1;
	public final static boolean SIGNED = true;
	public final static boolean BIG_ENDIAN = true;
	public final static AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;
	public final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
	public final static File FAILED_AUDIO = new File("");
	
}
