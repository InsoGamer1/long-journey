(function(){
	removeDuplicates= function(listimgs){	 
		var uniqueNames = []; 
		$.each(listimgs, function(i, el){
			if($.inArray(el, uniqueNames) === -1) 
				uniqueNames.push(el);
		});
		return uniqueNames;
	}
})();
