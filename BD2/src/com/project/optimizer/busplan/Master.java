/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer.busplan;

import ilog.concert.IloColumn;
import ilog.concert.IloCopyable;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.project.optimizer.beans.TimeFileBean;

public class Master {

	private static IloCplex     mastCplex;
	private static IloLPMatrix  mastLp;
	private static IloNumVar[]  mastVars;
	private static IloRange[]   mastRngs;
	private static IloObjective obj;
	
	private static int[][]     indices;
	private static double[][]  values;
	private static double[]    xValues;
	
	private static double objValue;

	public static int[][] getIndices() {
		return indices;
	}

	public static void setIndices(int[][] indices) {
		Master.indices = indices;
	}

	public static double[][] getValues() {
		return values;
	}

	public static void setValues(double[][] values) {
		Master.values = values;
	}

	public static double[] getxValues() {
		return xValues;
	}

	public static void setxValues(double[] xValues) {
		Master.xValues = xValues;
	}

	public static void readMPSFile(String fileName)
	{
		try 
		{
			Master.mastCplex = new IloCplex();			
			Master.mastCplex.importModel(fileName + ".mps");
			Master.mastLp   = (IloLPMatrix)Master.mastCplex.LPMatrixIterator().next();
			Master.mastVars = Master.mastLp.getNumVars();
			Master.mastRngs = Master.mastLp.getRanges();
			
			Master.mastCplex.setOut(null);
			
			getNZValues();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: MP MPS Reader");			
		}	    
	}
	
	public static void getNames()
	{	
		ArrayList<String> names = new ArrayList<String>();
		
		for(int i=0;i < mastVars.length; i++)
			names.add(mastVars[i].getName());

		TimeFileBean.setMpsVarNames(names);
		
		names = new ArrayList<String>();
		
		for(int i=0;i < mastRngs.length; i++)		
			names.add(mastRngs[i].getName());
		
		TimeFileBean.setMpsConNames(names);		
	}
	
	public static void getMasterModel()
	{
		int enVMastIndex = TimeFileBean.getMpsVarNames().indexOf(TimeFileBean.getSsVar());		
		int enCMastIndex = TimeFileBean.getMpsConNames().indexOf(TimeFileBean.getSsCon());		
		
		try 
		{		
			System.out.println("Master.mastVars.length: " + Master.mastVars.length);
			System.out.println("Master.mastRngs.length: " + Master.mastRngs.length);
			
			System.out.println("enVMastIndex: " + enVMastIndex);
			System.out.println("enCMastIndex: " + enCMastIndex);
			
			Master.mastLp.removeCols(enVMastIndex, Master.mastVars.length - enVMastIndex);
			Master.mastLp.removeRows(enCMastIndex, Master.mastRngs.length - enCMastIndex);		
			
			IloCopyable[] vars = new IloCopyable[Master.mastVars.length - enVMastIndex];
			
			for(int i=enVMastIndex; i < Master.mastVars.length; i++)
				vars[i-enVMastIndex] = Master.mastVars[i];			

			Master.mastCplex.delete(vars);			
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	

	public static void getNZValues()
	{
		double[] lb = new double[Master.mastRngs.length];
		double[] ub = new double[Master.mastRngs.length];
		
		Master.indices = new int[Master.mastRngs.length][];
		Master.values  = new double[Master.mastRngs.length][];	
		
		try {
			Master.mastLp.getRows(0, Master.mastRngs.length, lb, ub, indices, values);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addColumn() throws Exception
	{
        obj =  Master.mastCplex.getObjective();	
//        obj.setSense(IloObjectiveSense.Maximize); // remove the comment if it is a maximize problem
        IloColumn col = Master.mastCplex.column(obj, 1.0);		
        IloNumVar thetaVar = Master.mastCplex.numVar(col,-Double.MAX_VALUE, Double.MAX_VALUE,"theta");   
		Master.mastLp.addColumn(thetaVar);
	}

	public static void addBendersCut(double[] val, double rhs) throws Exception
	{
		int[]    ind    = new int[val.length + 1];
		double[] values = new double[val.length + 1];
		
		for(int i=0; i< val.length; i++)
		{
			ind[i]    = i;
			values[i] = val[i];			
		}
		
		ind[ind.length-1] = Master.mastLp.getNcols()-1; 
		values[ind.length-1] = 1;		
		
		Master.mastLp.addRow(rhs, Double.MAX_VALUE, ind, values);		
	}

	public static void solve()
	{
		try 
		{	
//			Master.mastCplex.addMaximize(); // remove the comment if it is a maximize problem
			Master.mastCplex.solve();
			Master.objValue = Master.mastCplex.getObjValue();
			Master.xValues =  Master.mastCplex.getValues(Master.mastLp, 0, Master.mastCplex.getNcols(), IloCplex.IncumbentId);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: MasterSolve");			
		}		
	}	
	
	public static void writeLPFile(String fName, int iter)
	{
		try {
			Master.mastCplex.exportModel(fName + "mast" + iter + ".lp");
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: GetLPFile");			
		}
	}

	public static double getObjValue() {
		return objValue;
	}

	public static void setObjValue(double objValue) {
		Master.objValue = objValue;
	}
	
	public static double getFSOj(int iter)
	{
		if(iter == 0)
			return objValue;
		else
			return objValue-xValues[xValues.length - 1];
	}

}
