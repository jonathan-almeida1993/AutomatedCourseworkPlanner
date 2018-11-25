package com.osu.database.pojo;

import java.util.ArrayList;

public class ProgramPojo {
	
	private CoursePojoList coursework;
	private boolean isResearch;
	private String firstName;
	private String lastName;

	public CoursePojoList getCoursework() {
		return coursework;
	}
	public void setCoursework(CoursePojoList coursework) {
		this.coursework = coursework;
	}
	public boolean isResearch() {
		return isResearch;
	}
	public void setResearch(boolean isResearch) {
		this.isResearch = isResearch;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
