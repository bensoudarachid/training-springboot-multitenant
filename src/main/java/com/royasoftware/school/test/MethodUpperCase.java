package com.royasoftware.school.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodUpperCase {
	private static Logger logger = LoggerFactory.getLogger(MethodUpperCase.class);

	public boolean startsWithUpperCase(String input) {
		return input == null || input.isEmpty() ? false : Character.isUpperCase(input.charAt(0));
	}
	public boolean startsWithUpperCase2(String input) {
		return input == null || input.isEmpty() ? false : input.charAt(0) > 64 && input.charAt(0) < 91;
	}
	public boolean startsWithUpperCase3(String input) {
		char c = input == null || input.isEmpty() ? 0 : input.charAt(0);
		return c == 0 ? false : c > 64 && c < 91;
	}

	public static void main(String[] args) {
		MethodUpperCase muc = new MethodUpperCase();
		logger.info("Lets go!");
		String str = "hello"; 
		long startTime = System.nanoTime();
		for( long i = 0; i<100_000_000l ;i++ )
			muc.startsWithUpperCase(str+i);
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		logger.info(str+": " + muc.startsWithUpperCase(str));
		logger.info("Execution time in milliseconds: " + timeElapsed / 1000000);
		
		startTime = System.nanoTime();
		for( long i = 0; i<100_000_000l ;i++ )
			muc.startsWithUpperCase2(str+i);
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		logger.info(str+": " + muc.startsWithUpperCase2(str));
		logger.info("Execution time in milliseconds: " + timeElapsed / 1000000);

	}

}
