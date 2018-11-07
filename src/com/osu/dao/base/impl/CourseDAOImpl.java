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
}