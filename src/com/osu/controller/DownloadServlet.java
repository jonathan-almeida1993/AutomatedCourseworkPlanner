package com.osu.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.osu.common.utils.ProgramGenerator;
import com.osu.database.pojo.ProgramPojo;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final int ARBITARY_SIZE = 1048;
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
      throws ServletException, IOException {
     
        resp.setContentType("text/plain");
        resp.setHeader("Content-disposition", "attachment; filename=result.pdf");
        String jsonData = req.getParameter("JDATA");
        System.out.println("CourseList="+jsonData);
        
        Gson gson = new Gson();
        ProgramPojo program = gson.fromJson(jsonData, ProgramPojo.class);
		program.setFirstName("Test");
		program.setLastName("User");
		program.setEmail("testuser@oregonstate.edu");
		
		try {
			ProgramGenerator.createPDF(program);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		FileInputStream in = new FileInputStream("/Users/jonathanalmeida/Projects/eclipse-workspace/AutomatedCourseworkPlanner/WebContent/WEB-INF/result.pdf");
		
		//InputStream in = req.getServletContext().getResourceAsStream("/WEB-INF/result.pdf");
		OutputStream out = resp.getOutputStream();
		
		byte[] buffer = new byte[ARBITARY_SIZE];
		
		 int numBytesRead;
         while ((numBytesRead = in.read(buffer)) > 0) {
             out.write(buffer, 0, numBytesRead);
         }
         
         in.close();
         out.flush();
         out.close();
    }
}
