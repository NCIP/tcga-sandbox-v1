=begin

  Copyright (c) 2009 Infosoft Global Private Limited
 
=end

# Contains methods to help build the error message in case of any error during export.
class Fusioncharts::Exporter::ErrorHandler
  # Sets the errors in flash object. These flash messages can later be output in error view page
  # This method should ideally be called only once in the program execution. When an error occurs and the program halts and wants to show the error.
  # - parameter fc_error : Instance of FcError containing errors and warnings for this request.
  # - parameter flash : Instance of flash in which the errors need to be set
  # - parameter is_html : Whether the error string constructed should be in html format or query string format.
  # - parameter meta : The metadata values to be used.
  # - parameter file_name : The name of the file saved, if any.
    def self.set_flash_err(fc_error,flash,is_html,meta,file_name=nil)
         error_msgs = is_html ? fc_error.error2html : fc_error.error2qs
         warning_msgs= is_html ? fc_error.warnings2html : fc_error.warnings2qs
         flash[:notice]=warning_msgs
         flash[:error]=error_msgs
         separator = (is_html ? "<br>" : "&")
         meta_new = meta!=nil ? meta : Array.new
         if(!fc_error.empty?) 
           # This means error has occured, hence statusCode=0
           # Values for width and height are 0 in case of error. FileName is empty.
           width ="0"
           height="0"
           display_path=""
           meta_new.update({"width"=>width,"height"=>height})
         else 
           # status code =1
           flash[:error]+=separator+"statusCode=1"
           # Values for width and height in case of success. file_name is path to the file on server.
          display_path =Fusioncharts::Exporter::Properties.HTTP_URI.gsub!(/\/$/, '') + "/" +file_name
         end
            # Whether success or failure, add file URI , width and height and DomId when status success
             flash[:error]+= is_html ? meta2html(meta_new,display_path) : meta2qs(meta_new,display_path) 
       end
    
    # Builds the query string starting with & for meta values
    # - parameter meta : The metadata values to be used.
    # - parameter file_name_with_path : The name of the file saved along with the path, if any.
    def self.meta2qs(meta,file_name_with_path)
        meta_values=""
        if(!file_name_with_path.nil?)
          meta_values+="&fileName="+file_name_with_path
        end
        meta_values+= "&width=" +meta ["width"]
        meta_values+= "&height=" +meta ["height"]
        meta_values+= "&DOMId=" +meta ["DOMId"]
        return meta_values
      end
      
      # Builds the html string containing the meta values
      # - parameter meta : The metadata values to be used.
      # - parameter file_name_with_path : The name of the file saved along with the path, if any.
      def self.meta2html(meta,file_name_with_path)
        meta_values=""
        if(!file_name_with_path.nil?)
          meta_values="<br>fileName="+file_name_with_path
        end
        meta_values+= "<br>width=" +meta ["width"]
        meta_values+= "<br>height=" +meta ["height"]
        meta_values+= "<br>DOMId=" + (meta ["DOMId"]!=nil ? meta ["DOMId"] : "")
        return meta_values
      end
    
end