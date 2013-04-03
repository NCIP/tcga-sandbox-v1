#Creates xml with values for date of production and quantity for a particular factory
#It uses the factory_output parameter from locals hash.
#This data is used for building xml for chart with date of production and output quantity.
xml = Builder::XmlMarkup.new
xml.chart(:palette=>'2', :caption=>'Factory' + factory_id.to_s + ' Output ', :subcaption=>'(In Units)', :xAxisName=>'Date', :showValues=>'1', :labelStep=>'2') do
	factory_output.each do |output|
		xml.set(:label=>output.formatted_date,:value=>output.quantity)
	end
end
