package com.daway.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
	public static long id = 0;
	
	public static String northdata = "";
	public static String southdata = "";
	
	public Properties readProperties(String name) throws IOException{
		 InputStream is = Utils.class.getClassLoader().getResourceAsStream(name);
         Properties prop=new Properties();
         prop.load(is);
         return prop;
	}
}
	