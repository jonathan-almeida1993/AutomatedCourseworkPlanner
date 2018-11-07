package com.osu.common.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.osu.common.constants.CommonConstants;

public class CourseRecommender {
	
	private ArrayList<String> courseAreaList;
	
	private CourseRecommender() {
		courseAreaList = new ArrayList<String>();
		courseAreaList.add(CommonConstants.AI);
		courseAreaList.add(CommonConstants.CS);
		courseAreaList.add(CommonConstants.CVG);
		courseAreaList.add(CommonConstants.HCI);
		courseAreaList.add(CommonConstants.PL);
		courseAreaList.add(CommonConstants.SE);
		courseAreaList.add(CommonConstants.TCS);
	}
	
	
	private ArrayList<String> recommend3AreasRandom(){
		ArrayList<String> suggestions = new ArrayList<String>();
		for(int i = 0; i < 3; i++) {
			int random = (int) (Math.random() * courseAreaList.size());
			suggestions.add(courseAreaList.remove(random));
		}
		return suggestions;
	}
	
	
	public static ArrayList<String> recommendCourseAreas(String interest) {
		CourseRecommender cr = new CourseRecommender();
		ArrayList<String> list = cr.recommend3AreasRandom();
		return list;
	}
	
	
	public static void main(String args[]) {
		System.out.println(Arrays.toString(recommendCourseAreas("").toArray()));
	}
}