/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */
package com.project.optimizer;

import com.project.optimizer.busplan.Master;
import com.project.optimizer.busplan.Sub;

public class Algorithm {
	
	public Algorithm() throws Exception
	{
	}	

	/**
	 * Method to read the input files
	 * @param fileName
	 */
	public void CreateLPObjects(String fileName)
	{
		Master.readMPSFile(fileName);
		Master.getNames();
		Master.getMasterModel();		
		
		Sub.readMPSFile(fileName);
		Sub.getSubModel();
		
		Sub.getIndicesValues(Master.getIndices(), Master.getValues());
	}	
    
}
