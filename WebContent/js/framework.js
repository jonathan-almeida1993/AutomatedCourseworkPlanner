var program = initializeProgram();

$(document).ready(function(){
	
	/*initialize program object*/
	console.log(program);
	
	$('#nextBtn').click(function(){
		
			var currentTab = $('.moving-tab').text();
			
			/*do operation based on the current tab in the wizard*/
			if(currentTab == "Area of Interest"){
				
					populateCourseAreaDropDown();
					
					/*fetch suggested course area from the backend and stick them to the UI*/
					var interestArea = $('#selectAreaOfInterest').val();
					var courseAreaList = sendDataSync(interestArea, "fetchCourseAreas", "DataController");
				
					appendSuggestedCourseArea(courseAreaList);
				
			}else if(currentTab == "Course Areas"){

					/*store course area names in the program state*/
					program.CourseArea1.name = $('#selectCourseArea1').val();
					program.CourseArea2.name = $('#selectCourseArea2').val();
					program.CourseArea3.name = $('#selectCourseArea3').val();
					
					/*validate course areas*/
					if(!validateBucketAreas()){
						
							alert("You should have atleast two CS Course Areas, and all should be different.");
							$('#prevBtn').click();
							
					}
					
					/*fetch courses for the selected course areas*/
					var courseAreaList = $('#courseAreaForm').serializeJSON();
					courseAreaList = JSON.stringify(courseAreaList);
					var courseList = sendDataSync(courseAreaList, "getCourses", "DataController");
					
					appendCourseAreaCourses(courseList);
					
					$('.courseArea1Desc').text(program.CourseArea1.name);
					$('.courseArea2Desc').text(program.CourseArea2.name);
					$('.courseArea3Desc').text(program.CourseArea3.name);
					
					/*restore ui state on courses tab if state if available*/
					restoreCoursesTabState();
				
			}else if(currentTab == "Courses"){
				
					/*save the selected course area courses to the state*/
					saveCourseAreaCoursesState();
					
					/*validate bucket area courses*/
					if(!validateBucketCourses()){
						
							alert('Please check the courses you have selected.');
							$('#prevBtn').click();
							
					}
					
					/*print info on finalize courses and review tab*/
					$('.courseArea1Course1Desc').text(program.CourseArea1.Course1.title);
					$('.courseArea1Course2Desc').text(program.CourseArea1.Course2.title);
					$('.courseArea2Course1Desc').text(program.CourseArea2.Course1.title);
					$('.courseArea2Course2Desc').text(program.CourseArea2.Course2.title);
					$('.courseArea3Course1Desc').text(program.CourseArea3.Course1.title);
					$('.courseArea3Course2Desc').text(program.CourseArea3.Course2.title);
	
					$('#courseTypeTitle').text(program.Type+" Option:");
					$('#courseTypeDesc').text(program.Desc);
					
					/*fetch and append all available courses on the additional courses drop downs*/
					appendAdditionalCourses();
					
					/*restore ui state on finalize courses tab is available*/
					restoreAdditionalCourses();
					
				
			}else if(currentTab == "Finalize Courses"){
				
					/*save the selected additional courses information to the state*/
					saveAdditionalCoursesState();
					
					/*apply validations on additional courses*/
					if(!validateAdditionalCourses()){
						
						//alert('Please check the courses you have selected. You need to have atleast 45 credits in total, and Blanket Credits cannot be more than 6.');
						$('#prevBtn').click();
						
					}
					
					/*print additional courses info on the review tab*/
					printAdditionalCoursesOnFinish();
			}
		
	});
	
	
	$('#finishBtn').click(function(){

			/*collect data from program object and send it to server in the 
			format specified in the ProgramPojo*/
			
			var courseList = [];
			
			courseList.push(program.CourseArea1.Course1);
			courseList.push(program.CourseArea1.Course2);
			courseList.push(program.CourseArea2.Course1);
			courseList.push(program.CourseArea2.Course2);
			courseList.push(program.CourseArea3.Course1);
			courseList.push(program.CourseArea3.Course2);
			
			$(program.AdditionalCourses).each(function(idx, obj){
					courseList.push(obj);
			});
			
			courseList = JSON.stringify(courseList).replace(/name/g,'code');
			
			var programStr = '{"results":'+courseList+',"type":"'+program.Type+'","blanketCredits":'+program.blanketCredits+',"bucketCredits":'
								+program.bucketCredits+',"capstoneCredits":'+program.capstoneCredits+',"otherCredits":'+program.otherCredits+'}';
			
			//append json string to form input and submit the form
			$('#submitPOS input').val(programStr);
			$('#submitPOS').submit();
	});
	
});// END OF READY


