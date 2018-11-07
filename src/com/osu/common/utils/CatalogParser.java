package com.osu.common.utils;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import com.google.gson.Gson;
import com.osu.database.pojo.CoursePojo;
import com.osu.database.pojo.CoursePojoList;

public class CatalogParser {
	
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
				courseDetails.put(obj.getCode(), obj);
			}
		}
		
		for(String key: courseDetails.keySet()) {
			System.out.println("\"<option value='"+courseDetails.get(key).getCode()+"'>"+courseDetails.get(key).getCode()+"-"+courseDetails.get(key).getTitle()+"</option>\"+");
		}
		
		return courseDetails;
	}
}
