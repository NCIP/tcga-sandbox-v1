# This controller class shows how to render a pie-chart by retrieving 
# factory name and total output quantity dynamically at run-time,
# from the database. setdataUrl method is used here.
# As per Ruby On Rails conventions, we have the corresponding views with the same name 
# as the function name in the controller.
class Fusioncharts::DbDataUrlController < ApplicationController
  
  #Set dataUrl with animation property to 1.
	#NOTE: It's necessary to encode the dataUrl if you've added parameters to it.
  #In this example, we show how to connect FusionCharts to a database 
	#using dataUrl method. In our other examples, we've used dataStr method
	#where the XML is generated in the same page as chart. Here, the XML data
	#for the chart would be generated in pie_data function.
  #To illustrate how to pass additional data as querystring to dataUrl, 
	#we've added an animate	property, which will be passed to pie_data action. 
	#pie_data action would handle this animate property and then generate the 
	#XML accordingly.
  def default

    @str_data_url = "/Fusioncharts/db_data_url/pie_data?animate=0"
    
    #The common layout for this view
    render(:layout => "layouts/common")
  end
  
  # Finds all factories
  # Uses the Model FactoryMaster
  # Content-type for its view is text/xml
  def pie_data
    

      response.content_type = Mime::XML
      @animate_chart = params[:animate]
            
      if @animate_chart.nil? or @animate_chart.empty?
         @animate_chart = '1'
      end  
      
      # Find all the factories
      @factories = Fusioncharts::FactoryMaster.find(:all)
  end

end