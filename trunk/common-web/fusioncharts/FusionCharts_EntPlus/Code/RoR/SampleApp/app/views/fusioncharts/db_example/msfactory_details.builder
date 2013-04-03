#Creates xml with values for Factory Output
#along with their names.
#It uses the factories parameter from locals hash.
#This data is used for building xml for multi-series chart with factory name and output for each factory.
#For a multi-series chart, dataset tag with seriesName attribute has to be present.
#Within the dataset, add the set tag with value attribute.
xml = Builder::XmlMarkup.new
xml.chart(@chart_attributes) do
  xml.categories do 
  	 factory_dates_of_production.each do |factory_datepro|
  		xml.category(:label=>factory_datepro.formatted_full_date)
    end
  end
  
  factories.each do |factory|
    # whenever the factory name changes, start a new dataet element
    xml.dataset(:seriesName=>factory.name) do 
      factory.factory_output_quantities.each do |output|
        xml.set(:value=>output.quantity)
      end
    end
  end
end