function restoreAdditionalCourses(){

		/*restore only if there is state information on additional courses*/
		if(program.AdditionalCourses.length > 0){
			
				$('.specifyCreditsDiv').remove();
				var selectList = $('#additionalCoursesDiv select');
				var pos = parseInt(0);
				
				$(program.AdditionalCourses).each(function(idx, obj){	
					
					if(obj.isBlanket){
						
						/*if it is a blanket course then we need the custom credit input field*/
						$(selectList[pos]).parent().parent().after(
								'<div class="col-xs-1 specifyCreditsDiv">'+
								'<div class="form-group is-empty label-floating"><label class="control-label">Credit #</label>'+
								'<input class="form-control specifyCreditsInput" type="number" min="1" max="6">'+
								'<span class="material-input"></span></div></div>');
						
						/*once the input field is created assign the right value to it*/
						$(selectList[pos]).parent().parent().next().find('input.specifyCreditsInput').val(obj.credits);
						$(selectList[pos]).parent().parent().next().find('input.specifyCreditsInput').parent('div').removeClass('is-empty');
						
					}						
					
					/*we can just assign the correct value to the dropdown*/
					$(selectList[pos]).val(obj.name) ;
					pos++;
					
					/*TODO: when pos becomes equal to the length of additionalCourses,
					we need to start creating new dropdowns*/
					
				});
		
		}
		
		/*this prints the current credit count on the additional courses tab after next is clicked on the previous tab*/
		$('#currentCredits').text(program.blanketCredits + program.bucketCredits + program.capstoneCredits + program.otherCredits);
		
}


function printAdditionalCoursesOnFinish(){
	
		$('#additionalCoursesDesc').empty();
		
		$(program.AdditionalCourses).each(function(idx, obj){	
				$('#additionalCoursesDesc').append('<div class="col-xs-4 col-xs-offset-5"><p class="description">Course #'+(idx+1)+': '+obj.title+'</p></div>');
		});
		
}

/*this function updates the current courses count on additional courses tab:
 * each time a change is made to one of the selects in additional courses,
 * recompute the additional credit count and add it to bucket, blanket, and capstone credits*/
$('#additionalCoursesDiv select').change(updateCurrentCreditCount);
$('#additionalCoursesDiv').on("change","input.specifyCreditsInput",updateCurrentCreditCount);

function updateCurrentCreditCount(){
	
	var tempAddCredits = 0;
	
	$('#additionalCoursesDiv select').each(function(idx, obj){	
		
		if( $(obj).val() != null && $(obj).val() != ""){
				
				var courseCode = $(obj).val();
				var courseTitle = $(this).find('option:selected').data('title');
				var creditCount = $(this).find('option:selected').data('credits');
				var isBlanket = false;
				
				if(creditCount == 0){
					/*this means this is a blanket course and it has custom credit count*/
					creditCount = parseInt($(this).parent().parent().next().find('input.specifyCreditsInput').val());
					isBlanket = true;
					if(isNaN(creditCount)){
						creditCount = 0;
					}
					console.log("Credit count for "+courseCode+" = "+creditCount);
				}
				
				/*create new course object, set the attributes and push to program additionalCourses*/
				/*additionalCourse = new Object();
				additionalCourse.name = courseCode;
				additionalCourse.title = courseTitle;
				additionalCourse.credits = creditCount;
				additionalCourse.isBlanket = isBlanket;
				
				program.AdditionalCourses.push(additionalCourse);*/
				
				tempAddCredits = tempAddCredits + creditCount;
		}
	});
	
	$('#currentCredits').text(program.bucketCredits + program.capstoneCredits + tempAddCredits);
	
}


