package com.grishberg.parser;

/**
 * Created by fesswood on 29.10.15.
 */
public class RdrParser {

	public static RdrRaw parseRdr(String string) {
		//log.info(String.format("parse size=%d",string == null ? -1 : string.length()));
		 RdrRaw result = null;
		 if(string.contains("TIME_STAMP")){
			 return null;
		 }
		 result = RdrRaw.getInstance(string);
	     return result;
	}
}
