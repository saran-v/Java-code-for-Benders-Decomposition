/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */


package com.project.optimizer;
import com.project.optimizer.dao.TimeFileDAO;

public class Optimizer {

	public static String fileName;	
	public static boolean debug        = false;
	public static boolean useReduction = false;
	
	public static int runTime = 0;
	public static long stTime = 0;	
	
	public Optimizer() throws Exception
	{

	}
	/**
	 * Main program - reads the input data and calls Benders Algorithm
	 * @param args
	 */
    public static void main(String[] args) {

    	try 
    	{
    		fileName = args[0];
    		new TimeFileDAO(args[0]);
    		
    		long t1 = System.currentTimeMillis();
    		Algorithm alg = new Algorithm();
    		alg.CreateLPObjects(args[0]);

    		stTime = System.currentTimeMillis();    		
    		new BendersAlgorithm();
    		
    		System.out.println( " Total Time Taken (Secs): "
    				+ (System.currentTimeMillis() - t1)/1000);    		
		} 
    	catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    
    
}
