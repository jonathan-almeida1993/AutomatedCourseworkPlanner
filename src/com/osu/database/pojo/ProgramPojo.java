package com.osu.database.pojo;

import java.util.ArrayList;

public class ProgramPojo {
	
	private CoursePojoList coursework;
	private boolean isResearch;
	private String firstName;
	private String lastName;
	private String email;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	private ArrayList<CoursePojo> results;

	public ArrayList<CoursePojo> getResults() {
		return results;
	}

	public void setResults(ArrayList<CoursePojo> results) {
		this.results = results;
	}

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
