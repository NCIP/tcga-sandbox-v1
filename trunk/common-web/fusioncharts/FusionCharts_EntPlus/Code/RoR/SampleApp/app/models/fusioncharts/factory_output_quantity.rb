#Model class to store output data of factories.
#As per Ruby On Rails conventions, we have the corresponding table 
#factory_output_quantities in the database.
class Fusioncharts::FactoryOutputQuantity < ActiveRecord::Base
  belongs_to :factory_master
  def formatted_full_date
     date_num= date_pro.strftime('%d').to_i
     month_num = date_pro.strftime('%m').to_i
     year_num = date_pro.strftime('%Y').to_i
     result_date=date_num.to_s+"/"+month_num.to_s+"/"+year_num.to_s 
     return result_date
  end
  def formatted_date
     date_num= date_pro.strftime('%d').to_i
     month_num = date_pro.strftime('%m').to_i
     result_date=date_num.to_s+"/"+month_num.to_s 
     return result_date
  end
  
end
