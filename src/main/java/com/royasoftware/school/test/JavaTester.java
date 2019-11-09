package com.royasoftware.school.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaTester {
	private static Logger logger = LoggerFactory.getLogger(JavaTester.class);
	
	public static void main(String[] args) {
//		B b = new B();
		try {
			Properties prop = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();           
//			InputStream stream = loader.getResourceAsStream("D:\\RP\\Tests\\SpringBoot_Training\\testjmsadpater.properties");
			prop.load(new FileInputStream("./testjmsadpater.properties") );
			String cloServString = prop.getProperty("cloServString");
			String osbPsString = prop.getProperty("osbPsString");
			String allServString = prop.getProperty("allServString");
			String allPsString = prop.getProperty("allPsString");

//			String cloServString = "serv1,serv2,serv3"; 
//			String osbPsString = "ps1,ps2,ps3"; 
//			String allServString = "serv1,serv2,serv3,serv4,serv5,serv6"; 
//			String allPsString = "ps1,ps2,ps3,ps4,ps5,ps6";

			Set<String> cloServSet = new HashSet<String>(Arrays.asList(cloServString.split(",")));
			Set<String> osbPsSet = new HashSet<String>(Arrays.asList(osbPsString.split(",")));
			Set<String> allServSet = new HashSet<String>(Arrays.asList(allServString.split(",")));
			Set<String> allPsSet = new HashSet<String>(Arrays.asList(allPsString.split(",")));

			Set<String> osbServSet = new HashSet<String>();
			osbServSet.addAll(allServSet);
			osbServSet.removeAll(cloServSet); 
					
			String osbServOut = String.join(",", osbServSet);
			logger.info("osbServOut="+osbServOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}

class B extends A {
	private static Logger logger = LoggerFactory.getLogger(A.class);
	private int l = 0;
}