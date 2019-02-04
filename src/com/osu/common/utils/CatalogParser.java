package com.osu.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.catalina.tribes.util.Arrays;

import com.google.gson.Gson;
import com.osu.common.constants.CommonConstants;
import com.osu.dao.base.impl.CourseDAOImpl;
import com.osu.dao.base.interfaces.CourseDAO;
import com.osu.database.pojo.CoursePojo;
import com.osu.database.pojo.CoursePojoList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
		courseCourseAreaMap.put(CommonConstants.COURSES_ST, CommonConstants.ST);
		courseCourseAreaMap.put(CommonConstants.COURSES_BA, CommonConstants.BUS);
		courseCourseAreaMap.put(CommonConstants.COURSES_ROB, CommonConstants.ROB);
	}
	
	public static HashMap<String, CoursePojo> processCourseCatalog(String filename, String dept) {
		
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
				obj.setDept(dept);
				String courseArea = new CatalogParser().identifyCourseArea(obj.getCode());
				obj.setCourseArea(courseArea);
				courseDetails.put(obj.getCode(), obj);
				try {
					obj.setCredits(fetchCreditHours(obj));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for(String key: courseDetails.keySet()) {
			System.out.println("\"<option value='"+courseDetails.get(key).getCode()+"'>"+courseDetails.get(key).getCode()+"-"+courseDetails.get(key).getTitle()+"-hrs = "+courseDetails.get(key).getCredits()+"</option>\"+");
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
	
		/*For ST courses*/
		courseCodeGeneralized = courseCode.substring(0, courseCode.length() - 2) + "XX";
		for(String key: courseCourseAreaMap.keySet()) {
			if(key.contains(courseCodeGeneralized)) {
				return courseCourseAreaMap.get(key);
			}
		}
		return "NaN";
	}
	
	public static int fetchCreditHours(CoursePojo obj) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Gson gson = new Gson();
		MediaType mediaType = MediaType.parse("application/json");
		//RequestBody body = RequestBody.create(mediaType, "{\"group\":\"code:ST 511\",\"key\":\"crn:30367\",\"srcdb\":\"201902\"}");
		String jsonStr = "{\"group\":\"code:"+obj.getCode()+"\",\"key\":\"crn:"+obj.getCrn()+"\",\"srcdb\":\""+obj.getSrcdb()+"\"}";
		System.out.println(obj.getSrcdb());
		System.out.println("Query = "+jsonStr);
		RequestBody body = RequestBody.create(mediaType, jsonStr);
		Request request = new Request.Builder()
		  .url("https://classes.oregonstate.edu/api/?page=fose&route=details")
		  .post(body)
		  .addHeader("content-type", "application/json")
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "5e73255f-3771-2e4e-dcdd-43abfc68d2e5")
		  .build();

		Response response = client.newCall(request).execute();
		String responseJson = response.body().string();
		//change hours_html credits -- better naming convention
		System.out.println("Response = "+responseJson);
		CoursePojo creditHours = gson.fromJson(responseJson, CoursePojo.class);
		try {
			creditHours.setCredits(Integer.parseInt(creditHours.getHours_html()));
		}catch(Exception ex) {
			creditHours.setCredits(0);
		}
		System.out.println(creditHours.getCredits());
		return creditHours.getCredits();
	}
	
	
	public static void getGraduateStanding() {
		
		ArrayList<CoursePojo> courseList = new ArrayList<CoursePojo>();
		CourseDAO dao = new CourseDAOImpl();
		courseList = dao.fetchAllCourses();
		
		for(int i = 0; i < courseList.size(); i++){
			CoursePojo obj = courseList.get(i);
			try {
				obj.setGradCourse(fetchGraduateStanding(obj.getCrn()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		dao.updateGradStanding(courseList);
	}
	
	public static boolean fetchGraduateStanding(int crn) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Gson gson = new Gson();
		MediaType mediaType = MediaType.parse("application/json");
		//{"key":"crn:50056"}
		String jsonStr = "{\"key\":\"crn:"+crn+"\"}";
		RequestBody body = RequestBody.create(mediaType, jsonStr);
		Request request = new Request.Builder()
		  .url("https://classes.oregonstate.edu/api/?page=fose&route=details")
		  .post(body)
		  .addHeader("content-type", "application/json")
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "5e73255f-3771-2e4e-dcdd-43abfc68d2e5")
		  .build();

		Response response = client.newCall(request).execute();
		String responseJson = response.body().string();
		//change clssnotes to isGradStanding -- better naming convention
		System.out.println("Course = "+crn);
		System.out.println("		Response = "+responseJson);
		if(responseJson == null || responseJson.equals("null")) {
			return true;
		}
		CoursePojo gradStanding = gson.fromJson(responseJson, CoursePojo.class);
		try {
			if(gradStanding.getClssnotes().contains("not")){
				gradStanding.setGradCourse(false);
			}else {
				gradStanding.setGradCourse(true);
			}
		}catch(Exception ex) {
			gradStanding.setGradCourse(true);
		}
		System.out.println("		isGradCourse = "+gradStanding.isGradCourse());
		return gradStanding.isGradCourse();
	}
	
}
