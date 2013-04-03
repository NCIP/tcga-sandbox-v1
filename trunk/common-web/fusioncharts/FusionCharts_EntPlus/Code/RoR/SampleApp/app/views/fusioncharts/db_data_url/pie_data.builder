#Creates xml with values for factories
#along with their names.
#The values required for building the xml is obtained from the corresponding controller action pie_data
#It accesses @factories from the controller.
#Here, this data is used for building xml for pie chart with factory name and total output.
xml = Builder::XmlMarkup.new(:indent=>0)
xml.chart(:caption=>'Factory Output report', :subCaption=>'By Quantity', :pieSliceDepth=>'30', :showBorder=>'1', :formatNumberScale=>'0', :numberSuffix=>' Units', :animation=>@animate_chart) do
	@factories.each do|factory|
		xml.set(:label=>factory.name,:value=>factory.total_quantity)
	end
end