/*this checks for blanketCredit count <= 6,
and total credits should be atleast 45, non repeating courses*/
function validateAdditionalCourses(){
		
		var emptyCredits = false;
		
		$(program.AdditionalCourses).each(function(idx, obj){	
			
			if(isNaN(program.AdditionalCourses[idx].credits)){
				emptyCredits = true;
			}
	
		});
		
		if(emptyCredits){
			console.log("BLANKET CREDIT COUNT CANNOT BE EMPTY.");
			alert("Blanket credit count cannot be empty.");
			return false;
		}
		
		if(program.blanketCredits > 6){
			console.log("BLANKET CREDITS MORE THAN 6.");
			alert("Blanket credits cannot be more than 6.");
			return false;
		}
		
		/*checks for repeating courses*/
		var repeatingCourses = false;
		var i;
		var j;
		
		outer:
		for(i = 0; i < program.AdditionalCourses.length; i++){
				
				for(j = i+1; j < program.AdditionalCourses.length; j++){
						
						if(program.AdditionalCourses[i].name == program.AdditionalCourses[j].name){
								console.log('REPEATING COURSES AT '+i +' AND '+j);
								alert("Courses at position "+i+" and "+j+" are same. Cannot select same courses multiple times.");
								repeatingCourses = true;
								break outer;
						}
						
				}
		}
		
		
		if(repeatingCourses){
			return false;
		}
		
		
		var total = parseInt(program.bucketCredits) + parseInt(program.otherCredits) + parseInt(program.capstoneCredits) + parseInt(program.blanketCredits);
		
		if(total < parseInt(45)){
			console.log("TOTAL CREDITS SHOULD BE ATLEAST 45. CURRENT TOTAL = "+total);
			alert("Total credits should be atleast 45. Current total = "+total);
			return false;
		}
		
		return true;
}


/*this function saves the additional courses state*/
function saveAdditionalCoursesState(){
	
		program.AdditionalCourses = new Array();
		var additionalCourse = null;
		
		$('#additionalCoursesDiv select').each(function(idx, obj){	
			
					if( $(obj).val() != null && $(obj).val() != ""){
							
							var courseCode = $(obj).val();
							var courseTitle = $(this).find('option:selected').data('title');
							var creditCount = $(this).find('option:selected').data('credits');
							var isBlanket = false;
							
							if(creditCount == 0){
								/*this means this is a blanket course and it has custom credit count*/
								creditCount = parseInt($(this).parent().parent().next().find('input.specifyCreditsInput').val());
								isBlanket = true;
								console.log("Credit count for "+courseCode+" = "+creditCount);
							}
							
							/*create new course object, set the attributes and push to program additionalCourses*/
							additionalCourse = new Object();
							additionalCourse.name = courseCode;
							additionalCourse.title = courseTitle;
							additionalCourse.credits = creditCount;
							additionalCourse.isBlanket = isBlanket;
							
							program.AdditionalCourses.push(additionalCourse);

					}
					
					
		});
		
		/*calculate blanket credits count and other credits count*/
		program.blanketCredits = 0;
		program.otherCredits = 0;
		$(program.AdditionalCourses).each(function(idx, obj){	
				
				if(obj.isBlanket){
						program.blanketCredits = parseInt(program.blanketCredits) + parseInt(obj.credits);
				}else {
						program.otherCredits = parseInt(program.otherCredits) + parseInt(obj.credits);
				}
		
		});
		
}


