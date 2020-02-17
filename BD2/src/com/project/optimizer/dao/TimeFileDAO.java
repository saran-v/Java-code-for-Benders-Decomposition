/**
 * Author - Saravanan V - Wayne State University
 *
 * This code is for the class IE-7995. Any unauthorized use
 * for any other purpose is prohibited. You cannot distribute this code.
 */

package com.project.optimizer.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import com.project.optimizer.beans.ParameterBean;
import com.project.optimizer.beans.TimeFileBean;

public class TimeFileDAO
{
    public TimeFileDAO(String fileName)
    {
         try
         {
           String strFileName = ParameterBean.getInputFolderName() + fileName + ".tim";
           BufferedReader in = new BufferedReader(new FileReader(strFileName));

           String str;
           String[] readVal = new String[10];

           while ((str = in.readLine()) != null)
           {              
                StringTokenizer st = new StringTokenizer(str,"   ");
                int arrIndex = 0;
                while (st.hasMoreTokens())
                {
                    readVal[arrIndex] = st.nextToken();                    
                    arrIndex++;
                }               
                	
                if(arrIndex > 2 && readVal[2].equals("PERIOD1")) //"STAGE-1"))
                {
                	TimeFileBean.setFsVar(readVal[0]);
                	TimeFileBean.setFsCon(readVal[1]);               	
                }
                	
                if(arrIndex > 2 && readVal[2].equals("PERIOD2")) // "STAGE-2"))
                {
                	TimeFileBean.setSsVar(readVal[0]);
                	TimeFileBean.setSsCon(readVal[1]);               	
                }                                
           }

           readVal = null;
           in.close();
         }
         catch(Exception e)
         {
        	e.printStackTrace();
        	System.out.println("Error in TimeFileDAO:");
            System.exit(0);
         }
    }
}