package com.royasoftware.school.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaTester {
	private static Logger logger = LoggerFactory.getLogger(JavaTester.class);
	
	public static void main(String[] args) {
		B b = new B();
		
	}
}

class A {
	private static Logger logger = LoggerFactory.getLogger(A.class);
	static int i = 100;
	private int l = 0;
	static{
		int b  = i--;
		logger.info("i="+i); 
		logger.info("b="+b); 
		int a = b - --i;
		logger.info("A static a="+a);		
	}

//	{
//		i = i++ + ++i;
//		logger.info("A Normal i="+i);
//		logger.info("A Normal l="+this.l);
//	}
}

class B extends A {
	private static Logger logger = LoggerFactory.getLogger(A.class);
	private int l = 0;
//	static{
//		i = i-- - --i;
//		logger.info("B static i="+i);		
//	}

//	{
//		i = i++ + ++i;
//		logger.info("B Normal i="+i);
//		logger.info("B Normal l="+this.l);
//	}
}