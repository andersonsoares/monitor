
$(document).ready(function() {
	// requisicao para pegar o numero de usuarios diferentes do evento
	
	$.ajax({
		url: jQuery('#urlGetTotalDiffUsers').text(),
		type: 'GET',
		success: function(response) {
			jQuery('#totalDiffUsers').text(response).fadeIn()
		}
	});
	
	// gerar grafico
	
	// carregar tweets

	
	// gerar grafico principal com tweets a cada dia
	document.body.appendChild(tt);
	$.ajax({
    	url: jQuery('#urlGetChartParams').text(),
    	type: 'GET',
    	success: function(response) {
    		var set = [];
    		$.each(response.map.countPerDay, function() {
                set.push({
                    x : this.x,
                    y : parseInt(this.y, 10)
                });
            });
    		
    		var data_get = {
                "xScale" : "time",
                "yScale" : "linear",
                "main" : [{
                    className : ".stats",
                    data : set
                }]
            };
    		
    		new xChart('line-dotted', data_get, '.xcharts2-line-dotted', opts);
    		
    	}
    });
	
}); // end document ready

var tt = document.createElement('div'),
leftOffset = -($('html').css('padding-left').replace('px', '') + $('body').css('margin-left').replace('px', '')),
topOffset = -32;
tt.className = 'ex-tooltip';

var opts = {
        paddingLeft:50,
        paddingRight: 0,
        axisPaddingTop: 5,
        axisPaddingLeft: 30,
        dataFormatX: function (x) { return Date.create(x); },
        tickFormatX: function (x) { return x.format('{dd}/{MM}'); },
        click: function (d, i) {
        	// parse date to milliseconds
        	var date = Date.parse(d.x);
        	
        	$.ajax({
            	url: jQuery('#urlGetChartParamsFromDay').text(),
            	type: 'GET',
            	data: {
            		date: date
            	},
            	success: function(response) {
            		console.log(response)
            		var set = [];
            		$.each(response.map.countPerHour, function() {
                        set.push({
                            x : this.x,
                            y : parseInt(this.y, 10)
                        });
                    });
            		
            		var data_get = {
                        "xScale" : "time",
                        "yScale" : "linear",
                        "main" : [{
                        	color: "color3",
                            className : ".stats",
                            data : set
                        }]
                    };
            		
            		new xChart('line-dotted', data_get, '.graficoHoras', optsGraphHours);
            		jQuery('#titleChartHours').html("Graphic for day '<strong>"+d.x.format('{Month} {ord}')+"</strong>'")
            	}
            });
        	
        },
        mouseover: function (d, i) {
        	 var pos = $(this).offset();
        	 jQuery('#titleChartHours').html("Click to see Graphic for '<strong>"+d.x.format('{Month} {ord}')+"</strong>'")
	            $(tt).text(d.x.format('{Month} {ord}') + ': ' + d.y).css({
	
	                top: topOffset + pos.top,
	                left: pos.left
	
	            }).show();
	         
        },
        "mouseout": function (x) { $(tt).hide(); }
};

var optsGraphHours = {
        paddingLeft:50,
        paddingRight: 0,
        axisPaddingTop: 5,
        axisPaddingLeft: 30,
        dataFormatX: function (x) { return Date.create(x); },
        tickFormatX: function (x) { return x.format('{HH}:{mm}'); },
        mouseover: function (d, i) {
        	 var pos = $(this).offset();
        		
	            $(tt).text(d.x.format('{HH}:{mm}') + ' - ' + d.y +' tweets').css({
	
	                top: topOffset + pos.top,
	                left: pos.left
	
	            }).show();
	         
        },
        "mouseout": function (x) { $(tt).hide(); }
};

