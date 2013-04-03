# In this controller, we will plot a pie-chart showing total output of quantities 
# and name of each factory in a pie-section.
# On clicking on a pie-section, we obtain detailed information on the quantity 
# produced and date of production of the factory by using javascript.
class Fusioncharts::DbJsController < ApplicationController
  
  #This action retrieves the factory data
  # The corresponding view default.html.erb will be rendered. 
  # In the view, rjs template (data.rjs) is rendered to dynamically create a javascript array to hold the factory data.
  # This javascript array is used by the updateChart javascript function, to dynamically 
  # update the second chart when clicked on a pie section in the first chart.
  def default
    response.content_type = Mime::HTML
    #Get data from factory masters table
    @factories = Fusioncharts::FactoryMaster.find(:all)
  end
end
