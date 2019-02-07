package com.osu.dao.base.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.NamingException;

import com.osu.common.constants.CommonConstants;
import com.osu.common.constants.SqlConstants;
import com.osu.dao.base.interfaces.CourseDAO;
import com.osu.database.connection.DBConnectionFactory;
import com.osu.database.pojo.CoursePojo;


public class CourseDAOImpl implements CourseDAO {

	Connection connection = null;

	@Override
	public Connection getConnection() throws SQLException, FileNotFoundException, ClassNotFoundException, IOException, NamingException {
		if ((connection == null) || (connection.isClosed())) {
			connection = DBConnectionFactory.getConnection();
		}
		return connection;
	}
	
	public String populateDatabase(ArrayList<CoursePojo> courseDetails) {
		String status = CommonConstants.JDBC_ERROR;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//course_code, course_title, course_crn, credits, department, course_area, term_offered, weight, course_instructor
		try {
			conn = getConnection();
			preparedStatement = conn.prepareStatement(SqlConstants.POPULATE_DATABASE);
			for(int i = 0; i < courseDetails.size(); i++) {
				CoursePojo singleCourse = courseDetails.get(i);
				preparedStatement.setString(1, singleCourse.getCode());
				preparedStatement.setString(2, singleCourse.getTitle());
				preparedStatement.setInt(3, singleCourse.getCrn());
				preparedStatement.setInt(4, singleCourse.getCredits());
				preparedStatement.setString(5, singleCourse.getDept());
				preparedStatement.setString(6, singleCourse.getCourseArea());
				preparedStatement.setString(7, singleCourse.getTerm());
				preparedStatement.setInt(8, singleCourse.getWeight());
				preparedStatement.setString(9, singleCourse.getInstr());
				preparedStatement.addBatch();
			}
			int[] executionStatus = preparedStatement.executeBatch();
			status = CommonConstants.JDBC_OK;
			
			for(int s: executionStatus) {
				if(!(s >= 0)) {
					status = CommonConstants.JDBC_ERROR;
					break;
				}
			}
			
		}catch(Exception ex) {
			status = CommonConstants.JDBC_ERROR;
			ex.printStackTrace();
		}finally{
			DBConnectionFactory.close(resultSet, preparedStatement, conn);
		}
		return status;
	}
	
	public ArrayList<CoursePojo> fetchCoursesForCourseArea(String courseArea){
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String status =  CommonConstants.JDBC_ERROR;
		ArrayList<CoursePojo> courseList = new ArrayList<CoursePojo>();
		
		try {
			conn = getConnection();
			preparedStatement = conn.prepareStatement(SqlConstants.FETCH_COURSES);
			preparedStatement.setString(1, courseArea);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				CoursePojo singleCourse = new CoursePojo();
				singleCourse.setCode(resultSet.getString("course_code"));
				singleCourse.setTitle(resultSet.getString("course_title"));
				singleCourse.setGradCourse(resultSet.getBoolean("is_grad_course"));
				singleCourse.setCredits(resultSet.getInt("credits"));
				singleCourse.setWeight(resultSet.getInt("weight"));
				courseList.add(singleCourse);
			}
			status = CommonConstants.JDBC_OK;
		}catch(Exception ex) {
			status = CommonConstants.JDBC_ERROR;
			ex.printStackTrace();
		}finally {
			DBConnectionFactory.close(resultSet, preparedStatement, conn);
		}
		return courseList;
	}
	
	public ArrayList<CoursePojo> fetchAllCourses(){
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String status =  CommonConstants.JDBC_ERROR;
		ArrayList<CoursePojo> courseList = new ArrayList<CoursePojo>();
		
		try {
			conn = getConnection();
			preparedStatement = conn.prepareStatement(SqlConstants.FETCH_ALL_COURSES);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				CoursePojo singleCourse = new CoursePojo();
				singleCourse.setCrn(resultSet.getInt("course_crn"));
				singleCourse.setCode(resultSet.getString("course_code"));
				singleCourse.setTitle(resultSet.getString("course_title"));
				singleCourse.setGradCourse(resultSet.getBoolean("is_grad_course"));
				singleCourse.setCredits(resultSet.getInt("credits"));
				singleCourse.setWeight(resultSet.getInt("weight"));
				courseList.add(singleCourse);
			}
			status = CommonConstants.JDBC_OK;
		}catch(Exception ex) {
			status = CommonConstants.JDBC_ERROR;
			ex.printStackTrace();
		}finally {
			DBConnectionFactory.close(resultSet, preparedStatement, conn);
		}
		return courseList;
	}
	
	public ArrayList<CoursePojo> fetchSlashCourses(){
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String status =  CommonConstants.JDBC_ERROR;
		ArrayList<CoursePojo> courseList = new ArrayList<CoursePojo>();
		
		try {
			conn = getConnection();
			preparedStatement = conn.prepareStatement(SqlConstants.FETCH_SLASH_COURSES);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				CoursePojo singleCourse = new CoursePojo();
				singleCourse.setCrn(resultSet.getInt("course_crn"));
				singleCourse.setCode(resultSet.getString("course_code"));
				singleCourse.setTitle(resultSet.getString("course_title"));
				singleCourse.setGradCourse(resultSet.getBoolean("is_grad_course"));
				singleCourse.setCredits(resultSet.getInt("credits"));
				singleCourse.setWeight(resultSet.getInt("weight"));
				courseList.add(singleCourse);
			}
			status = CommonConstants.JDBC_OK;
		}catch(Exception ex) {
			status = CommonConstants.JDBC_ERROR;
			ex.printStackTrace();
		}finally {
			DBConnectionFactory.close(resultSet, preparedStatement, conn);
		}
		return courseList;
	}
	
	public String updateGradStanding(ArrayList<CoursePojo> courseDetails) {
		String status = CommonConstants.JDBC_ERROR;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//"UPDATE course_master SET is_grad_course=? WHERE course_crn=?"
		try {
			conn = getConnection();
			preparedStatement = conn.prepareStatement(SqlConstants.UPDATE_GRAD_STANDING);
			for(int i = 0; i < courseDetails.size(); i++) {
				CoursePojo singleCourse = courseDetails.get(i);
				preparedStatement.setBoolean(1, singleCourse.isGradCourse());
				preparedStatement.setInt(2, singleCourse.getCrn());
				preparedStatement.addBatch();
			}
			int[] executionStatus = preparedStatement.executeBatch();
			status = CommonConstants.JDBC_OK;
			
			for(int s: executionStatus) {
				if(!(s >= 0)) {
					status = CommonConstants.JDBC_ERROR;
					break;
				}
			}
			
		}catch(Exception ex) {
			status = CommonConstants.JDBC_ERROR;
			ex.printStackTrace();
		}finally{
			DBConnectionFactory.close(resultSet, preparedStatement, conn);
		}
		return status;
	}
}