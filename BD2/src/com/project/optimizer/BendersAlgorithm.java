/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer;

import com.project.optimizer.busplan.Master;
import com.project.optimizer.busplan.Sub;

public class BendersAlgorithm {

	public static double lb = 0;
	public static double ub = 0;

	public BendersAlgorithm() {

		super();

		try {
			Algorithm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(" Error: L-shaped Algorithm:");
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * Logic for Benders' algorithm
	 * @throws Exception
	 */
	public static void Algorithm() throws Exception
	{
		Master.solve();
		Master.addColumn();

		double lBound,tUbound = 0;
		double uBound = Double.MAX_VALUE;
		int iter = 0;
 	    lBound = Master.getObjValue();

 	    Sub.getORhs();
		
 	    System.out.println(); 	    
 	    System.out.println("Algorithm Starts: "); 	    
		do
		{ // starts here
			Sub.createAlphaBeta(Master.getxValues().length);
	 	    tUbound = Master.getFSOj(iter);

		   	Sub.adjustT(Master.getxValues());
		 	Sub.getLPFile("",0,iter);		   	
		   	Sub.solve(false);

		   	Sub.calculateAlpha(1.0); //StoFileBean.sceProb.get(entry.getKey()));
		   	Sub.calculateBeta(Master.getxValues().length, 1.0); //StoFileBean.sceProb.get(entry.getKey()));
		   	tUbound += (Sub.getObj()*1.0); //StoFileBean.sceProb.get(entry.getKey()));
			
	 	    Master.addBendersCut(Sub.getBetaValue(), Sub.getAlphaValue());

		 	Master.writeLPFile("",iter);	 	    
	 	    Master.solve();
	 	    lBound = Master.getObjValue();

	 	    if(tUbound < uBound)
	 	    	uBound = tUbound;

	 	    iter++;
	 	    
			System.out.println("LB: " + lBound + " UB: " + uBound + " Iter: " + iter);
			// ends here

		}while(uBound - lBound > 0.001); 
		
		// use the following criterion if you would like to stop the run based on timelimit
		// && (System.currentTimeMillis() - Optimizer.stTime)/1000 < Optimizer.runTime); // && iter < 10);

 	    System.out.println("Algorithm Ends: ");		
		System.out.println();   
		
		lb = lBound;
		ub = uBound;
	}

}
