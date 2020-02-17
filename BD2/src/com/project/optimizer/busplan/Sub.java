/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer.busplan;

import ilog.concert.IloConversion;
import ilog.concert.IloCopyable;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.project.optimizer.Optimizer;
import com.project.optimizer.beans.RowValBean;
import com.project.optimizer.beans.TimeFileBean;

public class Sub {

	public static IloCplex    subCplex;
	public static IloLPMatrix subLp;
	public static IloNumVar[] subVars;
	public static IloRange[]  subRngs;

	public static IloNumVar[] rsubVars;
	public static IloRange[]  rsubRngs;

	private static IloConversion relax;

	private static ArrayList<String>  rrowNames = new
			ArrayList<String>();

	private static int    rVars;
	private static int    rCons;
	private static double obj;

	private static double[] rhs;
	public  static double[] oRhs; // accessed by Fenchel cut model
	private static double[] vUB;

	private static double[] duals;
	public  static double[] soln;
	private static double   alphaValue;
	private static double[] betaValue;

	private static int[][]     indices;
	private static double[][]  values;

	private static boolean oStored = false;
	public static int     nFCuts;
	private static IloObjective objF;

	public static void readMPSFile(String fileName)
	{
		try
		{
			Sub.subCplex = new IloCplex();
			Sub.subCplex.importModel(fileName+".mps");
			Sub.subLp   = (IloLPMatrix)subCplex.LPMatrixIterator().next();
			Sub.subVars = Sub.subLp.getNumVars();
			Sub.subRngs = Sub.subLp.getRanges();

			Sub.subCplex.setOut(null);

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: MP MPS Reader");
		}
	}

	public static double getAlphaValue() {
		return alphaValue;
	}

	public static void setAlphaValue(double alphaValue) {
		Sub.alphaValue = alphaValue;
	}

	public static double[] getBetaValue() {
		return betaValue;
	}

	public static void setBetaValue(double[] betaValue) {
		Sub.betaValue = betaValue;
	}

	public static void getSubModel()
	{
		int enVMastIndex = TimeFileBean.getMpsVarNames().indexOf(TimeFileBean.getSsVar());
		int enCMastIndex = TimeFileBean.getMpsConNames().indexOf(TimeFileBean.getSsCon());

		try
		{
			Sub.subLp.removeCols(0, enVMastIndex);
			Sub.subLp.removeRows(0, enCMastIndex);

			IloCopyable[] vars = new IloCopyable[enVMastIndex];

			for(int i=0; i < enVMastIndex; i++)
				vars[i] = Sub.subVars[i];

			Sub.subCplex.delete(vars);

			Sub.rVars = Sub.subCplex.getNcols();
			Sub.rCons = Sub.subCplex.getNrows();

			Sub.rsubVars = Sub.subLp.getNumVars();

			addColsRows(); // adding the cons to cplex object
			Sub.rsubRngs = Sub.subLp.getRanges();

			for(int i=0; i < Sub.rsubRngs.length; i++)
				rrowNames.add(Sub.rsubRngs[i].getName());

            relax = Sub.subCplex.conversion(Sub.subLp.getNumVars(),
                    IloNumVarType.Float);

            Sub.subCplex.add(relax);

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: MasterModel");
		}
	}


	public static void addColsRows() throws IloException
	{
		ArrayList<Double> rhsArr = new ArrayList<Double>();

		int index = 0;
		for(IloNumVar v:Sub.rsubVars)
		{
			if(v.getUB() > 0) // initially it was 1
			{
				int[] ind    = new int[1];
				double[] val = new double[1];

				ind[0] = index;
				val[0] = 1.0;

				IloLinearNumExpr cons = Sub.subCplex.linearNumExpr();
				cons.addTerm(v, 1);

				IloRange r = Sub.subCplex.addLe(cons, v.getUB(), "v_UB_" + index);
				Sub.subLp.addRow(r);
//				SPBean.subLp.addRow(-0, v.getUB(), ind, val);
				v.setUB(Double.MAX_VALUE);
				rhsArr.add(v.getUB());
			}

			index++;
		}

		vUB =  new double[rhsArr.size()];

		index = 0;
		
		for(Double d:rhsArr)
			vUB[index++] = d;
		
	}

	public static void getLPFile(String fName, int insNo, int iter)
	{
		try {
			String filename = fName + "sub_" + insNo + "_" + iter + ".lp";

			if(Optimizer.debug)
				System.out.println(" fileName: " + filename);

			Sub.subCplex.exportModel(filename);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: GetLPFile");
		}
	}

