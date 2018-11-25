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
import com.osu.database.pojo.CoursePojoList;
import com.osu.database.pojo.ProgramPojo;

public class ProgramGenerator{
	
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 65432;
    
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

	public static void main(String args[]) throws IOException, InterruptedException {
		Gson gson = new Gson();
		HashMap<String, String> pdfOtterParams = new HashMap<>();
		pdfOtterParams.put(PDFOtterParameters.CourseTitle1,"THEORY OF COMPUTATION");
		pdfOtterParams.put(PDFOtterParameters.CourseTitle2,"SOFTWARE ENGINEERING METHODS");
		pdfOtterParams.put(PDFOtterParameters.CourseTitle3,"ARTIFICIAL INTELLIGENCE");
		pdfOtterParams.put(PDFOtterParameters.EMAIL,"almeidaj@oregonstate.edu");
		pdfOtterParams.put(PDFOtterParameters.FIRSTNAME,"JONATHAN");
		pdfOtterParams.put(PDFOtterParameters.LASTNAME,"ALMEIDA");
		pdfOtterParams.put(PDFOtterParameters.MS,"x");
		pdfOtterParams.put(PDFOtterParameters.NONTHESIS,"x");
		String jsonStr = gson.toJson(pdfOtterParams);
		System.out.println("PDF String = "+jsonStr);
		System.out.println("PDF String length = "+jsonStr.length());
		
		ProcessBuilder pb = new ProcessBuilder("py","C:/Temp/generatePDF.py");
	    pb.directory(new File("C:/Temp"));
	    pb.redirectErrorStream(true);
	    Process p = pb.start();
	    Thread.sleep(5000);
		sendServerData(jsonStr);
	}
	
	public static ArrayList<String> parseProgramOfStudyInfo(ProgramPojo program) {
		
	    ArrayList<String> command = new ArrayList<>();
	    /*create a curl command with authentication parameters*/
	    command.add("curl");
	    command.add("-X");
	    command.add("POST");
	    command.add("https://www.pdfotter.com/api/v1/pdf_templates/tem_G2r8QYm5bHroPf/fill");
	    command.add("-u");
	    command.add("test_o4PWUBbYZ87K45jrKGswbQCFvYe4vNt3:");
	    
	    /*parse program details and add them to the command*/
	    command.add("-d");
	    command.add("data[First Name]="+program.getFirstName());
	    command.add("-d");
	    command.add("data[Last Name ]="+program.getLastName());
	    command.add("-d");
	    command.add("data[MS]=x");
		command.add("-d");
//		command.add("data[CourseTitle1]="+coursework.getResults().get(1).getTitle());
		command.add("data[CourseTitle1]=THEORY OF COMPUTATION");
		command.add("-d");
		command.add("data[DeptRow1]=CS");
		command.add("-d");
		command.add("data[NoRow1]=325");
		command.add("-d");
		command.add("data[CourseTitle2]=THEORY OF COMPUTATION");
		command.add("-d");
		command.add("data[CourseTitle3]=THEORY OF COMPUTATION");
	    //add courses to the command
	    //addCourses(command, program.getCoursework());
	    return command;
	}

	private static void addCourses(ArrayList<String> command, CoursePojoList coursework) {
		command.add("-d");
//		command.add("data[CourseTitle1]="+coursework.getResults().get(1).getTitle());
		command.add("data[CourseTitle1]='THEORY'");
		command.add("-d");
		command.add("data[DeptRow1]=CS");
		command.add("-d");
		command.add("data[NoRow1]=325");
		command.add("-d");
		command.add("data[CourseTitle2]=THEORY OF COMPUTATION");
		command.add("-d");
		command.add("data[CourseTitle3]=THEORY OF COMPUTATION");
//		command.add("data[CourseTitle2]="+coursework.getResults().get(1).getTitle());
/*		command.add("-d");
		command.add("data[CourseTitle3]="+coursework.getResults().get(1).getTitle());
		command.add("-d");
		command.add("data[CourseTitle4]="+coursework.getResults().get(1).getTitle());*/
	}

	public static void generatePDF(ArrayList<String> command) throws IOException {

	    ProcessBuilder pb = new ProcessBuilder(command);
	    pb.directory(new File("C:/Temp"));
	    pb.redirectErrorStream(true);
	    Process p = pb.start();
	    InputStream is = p.getInputStream();

	    FileOutputStream outputStream = new FileOutputStream("C:/Temp/program_of_study.pdf");

	    BufferedInputStream bis = new BufferedInputStream(is);
	    int x = 1000000;
	    byte[] bytes = new byte[x];
	    int numberByteReaded;
	    while ((numberByteReaded = bis.read(bytes, 0, 1000000)) != -1) {
	        outputStream.write(bytes, 0, numberByteReaded);
	        Arrays.fill(bytes, (byte) 0);
	    }

	    outputStream.flush();
	    outputStream.close();
	}
	
	public static void checkPDFPy() throws IOException {
		ProcessBuilder pb = new ProcessBuilder("py","C:/Temp/generatePDF.py");
	    pb.directory(new File("C:/Temp"));
	    pb.redirectErrorStream(true);
	    Process p = pb.start();
	}
}