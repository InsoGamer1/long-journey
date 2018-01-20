function send(){
var data_to_be_sent = {"a":[1976161,2050556,2050557]};
/*
$.ajax({
		type: 'POST',
		async: true,
		dataType: "json",
		url: '<url>',
		data:{"gerrits":[1976161,2050556,2050557]}
})
.done(function (data) { //alert("success");
console.log(data); } )
.fail( function (x){ console.log(x);});
*/

$.ajax({
		type: 'GET',
		async: true,
		dataType: "json",
		url: '<url>',
		data:JSON.stringify(data_to_be_sent),
})
.done(function (data) { alert("success");console.log(data); } )
.fail( function (x){ console.log(x);});

}