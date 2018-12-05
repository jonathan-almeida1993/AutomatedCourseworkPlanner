package com.osu.common.constants;

public interface CommonConstants {

	String OP_PARSE_CATALOG = "parseCatalog";
	String OP_FETCH_COURSE_AREAS = "fetchCourseAreas";
	String OP_GET_COURSES = "getCourses";
	String OP_ALL_GET_COURSES = "getAllCourses";
	
	String JDBC_ERROR = "JDBC_ERROR";
	String JDBC_OK = "JDBC_OK";

	//Course Areas
	/*
	Theoretical Computer Science: CS 515-517, CS 52X
	Artificial Intelligence: CS 53X
	Computer Systems: CS 57X, CS 54X, ECE 57X
	Programming Languages: CS 58X
	Software Engineering: CS 560, CS 561, CS 562, CS 563, CS 564, CS 567, CS 569
	Human Computer Interaction: CS 564, CS 565, CS 567, CS 568, ROB 567
	Computer Vision and Graphics: CS 55X*/
	
	String TCS = "Theoretical Computer Science";
	String AI = "Artificial Intelligence";
	String CS = "Computer Systems";
	String PL = "Programming Languages";
	String SE = "Software Engineering";
	String HCI = "Human Computer Interaction";
	String CVG = "Computer Vision and Graphics";
	String ST = "Statistics";
	String BUS = "Business";
	String ROB = "Robotics";
	
	String COURSES_TCS = "CS 515,CS 517,CS 52X";
	String COURSES_AI = "CS 53X";
	String COURSES_CS = "CS 57X,CS 54X,ECE 57X";
	String COURSES_PL = "CS 58X";
	String COURSES_SE = "CS 560,CS 561,CS 562,CS 563,CS 564,CS 567,CS 569";
	String COURSES_HCI = "CS 564,CS 565,CS 567,CS 568,ROB 567";
	String COURSES_CVG = "CS 55X";
	String COURSES_ST = "ST 5XX, ST 6XX";
	String COURSES_BA = "BA 5XX, BA 6XX";
	String COURSES_ROB = "ROB 5XX, ROB 6XX";
	String OP_GENERATE_POS = "generateProgramOfStudy";
	
}
