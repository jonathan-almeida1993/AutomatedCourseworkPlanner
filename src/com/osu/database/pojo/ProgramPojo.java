package com.osu.database.pojo;

import java.util.ArrayList;

public class ProgramPojo {
	
	private CoursePojoList coursework;
	private boolean isResearch;
	private String firstName;
	private String lastName;
	private String email;
	private String type;
	private int blanketCredits;
	private int bucketCredits;
	private int capstoneCredits;
	private int otherCredits;
	
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

	public int getBlanketCredits() {
		return blanketCredits;
	}

	public void setBlanketCredits(int blanketCredits) {
		this.blanketCredits = blanketCredits;
	}

	public int getBucketCredits() {
		return bucketCredits;
	}

	public void setBucketCredits(int bucketCredits) {
		this.bucketCredits = bucketCredits;
	}

	public int getCapstoneCredits() {
		return capstoneCredits;
	}

	public void setCapstoneCredits(int capstoneCredits) {
		this.capstoneCredits = capstoneCredits;
	}

	public int getOtherCredits() {
		return otherCredits;
	}

	public void setOtherCredits(int otherCredits) {
		this.otherCredits = otherCredits;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



}
