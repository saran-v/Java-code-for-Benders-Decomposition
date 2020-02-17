/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer.beans;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeFileBean 
{
	private static String fsVar = "";
	private static String fsCon = "";
	
	private static String ssVar = "";
	private static String ssCon = "";
	
	private static int ssVarIndex = 0;
	private static int ssConIndex = 0;	
	
	private static ArrayList<String> mpsVarNames
		= new ArrayList<String>();

	private static ArrayList<String> mpsConNames
		= new ArrayList<String>();	
	
	public static int getSsVarIndex() {
		ssVarIndex = mpsVarNames.indexOf(ssVar);		
		return ssVarIndex;
	}

	public static void setSsVarIndex(int ssVarIndex) {
		TimeFileBean.ssVarIndex = ssVarIndex;
	}	
	
	public static int getSsConIndex() {
		ssConIndex = mpsConNames.indexOf(ssCon);
		return ssConIndex;
	}

	public static void setSsConIndex(int ssConIndex) {
		TimeFileBean.ssConIndex = ssConIndex;
	}

	public static ArrayList<String> getMpsVarNames() {
		return mpsVarNames;
	}
	
	public static void setMpsVarNames(ArrayList<String> mpsVarNames) {
		TimeFileBean.mpsVarNames.addAll(mpsVarNames); 
	}
	
	public static ArrayList<String> getMpsConNames() {
		return mpsConNames;
	}
	
	public static void setMpsConNames(ArrayList<String> mpsConNames) {
		TimeFileBean.mpsConNames.addAll(mpsConNames); 
	}
	
	public static String getFsVar() {
		return fsVar;
	}
	
	public static void setFsVar(String fsVar) {
		TimeFileBean.fsVar = fsVar;
	}
	public static String getFsCon() {
		return fsCon;
	}
	public static void setFsCon(String fsCon) {
		TimeFileBean.fsCon = fsCon;
	}
	public static String getSsVar() {
		return ssVar;
	}
	public static void setSsVar(String ssVar) {
		TimeFileBean.ssVar = ssVar;
	}
	public static String getSsCon() {
		return ssCon;
	}
	public static void setSsCon(String ssCon) {
		TimeFileBean.ssCon = ssCon;
	}	
}
