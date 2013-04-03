#There are two examples in this controller.
#*Pie-chart for total ouput quantities of each factory by getting data from database and using dataStr method
#*Pie-chart for total ouput quantities of each factory and a link to another chart which gives detailed information for selected factory
#All the views related to this controller will use the "common" layout.
#As per Ruby On Rails conventions, we have the corresponding views 
#with the same name as the function name in the controller.
class Fusioncharts::DbExampleController < ApplicationController
  #This is the layout which all functions in this controller make use of.
  layout "common"
  
  #This action retrieves the Factory data 
  #which holds factory name and corresponding total output quantity.
  #The view for this action basic_dbexample will use these values to construct the
  #xml for this chart. To build the xml, the view takes help of the builder file (basic_factories_quantity.builder)
  def basic_dbexample
      response.content_type = Mime::HTML
      #Get data from factory masters table
      @factories = Fusioncharts::FactoryMaster.find(:all)
  end
  
  #This action retrieves the factory data, sets the default value of @animate_chart to 1 if ":animate" is not present in the request.
  #The view for this action default.html.erb will use the array values to construct the
  #xml for this chart. To build the xml, the view takes help from the builder file (default_factories_quantity.builder)
  def default
     response.content_type = Mime::HTML
    @animate_chart = params[:animate]
    if @animate_chart.nil? or @animate_chart.empty?
      @animate_chart = '1'
    end
    #Get data from factory masters table
    @factories = Fusioncharts::FactoryMaster.find(:all)
  end
  
  #This action retrieves the factory data for the given "id" parameter
  #The view for this action is detailed.html.erb and it uses the builder file
  #factory_details.builder to build the xml for the column chart.
  def detailed
      response.content_type = Mime::HTML
      @factory_id = params[:id]
      factory = Fusioncharts::FactoryMaster.find(@factory_id)
      @factory_output_quantities = factory.factory_output_quantities
  end

     
  #This action retrieves the factory data for all factories
  #The view for this action is multiseries.html.erb and it uses the builder file
  #msfactory_details.builder to build the xml for the chart.
  def multiseries
      response.content_type = Mime::HTML
      @chart_attributes={:caption=>'Factory Output report', :subCaption=>'By Quantity', :xAxisName=>'Factory',:yAxisName=>'Units', :rotateValues=>'1', :formatNumberScale=>'0',  :animation=>'1' }
      #Get data from factory masters table
      @factories = Fusioncharts::FactoryMaster.find(:all,:include=>"factory_output_quantities")
      #selects the date of production in the ascending order
      @factory_dates_of_production = Fusioncharts::FactoryOutputQuantity.find(:all,:select=>"DISTINCT date_pro",:order=>"date_pro asc")
  end
end
