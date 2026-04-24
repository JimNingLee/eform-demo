/*
 * 사용자 안내를 위한 말풍선
 * author : 박용준, 설유진
 * date : 2013.08.09
 */

$.fn.popbox = function(options){
    var settings = $.extend({
      selector      : this.selector,
      open          : '.open',
      box           : '.box',
      arrow         : '.arrow',
      arrow_border  : '.arrow-border',
      close         : '.close'
    }, options);

    if(!options){
    	options = {
    			targetId	: '',
    			width		: 350,
				left 		: 50,
				top 		: 10,
				arrowpos	: 10,
				autoClose 	: 0,
				color		: "#fff",
				html		: "<small>확인증을 인쇄하시려면 위 <font color='red'>전자문서발급</font> 하시어 출력하시기 바랍니다.</small>"
    	};
    }
    
    $('#popboxDes').html(options.html);
    $('.box').css('width', options.width);
    $('.box').css('background-color', options.color);
    $('.arrow').css('border-bottom-color', options.color);
    $('.arrow-border').css('border-bottom-color', options.color);
    
    var methods = {
      open: function(){
        var pop = $(this);
        var box = $('.box');

        box.find(settings['arrow']).css({'left': options.arrowpos});
        box.find(settings['arrow_border']).css({'left': options.arrowpos});

        if(box.css('display') == 'block'){
          this.close();
        } else {
          box.css({'display': 'block', 'top': options.top, 'left': (options.left)-(box.position().left)});
        }
      },

      close: function(){
    	  $(settings['box']).css('display','none');
//          $(settings['box']).fadeOut("slow");
        }
    };

//    $(document).bind('keyup', function(event){
//        methods.close();
//    });
    
    $('.box').bind('click', function(event){
    	methods.close();
    });

//    $('#'+options.targetId).bind('click', function(event){
//    	var fn = $('#'+options.targetId).attr('onclick');
//    	if(null != fn && fn.indexOf('methods.close();') == -1){
//    		eval(fn);
//    		methods.close();
//    	}else{
//    		methods.close();
//    	}
//    });

    $(window).scroll(function(){
    	methods.close();
    }); 
    
    if(null != options && options.autoClose > 0){
    	self.setTimeout(function(){methods.close();}, options.autoClose);
    }

    methods.open();
    
  };
