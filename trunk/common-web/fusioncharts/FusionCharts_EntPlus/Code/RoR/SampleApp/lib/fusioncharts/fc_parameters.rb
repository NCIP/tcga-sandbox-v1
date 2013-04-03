class Fusioncharts::FcParameters
  attr_accessor :swfUrl
  attr_accessor :width
  attr_accessor :height
  attr_accessor :renderAt
  attr_accessor :renderer
  attr_accessor :dataSource
  attr_accessor :dataFormat
  attr_accessor :id
  attr_accessor :lang
  attr_accessor :debugMode
  attr_accessor :registerWithJS              
  attr_accessor :detectFlashVersion
  attr_accessor :autoInstallRedirect
  attr_accessor :scaleMode
  attr_accessor :menu
  attr_accessor :bgColor
  attr_accessor :quality
 
  def initialize(swf_url, chart_id,  width,
       height,  debug_mode,  register_with_js,data_source,  data_format,
       window_mode=nil,  bg_color=nil,  scale_mode=nil,  lang=nil,
       detect_flash_version=nil,  auto_install_redirect=nil,
         renderer=nil,render_at=nil,menu=nil)
     
    @swfUrl = swf_url
    @id = chart_id
    @width=width
    @height=height
    @debugMode= debug_mode
    @registerWithJS  = register_with_js
    @dataSource = data_source
    @dataFormat=data_format
    if(!window_mode.nil?)
       @wMode = window_mode
    end
    if(!bg_color.nil?)
        @bgColor = bg_color
    end
    if(!scale_mode.nil?)
        @scaleMode = scale_mode
    end
    if(!lang.nil?)
    @lang = lang
    end
    if(!detect_flash_version.nil?)
    @detectFlashVersion = detect_flash_version
    end
    if(!auto_install_redirect.nil?)
        @autoInstallRedirect = auto_install_redirect
    end
    if(!renderer.nil?)
        @renderer = renderer
    end
    if(!render_at.nil?)
       @renderAt = render_at
    end
    if(!menu.nil?)
       @menu= menu
    end
  end

end