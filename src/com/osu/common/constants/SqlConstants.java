package com.osu.common.constants;

public interface SqlConstants {

	String POPULATE_DATABASE = "INSERT INTO course_master (course_code, course_title, course_crn, "+
				"credits, department, course_area, term_offered, weight, course_instructor) " + 
				"VALUES (?,?,?,?,?,?,?,?,?)";

}
