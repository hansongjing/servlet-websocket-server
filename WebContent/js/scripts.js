/**
 * @author Dzharvis
 */
var m = ['#1', '#2', '#3', '#4'];
var incr = 0;
function changePics() {
	
	var $pics = $("#home_gallery").find("div");
	
	if (incr >= $pics.length) {
		incr = 0;
	}

	var next = incr + 1;

	if (incr + 1 >= $pics.length) {
		next = 0;
	}
	
	$($($pics).get(incr)).animate({
		opacity : 0
	}, 1000, function() {
		$($($pics).get(incr)).css("opacity", "0");
	});

	$($($pics).get(next)).animate({
		opacity : 1
	}, 1000, function() {
		$($($pics).get(next)).css("opacity", "1");
	});

	incr++;
}

function showMenu() {
	$("#navig").animate({
		height : "1.3em",
		opacity : 0.7
	}, 1500, function() {
		$("#navig").css("height", "auto");
		$("#navig").css("opacity", "0.7");
	});
}

function shiftPics() {
	var $albums = $("div.p_block");
	var g_shift_x = 0;
	var g_shift_y = 0;
	var w = ($(document).width()*0.7)/4.8;
	$("div.p_block").css("width", w +"px");
	//alert($albums.length);
	for (var k = 0; k < $albums.length; k++) {
		
		var $p = $albums.get(k);
		//alert($($p).find($('img')).length);
		
		var leng = $($p).find($('img')).length;
		for (var i = 0; i < leng; i++) {
			var elem = $($p).find($('img'))[i];
			//alert(elem);
			var pos = $(elem).offset();
			//alert(pos);
			$(elem).css("opacity","0");
			$(elem).animate({
				top : "" + (20 + g_shift_y + (i * 4)),
				left : "+=" + (g_shift_x+(i * 4)),
				opacity : (i + 1) / $($p).find($('img')).length
			}, 200+(leng*i/2*50*(Math.random()+.1))+(leng*80*i/2*(Math.random()+.1)), function() {
				$(elem).css("opacity","1");
			});
		}
		g_shift_x+=w+w*0.2;
		//alert($(document).width().toInt());
		if(g_shift_x>($(document).width()*0.7)-w){
			
			g_shift_x = 0;
			g_shift_y += w+w*0.2;
		}
	}

}

function showBigPic(pic_url){
	alert(pic_url);
}

function sortPics(){
	$("#home_gallery").find("img").css("width", ($(document).width()*0.65)/4);
	$("#home_gallery").find("img").css("cursor", "pointer");
	var $pics = $("#home_gallery").find("img");
//	for(var i=0; i<$pics.length; i++){
//		var elem = $pics[i];
//		//alert(elem);
//		$(elem).click(function(){
//			showBigPic("q"+i);
//		});
//	}
	
	
}
