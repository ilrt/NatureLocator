(function($){ 
    $.fn.cropresize = function(options) {  

        var defaults = {  
            width:      50,  
            height:     50,
            vertical:   "center",
            horizontal: "center"
        };

        var options = $.extend(defaults, options);  

        return this.each(function() {  
        
            obj = $(this);
            
            var div = $('<div class="cropresize"></div>').css({
                width:    options.width,
                height:   options.height,
                overflow: "hidden",
                display:  "inline-block",
                position: "relative"
            });
            
            obj.wrap(div);
            
            obj.load(function() {

                width_ratio  = options.width  / $(this).width();
                height_ratio = options.height / $(this).height();
                
                if (width_ratio > height_ratio) {

                    $(this).width(options.width);
                    
                    switch(options.vertical) {
                        case 'top':
                            $(this).css("top", 0);
                        break;
                        case 'bottom':
                            $(this).css("bottom", 0);
                        break;
                        case 'center':
                        default:
                            $(this).css("top", (($(this).height() - options.height) / -2) + "px");
                    }
                   
                } else {
                    
                    $(this).height(options.height);
                    
                    switch(options.horizontal) {
                        case 'left':
                            $(this).css("left", 0);
                        break;
                        case 'right':
                            $(this).css("right", 0);
                        break;
                        case 'center':
                        default:
                            $(this).css("left", (($(this).width() - options.width) / -2) + "px");
                    }
                    
                }

                $(this).css("position", "absolute");
                
            });
                    
        }); 
        
    };
})(jQuery); 