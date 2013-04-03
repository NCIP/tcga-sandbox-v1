# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
   include FusionChartsHelper
    
  # Formats the date to dd/mm without leading zeroes
  def formatted_date(date_to_format)  
        date_num= date_to_format.strftime('%d').to_i
        month_num = date_to_format.strftime('%m').to_i
        result_date=date_num.to_s+"/"+month_num.to_s       
  end
end
