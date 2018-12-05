package com.osu.common.utils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;

import com.google.gson.Gson;
import com.osu.common.constants.PDFOtterParameters;
import com.osu.database.pojo.CoursePojo;
import com.osu.database.pojo.CoursePojoList;
import com.osu.database.pojo.ProgramPojo;

public class ProgramGenerator{
	
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 65432;
    private static final String PYTHON_COMPONENT = "D:/devOps/eclipse-workspace/AutomatedCourseworkPlanner/WebContent/WEB-INF";
    
    public static void sendServerData(String data) {
    	
	    // initialize socket and input output streams 
        Socket socket            = null; 
        DataOutputStream out     = null; 
        
    	// establish a connection 
        try { 
	        
        	socket = new Socket(ADDRESS, PORT); 
	        System.out.println("Connected"); 
	       
	        // sends output to the socket 
	        out = new DataOutputStream(socket.getOutputStream()); 
	        
        }catch(UnknownHostException u) { 
        	System.out.println(u); 
        }catch(IOException i) { 
            System.out.println(i); 
        } 
        
        byte buffer[] = new byte[100];
        try {
			out.write(data.getBytes());
			out.write("@@@".getBytes());
        	socket.getInputStream().read(buffer);
            System.out.println("SERVER: "+new String(buffer));
            
	    }catch(IOException i) { 
	        System.out.println(i); 
	    } 
  
        // close the connection 
        try { 
            out.close(); 
            socket.close(); 
        }catch(IOException i) { 
            System.out.println(i); 
        } 
    }
    
    public static void createPDF(ProgramPojo program) throws IOException, InterruptedException {
    	//create json string
    	String jsonStr = createProgramJSON(program);
    	
    	//send the json string to server
		ProcessBuilder pb = new ProcessBuilder("py",PYTHON_COMPONENT+"/generatePDF.py");
	    pb.directory(new File(PYTHON_COMPONENT));
	    pb.redirectErrorStream(true);
	    Process p = pb.start();
	    Thread.sleep(5000);
		sendServerData(jsonStr);
		Thread.sleep(5000);
    }

    public static String createProgramJSON(ProgramPojo program) {
    	String jsonStr = null;
    	Gson gson = new Gson();
		HashMap<String, String> pdfOtterParams = new HashMap<>();
		ArrayList<CoursePojo> coursework = program.getResults();
		for(int i = 0; i < coursework.size(); i++) {
			String courseDept = "";
			String courseNo = "";
			if(coursework.get(i).getCode().contains(" ")) {
				courseDept = coursework.get(i).getCode().split(" ")[0];
				courseNo = coursework.get(i).getCode().split(" ")[1];
			}
			pdfOtterParams.put(PDFOtterParameters.CourseTitle.replaceAll("#", ""+(i+1)),coursework.get(i).getTitle());
			pdfOtterParams.put(PDFOtterParameters.CourseDept.replaceAll("#", ""+(i+1)),courseDept);
			pdfOtterParams.put(PDFOtterParameters.CourseNo.replaceAll("#", ""+(i+1)),courseNo);
		}
		pdfOtterParams.put(PDFOtterParameters.EMAIL,program.getEmail());
		pdfOtterParams.put(PDFOtterParameters.FIRSTNAME,program.getFirstName());
		pdfOtterParams.put(PDFOtterParameters.LASTNAME,program.getLastName());
		pdfOtterParams.put(PDFOtterParameters.MS,"x");
		if(program.isResearch()) {
			pdfOtterParams.put(PDFOtterParameters.THESIS,"x");
		}else {
			pdfOtterParams.put(PDFOtterParameters.NONTHESIS,"x");
		}
		jsonStr = gson.toJson(pdfOtterParams);
		System.out.println("PDF String = "+jsonStr);
		System.out.println("PDF String length = "+jsonStr.length());
    	return jsonStr;
    }
    

}