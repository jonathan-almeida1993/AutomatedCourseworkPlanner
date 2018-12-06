package com.osu.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osu.common.constants.CommonConstants;
import com.osu.dao.base.impl.CourseDAOImpl;
import com.osu.dao.base.interfaces.CourseDAO;
import com.osu.database.pojo.CoursePojo;

public class CourseRecommender {
	
	/*This class provides instance method which recommends courses
	based on the interest specified by using a model.*/
	
	private static final String MODEL_FILE = "D:\\devOps\\eclipse-workspace\\AutomatedCourseworkPlanner\\src\\com\\osu\\common\\utils\\model.json";
	
	private static HashMap<String, ArrayList<String>> model;
	
	private CourseRecommender() {

			model = new HashMap<String, ArrayList<String>>();
			Gson gson = new Gson();
	
			BufferedReader br = null;
			String jsonStr = "";
			
			try {
				
					br = new BufferedReader(new FileReader(MODEL_FILE));
					String line = null;
					while((line = br.readLine()) != null) {
						jsonStr = jsonStr + line;
					}
					
					//System.out.println("MODEL = "+jsonStr);
					
					Type myType = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
					model = gson.fromJson(jsonStr, myType);
					
					//System.out.println("Deserialize = "+gson.toJson(model));
				
			}catch(IOException ex) {
				
					System.out.println("ERROR OCCURED -> "+ex.getMessage());
			
			}
	}
	
	

	public static ArrayList<String> recommendCourseAreas(String interest) {
		
			CourseRecommender cr = new CourseRecommender();
			
			ArrayList<String> courseSuggestions = new ArrayList<String>();
			courseSuggestions.add(model.get(interest).get(0));
			courseSuggestions.add(model.get(interest).get(1));
			courseSuggestions.add(model.get(interest).get(2));
			return courseSuggestions;
		
	}
	
	
	public static void main(String args[]) {
		
		System.out.println(Arrays.toString(recommendCourseAreas(CommonConstants.AI).toArray()));
		
	}
}