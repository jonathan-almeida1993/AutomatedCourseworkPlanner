$(document).ready(function(){
	
	var totalCredits = 45; //constant
	var bucketAreaCredits = 0;
	var blanketCredits = 0;
	var additionalCredits = 0;
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
			$('.courseArea1Course1Desc').text($($('#courseArea1CoursesDiv select')[0]).find('option:selected').data('title'));
			$('.courseArea1Course2Desc').text($($('#courseArea1CoursesDiv select')[1]).find('option:selected').data('title'));
			$('.courseArea2Course1Desc').text($($('#courseArea2CoursesDiv select')[0]).find('option:selected').data('title'));
			$('.courseArea2Course2Desc').text($($('#courseArea2CoursesDiv select')[1]).find('option:selected').data('title'));
			$('.courseArea3Course1Desc').text($($('#courseArea3CoursesDiv select')[0]).find('option:selected').data('title'));
			$('.courseArea3Course2Desc').text($($('#courseArea3CoursesDiv select')[1]).find('option:selected').data('title'));
			
			var courseType = $('input[name=courseType][checked=checked]').val();
			$('#courseTypeTitle').text(courseType+" Option:");
			$('#courseTypeDesc').text($('input[name=courseType][checked=checked]').parent().data('original-title'));
			
			bucketAreaCredits = $($('#courseArea1CoursesDiv select')[0]).find('option:selected').data('credits');
			bucketAreaCredits = bucketAreaCredits + $($('#courseArea1CoursesDiv select')[1]).find('option:selected').data('credits');
			bucketAreaCredits = bucketAreaCredits + $($('#courseArea2CoursesDiv select')[0]).find('option:selected').data('credits');
			bucketAreaCredits = bucketAreaCredits + $($('#courseArea2CoursesDiv select')[1]).find('option:selected').data('credits');
			bucketAreaCredits = bucketAreaCredits + $($('#courseArea3CoursesDiv select')[0]).find('option:selected').data('credits');
			bucketAreaCredits = bucketAreaCredits + $($('#courseArea3CoursesDiv select')[1]).find('option:selected').data('credits');
			
			blanketCredits = $('input[name=courseType][checked=checked]').data('credits');
			alert("Bucket Area credits = "+bucketAreaCredits);
			alert("Blanket credits = "+blanketCredits);
			
			//trigger change event to refresh addionalCredit
			//$('#additionalCoursesDiv select:first').change();
			$('#remainingCredits').text(totalCredits - blanketCredits - bucketAreaCredits - additionalCredits);
			
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
	
	/*when any of the dropdowns in additionalCoursesDiv have a change in their values,
	recompute the additional credits and update the remainingCredits*/
	$('#additionalCoursesDiv select').change(function(){
		
		//finds the research courses which have 0 credits by default
		if($(this).find('option:selected').data('credits') == 0){
			console.log("Please enter the number of credits");
			
			//append the input field to specify credits for blanket credits
			$(this).parent().parent().after('<div class="col-xs-1 specifyCreditsDiv">'+
					'<div class="form-group is-empty label-floating"><label class="control-label">Credit #</label>'+
					'<input class="form-control specifyCreditsInput" type="number" min="1" max="6">'+
					'<span class="material-input"></span></div></div>');
			
			//sets new credit value to the blanket course
			/*$('.specifyCreditsInput').change(function(){
				$('.specifyCredits').parent('div').parent().prev().find("option:selected").data('credits', $(this).val());
				$('#additionalCoursesDiv select').change();
			});*/
		}else if($(this).parent().parent().next().hasClass('specifyCreditsDiv')){
			//remove the input credits field if the value of the drop down changes
			$(this).parent().parent().next().remove();
		}
		
		additionalCredits = 0;
		$('#additionalCoursesDiv select').each(function(idx, obj){
			var credit = $(obj).find('option:selected').data('credits');
			if(credit != undefined){
				additionalCredits = parseInt(additionalCredits) + parseInt(credit); 
			}
		});
		
		$('.specifyCreditsInput').each(function(idx, obj){
			additionalCredits = additionalCredits + parseInt($(obj).val());
		});
		
		console.log("Addition Credits = "+additionalCredits);
		$('#remainingCredits').text(totalCredits - blanketCredits - bucketAreaCredits - additionalCredits);
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
		
		var courseType = $('input[name="courseType"][checked="checked"]').val();
		var isResearch;
		if(courseType == 'research'){
			isResearch = true;
		}else{
			isResearch = false;
		}
		courseList = '{"results":'+courseList+',"isResearch":'+isResearch+'}';
		//append json string to form input and submit the form
		$('#submitPOS input').val(courseList);
		$('#submitPOS').submit();
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
	
	$('#courseArea1CoursesDiv select').empty();
	$('#courseArea1CoursesDiv select').append('<option disabled="" selected=""></option>');
	$(courseList['CourseArea1']).each(function(idx, obj){
		$('#courseArea1CoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
	});

	$('#courseArea2CoursesDiv select').empty();
	$('#courseArea2CoursesDiv select').append('<option disabled="" selected=""></option>');
	$(courseList['CourseArea2']).each(function(idx, obj){
		$('#courseArea2CoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
	});
	
	$('#courseArea3CoursesDiv select').empty();
	$('#courseArea3CoursesDiv select').append('<option disabled="" selected=""></option>');
	$(courseList['CourseArea3']).each(function(idx, obj){
		$('#courseArea3CoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
	});
}

function appendAdditionalCourses(courseList){
	var courseList = jQuery.parseJSON(courseList);
	$('#additionalCoursesDiv select').empty();
	$('#additionalCoursesDiv select').append('<option></option>');
	$(courseList).each(function(idx, obj){
		$('#additionalCoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
	});
}

/*Appends the following course areas to the three course area dropdowns*/
function populateCourseAreaDropDown(){
	var courseAreas = "<option value='Theoretical Computer Science'>Theoretical Computer Science</option>"+
	"<option value='Artificial Intelligence'>Artificial Intelligence</option>"+
	"<option value='Computer Systems'>Computer Systems</option>"+
	"<option value='Programming Languages'>Programming Languages</option>"+
	"<option value='Software Engineering'>Software Engineering</option>"+
	"<option value='Human Computer Interaction'>Human Computer Interaction</option>"+
	"<option value='Computer Vision and Graphics'>Computer Vision and Graphics</option>"+
	"<option value='Statistics'>Statistics</option>"+
	"<option value='Business'>Business</option>"+
	"<option value='Robotics'>Robotics</option>";
	
	$("#courseAreaDiv select").each(function( index ) {
		  $(this).append(courseAreas);
	});
}
