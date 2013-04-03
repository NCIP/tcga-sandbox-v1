# Include this module wherever you want to use render_chart, render_chart_html and other functions
# Version 1.5 (August 2010) - Added other parameters as options hash. Added function enable_FC_print_manager_js to enable PrintManager.
# Version 1.4 (2010) - Added other parameters to the render_chart and render_chart_html methods. ( w_mode, color, scale_mode, lang, detect_flash_version,auto_install_redirect etc)
# Version 1.3 (March 2009) - debug_mode changes
# Version 1.2 (February 2009) - Compatible with rails 2.2 and above - removed the second parameter in concat.
# Version 1.1 (January 2009) - Added get_UTF8_BOM function
module FusionChartsHelper
  
  # Renders a chart from the swf file passed as parameter either making use of setChartDataURL method or
  # setChartData method. The width and height of chart are passed as parameters to this function. If the chart is not rendered,
  # the errors can be detected by setting debugging mode to true while calling this function. The chart can be registered with javascript 
  # by setting registerWithJS to true while calling this function.
  # - parameter chart_swf :  pass swf file that renders the chart. 
  # - parameter str_url : pass URL path to the xml file.
  # - parameter str_data : pass xml content.
  # - parameter chart_id : Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id. Datatype: String 
  # - parameter chart_width : pass value as integer as the width of the chart.
  # - parameter chart_height : pass value as integer as the height of the chart.
  # - parameter debug_mode : By default, false. pass value as true ( a boolean ) for debugging errors, if any, while rendering the chart.
  # - parameter register_with_js :By default, false. pass value as true ( a boolean ) for chart to be registered with javascript.
  # - parameter options={} : Hash containing other options to render the chart. The options can be
  #   - data_format : "xml" or "json".
  #   - renderer : "flash" or "javascript".
  #   - w_mode : Window Mode - expected values '', 'window','transparent' or 'opaque'.
  #   - color : Background color of the flash movie (here chart).
  #   - scale_mode : 'noScale','exactFit','noBorder','showAll'.
  #   - lang :  Application Message Language - 2-letter code. Currently, only "EN" is supported.
  #   - detect_flash_version : If set on, the user would be redirected to Adobe site if Flash player 8 is not installed.
  #   - auto_install_redirect : Checks the Flash Player version and if version is less than 8 and autoInstallRedirect is set on then asks the user to install Flash Player from Adobe site.
  # Can be called from html block in the view where the chart needs to be embedded.
  def render_chart(chart_swf,str_url,str_data,chart_id,chart_width,chart_height,debug_mode=false,register_with_js=false,options={})
    chart_width=chart_width.to_s
    chart_height=chart_height.to_s
    concat("\t\t<!-- START Script Block for Chart "+chart_id+" -->\n\t\t") 
    concat(content_tag("div","\n\t\t\t\tChart.\n\t\t",{:id=>chart_id+"Div",:align=>"center"}))
    concat("\n\t\t<script type='text/javascript'>\n")
        
    debug_mode_num= debug_mode ? "1" : "0"
    register_with_js_num = register_with_js ? "1" : "0" 
    
    w_mode = nil
    color = nil
    scale_mode = nil
    lang = nil
    detect_flash_version = nil
    auto_install_redirect = nil
    data_format = nil
    renderer=nil
    render_at = nil
    data_source = nil
      
    if !options.nil? && options.is_a?(Hash)
      w_mode = options[:w_mode]
      color = options[:color]
      scale_mode = options[:scale_mode]
      lang = options[:lang]
      detect_flash_version = options[:detect_flash_version] ? "1" : "0"
      auto_install_redirect = options[:auto_install_redirect] ? "1" : "0"
      render_at = options[:render_at]
      data_format = options[:data_format]
      renderer = options[:renderer]
    end
    
     if !str_data.empty?
      data_source = str_data
      if data_format.nil? || data_format.blank?
        data_format="xml"
      end
      logger.info("The dataSource used is str_data.The data is " + str_data)
    else
      data_source = str_url
      if data_format.nil? || data_format.blank?
        data_format="xmlurl"
      end

      logger.info("The dataSource used is strUrl.The URL is " + str_url)
    end
    
    if(render_at.nil?)
      render_at=chart_id+"Div"
    end
    
    fcParams =Fusioncharts::FcParameters.new(chart_swf,chart_id,chart_width,chart_height,debug_mode_num,register_with_js_num,data_source,data_format,w_mode,color,scale_mode,lang,detect_flash_version,auto_install_redirect,renderer,render_at)
    fcParamsInJson = fcParams.to_json # this takes care of nil values
    concat("\t\t\t\tvar chart_"+chart_id+"=new FusionCharts("+fcParamsInJson+").render();\n")
    concat("\t\t</script>\n")
    concat("\t\t<!-- END Script Block for Chart "+chart_id+" -->\n")
    
  end
  # Renders a chart from the swf file passed as parameter either making use of setChartDataURL method or 
  # setChartData method. The width and height of chart are passed as parameters to this function. If the chart is not rendered,
  # the errors can be detected by setting debug mode to true while calling this function.
  # - parameter chart_swf :  SWF file that renders the chart. 
  # - parameter str_url : URL path to the xml file.
  # - parameter str_data : XML content.
  # - parameter chart_id :  String for identifying chart.
  # - parameter chart_width : Integer for the width of the chart.
  # - parameter chart_height : Integer for the height of the chart.
  # - parameter debug_mode : By default, false. true ( a boolean ) for debugging errors, if any, while rendering the chart.
  # - parameter register_with_js :By default, false. pass value as true ( a boolean ) for chart to be registered with javascript.
  # - parameter options={} : Hash containing other options to render the chart. The options can be
  # -  w_mode : Window Mode - expected values '', 'window','transparent' or 'opaque'.
  # -  color : Background color of the flash movie (here chart).
  # -  scale_mode : 'noScale','exactFit','noBorder','showAll'.
  # -  lang :   Application Message Language - 2-letter code. Currently, only "EN" is supported.
  # Can be called from html block in the view where the chart needs to be embedded.
  def render_chart_html(chart_swf,str_url,str_data,chart_id,chart_width,chart_height,debug_mode=false,register_with_js=false,options={})
    chart_width=chart_width.to_s
    chart_height=chart_height.to_s
    
    debug_mode_num= debug_mode ? "1" : "0"
    register_with_js_num= register_with_js ? "1" : "0" 
      w_mode = ""
      color = ""
      scale_mode = ""
      lang = ""     
      
    if !options.nil? && options.is_a?(Hash)
      w_mode = options[:w_mode].nil? ? "" : options[:w_mode]
      color = options[:color].nil? ? "" : options[:color]
      scale_mode = options[:scale_mode].nil? ? "" : options[:scale_mode]
      lang = options[:lang].nil? ? "" : options[:lang]
     
    end
    
    str_flash_vars=""
    if str_data.empty?
      str_flash_vars="chartWidth="+chart_width+"&chartHeight="+chart_height+"&debugmode="+debug_mode_num+"&registerWithJS="+register_with_js_num+"&DOMId="+chart_id+"&dataURL="+str_url
      logger.info("The method used is setDataURL.The URL is " + str_url)
    else
      str_flash_vars="chartWidth="+chart_width+"&chartHeight="+chart_height+"&debugmode="+debug_mode_num+"&registerWithJS="+register_with_js_num+"&DOMId="+chart_id+"&dataXML="+str_data
      logger.info("The method used is setDataXML.The XML is " + str_data)
    end
    
    str_flash_vars+="&scaleMode="+scale_mode+"&lang="+lang
    
    concat("\t\t<!-- START Code Block for Chart "+chart_id+" -->\n\t\t")
    
    object_attributes={:classid=>"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000",:codebase=>"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0",:width=>chart_width,:height=>chart_height,:id=>chart_id}
    
    param_attributes1={:name=>"allowscriptaccess",:value=>"always"}
    param_tag1=content_tag("param","",param_attributes1)
    
    param_attributes2={:name=>"movie",:value=>chart_swf}
    param_tag2=content_tag("param","",param_attributes2)
    
    param_attributes3={:name=>"FlashVars",:value=>str_flash_vars}
    param_tag3=content_tag("param","",param_attributes3)
    
    param_attributes4={:name=>"quality",:value=>"high"}
    param_tag4=content_tag("param","",param_attributes4)
    
    # w_mode, by default is set to be "window"
    w_mode = (w_mode=='transparent' || w_mode=='opaque') ? w_mode : "window"
    
    param_attributes5={:name=>"wmode",:value=>w_mode}
    param_tag5=content_tag("param","",param_attributes5)
    
    param_attributes6={:name=>"bgcolor",:value=>color}
    param_tag6=content_tag("param","",param_attributes6)
    
    embed_attributes={:src=>chart_swf,:FlashVars=>str_flash_vars,:quality=>"high",:width=>chart_width,:height=>chart_height,:name=>chart_id,:allowScriptAccess=>"always",:type=>"application/x-shockwave-flash",:pluginspage=>"http://www.macromedia.com/go/getflashplayer",:wmode=>w_mode,:bgColor=>color}
    
    embed_tag=content_tag("embed","",embed_attributes)
    
    concat(content_tag("object","\n\t\t\t\t"+param_tag1+"\n\t\t\t\t"+param_tag2+"\n\t\t\t\t"+param_tag3+"\n\t\t\t\t"+param_tag4+"\n\t\t\t\t"+param_tag5+"\n\t\t\t\t"+param_tag6+"\n\t\t\t\t"+embed_tag+"\n\t\t",object_attributes))
    concat("\n\t\t<!-- END Code Block for Chart "+chart_id+" -->\n")
  end
  
  # Deprecated from rails 2.2.2.
  # Uses render_component.  
  # Renders a chart using the swf file passed as parameter by calling an action to get the xml for the 
  # setDataXML method. The width and height of chart are passed as parameters to this function. If the chart is not rendered,
  # the errors can be detected by setting debugging mode to true while calling this function.
  # - parameter chart_swf :  SWF file that renders the chart. 
  # - parameter controller_name : The complete name of the controller containing the action.
  # - parameter action_name : The name of the action which will provide the xml.
  # - parameter params : The parameters that have to be passed to the action as an array.
  # - parameter chart_id :  String for identifying chart.
  # - parameter chart_width : Integer for the width of the chart.
  # - parameter chart_height : Integer for the height of the chart.
  # - parameter debug_mode : True ( a boolean ) for debugging errors, if any, while rendering the chart.
  # - parameter options={} : Hash containing other options to render the chart. 
  # Can be called from html block in the view where the chart needs to be embedded.
  def render_chart_get_xml_from_action(chart_swf,controller_name,action_name,params,chart_id,chart_width,chart_height,debug_mode=false,register_with_js=false,options={})
    logger.info("The controller to be contacted is " + controller_name)
    logger.info("The action to be performed is " + action_name)
    str_data= render_component(:action=>action_name,:controller=>controller_name,:params=>params)
    logger.info("The xml obtained from the given action is " + str_data)
    render_chart(chart_swf,"",str_data,chart_id,chart_width,chart_height,debug_mode,register_with_js,options)
  end
  
  # Inserts the javascript code to enable the FC Print Manager.
  # New feature since v3.2
  def enable_FC_print_manager_js
    
    concat("<script type='text/javascript'><!--\n FusionCharts.printManager.enabled(true);\n// -->\n</script>")
    
  end
  
  
  # This function can be used when time needs to be added to the URL.
  # This will help avoiding cache of the page rendered by the URL.
  # Can be used for dataURL method.
  def add_cache_to_data_url(str_data_url)
    cache_buster= Time.now.strftime('%d_%m_%y_%H_%M_%S')
    if(str_data_url.index('?')==nil)
      str_data_url = str_data_url + "?FCCurrTime=" + cache_buster.to_s
    else
      str_data_url = str_data_url + "&FCCurrTime=" + cache_buster.to_s
    end
    logger.info("The URL after appending time is " + str_data_url)
    return str_data_url
  end
  
  # This function returns the BOM for UTF8.
  # BOM needs to be placed as first few bytes in the xml before providing to the chart.
  # This can be used in the XML provider views.
  def get_UTF8_BOM
    
    utf8_arr=[0xEF,0xBB,0xBF]
    utf8_str = utf8_arr.pack("c3")
    
    return utf8_str
  end
end