/*when an additional course is selected, check if the credits are 0,
if yes, then that is a blanked course and the user needs to enter 
the credit count.*/
$('#additionalCoursesDiv select').change(function(){
	
		/*gets the credit count for a selected course*/
		var creditCount = $(this).find('option:selected').data('credits');
		var courseCode = $(this).find('option:selected').val();
		
		if(creditCount == 0){
			
				console.log("Please enter the number of credits for " + courseCode);
				
				/*append input field to specify credits for blanket courses. 
				 * if it exist already, remove it and then reappend.*/
				var hasCustomInput = $(this).parent().parent().next().hasClass('specifyCreditsDiv');
				
				if(hasCustomInput){
					$(this).parent().parent().next().remove();
				}
				
				$(this).parent().parent().after(
						'<div class="col-xs-1 specifyCreditsDiv">'+
						'<div class="form-group is-empty label-floating"><label class="control-label">Credit #</label>'+
						'<input class="form-control specifyCreditsInput" type="number" min="1" max="6">'+
						'<span class="material-input"></span></div></div>');
				
		}else {
			
				/*when credit for current course is non zero, and if there exists
				a custom credit input box then remove it*/
				
				var hasCustomInput = $(this).parent().parent().next().hasClass('specifyCreditsDiv');
				if(hasCustomInput){
					$(this).parent().parent().next().remove();
				}
				
		}
		
});


