/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer.beans;

import java.util.ArrayList;
import java.util.HashMap;

public class StoFileBean 
{
	public static HashMap<String, ArrayList<RowValBean>> stoHash
		= new HashMap<String, ArrayList<RowValBean>>();
	
    public static HashMap<String,Double> sceProb = new HashMap<String,Double>();
	
	private static String fsVar = "";
	private static String fsCon = "";
	
	private static String ssVar = "";
	private static String ssCon = "";	
	
	private static ArrayList<String> mpsVarNames
		= new ArrayList<String>();

	private static ArrayList<String> mpsConNames
		= new ArrayList<String>();	
	
	public static ArrayList<String> getMpsVarNames() {
		return mpsVarNames;
	}
	
	public static void setMpsVarNames(ArrayList<String> mpsVarNames) {
		StoFileBean.mpsVarNames.addAll(mpsVarNames); 
	}
	
	public static ArrayList<String> getMpsConNames() {
		return mpsConNames;
	}
	
	public static void setMpsConNames(ArrayList<String> mpsConNames) {
		StoFileBean.mpsConNames.addAll(mpsConNames); 
	}
	
	public static String getFsVar() {
		return fsVar;
	}
	
	public static void setFsVar(String fsVar) {
		StoFileBean.fsVar = fsVar;
	}
	public static String getFsCon() {
		return fsCon;
	}
	public static void setFsCon(String fsCon) {
		StoFileBean.fsCon = fsCon;
	}
	public static String getSsVar() {
		return ssVar;
	}
	public static void setSsVar(String ssVar) {
		StoFileBean.ssVar = ssVar;
	}
	public static String getSsCon() {
		return ssCon;
	}
	public static void setSsCon(String ssCon) {
		StoFileBean.ssCon = ssCon;
	}	
}
