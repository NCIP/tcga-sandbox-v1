#Creates xml with values for monthly sales data 
#The values required for building the xml are hard-coded in this file
#The chart_options are merged
xml = Builder::XmlMarkup.new
options = {:caption=>'Monthly Unit Sales', :xAxisName=>'Month', :yAxisName=>'Units'}
options.merge!(chart_options[option])
xml.chart(options) do
  xml.set(:label=>'Jan',:value=>'462') 
  xml.set(:label=>'Feb',:value=>'857') 
  xml.set(:label=>'Mar',:value=>'671')
  xml.set(:label=>'Apr',:value=>'494')
  xml.set(:label=>'May',:value=>'761')
  xml.set(:label=>'Jun',:value=>'960')
  xml.set(:label=>'Jul',:value=>'629') 
  xml.set(:label=>'Aug',:value=>'622')
  xml.set(:label=>'Sep',:value=>'376')
  xml.set(:label=>'Oct',:value=>'494')
  xml.set(:label=>'Nov',:value=>'761')
  xml.set(:label=>'Dec',:value=>'960')
end