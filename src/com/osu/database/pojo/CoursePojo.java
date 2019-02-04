package com.osu.database.pojo;

public class CoursePojo {
	
	//key,code,title,crn,no,total,schd,stat,isCancelled,meets,mpkey,instr,start_date,end_date,srcdb
	
	private int courseId;
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
	private String group;
	private String srcdb;
	private String key;
	private String hours_html;
	private boolean isGradCourse;
	private String clssnotes;
	private boolean isBlanket;
	
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getSrcdb() {
		return srcdb;
	}
	public void setSrcdb(String srcdb) {
		this.srcdb = srcdb;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getHours_html() {
		return hours_html;
	}
	public void setHours_html(String hours_html) {
		this.hours_html = hours_html;
	}
	public boolean isBlanket() {
		return isBlanket;
	}
	public void setBlanket(boolean isBlanket) {
		this.isBlanket = isBlanket;
	}
	public String getClssnotes() {
		return clssnotes;
	}
	public void setClssnotes(String clssnotes) {
		this.clssnotes = clssnotes;
	}
	public boolean isGradCourse() {
		return isGradCourse;
	}
	public void setGradCourse(boolean isGradCourse) {
		this.isGradCourse = isGradCourse;
	}

}
