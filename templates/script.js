
	var selected_element=null;
	var original_css=null;
	
	var selectCell = 
		function(event, cell){
			if(selected_element!==null)
				unselectCell(selected_element);
			selected_element = cell;
			cell = $(cell);
			original_css=null;
			original_css = {
				"border-color" : cell.css("border-color"),
				"border-width" : cell.css("border-width"),
				"border-style" : cell.css("border-style"),
			};
			cell.css({ "border-color": "red", "border-width" : "1px", "border-style":"dotted" });
			showSize(null, cell);
		};
	var unselectCell =
		function(cell){
			cell = $(cell);
			$(cell).css(original_css);
		}
	
	var showResize = 
		function( event, ui ) {
				selectCell(event, this);
			//ui.size.height = Math.round( ui.size.height / 30 ) * 30;
			$("#width").val(Math.round( ui.size.width*0.2645833 * 100)/100);
			$("#height").val(Math.round(ui.size.height*0.2645833 * 100)/100);
		};
	
	var showSize = 
		function( event, ui ) {
			if(ui.element)
			{
				$("#width").val(Math.round( $(ui.element).width()*0.2645833 * 100)/100);
				$("#height").val(Math.round( $(ui.element).height()*0.2645833 * 100)/100);
			}else
			{
				$("#width").val(Math.round( $(ui).width()*0.2645833 * 100)/100);
				$("#height").val(Math.round( $(ui).height()*0.2645833 * 100)/100);
			}
		};
	
  	  $( function() {
		$( ".main" )
			.resizable(
			{
				resize: showResize,
				start: showSize,
				stop: showSize
			})
			.click(function(e){selectCell(e, this)})
			;
		$( ".qrcode")
			.resizable(
			{
				aspectRatio: 1,
				resize: showResize,
				start: showSize,
				stop: showSize,
			})
			.click(function(e){selectCell(e, this)});
	  } );
  