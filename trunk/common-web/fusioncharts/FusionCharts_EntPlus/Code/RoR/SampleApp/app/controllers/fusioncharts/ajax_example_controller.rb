# Example controller to show the usage of FusionCharts with ajax
class Fusioncharts::AjaxExampleController < ApplicationController
  
 # The data for the first chart is obtained and this is used in the corresponding builder and view files 
 def default_factories_chart
   
   response.content_type = Mime::HTML 
   @factories = Fusioncharts::FactoryMaster.find(:all)
   if(@factories.size>0)
     factory = @factories[0]
    @factory_id = factory.id # Default factory
      factory = Fusioncharts::FactoryMaster.find(@factory_id)
    @factory_name = factory.name
    @factory_output_quantities = factory.factory_output_quantities
   end
  render :layout=>"common"
 end
 
# This action is called from the javascript function updateChart via ajax 
# Expects the request parameter factory_id. Finds the details for the requested factory
# This data is then used by the corresponding view. 
# The view renders the chart using the object tag.
 def factory_chart
    @factory_id = params[:factory_id]
    factory = Fusioncharts::FactoryMaster.find(@factory_id)
    @factory_name = factory.name
    @factory_output_quantities = factory.factory_output_quantities
   
  end

end
