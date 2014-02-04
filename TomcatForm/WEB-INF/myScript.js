document.ready(function () {
	$('.addtocart').click(function {
		var title = $('.addtocart').siblings('.title').text();
		title = title.split("Title:")[1];
		var url = "/WEB-INF/sources/Cart";
		
		
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