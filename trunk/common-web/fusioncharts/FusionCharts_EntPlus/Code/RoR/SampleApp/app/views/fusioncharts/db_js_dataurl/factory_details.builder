#Creates xml with values for date of production and quantity for a particular factory
#It uses the @factory_output_quantities and @factory_id from the corresponding controller.
#This data is used for building xml for chart with date of production and output quantity
xml = Builder::XmlMarkup.new
xml.chart(:palette=>'2', :caption=>'Factory' + @factory_id.to_s + ' Output ', :subcaption=>'(In Units)', :xAxisName=>'Date', :showValues=>'1', :labelStep=>'2') do
	@factory_output_quantities.each do |output|
		xml.set(:label=>output.formatted_date,:value=>output.quantity)
	end
end

