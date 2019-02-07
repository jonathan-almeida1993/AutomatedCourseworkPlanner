package com.osu.dao.base.interfaces;


import java.util.ArrayList;

import com.osu.dao.base.GenericDAO;
import com.osu.database.pojo.CoursePojo;

public interface CourseDAO extends GenericDAO {
	
	public String populateDatabase(ArrayList<CoursePojo> courseDetails);

	public ArrayList<CoursePojo> fetchCoursesForCourseArea(String courseArea);
	
	public ArrayList<CoursePojo> fetchAllCourses();
	
	public ArrayList<CoursePojo> fetchSlashCourses();
	
	public String updateGradStanding(ArrayList<CoursePojo> courseDetails);
}
