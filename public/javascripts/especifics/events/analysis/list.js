$(document).ready(function() {
	
	$('#startDate').datetimepicker({
		language : 'pt-BR',
		maskInput: true,
		pick12HourFormat: true,
		format   : 'dd/MM/yyyy hh:mm:ss PP'
	});
	$('#finishDate').datetimepicker({
		language : 'pt-BR',
		maskInput: true,
		pick12HourFormat: true,
		format   : 'dd/MM/yyyy hh:mm:ss PP'
	});
	
	jQuery("#buttonSend").click(function(e) {
		e.preventDefault();
		
		var data = jQuery('#formNew').serialize();
		var url = jQuery('#urlRunAnalysis').text();
		console.log(data)
		
		$.ajax({
			url: url,
			type: 'POST',
			data: data,
			success: function(response) {
				console.log(response)
				var code = response.code;
				
				if (code == 200) {
					// everything ok
					
					jQuery('#modal-newForm').modal('hide');
					
					Growl.success({
		                title: "Analysis is running!",
		                text: response.message,
		                sticky: true
		            })
					
				} else {
					if (code == 400) {
						jQuery('#error-msg').text(response.message)
						jQuery('#error-box').show()
					}
				}
				
			},
			error: function(response) {
				console.log(response)
			}
		});
		
	});
});

