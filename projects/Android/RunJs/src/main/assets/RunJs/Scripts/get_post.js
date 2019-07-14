function send(data,url,method='GET'){
	$.ajax({
			type: method,
			async: true,
			dataType: "json",
			url: url,
			data:JSON.stringify(data),
	})
	.done(function (x) { return JSON.stringify(x); } )
	.fail( function (x){ console.log(x);return null;});
}