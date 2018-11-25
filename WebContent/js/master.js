$(document).ready(function(){
	
	populateCourseAreaDropDown();
	
	$('#nextBtn').click(function(){
		var currentTab = $('.moving-tab').text();
		
		/*do operation based on the current tab in the wizard*/
		if(currentTab == "Area of Interest"){
			
			var interestArea = $('#selectAreaOfInterest').val();
			var courseAreaList = sendDataSync(interestArea, "fetchCourseAreas", "DataController");
			//console.log(courseAreaList);
			appendSuggestedCourseArea(courseAreaList);
			
		}else if(currentTab == "Course Areas"){
			$('.courseArea1Desc').text($('#selectCourseArea1').val());
			$('.courseArea2Desc').text($('#selectCourseArea2').val());
			$('.courseArea3Desc').text($('#selectCourseArea3').val());
			
			/*fetch courses for the selected course areas*/
			var courseAreaList = $('#courseAreaForm').serializeJSON();
			courseAreaList = JSON.stringify(courseAreaList);
			var courseList = sendDataSync(courseAreaList, "getCourses", "DataController");
			appendCourses(courseList);
			
		}else if(currentTab == "Courses"){
			
			/*set the courses selected, on the finalize courses and finish tabs*/
			$('.courseArea1Course1Desc').text($($('#courseArea1CoursesDiv select')[0]).find('option:selected').text());
			$('.courseArea1Course2Desc').text($($('#courseArea1CoursesDiv select')[1]).find('option:selected').text());
			$('.courseArea2Course1Desc').text($($('#courseArea2CoursesDiv select')[0]).find('option:selected').text());
			$('.courseArea2Course2Desc').text($($('#courseArea2CoursesDiv select')[1]).find('option:selected').text());
			$('.courseArea3Course1Desc').text($($('#courseArea3CoursesDiv select')[0]).find('option:selected').text());
			$('.courseArea3Course2Desc').text($($('#courseArea3CoursesDiv select')[1]).find('option:selected').text());
			
			var allCourseList = sendDataSync("", "getAllCourses", "DataController");
			//console.log(allCourseList);
			appendAdditionalCourses(allCourseList);
			
		}else if(currentTab == "Finalize Courses"){
			var additionalCourses = [];
			$('#additionalCoursesDiv select').each(function(idx, obj){
				additionalCourses.push($(obj).find('option:selected').text());
			});
			
			$('#additionalCoursesDesc').empty();
			$(additionalCourses).each(function(idx,obj){
				$('#additionalCoursesDesc').append('<div class="col-xs-4 col-xs-offset-5"><p class="description">Course #'+(idx+1)+': '+obj+'</p></div>');
			});
		}
		
	});
	
	$('#finishBtn').click(function(){
		/*gather data from all the input fields and send them to the server*/
		//collect course area courses
		var courseList = [];
		$("#courses select").each(function(idx, obj){
			var jsonStr = '{"code":"'+obj.value+'","title":"'+$(obj).find('option:selected').text()+'"}';
			jsonStr = jQuery.parseJSON(jsonStr);
			courseList.push(jsonStr);
		});
		
		//collect additional courses
		$("#additionalCoursesDiv  select").each(function(idx, obj){
			var jsonStr = '{"code":"'+obj.value+'","title":"'+$(obj).find('option:selected').text()+'"}';
			jsonStr = jQuery.parseJSON(jsonStr);
			courseList.push(jsonStr);
		});
		courseList = JSON.stringify(courseList);
		courseList = '{"results":'+courseList+'}';
		sendDataSync(courseList,'generateProgramOfStudy','DataController');
	});
});

function appendSuggestedCourseArea(courseAreaList){
	courseAreaList = jQuery.parseJSON(courseAreaList);
	$('#selectCourseArea1').val(courseAreaList[0]);
	$('#selectCourseArea1').parent('div').removeClass('is-empty');
	
	$('#selectCourseArea2').val(courseAreaList[1]);
	$('#selectCourseArea2').parent('div').removeClass('is-empty');
	
	$('#selectCourseArea3').val(courseAreaList[2]);
	$('#selectCourseArea3').parent('div').removeClass('is-empty');
}

function appendCourses(courseList){
	courseList = jQuery.parseJSON(courseList);
	
	$(courseList['CourseArea1']).each(function(idx, obj){
		$('#courseArea1CoursesDiv select').append("<option value='"+obj.code+"'>"+obj.title+"</option>");
	});

	$(courseList['CourseArea2']).each(function(idx, obj){
		$('#courseArea2CoursesDiv select').append("<option value='"+obj.code+"'>"+obj.title+"</option>");
	});
	
	$(courseList['CourseArea3']).each(function(idx, obj){
		$('#courseArea3CoursesDiv select').append("<option value='"+obj.code+"'>"+obj.title+"</option>");
	});
}

function appendAdditionalCourses(courseList){
	var courseList = jQuery.parseJSON(courseList);
	$(courseList).each(function(idx, obj){
		$('#additionalCoursesDiv select').append("<option value='"+obj.code+"'>"+obj.title+"</option>");
	});
}

function populateCourseAreaDropDown(){
	var courseAreas = /*"<option value=''>Select Course Area</option>"+*/
	"<option value='Theoretical Computer Science'>Theoretical Computer Science</option>"+
	"<option value='Artificial Intelligence'>Artificial Intelligence</option>"+
	"<option value='Computer Systems'>Computer Systems</option>"+
	"<option value='Programming Languages'>Programming Languages</option>"+
	"<option value='Software Engineering'>Software Engineering</option>"+
	"<option value='Human Computer Interaction'>Human Computer Interaction</option>"+
	"<option value='Computer Vision and Graphics'>Computer Vision and Graphics</option>";
	
	$("#courseAreaDiv select").each(function( index ) {
		  $(this).append(courseAreas);
	});
}