/*this filters courses which are already selected as bucket courses*/
function appendAdditionalCourses(){
	
		var allCourseList = sendDataSync("", "getAllCourses", "DataController");
		allCourseList = jQuery.parseJSON(allCourseList);
		
		$('#additionalCoursesDiv select').empty();
		$('#additionalCoursesDiv select').append('<option></option>');
		
		/*checks if the program string contains the course code*/
		var courseArea1Str = JSON.stringify(program.CourseArea1);
		var courseArea2Str = JSON.stringify(program.CourseArea2);
		var courseArea3Str = JSON.stringify(program.CourseArea3);

		$(allCourseList).each(function(idx, obj){
			if(!(courseArea1Str.includes(obj.code) || courseArea2Str.includes(obj.code) || courseArea3Str.includes(obj.code))){
				$('#additionalCoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
			}
		});
		
}


function validateBucketCourses(){
		
		/*check for empty values*/
		var error = false;
		$('#courses select').each(function(idx, obj){
			if( $(this).val() == "" || $(this).val() == null){
				console.log(idx+"SOME COURSE HAS EMPTY VALUE.");
				error = true;
				return false;
			}
			
			
		});
		if(error){
			return false;
		}
		
		/*check for same courses selected multiple times*/
		if(program.CourseArea1.Course1.name == program.CourseArea1.Course2.name){
			console.log("COURSE AREA #1 HAS SAME COURSES.");
			return false;
		}
		
		if(program.CourseArea2.Course1.name == program.CourseArea2.Course2.name){
			console.log("COURSE AREA #2 HAS SAME COURSES.");
			return false;
		}
		
		if(program.CourseArea3.Course1.name == program.CourseArea3.Course2.name){
			console.log("COURSE AREA #3 HAS SAME COURSES.");
			return false;
		}
		
		if(program.Type == null || program.Type == ""){
			console.log("COURSE TYPE NOT SELECTED.");
			return false;
		}
		
		program.bucketCredits = 0;
		program.bucketCredits = parseInt(program.CourseArea1.Course1.credits)+parseInt(program.CourseArea1.Course2.credits)+  
								parseInt(program.CourseArea2.Course1.credits)+parseInt(program.CourseArea2.Course2.credits)+
								parseInt(program.CourseArea3.Course1.credits)+parseInt(program.CourseArea3.Course2.credits);
		
		
		return true;
		
}


function restoreCoursesTabState(){
	
		if(program.CourseArea1.Course1.name != null && program.CourseArea1.Course2.name != null){
			
				$($('#courseArea1CoursesDiv select')[0]).val(program.CourseArea1.Course1.name);
				$($('#courseArea1CoursesDiv select')[0]).parent('div').removeClass('is-empty');
				
				$($('#courseArea1CoursesDiv select')[1]).val(program.CourseArea1.Course2.name);
				$($('#courseArea1CoursesDiv select')[1]).parent('div').removeClass('is-empty');
		
		}
		
		if(program.CourseArea2.Course1.name != null && program.CourseArea2.Course2.name != null){
			
				$($('#courseArea2CoursesDiv select')[0]).val(program.CourseArea2.Course1.name);
				$($('#courseArea2CoursesDiv select')[0]).parent('div').removeClass('is-empty');
				
				$($('#courseArea2CoursesDiv select')[1]).val(program.CourseArea2.Course2.name);
				$($('#courseArea2CoursesDiv select')[1]).parent('div').removeClass('is-empty');
				
		}
		
		if(program.CourseArea3.Course1.name != null && program.CourseArea3.Course2.name != null){
			
				$($('#courseArea3CoursesDiv select')[0]).val(program.CourseArea3.Course1.name);
				$($('#courseArea3CoursesDiv select')[0]).parent('div').removeClass('is-empty');
				
				$($('#courseArea3CoursesDiv select')[1]).val(program.CourseArea3.Course2.name);
				$($('#courseArea3CoursesDiv select')[1]).parent('div').removeClass('is-empty');
		
		}
		
}


function saveCourseAreaCoursesState(){
	
		/*save course area #1 state*/
		var courseArea1Course1Selector = $($('#courseArea1CoursesDiv select')[0]).find('option:selected');
		var courseArea1Course2Selector = $($('#courseArea1CoursesDiv select')[1]).find('option:selected');
		program.CourseArea1.Course1.name = courseArea1Course1Selector.val();
		program.CourseArea1.Course1.title = courseArea1Course1Selector.data('title');
		program.CourseArea1.Course1.credits = courseArea1Course1Selector.data('credits');
		program.CourseArea1.Course1.isBlanket = false;

		program.CourseArea1.Course2.name = courseArea1Course2Selector.val();
		program.CourseArea1.Course2.title = courseArea1Course2Selector.data('title');
		program.CourseArea1.Course2.credits = courseArea1Course2Selector.data('credits');
		program.CourseArea1.Course2.isBlanket = false;
		
		/*save course area #2 state*/
		var courseArea2Course1Selector = $($('#courseArea2CoursesDiv select')[0]).find('option:selected');
		var courseArea2Course2Selector = $($('#courseArea2CoursesDiv select')[1]).find('option:selected');
		program.CourseArea2.Course1.name = courseArea2Course1Selector.val();
		program.CourseArea2.Course1.title = courseArea2Course1Selector.data('title');
		program.CourseArea2.Course1.credits = courseArea2Course1Selector.data('credits');
		program.CourseArea2.Course1.isBlanket = false;

		program.CourseArea2.Course2.name = courseArea2Course2Selector.val();
		program.CourseArea2.Course2.title = courseArea2Course2Selector.data('title');
		program.CourseArea2.Course2.credits = courseArea2Course2Selector.data('credits');
		program.CourseArea2.Course2.isBlanket = false;

		/*save course area #3 state*/
		var courseArea3Course1Selector = $($('#courseArea3CoursesDiv select')[0]).find('option:selected');
		var courseArea3Course2Selector = $($('#courseArea3CoursesDiv select')[1]).find('option:selected');
		program.CourseArea3.Course1.name = courseArea3Course1Selector.val();
		program.CourseArea3.Course1.title = courseArea3Course1Selector.data('title');
		program.CourseArea3.Course1.credits = courseArea3Course1Selector.data('credits');
		program.CourseArea3.Course1.isBlanket = false;

		program.CourseArea3.Course2.name = courseArea3Course2Selector.val();
		program.CourseArea3.Course2.title = courseArea3Course2Selector.data('title');
		program.CourseArea3.Course2.credits = courseArea3Course2Selector.data('credits');
		program.CourseArea3.Course2.isBlanket = false;
		
		program.Type = $('input[name=courseType][checked=checked]').val();
		program.Desc = $('input[name=courseType][checked=checked]').parent().data('original-title');
		program.capstoneCredits = parseInt($('input[name=courseType][checked=checked]').data('credits'));
}


/*Appends the following course areas to the three course area dropdowns*/
function populateCourseAreaDropDown(){
	
		$("#courseAreaDiv select").empty();
	
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


/*validate course areas for multiple selections, 
and enforcing atleast two CS buckets*/
function validateBucketAreas(){
		
		/*validation for atleast two CS buckets*/
		var combinedAreaStr = program.CourseArea1.name+","+program.CourseArea2.name+","+program.CourseArea3.name;
		if(combinedAreaStr.includes("Statistics") && combinedAreaStr.includes("Business")){
			console.log("BUCKET AREAS CONTAIN BOTH STATISTICS AND BUSINESS");
			return false;
		}
		
		if(combinedAreaStr.includes("Business") && combinedAreaStr.includes("Robotics")){
			console.log("BUCKET AREAS CONTAIN BOTH BUSINESS AND ROBOTICS");
			return false;
		}
		
		if(combinedAreaStr.includes("Statistics") && combinedAreaStr.includes("Robotics")){
			console.log("BUCKET AREAS CONTAIN BOTH STATISTICS AND ROBOTICS");
			return false;
		}
		
		/*validation for multiple selections*/
		if(program.CourseArea1.name == program.CourseArea2.name){
			console.log("BUCKET AREAS #1 AND #2 ARE SAME.");
			return false;
		}
		
		if(program.CourseArea2.name == program.CourseArea3.name){
			console.log("BUCKET AREAS #2 AND #3 ARE SAME.");
			return false;
		}
		
		if(program.CourseArea1.name == program.CourseArea3.name){
			console.log("BUCKET AREAS #1 AND #3 ARE SAME.");
			return false;
		}
		
		return true;
}


/*selected automatically suggested courses on the UI*/
function appendSuggestedCourseArea(courseAreaList){
	
		courseAreaList = jQuery.parseJSON(courseAreaList);
		$('#selectCourseArea1').val(courseAreaList[0]);
		$('#selectCourseArea1').parent('div').removeClass('is-empty');
		
		$('#selectCourseArea2').val(courseAreaList[1]);
		$('#selectCourseArea2').parent('div').removeClass('is-empty');
		
		$('#selectCourseArea3').val(courseAreaList[2]);
		$('#selectCourseArea3').parent('div').removeClass('is-empty');
	
}


/*append the courses fetch for each course area on the ui*/
/*this also filters blanket courses (credits <= 2), since buckets shouldn't have them*/
function appendCourseAreaCourses(courseList){

		courseList = jQuery.parseJSON(courseList);
		
		$('#courseArea1CoursesDiv select').empty();
		$('#courseArea1CoursesDiv select').append('<option disabled="" selected="">');
		$(courseList['CourseArea1']).each(function(idx, obj){
			var num = obj.code.split(" ")[1];
			if(parseInt(obj.credits) > 2){
				$('#courseArea1CoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
			}
		});
	
		$('#courseArea2CoursesDiv select').empty();
		$('#courseArea2CoursesDiv select').append('<option disabled="" selected="">');
		$(courseList['CourseArea2']).each(function(idx, obj){
			var num = obj.code.split(" ")[1];
			if(parseInt(obj.credits) > 2){
				$('#courseArea2CoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
			}
		});
		
		$('#courseArea3CoursesDiv select').empty();
		$('#courseArea3CoursesDiv select').append('<option disabled="" selected="">');
		$(courseList['CourseArea3']).each(function(idx, obj){
			var num = obj.code.split(" ")[1];
			if(parseInt(obj.credits) > 2){
				$('#courseArea3CoursesDiv select').append("<option value='"+obj.code+"' data-title='"+obj.title+"' data-credits='"+obj.credits+"'>"+obj.code+" - "+obj.title+" ("+obj.credits+")</option>");
			}
		});
}


/*this contains the state of the UI,
all the validation run on this state*/
function initializeProgram(){
	
		var program = new Object();
		program.CourseArea1 = new Object();
		program.CourseArea2 = new Object();
		program.CourseArea3 = new Object();
		program.Type = null;
		program.Desc = null;
		program.bucketCredits = 0;
		program.capstoneCredits = 0;
		program.blanketCredits = 0;
		program.otherCredits = 0;
		program.AdditionalCourses = new Array();
		
		program.CourseArea1.name = null;
		program.CourseArea1.Course1 = new Object();
		program.CourseArea1.Course2 = new Object();
		
		program.CourseArea2.name = null;
		program.CourseArea2.Course1 = new Object();
		program.CourseArea2.Course2 = new Object();
		
		program.CourseArea3.name = null;
		program.CourseArea3.Course1 = new Object();
		program.CourseArea3.Course2 = new Object();
		return program;
		
}