	public static void getORhs() throws IloException
	{
		rhs = new double[Sub.rsubRngs.length];

		int i = 0;

		for(IloRange ir : Sub.rsubRngs)
			if(ir.getUB() < Double.MAX_VALUE)
				rhs[i++] = ir.getUB();
			else
				rhs[i++] = ir.getLB();

		if(!Sub.oStored)
		{
			oRhs = new double[Sub.rsubRngs.length];
			System.arraycopy(rhs, 0, oRhs, 0, oRhs.length); // source to dest
			Sub.oStored = true;
		}
	}
	
	
	public static void chgRhs(ArrayList<RowValBean> rwValBeanArr) throws IloException
	{
		// As of now all the constraints for T have to be stored
		//  change it to orhs to rhs. to make it generic
		for(RowValBean rvBean : rwValBeanArr)
		{
			int index = rrowNames.indexOf(rvBean.getrName());

			if(Sub.subLp.getRange(index).getLB() > -Double.MAX_VALUE )
				Sub.subLp.getRange(index).setLB(rvBean.getRhsVal());

			if(Sub.subLp.getRange(index).getUB() < Double.MAX_VALUE )
				Sub.subLp.getRange(index).setUB(rvBean.getRhsVal());

			if(Sub.oStored)
			{
				oRhs[index] = rvBean.getRhsVal(); // rhs changes for the changes scenario
			}
		}

		rhs = new double[Sub.rsubRngs.length];

		int i = 0;
		// ub for <= constraints  and lb for >= constraints
		for(IloRange ir : Sub.rsubRngs)
			if(ir.getUB() < Double.MAX_VALUE)
				rhs[i++] = ir.getUB();
			else
				rhs[i++] = ir.getLB();

		if(!Sub.oStored)
		{
			oRhs = new double[Sub.rsubRngs.length];
			System.arraycopy(rhs, 0, oRhs, 0, oRhs.length); // source to dest
			Sub.oStored = true;
		}
	}

	public static void adjustT(double[] xSol) throws IloException
	{

		for(int i=0; i < Sub.rCons; i++)
		{
			double adjValue = 0;
			for(int j=0; j < Sub.indices[i].length; j++)
			{
				if(Sub.indices[i][j] < TimeFileBean.getSsVarIndex())									
					adjValue += Sub.values[i][j]*xSol[Sub.indices[i][j]];
			}

				double rhs;
				rhs = Sub.oRhs[i];
				rhs -= adjValue;

				if(Sub.subLp.getRange(i).getLB() > -Double.MAX_VALUE )
					Sub.subLp.getRange(i).setLB(rhs);
				
				Sub.subLp.getRange(i).setUB(rhs);
		}
	}

	public static void solve(boolean isMIP)
	{
		try
		{
			Sub.subCplex.setParam(IloCplex.IntParam.RootAlg,
                    IloCplex.Algorithm.Dual);
			Sub.subCplex.setParam(IloCplex.BooleanParam.PreInd, false);

			Sub.subCplex.solve();
			obj   = Sub.subCplex.getObjValue();
			soln  = Sub.subCplex.getValues(rsubVars);

			if(!isMIP)
				duals = Sub.subCplex.getDuals(Sub.subLp);

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: MasterSolve");
		}
	}

	public static double getObj() {
		return obj;
	}

	public static void setObj(double obj) {
		Sub.obj = obj;
	}

	public static double[] getDuals() {
		return duals;
	}

	public static void setDuals(double[] duals) {
		Sub.duals = duals;
	}

	public static  void calculateAlpha(double prb)
	{
	  try 
		{
			for(int i=0; i < duals.length; i++)
				Sub.alphaValue += (duals[i]*oRhs[i]*prb);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static  void calculateBeta(int xArray, double prb) throws IloException
	{
		for(int i=0; i < Sub.rCons; i++)
		{
			for(int j=0; j < Sub.indices[i].length; j++)
			{

				if(Sub.indices[i][j] < TimeFileBean.getSsVarIndex())
					Sub.betaValue[Sub.indices[i][j]] += (Sub.values[i][j]*duals[i]*prb);
			}
		}
	}

	public static void getIndicesValues(int[][] ind, double[][] val)
	{
		Sub.indices = new int[Sub.subRngs.length][];
		Sub.values  = new double[Sub.subRngs.length][];

		for(int i=TimeFileBean.getSsConIndex(); i < ind.length; i++)
		{
			Sub.indices[i-TimeFileBean.getSsConIndex()] = new int[ind[i].length];
			Sub.values[i-TimeFileBean.getSsConIndex()]  = new double[val[i].length];

			for(int j=0; j < ind[i].length; j++)
			{
				Sub.indices[i-TimeFileBean.getSsConIndex()][j] = ind[i][j];
				Sub.values[i-TimeFileBean.getSsConIndex()][j]  = val[i][j];
			}
		}
	}

	public static int[][] getIndices() {
		return indices;
	}

	public static void setIndices(int[][] indices) {
		Sub.indices = indices;
	}

	public static double[][] getValues() {
		return values;
	}

	public static void setValues(double[][] values) {
		Sub.values = values;
	}

	public static double[] getSoln() {
		return soln;
	}

	public static void setSoln(double[] soln) {
		Sub.soln = soln;
	}

	public static  void createAlphaBeta(int xSize)
	{
		Sub.betaValue  = new double[xSize];
		Sub.alphaValue = 0;
	}

}
