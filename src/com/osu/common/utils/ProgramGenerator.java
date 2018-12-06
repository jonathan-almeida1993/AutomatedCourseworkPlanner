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
		
		boolean isThesisSet = false;
		boolean isProjectSet = false;
		int position = 1; //this is the course position in the document
		int totalRegularCredits = 0;
		
		/*set the name and email*/
		pdfOtterParams.put(PDFOtterParameters.EMAIL,program.getEmail());
		pdfOtterParams.put(PDFOtterParameters.FIRSTNAME,program.getFirstName());
		pdfOtterParams.put(PDFOtterParameters.LASTNAME,program.getLastName());
		pdfOtterParams.put(PDFOtterParameters.MIDDLENAME,"M");
		pdfOtterParams.put(PDFOtterParameters.ID,"9999999999");
		
		/*set the type of degree*/
		if("Coursework".equals(program.getType())) {
			pdfOtterParams.put(PDFOtterParameters.MENG,"X");
			pdfOtterParams.put(PDFOtterParameters.NONTHESIS,"X");
		}else {
			pdfOtterParams.put(PDFOtterParameters.MS,"X");
		}
		
		/*set the capstone credits*/
		if("Research".equals(program.getType())) {
			pdfOtterParams.put(PDFOtterParameters.THESIS,"X");
			pdfOtterParams.put(PDFOtterParameters.MSTHESIS, "MS THESIS");
			pdfOtterParams.put(PDFOtterParameters.MS_THESIS_CREDITS, String.valueOf(program.getCapstoneCredits()));
			pdfOtterParams.put(PDFOtterParameters.MS_THESIS_DEPT, "CS");
			isThesisSet = true;
		}

		if("Project".equals(program.getType())) {
			pdfOtterParams.put(PDFOtterParameters.NONTHESIS,"X");
			pdfOtterParams.put(PDFOtterParameters.MSPROJECT, "PROJECT");
			pdfOtterParams.put(PDFOtterParameters.MS_PROJECT_CREDITS, String.valueOf(program.getCapstoneCredits()));
			pdfOtterParams.put(PDFOtterParameters.MS_PROJECT_DEPT, "CS");
			isProjectSet = true;
		}
		
		/*set the coursework. ASSUMES THAT THESIS AND PROJECT BLANKET CREDITS ARE FROM CS DEPT */
		ArrayList<CoursePojo> coursework = program.getResults();
		for(int i = 0; i < coursework.size(); i++) {
			
			CoursePojo course = coursework.get(i);
			
			String courseDept = "";
			String courseNo = "";
			
			if(course.getCode().contains(" ")) {
				courseDept = course.getCode().split(" ")[0];
				courseNo = course.getCode().split(" ")[1];
			}
			
			/*check to see if its a blanket course*/
			if(course.isBlanket()) {
				
				/*check if the course is thesis or project. if this is already set then just alter the credits*/
				if("CS 503".equals(course.getCode()) && isThesisSet) {
					
					pdfOtterParams.put(PDFOtterParameters.MS_THESIS_CREDITS, String.valueOf(program.getCapstoneCredits() + course.getCredits()));
					
				}else if("CS 506".equals(course.getCode()) && isProjectSet) {
					
					pdfOtterParams.put(PDFOtterParameters.MS_PROJECT_CREDITS, String.valueOf(program.getCapstoneCredits() + course.getCredits()));
					
				}else {
					
					/*set blanket credits entirely in their proper location.*/
					if("503".equals(courseNo)) {
						pdfOtterParams.put(PDFOtterParameters.MSTHESIS, course.getTitle());
						pdfOtterParams.put(PDFOtterParameters.MS_THESIS_CREDITS, String.valueOf(course.getCredits()));
						pdfOtterParams.put(PDFOtterParameters.MS_THESIS_DEPT, courseDept);
					}else if("501".equals(courseNo)) {
						pdfOtterParams.put(PDFOtterParameters.RESEARCH, course.getTitle());
						pdfOtterParams.put(PDFOtterParameters.RESEARCH_CR, String.valueOf(course.getCredits()));
						pdfOtterParams.put(PDFOtterParameters.RESEARCH_DEPT, courseDept);
					}else if("505".equals(courseNo)) {
						pdfOtterParams.put(PDFOtterParameters.READING, course.getTitle());
						pdfOtterParams.put(PDFOtterParameters.READING_CR, String.valueOf(course.getCredits()));
						pdfOtterParams.put(PDFOtterParameters.READING_DEPT, courseDept);
					}else if("506".equals(courseNo)) {
						pdfOtterParams.put(PDFOtterParameters.MSPROJECT, course.getTitle());
						pdfOtterParams.put(PDFOtterParameters.MS_PROJECT_CREDITS, String.valueOf(course.getCredits()));
						pdfOtterParams.put(PDFOtterParameters.MS_PROJECT_DEPT, courseDept);
					}else if("510".equals(courseNo)) {
						pdfOtterParams.put(PDFOtterParameters.INTERNSHIP, course.getTitle());
						pdfOtterParams.put(PDFOtterParameters.INTERNSHIP_CR, String.valueOf(course.getCredits()));
						pdfOtterParams.put(PDFOtterParameters.INTERNSHIP_DEPT, courseDept);
					}
					
				}
				
			}else {
				
				/*this is a regular course*/
				totalRegularCredits += course.getCredits();
				pdfOtterParams.put(PDFOtterParameters.CourseTitle.replaceAll("#", ""+position),course.getTitle());
				pdfOtterParams.put(PDFOtterParameters.CourseDept.replaceAll("#", ""+position),courseDept);
				pdfOtterParams.put(PDFOtterParameters.CourseNo.replaceAll("#", ""+position),courseNo);
				pdfOtterParams.put(PDFOtterParameters.CourseCr.replaceAll("#", ""+position),String.valueOf(course.getCredits()));
				position++;
			}
		}
		
		/*set miscellaneous figures*/
		pdfOtterParams.put(PDFOtterParameters.GRAND_TOTAL_CR, String.valueOf(program.getBlanketCredits() + program.getCapstoneCredits() +
																program.getBucketCredits() + program.getOtherCredits()));
		pdfOtterParams.put(PDFOtterParameters.BLANKET_TOTAL, String.valueOf(program.getBlanketCredits() + program.getCapstoneCredits()));
		pdfOtterParams.put(PDFOtterParameters.ETHICAL_TRAINING, "CITI");
		pdfOtterParams.put(PDFOtterParameters.ACAD_UNIT, "DEPT. OF ELECTRICAL ENGG. & COMPUTER SCI.");
		pdfOtterParams.put(PDFOtterParameters.MAJOR, "COMPUTER SCIENCE");
		pdfOtterParams.put(PDFOtterParameters.DEGREE_RCVD, "TEST UNIVERSITY");
		pdfOtterParams.put(PDFOtterParameters.DEGREE, "BS");
		pdfOtterParams.put(PDFOtterParameters.PHONE, "111 - 222 - 1234");
		pdfOtterParams.put(PDFOtterParameters.TOTAL_COURSE_CR, String.valueOf(totalRegularCredits));
		
		jsonStr = gson.toJson(pdfOtterParams);
		System.out.println("PDF String = "+jsonStr);
		System.out.println("PDF String length = "+jsonStr.length());
    	return jsonStr;
    }
    

}