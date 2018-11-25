package com.osu.common.utils;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import com.google.gson.Gson;
import com.osu.common.constants.CommonConstants;
import com.osu.database.pojo.CoursePojo;
import com.osu.database.pojo.CoursePojoList;

public class CatalogParser {
	
	HashMap<String, String> courseCourseAreaMap = null;
	
	public CatalogParser() {
		courseCourseAreaMap = new HashMap<String, String>();
		courseCourseAreaMap.put(CommonConstants.COURSES_AI, CommonConstants.AI);
		courseCourseAreaMap.put(CommonConstants.COURSES_CS, CommonConstants.CS);
		courseCourseAreaMap.put(CommonConstants.COURSES_CVG, CommonConstants.CVG);
		courseCourseAreaMap.put(CommonConstants.COURSES_HCI, CommonConstants.HCI);
		courseCourseAreaMap.put(CommonConstants.COURSES_PL, CommonConstants.PL);
		courseCourseAreaMap.put(CommonConstants.COURSES_SE, CommonConstants.SE);
		courseCourseAreaMap.put(CommonConstants.COURSES_TCS, CommonConstants.TCS);
	}
	
	public static HashMap<String, CoursePojo> processCourseCatalog(String filename) {
		
		HashMap<String, CoursePojo> courseDetails = new HashMap<String, CoursePojo>();
		Gson gson = new Gson();

		BufferedReader br = null;
		String jsonStr = "";
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = null;
			while((line = br.readLine()) != null) {
				jsonStr = jsonStr + line;
			}
		}catch(IOException ex) {
			System.out.println("ERROR OCCURED -> "+ex.getMessage());
		}
		
		CoursePojoList courseList = gson.fromJson(jsonStr, CoursePojoList.class);
		
		for(CoursePojo obj: courseList.getResults()) {
			if(!courseDetails.containsKey(obj.getCode())) {
				obj.setDept("CS");
				String courseArea = new CatalogParser().identifyCourseArea(obj.getCode());
				obj.setCourseArea(courseArea);
				courseDetails.put(obj.getCode(), obj);
			}
		}
		
		for(String key: courseDetails.keySet()) {
			System.out.println("\"<option value='"+courseDetails.get(key).getCode()+"'>"+courseDetails.get(key).getCode()+"-"+courseDetails.get(key).getTitle()+"</option>\"+");
		}
		
		return courseDetails;
	}
	
	public String identifyCourseArea(String courseCode) {
		
		/*get course area for raw course code*/
		for(String key: courseCourseAreaMap.keySet()) {
			if(key.contains(courseCode)) {
				return courseCourseAreaMap.get(key);
			}
		}
		
		/*if course area for raw course code not found,
		then replace last character in course code string
		with X to get generalized course code*/
		
		String courseCodeGeneralized = courseCode.substring(0, courseCode.length() - 1) + "X";
		for(String key: courseCourseAreaMap.keySet()) {
			if(key.contains(courseCodeGeneralized)) {
				return courseCourseAreaMap.get(key);
			}
		}
		
		return "NaN";
	}
}
