package com.osu.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.osu.common.constants.CommonConstants;
import com.osu.common.utils.CatalogParser;
import com.osu.common.utils.CourseRecommender;
import com.osu.dao.base.impl.CourseDAOImpl;
import com.osu.dao.base.interfaces.CourseDAO;
import com.osu.database.pojo.CoursePojo;


@WebServlet("/DataController")
public class DataController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String operation = request.getParameter("operationType");
		String jsonData = request.getParameter("jdata");

		if(operation != null && CommonConstants.OP_PARSE_CATALOG.equals(operation)) {
			//process catalog info and store into DB
			HashMap<String, CoursePojo> courseDetails = CatalogParser.processCourseCatalog("D:/devOps/eclipse-workspace/AutomatedCourseworkPlanner/src/com/osu/common/catalog/cs.json");
			ArrayList<CoursePojo> courseDetailsList = new ArrayList<CoursePojo>();
			for(String key: courseDetails.keySet()) {
				courseDetailsList.add(courseDetails.get(key));
			}
			CourseDAO dao = new CourseDAOImpl();
			String status = dao.populateDatabase(courseDetailsList);
			System.out.println("Populate DB Status: "+status);
		}
		response.getWriter().append("Served at: ").append(request.getContextPath()).append("\nOP = ").append(operation);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String operation = request.getParameter("message");
		String jsonData = request.getParameter("JDATA");

		System.out.println("DataController:: OP = "+operation+"::JDATA = "+jsonData+"	::\n");
		if(operation != null && CommonConstants.OP_FETCH_COURSE_AREAS.equals(operation)) {
			//process catalog info and store into DB
			Gson gson = new Gson();
			String courseList = gson.toJson(CourseRecommender.recommendCourseAreas(jsonData));
			System.out.println("CourseList = "+courseList);
			response.getWriter().write(courseList);
		}else if(operation != null && CommonConstants.OP_GET_COURSES.equals(operation)) {
			
		}
		System.out.println("DataController:: Exiting");
	}

}
