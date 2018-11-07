package com.osu.database.pojo;

public class CoursePojo {
	
	//key,code,title,crn,no,total,schd,stat,isCancelled,meets,mpkey,instr,start_date,end_date,srcdb
	
	private int key;
	private String code;
	private String title;
	private int crn;
	private String instr;
	private String start_date;
	private String end_date;
	private int credits;
	private String dept;
	private String courseArea;
	private String term;
	private int weight;
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getCrn() {
		return crn;
	}
	public void setCrn(int crn) {
		this.crn = crn;
	}
	public String getInstr() {
		return instr;
	}
	public void setInstr(String instr) {
		this.instr = instr;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getCourseArea() {
		return courseArea;
	}
	public void setCourseArea(String courseArea) {
		this.courseArea = courseArea;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	
	}
