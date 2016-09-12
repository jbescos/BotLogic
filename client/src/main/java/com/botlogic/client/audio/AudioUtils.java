package com.botlogic.client.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioUtils {

	private final static int HEAD_WAV_BYTES = 52;
	private final static Logger log = LogManager.getLogger();
	
	public static int getMaxAvg(byte[] audioData, int divideBy){
		int chunk = audioData.length/divideBy;
		List<Integer> avgs = new ArrayList<>();
		int from = 0;
		for(int i=0;i<divideBy;i++){
			int to = from + chunk;
			int avg = getAvgVolume(Arrays.copyOfRange(audioData, from, to));
			avgs.add(avg);
			from = to;
		}
//		log.debug("AVG: "+avgs);
		return Collections.max(avgs);
	}
	
	private static int getAvgVolume(byte[] audioData){
		long lSum = 0;
	    for(int i=0; i<audioData.length; i++){
	        lSum = lSum + audioData[i];
	    }
	    double dAvg = lSum / audioData.length;

	    double sumMeanSquare = 0d;
	    for(int j=0; j<audioData.length; j++)
	        sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

	    double averageMeanSquare = sumMeanSquare / audioData.length;
	    int total = (int)(Math.pow(averageMeanSquare,0.5d) + 0.5)-HEAD_WAV_BYTES;
	    return total;
	}
	
}
