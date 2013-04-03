# Class to hold the errors and warnings.
# The error code is stored in a string and warning codes are stored in an array.
# Contains methods to add warning or set error code.
# Also contains methods to access the warning codes, error code, warning messages, error message 
# and methods to convert the errors and warnings to other formats like query string or html.
class Fusioncharts::Exporter::FcError 
  # Warning codes.
  attr_reader :warn_codes
  # Error code
  attr_reader :err_code
  
  # Hash containing the error codes and corresponding error messages. 
  @@err_messages={
    "E100"=> "Insufficient Data.",
    "E101" => "Width/height not provided.",
    "E102" => "Insufficient export parameters.",
    "E400" => "Bad Request.",
    "E401"=> "Unauthorized Access.",
    "E403"=> "Access Forbidden.",
    "E404" => "Export Resource not found.",
    "E507" => "Insufficient Storage.",
    "E508" => "Server Directory does not exist.",
    "W509" => "File already exists.",
    "W510" => "Export handler's Overwrite setting is on. Trying to overwrite.",
    "E511" => "Overwrite forbidden. File cannot be overwritten.",
    "E512" => "Intelligent File Naming is Turned off",
    "W513" => " Background color not specified. Taking White (FFFFF) as default background color.",
    "E514" => " Error while creating binary data.",
    "E515" => " Problem creating the image. Please verify that RMagick is installed correctly.",
    "E516" => " Problem creating the PDF data. Please verify that Zlib is installed correctly."
    }
  
  # Initialize with a error code and an array of warnings.
  # - parameter ierror_code : The error code string
  # - parameter iw_codes : The array of warnings. (Optional)
  def initialize(ierror_code="",iw_codes=Array.new)
    @err_code = ierror_code
    @warn_codes = iw_codes
  end
  
  # Returns the array of warning codes for this FcError.
  def warning_codes
      return @warn_codes
  end
    
  # Returns the error code for this FcError.
  def error_code
      return @err_code
  end  
    
  # Returns the string of warning messages for this FcError.
  def warnings
    warning_msgs=""
    0.upto(@warn_codes.length-1) do |i|
        message = Fusioncharts::Exporter::FcError.warning_message(@warn_codes[i])
        if(message == nil or message.empty?)
         message = "Could not find warning message for "+ @warn_codes[i] 
       end
        # This is just a warning/notice
       warning_msgs+=message
     end
     return warning_msgs
  end  
  
  # Adds a warning  to this FcError.
  # - parameter warning_code : The code to be added
  def add_warning(warning_code)
    @warn_codes << warning_code
  end
  
  # Sets the error code for this FcError.
  # - parameter ierror_code : The error code to be set.
  def set_error_code(ierror_code)
   @err_code =  ierror_code
  end

  #Gets the error message for a particular code, returns nil if not found  
  def code2message()
    err_message = @@err_messages["E"+@err_code.to_s]
    return err_message
  end 
  
  # Whether there is no error code for this FcError.
  def empty?
    return @err_code.nil? || @err_code.empty? || @err_code==""
  end
  
  # Whether there are no warnings for this FcError.
  def no_warnings?
    return @warn_codes.empty?
  end
  
  # Converts the error to QueryString.
  # If there is an error, then adds successful status message and statusCode as 1.
  # Else, sets the statusMessage as the error message and statusCode as 0.
  def error2qs
    error_str=""
     if(@err_code==nil or @err_code.empty?)
      error_str+="&statusMessage=successful&statusCode=1"
    else 
      error_str+="&statusMessage="+code2message+"&statusCode=0"
    end
    return error_str
  end
  
  # Converts the warnings to query string format.
  # Returns a string starting with "&notice="
  def warnings2qs
    error_str = "&notice="+warnings
    return error_str
  end

  # Converts the error to html format.
  # If there is an error, then adds successful status message and statusCode as 1.
  # Else, sets the statusMessage as the error message and statusCode as 0.
  def error2html
    error_str = ""
    if(@err_code==nil or @err_code.empty?)
      error_str+="<br>statusMessage=successful<br>statusCode=1"
    else 
      error_str+="<br>statusMessage="+code2message+"<br>statusCode=0"
    end
    return error_str
  end

  # Converts the warnings to html format.
  # Returns a string starting with "<br>notice="
  def warnings2html
    error_str = "<br>notice="+warnings
    return error_str
  end

  
  # Converts the FcError to QueryString.
  def to_qs
    error_str = warnings2qs
    error_str+=error2qs
  end
  
  # Converts the FcError to html.
  def to_html
    error_str = warnings2html
     error_str+=error2html
  end
  
  # Gets the error message for a particular code, returns nil if not found  
  def self.code2message(err_code)
    err_message = @@err_messages[err_code.to_s]
    return err_message
  end    
  
  # Gets the warning message for a particular code, returns nil if not found  
  def self.warning_message(warning_code)
    message = code2message("W"+warning_code)
    return message
  end
  
end

