document.ready(function () {
	$('.addToCart').click(function {
		var title = $('.addToCart').siblings('.title').text();
		title = title.split("Title:")[1];
		var url = "/TomcatForm/WEB-INF/sources/Cart";
		
		
		$.ajax({
			url: url,
			data: {"title" : title},
			type: "POST",
			success: function () {
				alert("success!");
			}),
			error: function () {
				alert("failure!");
			}
		});
	});
	
	
});