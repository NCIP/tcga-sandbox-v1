#Model class to store data of factory id and name.
#As per Ruby On Rails conventions, we have the corresponding table 
#factory_masters in the database.
class Fusioncharts::FactoryMaster < ActiveRecord::Base
  has_many :factory_output_quantities,
                :order => 'date_pro asc'
  #Calculates the total output quantity by summing the quantity from factory_output_quantities             
  def total_quantity
    self.factory_output_quantities.sum(:quantity)
  end

end
