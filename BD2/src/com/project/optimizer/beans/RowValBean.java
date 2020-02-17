/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer.beans;

public class RowValBean {
	
	private String rName;
	private double rhsVal;
	
	public String getrName() {
		return rName;
	}
	public void setrName(String rName) {
		this.rName = rName;
	}
	public double getRhsVal() {
		return rhsVal;
	}
	public void setRhsVal(double rhsVal) {
		this.rhsVal = rhsVal;
	}
	public RowValBean(String rName, double rhsVal) {
		super();
		this.rName = rName;
		this.rhsVal = rhsVal;
	}

	
	
}
