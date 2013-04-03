# Example controller to show the usage of FusionCharts with json
class Fusioncharts::JsonExampleController < ApplicationController
  
#This is the layout which all views corresponding to the actions in this controller will make use of.  
layout "common"

 #Here, we've used a pre-defined Data.json (contained in /Data/ folder)
 #Ideally, you would NOT use a physical data file. Instead you'll have 
 #your own code virtually relay the XML data document.
 #The action itself has no code, all the work is done in the builder and the view.
 def json_url_chart
   response.content_type = Mime::HTML
 end
 
  #A .json.erb Template is used to build the JSON data which is hard-coded.
  #Ideally, you would generate JSON data documents in the template or action at run-time, 
  #after interfacing with forms or databases etc. 
  #render_chart function from the helper is invoked to render the chart.
  #The action itself has no code, all the work is done in the view and json template.  
  def json_data_chart
   response.content_type = Mime::HTML
  end

end
