$(document).ready(function(){
	
	$('#editCourseArea').click(function(){
		$('a[href="#courseArea"]').click();
	});

	$('#editOptionSelected').click(function(){
		$('a[href="#courses"]').click();
            $('html, body').animate({
                scrollTop: $("div.choice").offset().top
            }, 2000);
	});

	$('#editAdditionalCourses').click(function(){
		$('a[href="#finalizeCourses"]').click();
		    $('html, body').animate({
                scrollTop: $("div.separator").offset().top
            }, 1000);
	});
});