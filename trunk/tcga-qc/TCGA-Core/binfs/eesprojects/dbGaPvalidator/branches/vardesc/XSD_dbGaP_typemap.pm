package XSD_dbGaP_typemap;
our (@ISA, @EXPORT);

BEGIN {
    require Exporter;
    @ISA = ('Exporter');
    @EXPORT = qw($typemap);
}
# BCR XSD types mapped to equivalent dbGaP DD types
# if the type is mapped to an arrayref, the first array elt is 'string'
# and the remaining elts are the allowed values
$typemap = {
    'admin_res_attribute' => 'string',
    'onePlusToFourPlus' => ['string','0','1+','2+','3+','4+','null'],
    'percentByTens' => ['string',"<10%","10-19%","20-29%","30-39%","40-49%","50-59%","60-69%","70-79%","80-89%","90-99%", 'null'],
    'posNegClose' => ['string',"Positive","Negative","Close",'null'],
    'posNegStat' => ['string',"Positive","Negative","Unknown","Equivocal","Indeterminate","Performed but Not Available","Not Performed",'null'],
    'result_outcome' => ['string','Pelvic Nodes Absent','Pelvic Nodes Present','Paraaortic Nodes Absent','Paraaortic Nodes Present','Supraclavicular Absent','Supraclavicular Present','Parametrium Absent','Parametrium Present','Bladder Absent','Bladder Present','Extra-Pelvic Metastatic Disease Absent','Extra-Pelvic Metastatic Disease Present','null'],
    'utility:clinical_res_attributes' => 'string',
    'utility:common_res_attributes' => 'string',
    'utility:generic_day' => 'integer',
    'utility:generic_month' => 'integer',
    'utility:generic_year' => 'integer',
    'utility:yes_or_no' => ['string','YES','NO','null'],
    'xs:NCName' => 'string',
    'xs:anyURI' => 'string',
    'xs:decimal' => 'decimal',
    'xs:integer' => 'integer',
    'xs:string' => 'string'
};
