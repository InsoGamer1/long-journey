function sortTable( table , colindex="NA" ,clickEvnt="NA") {
	if ( !table ){
		console.log( "Warning: table value must be given , either ID or Object ");
		return;
	}
	if ( (colindex=="NA" && clickEvnt=="NA")){
		console.log( "Warning: atleast coloumn index or clickEvnt should be provided!!");
		return;
	}
	if ( !(clickEvnt=="NA") )
		var colindex = $(clickEvnt.target)[0].cellIndex;
	if ( typeof( table ) == "object" )// JqueryObj is given 
		var $table = $(table);
	if ( typeof( table ) == "string" )// id is given 
		var $table = $("#"+table+".filter-table");

	//var $table = $("#table-42.filter-table");
	var shouldSwitch;
	var switching = true;
	var isAsc = $($table.find(".header").find("th")[colindex]).hasClass("asc");
	if ( isAsc ){
		while(switching){
			switching = false;
			$rows =$table.find("tr").not(".header,.filter-row");
			for (i = 0; i < ($rows.length - 1); i++) {
				shouldSwitch = false;
				curr = $($rows[i]).find("td")[colindex];
				next = $($rows[i+1]).find("td")[colindex];
				if ($(curr).text().toLowerCase() > $(next).text().toLowerCase()) {
					shouldSwitch= true;
					break;
					
				}
			}
			if (shouldSwitch) {
				$(next).parent().insertBefore( $(curr).parent());
				switching = true;
			}
		}
		$($table.find(".header").find("th")[colindex]).removeClass("asc").addClass("desc");
	}
	else{
		while(switching){
			switching = false;
			$rows =$table.find("tr").not(".header,.filter-row");
			for (i = 0; i < ($rows.length - 1); i++) {
				shouldSwitch = false;
				curr = $($rows[i]).find("td")[colindex];
				next = $($rows[i+1]).find("td")[colindex];
				if ($(curr).text().toLowerCase() < $(next).text().toLowerCase()) {
					shouldSwitch= true;
					break;
					
				}
			}
			if (shouldSwitch) {
				$(next).parent().insertBefore( $(curr).parent());
				switching = true;
			}
		}
		$($table.find(".header").find("th")[colindex]).removeClass("desc").addClass("asc");
	}
}

function filterFunction(from , table_id=null) {
	inputC = {};
	$(from).closest("tr.filter-row").find("input.filter-input").each( function(i,x){ if( x.value!=""){ inputC[i]=x.value.toUpperCase(); } });
	var inputsLen = Object.keys(inputC).length;
	var input,  table, tr, td, i;
	input = from;
	var matchc = 0;
	if( !table_id)
	table_id = $(from).closest('table')[0].id;
	table = document.getElementById(table_id);
	tr = table.getElementsByTagName("tr");
	for (i = 2; i < tr.length; i++) {
		var matchFound = 0;
		for ( j in inputC ){
			td = tr[i].getElementsByTagName("td")[j];
			if (td ) {
				if (td.innerHTML.toUpperCase().indexOf(inputC[j]) > -1)
					matchFound += 1;
			}
		}
		if (matchFound == inputsLen) {
			tr[i].style.display = "";
			matchc += 1;
		} 
		else {
			tr[i].style.display = "none";
		}
	}
	$(table).closest(".result-section").find("H3>span").html(tr[0].childElementCount +"X"+matchc);
}


function create_filter_table( table_data_obj , target_id ,table_id=null , needfilter=1){
	if( table_data_obj.length != 0){
		var data = table_data_obj ;
		table = $('<table>');
		if( !table_id)
			table_id = "table-"+Math.floor((Math.random() * 1000) + 1).toString();
		table.attr({"id":table_id});
		table.attr({"class":"filter-table"});
		
		//*********************************************
		// creating filter input
		// creating header row
		if ( needfilter){
			var headerTr = $('<tr>');
			headerTr.attr({"class":"header"});
			var filterTr = $('<tr>');
			filterTr.attr({"class":"filter-row"});
			for ( key in data[0] ) {
				filterTr.append('<th><input type="text" class="filter-input"  onkeyup="filterFunction(this,'+table_id+' )" placeholder="Search for '+key+'.." title="Type in a '+key+'"><span class="filter-input-clear">x</span></th>');
				headerTr.append('<th>' +key + '</th>');
			}
			table.append(headerTr);
			table.append(filterTr);
			
		}
		else{
			// creating header row
			var tr = $('<tr>');
			tr.attr({"class":"header"});
			for ( key in data[0] ) {
				tr.append('<th>' +key + '</th>');
			}
			table.append(tr);
		}
			
		//*********************************************
		
		for ( i in data ) {
		  var tr = $('<tr>');
		  var row = data[i] ;
		  for ( key in row ) {
		    tr.append('<td>' + row[key] + '</td>');
		  }
		  table.append(tr);
		}
		$("#"+target_id).html( table );
		$(".filter-input-clear").unbind('click').click( x => { console.log( $(x.currentTarget).siblings("input").val("").keyup() ); } );
		return table_id
	}
	return null;
}

function make_filter_table(){
	$("table.filter-table").each( function( i , tab ){
		var firstrow = $(tab).find("tr")[0];
		if ( $(firstrow).attr("class") != "filter-row" ){//get first row and it should not be already filtered
			// creating filter input
			var tr = $('<tr>');
			tr.attr({"class":"filter-row"});
			$(firstrow).find("th").each( function ( i , tdname){
				tr.append('<th><input type="text" class="filter-input"  onkeyup="filterFunction(this)" placeholder="Search for '+tdname.innerText+'.." title="Type in a '+tdname.innerText+'"></th>');
			});
		$(firstrow).before(tr); 
		}
	});
}

var table_data = [{"ECJobLink":"https://Idontknow","ElapsedTime":"00:03:46","RunStatus":"approvals_","SubmittedTime":"3/21/2017 2:20:44 PM","UserSubmitted":"Raheman","gerrits":123232    },{"ECJobLink":"https://Idondsads",
    "ElapsedTime":"00:03:46","RunStatus":"approvals_app","SubmittedTime":"3/21/2017 2:20:44 PM","UserSubmitted":"Raheman","gerrits":123232    },{"ECJobLink":"https://Idontknow",    "ElapsedTime":"00:03:46","RunStatus":"approvals_rev","SubmittedTime":"3/21/2017 2:20:44 PM","UserSubmitted":"Mahesh",
    "gerrits":123232    }, {    "ECJobLink":"https://Idontknow",    "ElapsedTime":"00:03:46","RunStatus":"approvals_pass","SubmittedTime":"3/21/2017 2:20:44 PM","UserSubmitted":"Visahnu",    "gerrits":1232545}
];
//var tableId = create_filter_table( table_data , "table-holder" );

$(document).ready(function(){
	make_filter_table();
});
