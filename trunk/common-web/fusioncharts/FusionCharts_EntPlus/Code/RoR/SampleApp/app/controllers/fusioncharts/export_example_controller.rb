# Contains functions to illustrate the following export features:
# * downloading the chart to the user's system
# * saving the chart to the server side
# * exporting multiple charts on a page
# * automatic initiation of export
# Demonstrates the ease of exporting FusionCharts as image or PDF.
# All the views related to this controller will use the "common" layout.
# As per Ruby On Rails conventions, we have the corresponding views with the same name as the function name in the controller.

class Fusioncharts::ExportExampleController < ApplicationController
  #This is the layout which all functions in this controller make use of.
  layout "common"
  
  protect_from_forgery  :except=>[:download,:save,:multiple_charts,:automatic]
  # Disable csrf protection on this controller:
  skip_before_filter :verify_authenticity_token


  
  #A Builder Template is used to build the XML data which is hard-coded.
  #Ideally, you would generate XML data documents in the builder at run-time, 
  #after interfacing with forms or databases etc. Such examples are also present.
  #We set the content-type header to text/html. 
  #render_chart function from the helper is invoked to render the chart.
  #The function itself has no code, all the work is done in the builder and the view.  
  def download
    
  end
  def save
    
  end
  def multiple_charts
   
  end
  def automatic
   
  end
end
