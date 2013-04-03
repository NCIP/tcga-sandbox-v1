<?php
/***********************************************************************************
 * FUSIONCHARTS XT API PHP CLASS 
 * @author     	FusionCharts 
 * @version 	3.2.2        
 *
 *
 * 
 * Version 3.2.2 (16 August 2011)
 *  - Added support for all FusionChart v3.2.2 chart types
 *  - encodeChars property is set to false by default
 *  - Added support for external JSON string data
 *  - Added support to enable Print Manager
 *  - Added support to set renderer to JavaScript mode.
 *  
 *
 *
 *
 *
 *
 *  30/12/2008     [
 * 					 A new Parameter - 'display' in renderChart() to return chart HTML 
 *						as string rather that sending it to response; 
 * 					 Function encodeSpecialChars modified for PHP 4 Support;
 *                   Function renderChartHTML modified for <embed> tag.
 *                 ]
 *  Version: 1.0.1 (10 November 2008) [ Fix PHP Short Tag, MSColumnLine3D Fix, Function  
 *                                     addDatasetsFromDatabase Modifiaction for Transposed Data,
 *                                     Fix Transparent setting  ]
 *  Version: 1.0 (30 July 2008)
 *
 *  FusionCharts PHP Class easily handles all FusionCharts XML elements like
 *  chart, categories, dataset, set, Trendlines, vTrendlines, vline, styles etc.
 *  It's easy to use. It binds data into FusionCharts XML Structures.
 *
 ***********************************************************************************  
 */

class FusionCharts{

	var $chartType;               # Type of Chart
	var $chartID;				  # ID of the Chart for JS interactivity(optional)
	var $SWFFile; 				  # Name of the required FusionCharts SWF file 
	var $SWFPath;                 # Relative path to FusionCharts SWF files
	var $width;                   # FusionCharts width
	var $height;                  # FusionCharts height

	# Separator/Delimiter for list of Parameters
	var $del;

     # Chart XML string 
     var $strXML; 
	
	# Chart Series Types : 1 => single series, 2=> multi-series, 
	# 3=> scatter and bubble, 4 => MSStacked
     var $seriesType;               

	# Chart Atributes 
	var $chartParams = array();     # List of Chart Parameters
	
	var $categoriesParam;           # Categories Parameter Setting 
	var $categoryNames = array();   # Category array for storing Category set
	var $categoryNamesCounter;      # Category array counter
	
	var $dataset = array();         # Dataset array
	var $datasetParam = array();    # Dataset parameter setting array
	var $datasetCounter;            # Dataset array counter
	var $setCounter;                # Set array counter 


    # trendLines 
    var $trendLines = array();      # trendLines array
	var $tLineCounter;              # trendLines array counter

    # chart messages
	var $chartMSG;					#  Stores chart messages 

	var $chartSWF = array();		# Charts SWF array
	
	# Color 		
	var $arr_FCColors = array(); 	# Colorset to be applied to dataplots
	var $userColor= array();       	# Array to store user defined colors 
    var $userColorON;               # Flag : user defined color : true or false
    var $userColorCounter;			# Number of colors used in the user-defined color list
	
	# Cache Control
	var $noCache;					# Flag - Range : true/false : Stops caching chart SWFs
	var $DataBaseType;              # Flag - DataBase Type values : "mysql" => default, "Oracle". 	
	var $encodeChars;               # XML for dataXML or dataURL
	
	# Advanced Chart settings
	var $JSC = array();
	
	var $wMode;
	
	
	# Addtional variables for MSStacked charts
	var $MSStDataset = array();       # Store Primary Dataset
	var $MSStDatasetParams = array(); # Parameter setting for Primary Dataset
	
	var $MSStDatasetCounter;          # Number of Primary Datasets
    var $MSStSubDatasetCounter;       # Number of Secondary Datasets
	var $MSStSetCounter;              # Number of data values in Dataset
	# lineset 
	var $lineSet = array();           # Stores Lines/Linesets in MSStacked Dual Y charts
	var $lineSetParam = array();      # parametets of each line in a Lineset
	var $lineCounter;                 # Number of lines in a Lineset
	var $lineSetCounter;              # Number of Linesets
	var $lineIDCounter;               # LineIDs
	
	# vtrendLines array
    var $vtrendLines = array();       # Store vtrendLines
	var $vtLineCounter;               # Number of vtrendLines

    # style array
    var $styles = array();            # Styles array
    var $styleDefCounter;             # Define counter
	var $styleAppCounter;             # Apply counter
	
	
	var $FC_DEFAULT_CONFIG = array() ;   # Default configurations
	

	 # FusionCharts Constructor
	 # while creating FusionCharts object, the Constructor will initialize the object with values passed to it as arguments i.e.
	 # chats parameters like chartType, width, height, chartID
	 function FusionCharts($chartType="column2d",$width="400",$height="300",$chartID="",$isTransparent=""){
		
		  #Set mode to Transparent
		  $this->wMode=$isTransparent;
		  
		  # Initilaize the Array with definition of all Charts 
		  $this->setChartArrays();
		  
		  # Initilaize list of colors
		  $this->colorInit();
		  
		  # Set Chart's name
		  $this->chartType=strtolower($chartType);
		  # Get Chart's Series Type
		  $this->getSeriesType();		
		  
		  # Set Chart's Width and Height
		  $this->width=$width;
		  $this->height=$height;
		  
		  # Set ChartID, Default is {Chart Name + Counter}
		  # for this session is required
		  if ($chartID==""){
		  	# Count the number of charts rendered
		  	$chartCounter=@$_SESSION['chartcount'];
			if($chartCounter<=0 || $chartCounter==NULL){
				$chartCounter=1;
			}
			
		    $this->chartID=$chartType . $chartCounter;
			$_SESSION['chartcount']=++$chartCounter;
			
		  }else{
		    $this->chartID=$chartID;
		  }
	       	     
		  # Set Default Parameter Delimiter  to ';'
		  $this->del=";";
		
		  # Set Default Path to Chart SWF files
		  $this->SWFPath="";
		  $this->SWFFile=$this->SWFPath . $this->chartSWF[$this->chartType][0]  . ".swf";
		  
		  # Initialize Parameter for category set
		  $this->categoriesParam="";
		  $this->categoryNamesCounter=1;
		  
		  # Initialize Category Array
		  $this->createCategory($this->categoryNamesCounter);
		
		  # Initialize Dataset Variables
	      $this->datasetCounter=0;
          $this->setCounter= 0;
		  if($this->seriesType>1){
		    $this->setCounter++;
		  }
		
		  # Initialize MSStacked Dataset Variables
		  if($this->seriesType==4){
		    $this->MSStDatasetCounter=0;
		    $this->MSStSubDatasetCounter=0;
	        $this->MSStSetCounter=0;
			
			$this->lineCounter=0;
            $this->lineSetCounter=0;
			$this->lineIDCounter=0;
          }
		  
		  # Initialize vTrendLines Array 
		  if($this->seriesType==3){
		    $this->vtLineCounter=1;
		    $this->createvTrendLines($this->vtLineCounter);
	      }
		  
		  # Initialize TrendLines Array
		  $this->tLineCounter=1;
		  $this->createTrendLines($this->tLineCounter);
		
		  # Initialize Array to store STYLES
		  $this->styleDefCounter=1;
		  $this->styleAppCounter=1;
		  $this->createStyles("definition");
		  $this->createSubStyles("definition","style");
		  $this->createSubStylesParam("definition","style",$this->styleDefCounter);
		
	      $this->chartMSG="";
		  
		  #Variable to store XML
		  $this->strXML="";
		  
		  $this->userColorON = false;        # Flag for userColor (by default it is false)
		  $this->userColorCounter=0;    
		  
		  $this->noCache=false;              # Cache default false
		  $this->DataBaseType="mysql";       # default database MySql
		  
		  // JS Constructor
		  $this->JSC["debugmode"]=false;       # debugmode default is false
		  $this->JSC["registerwithjs"]=false;  # registerwithJS default is false
	      $this->JSC["bgcolor"]="";            # bgcolor default not set
	      $this->JSC["scalemode"]="noScale";   # scalemode default noScale
	      $this->JSC["lang"]="EN";             # Language default EN
		  
		  $this->encodeChars=false;
		  
		  
		  // initialize 
		  $this->FC_INITIALIZE();
		  $this->FC_INITSTATIC();
		  
	 }
	 
#------------------------- Public Functions ----------------------------------------------
     # Special Character
     function encodeXMLChars($option=true){
	     $this->encodeChars=$option;
	 }
	 # Set path where chart SWF files are stored
	 function setSWFPath($SWFPath){
	   $this->SWFPath=$SWFPath;
	   $this->SWFFile=$this->SWFPath . $this->chartSWF[$this->chartType][0]  . ".swf";
	 }
	 # Set Parameter Delimiter, Defult Parameter Separator is ";"
	 function setParamDelimiter($strDelm){
	   $this->del=$strDelm;
	 }
	 
	 # The setChartParam() function adds or changes single Chart parameter, -
	 # it takes Parameter Name and its Value
	 function setChartParam($paramName, $paramValue){
	   $this->chartParams[$paramName]=$this->encodeSpecialChars($paramValue);
	 }
	 
	 # The setChartParams() function adds or changes Chart parameters
	 # it takes list of parameters separated by delimiter
	 # e.g. "caption=xyz caption;subCaption=abcd abcd abcd;xAxisName=x axis;yAxisName=y's axis;bgColor=f2fec0;animation=1"
	 # Default Delimiter is ";"
	 function setChartParams($strParams){
	   $listArray=explode($this->del,$strParams);
	   foreach ($listArray as $valueArray) {
	       $paramValue=explode("=",$valueArray,2);
		   if($this->validateParam($paramValue)==true){
		    $this->chartParams[$paramValue[0]]=$this->encodeSpecialChars($paramValue[1]);
		   }
	   }
	 }
	 
	  # The setCategoriesParams() function sets parameters for Category set <categories>
	 function setCategoriesParams($catParams){
	   $this->categoriesParam .= $this->ConvertParamToXMLAttribute($catParams);
	 }
	 
	
	  # The addCategory() function adds a Chart Category (and optional vLine element)
	 function addCategory($label="",$catParams="",$vlineParams = "" ){
	 	 $strCatXML="";
		 $strParam="";
		 $label=$this->encodeSpecialChars($label);
		 # Check whether vlineParam is present
		 if($vlineParams==""){
		   # Check whether catParam is absent
		   if($catParams!=""){
			   # Convert category Parameters into XML
		       $strParam = $this->ConvertParamToXMLAttribute($catParams);

		    }
			# Add label and parameters to category 
		   $strCatXML ="<category label='" . $label . "' " . $strParam . " />";
           
		 }else{
		   # Add vLine
		   $strParam = $this->ConvertParamToXMLAttribute($vlineParams);
		   # Add vLine parameters
		   $strCatXML="<vLine " . $strParam . "  />"; 
		 }
		 # Store into categoryNames array
		 $this->categoryNames[$this->categoryNamesCounter]=$strCatXML;
		 # Increase Counter
		 $this->categoryNamesCounter++;
	 }
	 
	  # The addDataset() function adds a new dataset element
	 function addDataset($seriesName, $datasetParams=""){
	   $this->datasetCounter++;
       $this->createDataset($this->datasetCounter);
	   	   
	   $this->setCounter++;
	   $this->createDataValues($this->datasetCounter,"_" . $this->setCounter);	
		
	   $seriesName=$this->encodeSpecialChars($seriesName);		
	   # Create seriesName and dataset parameters
	   $tempParam="";
	   $tempParam ="seriesName='" . $seriesName . "' ";
	   $tempParam .= $this->ConvertParamToXMLAttribute($datasetParams);
	   
	   $colorParam="";
	    
		# Add user-defined Color    
	    if ($this->userColorON == true){
			$pos = strpos(strtolower($tempParam), " color");
			if ($pos === false) {
			 $colorParam=" color='" . $this->getColor($this->datasetCounter-1) . "'";
			}
        }
		
		# Set datasetParam array
		$this->datasetParam[$this->datasetCounter]=$tempParam . $colorParam;
		
	 }
	 
				   
	 # The addChartData() function adds chart data elements
	 function addChartData($value="",$params="",$vlineParams = "" ){
	     $strSetXML="";
		 
		 # Choose dataset depending on seriesType and get chart's XML
		 switch ($this->seriesType){
		 case 1:
		 case 2:
 		   	 $strSetXML=$this->genSSMSChartDataXML($value,$params,$vlineParams);
		     break;
     	 case 3:
			 $strSetXML=$this->genScatterBubbleChartDataXML($value,$params,$vlineParams); 
		     break;
		 case 4:
		 	 $strSetXML=$this->genSSMSChartDataXML($value,$params,$vlineParams);
			 break;
		 }
		 
		 # Add xml to dataset array and Increase setCounter
		 switch ($this->seriesType){
		 case 1:
		     $this->dataset[$this->setCounter]=$strSetXML;
		     $this->setCounter++;
			 break;
		 case 2:
		 case 3:
		     $this->dataset[$this->datasetCounter]["_" . $this->setCounter]=$strSetXML;
		     $this->setCounter++;
			 break;
		 case 4:
		     $this->MSStDataset[$this->MSStDatasetCounter][$this->MSStSubDatasetCounter][$this->MSStSetCounter]=$strSetXML;
			 $this->MSStSetCounter++;
			 break;
		 }
	 }
	 
	  # CreateMSStDataset function creates MS-Stacked Chart's Primary dataset 
	 function createMSStDataset(){
		 $this->MSStDatasetCounter++;
		 $this->MSStDataset[$this->MSStDatasetCounter]= array();
		 $this->MSStDatasetParams[$this->MSStDatasetCounter]=array();
	 }

	 # The addMSStSubDataset() function adds MS-Stacked Charts sub-dataset 
	 function addMSStSubDataset($seriesName, $datasetParams){
	   $this->MSStSubDatasetCounter++;
       $this->MSStDataset[$this->MSStDatasetCounter][$this->MSStSubDatasetCounter]= array();
	   		
	   $seriesName=$this->encodeSpecialChars($seriesName);			
	   $tempParam="";
	   # Creating seriesName
	   $tempParam ="seriesName='" . $seriesName . "' ";
	   $tempParam .= $this->ConvertParamToXMLAttribute($datasetParams);
	   		
	   $this->MSStSetCounter++;	
	   
	   # Add Parameter to MSStDatasetParams array
	   $this->MSStDatasetParams[$this->MSStDatasetCounter][$this->MSStSubDatasetCounter]=$tempParam;
	   
	 }

	 # The addMSLineset() function adds Lineset element to MS-Stacked Dual Y Chart
	 function addMSLineset($seriesName, $linesetParams){
	   $this->createLineset();
	   $this->lineSetCounter++;
       $this->lineSet[$this->lineCounter][$this->lineSetCounter]= array();
	   	
	   $seriesName=$this->encodeSpecialChars($seriesName);				
	   $tempParam="";
	   $tempParam ="seriesName='" . $seriesName . "' ";
	   
	   $tempParam .= $this->ConvertParamToXMLAttribute($linesetParams); 
	  		
	   $this->lineIDCounter++;
	   # Setting lineSetParam array with Parameter set	
	   $this->lineSetParam [$this->lineSetCounter]=$tempParam;
	   
	  	  
	 }
      
	 # The addMSLinesetData() function adds a line to the lineset
	 function addMSLinesetData($value="",$params="",$vlineParams = "" ){
	     $strSetXML="";
		 # Getting parameter set  
		 $strSetXML=$this->genSSMSChartDataXML($value,$params,$vlineParams);
         
		 # Setting paramter to lineSet array
		 $this->lineSet[$this->lineCounter][$this->lineSetCounter][$this->lineIDCounter]=$strSetXML;
		 
		 # Increase lineIDCounter
		 $this->lineIDCounter++;
	 }
	 
	  # The setGridParams() function sets SSGrid Chart's Parameters
     function setGridParams($gridParams){
	   $this->setChartMessage($gridParams);
	 }
	 
	 # The addTrendLine() function adds a trendline 
	 function addTrendLine($tlineParams){
	   
	   $listArray=explode($this->del,$tlineParams);
	   foreach ($listArray as $valueArray) {
    	   $paramValue=explode("=",$valueArray,2);
		   if($this->validateParam($paramValue)==true){
		      $this->trendLines[$this->tLineCounter][$paramValue[0]]=$this->encodeSpecialChars($paramValue[1]);
		   }
	   }
	   $this->tLineCounter++;
	 }
		
 	# The addVTrendLine() function adds a Vertical trendline to supported charts
	 function addVTrendLine($vtlineParams){
	   $listArray=explode($this->del,$vtlineParams);
	   foreach ($listArray as $valueArray) {
    	   $paramValue=explode("=",$valueArray,2);
		   if($this->validateParam($paramValue)==true){
		     $this->vtrendLines[$this->vtLineCounter][$paramValue[0]]=$this->encodeSpecialChars($paramValue[1]);
		   }
	   }
	   $this->vtLineCounter++;
	 }
	 
	 
	 # The addColors() function adds user-defined colors
	 function addColors($ColorList)
        {
		   $listArray=explode($this->del, $ColorList);
 		   $this->userColorON = true;
		   foreach ($listArray as $valueArray) {
		  	   $this->UserColor[$this->userColorCounter]=$valueArray;
			   $this->userColorCounter++;
	    	}
        }
	 
	 # The clearUserColor() function removes all user-defined colors
	 function clearUserColor()
     {
          $this->userColorON = false;
     }
	 
	  # The defineStyle() function defines a Charts Style
	 function defineStyle($styleName,$styleType,$styleParams){
		$this->styles["definition"]["style"][$this->styleDefCounter]["name"]= $styleName;
		$this->styles["definition"]["style"][$this->styleDefCounter]["type"]= $styleType;
		
		$listArray=explode($this->del,$styleParams);
	    foreach ($listArray as $valueArray) {
    	   $paramValue=explode("=",$valueArray,2);
		   if($this->validateParam($paramValue)==true){ 
		      $this->styles["definition"]["style"][$this->styleDefCounter][$paramValue[0]]= $this->encodeSpecialChars($paramValue[1]);
		   }
		}   
        $this->styleDefCounter++;
    	
	 }
     # The applyStyle() function applies a define style to chart elements
	 function applyStyle($toObject,$styles){
		$this->styles["application"]["apply"][$this->styleAppCounter]["toObject"]= $toObject;
		$this->styles["application"]["apply"][$this->styleAppCounter]["styles"]= $styles;
		
        $this->styleAppCounter++;
	 }
	  # The function addCategoryFromArray() adds Category from Array
	 function addCategoryFromArray($categoryArray){
	         # Iterate through each category in the array
			 foreach ($categoryArray as $value) {
				# Add category
			  	$this->addCategory($value);
			 }
	 }
	 
	 # The function addChartDataFromArray() creates dataset values(set) and category, from array
	 function addChartDataFromArray($dataArray, $dataCatArray=""){
		if(is_array($dataArray)){
			if ($this->seriesType==1){
			   # Array for Single series charts
			   # aa[..][..]="label" aa[..][..]="Value"
			   foreach($dataArray as $arrayvalue){
				 if(is_array($arrayvalue)){
				   $this->addChartData($arrayvalue[1],"label=" . $arrayvalue[0] );	   
				 }
			   } 		
			}else{
			   # Array for Multi series charts
			   if(is_array($dataCatArray)){
				   foreach($dataCatArray as $value){
				   	   # Add category
					   $this->addCategory($value);
				   }
			   }
			   # Add data to the chart by creating Datasets
			   foreach($dataArray as $arrayvalue){
			     if(is_array($arrayvalue)){
				   $i=0;
				   $aaa[0]=""; $aaa[1]="";
				   foreach($arrayvalue as $value){
				     if($i>=2){
					   	$this->addChartData($value);
					 }else{
					    $aaa[$i]=$value;
					 }				 					
				     if($i==1){
					   $this->addDataset($aaa[0],$aaa[1]);
					 }
					 $i++;
					 
				   }
				 } 
			   }
			}
		}	
	 }
	 
	   # Database type set like ORACLE and MYSQL
	 function setDataBaseType($dbType){
	    $this->DataBaseType=strtolower($dbType);
	 }
	
	 
	 	 # The addCategoryFromDatabase() function adds Category from database -
	 # by Default from MYSQL recordset. You can use setDatabaseType() function -
	 # to set the type of database.
	 function addCategoryFromDatabase($query_result, $categoryColumn){
		if($this->DataBaseType=="mysql"){	 
			 # fetch recordset till end of file is reached
			 while($row = mysql_fetch_array($query_result)){
				# add category
				$this->addCategory($row[$categoryColumn],"","" );
			 } 
		}elseif($this->DataBaseType=="oracle"){
		     # Fetch recordset till end of file is reached
			 while(OCIFetchInto($query_result, $row, OCI_ASSOC)){
				# addcategory() function adds the category
				$this->addCategory($row[$categoryColumn],"","" );
			 } 
		
		}	 
	 }
	 
	 # The addDataFromDatabase() function adds single series data from database -
	 # by default from MYSQL recordset. You can use setDatabaseType() function -
	 # to set the type of database to work on. 
	 function addDataFromDatabase($query_result, $db_field_ChartData,$db_field_CategoryNames="", $strParam="",$link=""){
	   	      	   
		$paramset="";		   
	   if($this->DataBaseType=="mysql"){	
		   # fetching recordset
		   while($row = mysql_fetch_array($query_result)){
			
			          if($link==""){
						  $paramset="";
					   }else{
						   # Get the link
						  $paramset="link=" . urlencode($this->getLinkFromPattern($row,$link));
					   }
					   if ($strParam=""){
						 $strParam=$paramset;
					   }else{
						 $strParam .= $this->del . $paramset; 
					   }
			
			 # Convert to set element and save to $partXML
			 if($db_field_CategoryNames==""){
				$data=@$row[$db_field_ChartData];
				if($strParam!="")
					$this->addChartData($this->encodeSpecialChars($data),$strParam);
				else
				 $this->addChartData($this->encodeSpecialChars($data));
			}
			else{
				$data=@$row[$db_field_ChartData];
				$label=@$row[$db_field_CategoryNames];
				$this->addChartData($this->encodeSpecialChars($data),"label=" . $this->encodeSpecialChars($label) . $this->del .$strParam,"" );
			}
         }
	   }elseif($this->DataBaseType=="oracle"){
		   # fetching recordset
		   while(OCIFetchInto($query_result, $row, OCI_ASSOC)){
			
			          if($link==""){
						  $paramset="";
					   }else{
						   # Getting link
						  $paramset="link=" . urlencode($this->getLinkFromPattern($row,$link));
					   }
					   if ($strParam=""){
						 $strParam=$paramset;
					   }else{
						 $strParam .= $this->del . $paramset; 
					   }
			
			 # Convert to set element and save to $partXML
			 if($db_field_CategoryNames==""){
				$data=@$row[$db_field_ChartData];
				if($strParam!="")
					$this->addChartData($this->encodeSpecialChars($data),$strParam);
				else
				 $this->addChartData($this->encodeSpecialChars($data));
			}
			else{
				$data=@$row[$db_field_ChartData];
				$label=@$row[$db_field_CategoryNames];
				$this->addChartData($this->encodeSpecialChars($data),"label=" . $this->encodeSpecialChars($label) . $this->del .$strParam,"" );
			}
	     }
       } 
   
	 }
	 	
	
	  # The addDatasetsFromDatabase() function adds dataset and set elements from -
	  # database, by Default, from MySql recordset. You can use setDatabaseType() function -
	  # to set the type of database to work on.
	 function addDatasetsFromDatabase($query_result, $ctrlField, $valueField,$datsetParamArray="",$link=""){
			
			 # Initialize variables
			 $paramset="";
			 $tempContrl="";
			 if(is_array($datsetParamArray)==false){
			 	$datsetParamArray=array();
			 }
			 
			 # Calculate total no of array elements in datsetParamArray
			 $arrLimit=count($datsetParamArray);
			 $i=1;
			 $tempParam="";
			 if($this->DataBaseType=="mysql"){ 	
				 ##### For My SQL Connection
				 $FieldArray=explode($this->del,$valueField);
				 if(count($FieldArray)>1){
   				     ### Muli Series
					 # fetching recordset
					 while($row = mysql_fetch_array($query_result)){
					    # Add Category
						$this->addCategory($row[$ctrlField]);
					 }
					
					 $k=0;
					 # Add daatset for multiple fields
					 foreach ($FieldArray as $FieldName) {
					   					  
						   if($k<$arrLimit){
							  $tempParam = $datsetParamArray[$k];  
  						   }else{
							  $tempParam="";
						   }
						   # Add Dataset with adddataset() function
						   $this->addDataset($FieldName,$tempParam);
						   
						   # rewind query result
						   mysql_data_seek($query_result,0);
						   while($row = mysql_fetch_array($query_result)){ 
						   
						        # Generating URL link 
						        if($link==""){
							      $paramset="";
						        }else{
							      # Generating URL link from getLinkFromPattern
							      $paramset="link=" . urlencode($this->getLinkFromPattern($row,$link));
								}
								# add value to dataset
						        $this->addChartData($row[$FieldName], $paramset, "");
								
						   }
						  $k++; 
					 }
					
				  }else{			 
				 
					 ### Single Series
					 # fetching recordset
					 while($row = mysql_fetch_array($query_result)){
						   # Creating Control break depending on ctrlField
						   # if ctrlField value changes then dataset will be Generated
						   if ($tempContrl!=$row[$ctrlField]){
									if($i<=$arrLimit){
									  $tempParam = $datsetParamArray[$i-1];  
									}else{
									  $tempParam="";
									}
									# Add Dataset with adddataset() function
									$this->addDataset($row[$ctrlField],$tempParam);
									$tempContrl=$row[$ctrlField];
									$i++;
						   }
						# Generating URL link 
						   if($link==""){
							  $paramset="";
						   }else{
							  # Generating URL link from getLinkFromPattern
							  $paramset="link=" . urlencode($this->getLinkFromPattern($row,$link));
						   }
						   # add value to dataset
						   $this->addChartData($row[$valueField], $paramset, "");
						   
					}
			    }		
		  }elseif($this->DataBaseType=="oracle"){
				 # For Oracle Connection
				 # fetching recordset
				 while(OCIFetchInto($query_result, $row, OCI_ASSOC)){
					   # Create Control break depending on ctrlField
					   # if ctrlField value changes then dataset will be Generated
					   if ($tempContrl!=$row[$ctrlField]){
								if($i<=$arrLimit){
								  $tempParam = $datsetParamArray[$i-1];  
								}else{
								  $tempParam="";
								}
								# add Dataset 
								$this->addDataset($row[$ctrlField],$tempParam);
								$tempContrl=$row[$ctrlField];
								$i++;
					   }
					# Generating URL link 
					   if($link==""){
						  $paramset="";
					   }else{
						  # Generating URL link from getLinkFromPattern
						  $paramset="link=" . urlencode($this->getLinkFromPattern($row,$link));
					   }
					   # add value to dataset
					   $this->addChartData($row[$valueField], $paramset, "");
				}
		  }	 
	}
	
	# Set SWF to Transparent
	function setwMode($isTransparent=""){
	    $this->wMode=$isTransparent;
	}
	 
	# The getXML() function renders final chart XML and returns it
	 function getXML(){
				
		$this->strXML="";
		# Call the getChartParamsXML() function to get chart parameter XML
		$strChartParam="";
		
		$strChartParam=$this->getChartParamsXML();
		if($this->seriesType==1){
			  # Addtional setting for Single Series Line chart
			   if(gettype(strpos($this->chartType,"line"))!="boolean"){
				  if(strpos($strChartParam,"lineColor")===false){
					$colorSet=$this->getColor(0);
					$this->setChartParams("lineColor=" . $colorSet );
				  }
				  
			   }
			  # Addtional setting for Single Series Area chart
			   if(gettype(strpos($this->chartType,"area"))!="boolean"){
				  if(strpos($strChartParam,"areaBgColor")===false){
					$colorSet=$this->getColor(0);
					$this->setChartParams("areaBgColor=" . $colorSet );
				  }
			   }
		}
		
		# Open Chart element
		$this->strXML  =  "<chart " . $this->getChartParamsXML() . " >";
			
		# call getCategoriesXML() function to generate category XML elements
		$this->strXML .= $this->getCategoriesXML();
		# call getDatasetXML() function to generate chart's dataset XML elements
		$this->strXML .= $this->getDatasetXML();
		# call getvTrendLinesXML() function to generate vTrendLines XML elements
		if($this->seriesType==3){
			$this->strXML .= $this->getvTrendLinesXML();
		}  
		#  Call getLinesetXML() function to generate lineSet XML elements
		if($this->seriesType==4){
			$this->strXML .= $this->getLinesetXML();
		} 
		# call getTrendLinesXML() function to generate TrendLines XML elements
		$this->strXML .= $this->getTrendLinesXML();
		# call getStylesXML() function to generate styles elements
		$this->strXML .= $this->getStylesXML();
		
		# Close Chart element
		$this->strXML .= "</chart>";
		
		# Return XML output
		return $this->strXML;
	  }
	  
	   # The function setChartMessage() sets the message to display on the chart
	 function setChartMessage($msgParam){
		if($this->chartMSG==""){
		  $this->chartMSG="?";
		}else{
		  $this->chartMSG .= "&";
		}
		
		$strParamCol="";	
		$strParamCol=$this->ConvertParamToXMLAttribute($msgParam, true);	
		
		$this->chartMSG .= $strParamCol; 
	}
	
	 # The setOffChartCaching() function sets whether chart SWF files are not to be cached 
	 function setOffChartCaching($swfNoCache=false){
	 	$this->noCache=$swfNoCache;
	 }

	 # The renderChart() function generates the chart 
	 function renderChart($renderAsHTML=false,  $display=true){
		
		$this->strXML=$this->getXML();	
		$this->SWFFile=$this->SWFPath . $this->chartSWF[$this->chartType][0]  . ".swf";
		
		# Stop chart caching if required
		if($this->noCache==true){
		  if($this->chartMSG==""){
		     $this->chartMSG = "?nocache=" . microtime();
		  }else{
		     $this->chartMSG .=  "&nocache=" . microtime();
		  }
		}
	    # render chart using RenderAsHTML option
		if($renderAsHTML==true){
			if($display==true){
			   # true: render using html embedding method
		       print $this->renderChartHTML($this->SWFFile . $this->chartMSG ,"", $this->strXML, $this->chartID,  $this->width, $this->height, $this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
			}else{
			   # true: render using html embedding method
		       return $this->renderChartHTML($this->SWFFile . $this->chartMSG ,"", $this->strXML, $this->chartID,  $this->width, $this->height, $this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
			
			}
		}else{
		   if($display==true){ 
			   # false: render using JavaScript embedding method
		       print $this->renderChartJS($this->SWFFile . $this->chartMSG ,"", $this->strXML, $this->chartID,  $this->width, $this->height, $this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
			   return true;
		   }else{
		       # false: render using JavaScript embedding method
		       return $this->renderChartJS($this->SWFFile . $this->chartMSG ,"", $this->strXML, $this->chartID,  $this->width, $this->height, $this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
		   
		   }	   
		}
		
	  } 
	  
	 # The renderChartFromExtXML() function renders Chart form External XML (string)
	 function renderChartFromExtXML($dataXML,$renderAsHTML=false){
		# Print the HTML chart
		if($renderAsHTML==true){
		 print $this->renderChartHTML($this->SWFFile . $this->chartMSG ,"", $dataXML, $this->chartID,  $this->width, $this->height, $this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
		}else{
		 print $this->renderChartJS($this->SWFFile,"",$dataXML,$this->chartID, $this->width, $this->height,$this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
		}
	  }
	  
     
	 # The renderChartFromExtData() function renders Chart form External data (string)
	 function renderChartFromExtData($data, $dataformat="xml",$renderAsHTML=false)
	 {
		# Print the HTML chart
		if($renderAsHTML==true)
		{
			 print $this->renderChartHTML($this->SWFFile . $this->chartMSG ,"", $data, $this->chartID,  $this->width, $this->height, $this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
		}
        else
        {
            $this->setDataFormat($dataformat);
            print $this->renderChartJS($this->SWFFile,"",$data,$this->chartID, $this->width, $this->height,$this->JSC["debugmode"], $this->JSC["registerwithjs"], $this->wMode);
		}
	  }
	  
	 #The function SetInitParam() adds extra chart settings 
	 function setInitParam($tname,$tvalue){
        
            $trimName= strtolower(str_replace(" ","",$tname));
            $this->JSC[$trimName]=$tvalue;

	 }


 ##------------ PRIVATE FUNCTIONS ------------------------------------------------------
	 
	 # The function getDatasetXML()  returns xml for the chart from the <dataset> .... </dataset>
	 function getDatasetXML(){
       # Calling dataset function depending on seriesType
	   switch ($this->seriesType){
	   case 1 :
	     return $this->getSSDatasetXML();
		 break;
	   case 2 :
	     return $this->getMSDatasetXML();
	     break;
	   case 3 :
	   	 return $this->getMSDatasetXML();
	     break;
	   case 4 :
	     return $this->getMSStackedDatasetXML();
	     break;
	   }
	 }
	 # By getChartParamsXML() function, we can fetch charts array and convert into XML -
	 # and return like "caption='xyz' xAxisName='x side' ............
	 function getChartParamsXML(){
		$partXML="";	
		# Fetching charts each array and converting into chart parameter
		foreach($this->chartParams as $part_type => $part_name){
		  
		   $partXML .= $part_type . "='" . $this->encodeSpecialChars($part_name) . "' ";
		   
		}
		# Return Chart Parameter
		return $partXML;
	 }
	 
	 
	 # The function getCategoriesXML() for getting Category part XML
	 function getCategoriesXML(){
	   if($this->seriesType>1){
		   $partXML="";
		   # Add categories parameter
		   $partXML="<categories " . $this->categoriesParam . " >";
		   if($this->categoryNamesCounter>1){
		   foreach($this->categoryNames as $part_type => $part_name){
				   if($part_name!=""){ 
					 # add elements 
					 if($part_name!="Array"){
						 $partXML .= $part_name;
					}
				   } 
		   }
		   }
		   # Closing <categories>
		   $partXML .="</categories>";
		   return $partXML;
	   }
	 }
	 # Create single set element
     #       <set value='30' />
     #       <set value='26' />

	 function getSSDatasetXML(){
	   if($this->seriesType==1){
		   $partXML="";
		   foreach($this->dataset as $part_type => $part_name){
			   if($part_name!=""){ 
				 # Add elements 
				 if($part_name!="Array"){
				   $partXML .= $part_name;
				 }
			   } 
		   }
		   return $partXML;
	   }
	 }
	 
	 # getMSDatasetXML for getting datset part XML
	 #     <dataset seriesName='Product A' color='AFD8F8' showValues='0'>
     #       <set value='30' />
     #       <set value='26' />
     #     </dataset>
	 function getMSDatasetXML(){
	   if($this->seriesType>1){
		   $partXML="";
		   foreach($this->dataset as $part_type => $part_name){
			   $partXML .="<dataset  " . $this->datasetParam[$part_type] . " >";
			   foreach($this->dataset[$part_type] as $part_type1 => $part_name1){
					   if($part_name1!=""){ 
						 # Add elements 
						 if($part_name1!="Array"){
						   $partXML .= $part_name1;
						 }
					   } 
			   }
			   $partXML .="</dataset>";
		   }
		   return $partXML;
	   }
	 }
	 
	 
	 # Function getMSStackedDatasetXML for getting datset part XML from ms stacked chart 	dataset array
	 # <dataset>
     #     <dataset seriesName='Product A' color='AFD8F8' showValues='0'>
     #       <set value='30' />
     #       <set value='26' />
     #     </dataset>
	 # </dataset>
	 
	 function getMSStackedDatasetXML(){
	   if($this->seriesType==4){
		   $partXML="";
		   
		   foreach($this->MSStDataset as $part_type => $part_name){
		     $partXML .= "<dataset>";
		     foreach($this->MSStDataset[$part_type] as $part_type1 => $part_name1){ 
		        $partXML .= "<dataset " . $this->MSStDatasetParams[$part_type][$part_type1] . " >";
		        foreach($this->MSStDataset[$part_type][$part_type1] as $part_type2 => $part_name2){ 
		             if ($part_type2!=""){
					    $partXML .= $part_name2;
					 } 
		        }
		        $partXML .= "</dataset>";
		     }
		     $partXML .= "</dataset>";
		   }
		   
		   return $partXML;
	   }
	 }

	 # The function getLinesetXML() for getting Lineset XML
	 function getLinesetXML(){
	   # If seriesType MSStackedColumn2DLineDY (4) then Lineset element will be Generated
	   if($this->seriesType==4){
		   $partXML="";
		   # Fetching lineSet array and Generating lineset xml element
		   foreach($this->lineSet as $part_type => $part_name){
		     $partXML .= "<lineset " . $this->lineSetParam[$part_type]   . " >";
		     foreach($this->lineSet[$part_type] as $part_type1 => $part_name1){ 
		         foreach($this->lineSet[$part_type][$part_type1] as $part_type2 => $part_name2){   
				   if ($part_type2!=""){
					  $partXML .= $part_name2;
					 } 
				 }	 
		       }
		     $partXML .= "</lineset>";
		   }
		   return $partXML;
	   }
	 }


	 # The function getTrendLinesXML() create XML output depending on trendLines array
	 #  <trendLines>
	 #    <line startValue='700000' color='009933' displayvalue='Target' /> 
	 # </trendLines>
	 function getTrendLinesXML(){
		$partXML="";
		$lineXML="";	
		# Fetch trendLines array
		foreach($this->trendLines as $l_type => $l_name){
		  # Line element
		  $lineXML .="<line ";
		  # Fetch trendLines array within array element
		  foreach($this->trendLines[$l_type] as $part_type => $part_name){
			 
				$lineXML .= $part_type . "='" . $this->encodeSpecialChars($part_name) . "' ";
			
		  }
		  # Close line element
		  $lineXML .=" />";
		}
		# If line element present, then adding $lineXML within trendLines element 
		
		$pos = strpos($lineXML, "=");
		if ($pos!==false){
		   $partXML = "<trendLines>" . $lineXML . "</trendLines>"; 
		}else{
		   # Return nothing
		   $partXML="";
		}
		# Return trendLines xml
		return $partXML;
	 }
	 
	 
	 # The function getvTrendLinesXML() returns XML output depending on trendLines array
	 #  <vTrendlines>
	 #    <line displayValue='vTrendLines' startValue='5' endValue='6' alpha='10' color='ff0000'  />
	 # </vTrendlines>
	 function getvTrendLinesXML(){
		$partXML="";
		$lineXML="";	
		# Fetch vtrendLines array
		foreach($this->vtrendLines as $l_type => $l_name){
		  # staring line element
		  $lineXML .="<line ";
		  # Fetch vtrendLines array with in array element
		  foreach($this->vtrendLines[$l_type] as $part_type => $part_name){
			 if($part_name!=""){ 
				$lineXML .= $part_type . "='" . $this->encodeSpecialChars($part_name) . "' ";
			 } 
		  }
		  # Close line element
		  $lineXML .=" />";
		}
		# If line element present then adding $lineXML with in vtrendLines element 
		$pos = strpos($lineXML, "=");
        if ($pos !== false) {
		   $partXML = "<vTrendlines>" . $lineXML . "</vTrendlines>"; 
		}else{
		   # Return nothing
		   $partXML="";
		}
		# Return vtrendLines xml
		return $partXML;
	 }
	 
	 # The function getStylesXML() returns the styles XML from styles array
	 /*
	 <styles>
       <definition>
         <style name='CanvasAnim' type='animation' param='_xScale' start='0' duration='1' />
       </definition>
       <application>
         <apply toObject='Canvas' styles='CanvasAnim' />
       </application>   
     </styles>
     */
	 function getStylesXML(){
		$partXML="";
		$lineXML="";	
	    # Fetch styles array	
		foreach($this->styles as $s_type => $s_name){
		 $lineXML .="<" . $s_type . ">";
		 # Fetch styles array within array	
		 foreach($this->styles[$s_type] as $sub_type => $sub_name){
		  # Create dynamic element depending on array name
		  
		  # Fetch styles array within array	with array element 
		  foreach($this->styles[$s_type][$sub_type] as $part_type => $part_name){
			 $lineXML .="<" . $sub_type . " ";
			 foreach($this->styles[$s_type][$sub_type][$part_type] as $part_type1 => $part_name1){
			   if($part_name1!=""){ 
				 # Add elements parameter
				 $lineXML .= $part_type1 . "='" . $this->encodeSpecialChars($part_name1) . "' ";
			   } 
			 }
			 $lineXML .=" />";
		  }
		  
		 }
		 # Close open element
		 $lineXML .="</" . $s_type .  ">";
		}
		# Add $lineXML  with in style element
		# Check element have any attribute or not
		$pos = strpos($lineXML, "=");
        if ($pos !== false) {
     		$partXML = "<styles>" . $lineXML . "</styles>"; 
		}else{
	        $partXML ="";	
		}
		# Returning the part of xml
		return $partXML;
	 }
	 
 	 # Add set element to dataset element for seriesType 1 and 2
	 function genSSMSChartDataXML($value="",$setParam="",$vlineParam = "" ){
	     $strSetXML="";
		 $strParam="";
		 $color=0;
		 if($vlineParam==""){
		   if($setParam!=""){
               $strParam = $this->ConvertParamToXMLAttribute($setParam);
			   
		   }
		  				  
		   $colorSet="";
		   # User defined color 
		   if ($this->userColorON == true){
			   if($this->seriesType==1 && (gettype(strpos($this->chartType,"line"))=="boolean" && gettype(strpos($this->chartType,"area"))=="boolean")){
				  if(strpos(strtolower($strParam)," color")===false){
					 $colorSet=" color='" . $this->getColor($this->setCounter) . "' ";
				  }   
			   }
		   }
			  # Setting set parameter 
			  $strSetXML ="<set  value='" . $value . "' " . $strParam . $colorSet . " />";
         
		 }else{
		   $strParam = $this->ConvertParamToXMLAttribute($vlineParam);
		   
		   # Setting vline parameter
		   $strSetXML="<vLine " . $strParam . "  />"; 
		 }
	     return $strSetXML;
	 }
	 

	 # Add set element to dataset element for seriesType 3
	 function genScatterBubbleChartDataXML($value="",$setParam="",$vlineParam = "" ){
	     $strSetXML="";
		 $strParam="";
		 if($vlineParam==""){
		   if($setParam!=""){
		        $strParam = $this->ConvertParamToXMLAttribute($setParam);
			   
		   }
		   # Add Parameter into set elements
		   $strSetXML ="<set  x='" . $value . "' " . $strParam . " />";
         
		 }else{
		   # Parameter for vLine
		   $strParam = $this->ConvertParamToXMLAttribute($vlineParam);
		   	   
		   # Add vLine element
		   $strSetXML="<vLine " . $strParam . "  />"; 
		 }
	     return $strSetXML;
	 }


	 ## - - - -   - -   Array Init Functions  - - --- - -- - - - - - - -- - - - - -
	
	 # The Function createCategory() create array element within Categories
	 function createCategory($catID){
		 $this->categoryNames[$catID]= array();
	 }
	 #The Function CreateDataset() creates dataset from array element
	 function createDataset($dataID){
		 $this->dataset[$dataID]= array();
	 }
	 # The function createDataValues() sets the value of  dataset from array element
	 function createDataValues($datasetID, $dataID){
		 $this->dataset[$datasetID][$dataID]= array();
	 }
	 # The function CreateTrendLines() create TrendLines array
	 function createTrendLines($lineID){
		$this->trendLines[$lineID] = array();
	 }
	 # The function SetTLine() create TrendLine parameter 
	 function setTLine($lineID,$paramName, $paramValue){
		 $this->trendLines[$lineID][$paramName]=$paramValue;
	 }
	 # Create Lineset array 
	 function createLineset(){
		 $this->lineCounter++;
		 $this->lineSet[$this->lineCounter]= array();
	 }
	 # The function createMSStSetData() returns set data within datset
	 function createMSStSetData(){
		 $this->MSStSetCounter++;
		 $this->MSStDataset[$this->MSStDatasetCounter][$this->MSStSubDatasetCounter][$this->MSStSetCounter]= array();
	 }
	 # The function createStyles() creates array element within styles array
	 function createStyles($styleID){
		 $this->styles[$styleID]= array();
	 }
	 # The function createSubStyles() creates individual style array element
	 function createSubStyles($styleID,$subStyle){
		 $this->styles[$styleID][$subStyle]= array();
	 }
	 # The function createvTrendLines() creates TrendLines array
	 function createvTrendLines($lineID){
		$this->vtrendLines[$lineID] = array();
	 }
	 # The function setvTLine() create TrendLine parameter 
	 function setvTLine($lineID,$paramName, $paramValue){
		 $this->vtrendLines[$lineID][$paramName]=$paramValue;
	 }
	 # The function createSubStylesParam() creates sub styles param
	 function createSubStylesParam($styleID,$subStyle,$subParam){
		 $this->styles[$styleID][$subStyle][$subParam]= array();
	 }
	 # The function setSubStylesParam() creates sub styles array to store parameters
	 function setSubStylesParam($styleID,$subStyle,$subParam,$id,$value){
		 $this->styles[$styleID][$subStyle][$subParam][$id]= $value;
	 }

 	 # ----- ----------    -----  Misc utility functions  ---- ------ -----------

	 # Converting ' and " to %26apos; and &quot; 
	 function encodeSpecialChars($strValue){
	    		
	    if ($this->encodeChars==true){
		    $pattern="/%(?![\da-f]{2}|[\da-f]{4})/i";
		    $strValue=preg_replace($pattern, "%25", $strValue);
			$strValue=str_replace("&","%26",$strValue);
			$strValue=str_replace("'","%26apos;",$strValue);
			$strValue=str_replace("\"","%26quot;",$strValue);
			$strValue=preg_replace("/\<br/i", "%26lt;BR", $strValue);
			$strValue=str_replace("<","%26lt;",$strValue);
			$strValue=str_replace(">","%26gt;",$strValue);
			
			$strValue=str_replace("=","%3d",$strValue);
		    $strValue=str_replace("+","%2b",$strValue);
		
		    $strValue=str_replace("¢","%a2",$strValue);
		    $strValue=str_replace("£","%a3",$strValue);
		    $strValue=str_replace("€","%E2%82%AC",$strValue);
		    $strValue=str_replace("¥","%a5",$strValue);
		    $strValue=str_replace("₣","%e2%82%a3",$strValue);
	    }else{
			$strValue=str_replace("'","&apos;",$strValue);
			$strValue=str_replace("\"","&quot;",$strValue);
			$strValue=preg_replace("/\<br/i", "&lt;BR", $strValue);
			$strValue=str_replace("<","&lt;",$strValue);
			$strValue=str_replace(">","&gt;",$strValue);
	    }
		
	    return $strValue;
	 }
	 
    # It converts pattern link to original link 
	# abcd.php?cid=##Field_name_1##&pname=##Field_name_2##
    function getLinkFromPattern($row,$tempLink){			
		# Convert link into array break on '##'
		$aa=explode("##",$tempLink);
		# Reading array
		foreach($aa as $v){
		  # Finding '=' into array
		  $pos = strpos($v, "=");
			  # Not found '=' 
			  if($pos === false){
			  	if($v!=""){
					$pet="##" . $v . "##";
	   			    $tempLink=str_replace($pet,$row[$v],$tempLink); 
				}
			  }
 		 }
		 return $tempLink;
	 }		

	 
	 # Conversion of semi colon(;) separeted paramater to XML attribute
	 function ConvertParamToXMLAttribute($strParam, $isChartParam=false){
	 	 		 
		 $xmlParam="";
		 $listArray=explode($this->del,$strParam);
		 foreach ($listArray as $valueArray) {
		   $paramValue=explode("=",$valueArray,2);
		   if($this->validateParam($paramValue)==true){
			   # Create parameter set
			   if( $isChartParam==false){
		         $xmlParam .= $paramValue[0] . "='" . $this->encodeSpecialChars($paramValue[1]) . "' ";
			   }else{
			      $xmlParam .= $paramValue[0] . "=" . $this->encodeSpecialChars($paramValue[1]) . "&";
			   }
		   }
		}
		
	    if($isChartParam==true){
		  $xmlParam=substr($xmlParam,0,strlen($xmlParam)-1);
		}
		# Return
        return $xmlParam;
			
	 }
	 # Check validity of parameter

	 function validateParam($paramValue){
	     if(count($paramValue)>=2){
		    if(trim($paramValue[0])==""){
			  return false;
			}
			
		    return true;
		 }else{
		    return false;
		 }
	 }

 
	 #This function returns a color from a list of colors
	 function getColor($counter){
		
		$strColor="";
		if ($this->userColorON == false){
		  $strColor=$this->arr_FCColors[$counter % count($this->arr_FCColors)];
		}else{
		  $strColor=$this->UserColor[$counter % count($this->UserColor)];
		}
		
		return $strColor;
	 }
	 
	 # Getting Charts series type from charts array. 1 => single series, 2=> multi-series, 3=> scatter and bubble, 4=> MSStacked. defult 1 => single series
	 function getSeriesType(){
	    $sValue=1;	
		if(is_array($this->chartSWF[$this->chartType])){
		  $sValue=$this->chartSWF[$this->chartType][1];
		}else{
		  $sValue=1;
		}
		$this->seriesType=$sValue; 
	 }

     ### -----Populate color array and the Chart SWF array  ------ ------- ---------------------
	 function colorInit(){
	 
	    $this->arr_FCColors[] = "AFD8F8";
		$this->arr_FCColors[] = "F6BD0F";
		$this->arr_FCColors[] = "8BBA00";
		$this->arr_FCColors[] = "FF8E46";
		$this->arr_FCColors[] = "008E8E";
		$this->arr_FCColors[] = "D64646";
		$this->arr_FCColors[] = "8E468E";
		$this->arr_FCColors[] = "588526";
		$this->arr_FCColors[] = "B3AA00";
		$this->arr_FCColors[] = "008ED6";
		$this->arr_FCColors[] = "9D080D";
		$this->arr_FCColors[] = "A186BE";
		$this->arr_FCColors[] = "CC6600";
		$this->arr_FCColors[] = "FDC689";
		$this->arr_FCColors[] = "ABA000";
		$this->arr_FCColors[] = "F26D7D";
		$this->arr_FCColors[] = "FFF200";
		$this->arr_FCColors[] = "0054A6";
		$this->arr_FCColors[] = "F7941C";
		$this->arr_FCColors[] = "CC3300";
		$this->arr_FCColors[] = "006600";
		$this->arr_FCColors[] = "663300";
		$this->arr_FCColors[] = "6DCFF6";
		
	 }

	 # Set up chart names, SWF file names and  Series Type
	 function setChartArrays(){
	 
	    $this->chartSWF['area2d'][0]="Area2D";
		$this->chartSWF['area2d'][1]=1;
		$this->chartSWF['bar2d'][0]="Bar2D";
		$this->chartSWF['bar2d'][1]=1;
		$this->chartSWF['column2d'][0]="Column2D";
		$this->chartSWF['column2d'][1]=1;
		$this->chartSWF['column3d'][0]="Column3D";
		$this->chartSWF['column3d'][1]=1;
		$this->chartSWF['doughnut2d'][0]="Doughnut2D";
		$this->chartSWF['doughnut2d'][1]=1;
		$this->chartSWF['doughnut3d'][0]="Doughnut3D";
		$this->chartSWF['doughnut3d'][1]=1;
		$this->chartSWF['line2d'][0]="Line";
		$this->chartSWF['line2d'][1]=1;
		$this->chartSWF['line'][0]="Line";
		$this->chartSWF['line'][1]=1;
		$this->chartSWF['pie2d'][0]="Pie2D";
		$this->chartSWF['pie2d'][1]=1;		
		$this->chartSWF['pie3d'][0]="Pie3D";
		$this->chartSWF['pie3d'][1]=1;	
		$this->chartSWF['grid'][0]="SSGrid";
		$this->chartSWF['grid'][1]=1;	
		// FusionCharts  added since v3.2
		$this->chartSWF['pareto2d'][0]="Pareto2D";
		$this->chartSWF['pareto2d'][1]=1;	
		$this->chartSWF['pareto3d'][0]="Pareto3D";
		$this->chartSWF['pareto3d'][1]=1;	
		// PowerCharts
		$this->chartSWF['spline'][0]="Spline";
		$this->chartSWF['spline'][1]=1;	
		$this->chartSWF['splinearea'][0]="SplineArea";
		$this->chartSWF['splinearea'][1]=1;	
		$this->chartSWF['kagi'][0]="Kagi";
		$this->chartSWF['kagi'][1]=1;	
		$this->chartSWF['waterfall'][0]="Waterfall2D";
		$this->chartSWF['waterfall'][1]=1;	
		// FusionWidgets
		$this->chartSWF['funnel'][0]="Funnel";
		$this->chartSWF['funnel'][1]=1;
		$this->chartSWF['pyramid'][0]="Pyramid";
		$this->chartSWF['pyramid'][1]=1;


		
		# Series Type #2
		$this->chartSWF['msarea2d'][0]="MSArea";
		$this->chartSWF['msarea2d'][1]=2;
		$this->chartSWF['msarea'][0]="MSArea";
		$this->chartSWF['msarea'][1]=2;
		$this->chartSWF['msbar2d'][0]="MSBar2D";
		$this->chartSWF['msbar2d'][1]=2;
		$this->chartSWF['msbar3d'][0]="MSBar3D";
		$this->chartSWF['msbar3d'][1]=2;
		$this->chartSWF['mscolumn2d'][0]="MSColumn2D";
		$this->chartSWF['mscolumn2d'][1]=2;
		$this->chartSWF['mscolumn3d'][0]="MSColumn3D";
		$this->chartSWF['mscolumn3d'][1]=2;
		$this->chartSWF['mscolumn3dlinedy'][0]="MSColumn3DLineDY";
		$this->chartSWF['mscolumn3dlinedy'][1]=2;
		$this->chartSWF['mscolumnline3d'][0]="MSColumnLine3D";
		$this->chartSWF['mscolumnline3d'][1]=2;
		$this->chartSWF['mscolumn3dline'][0]="MSColumnLine3D";
		$this->chartSWF['mscolumn3dline'][1]=2;
		$this->chartSWF['mscombi2d'][0]="MSCombi2D";
		$this->chartSWF['mscombi2d'][1]=2;
		$this->chartSWF['mscombi3d'][0]="MSCombi3D";
		$this->chartSWF['mscombi3d'][1]=2;
		$this->chartSWF['mscombidy2d'][0]="MSCombiDY2D";
		$this->chartSWF['mscombidy2d'][1]=2;
		$this->chartSWF['msline'][0]="MSLine";
		$this->chartSWF['msline'][1]=2;		
		$this->chartSWF['msline2d'][0]="MSLine";
		$this->chartSWF['msline2d'][1]=2;		
		$this->chartSWF['scrollarea2d'][0]="ScrollArea2D";
		$this->chartSWF['scrollarea2d'][1]=2;		
		$this->chartSWF['scrollcolumn2d'][0]="ScrollColumn2D";
		$this->chartSWF['scrollcolumn2d'][1]=2;		
		$this->chartSWF['scrollcombi2d'][0]="ScrollCombi2D";
		$this->chartSWF['scrollcombi2d'][1]=2;
		$this->chartSWF['scrollcombidy2d'][0]="ScrollCombiDY2D";
		$this->chartSWF['scrollcombidy2d'][1]=2;		
		$this->chartSWF['scrollline2d'][0]="ScrollLine2D";
		$this->chartSWF['scrollline2d'][1]=2;		
		$this->chartSWF['scrollstackedcolumn2d'][0]="ScrollStackedColumn2D";
		$this->chartSWF['scrollstackedcolumn2d'][1]=2;		
		$this->chartSWF['stackedarea2d'][0]="StackedArea2D";
		$this->chartSWF['stackedarea2d'][1]=2;		
		$this->chartSWF['stackedbar2d'][0]="StackedBar2D";
		$this->chartSWF['stackedbar2d'][1]=2;		
		$this->chartSWF['stackedbar3d'][0]="StackedBar3D";
		$this->chartSWF['stackedbar3d'][1]=2;
		$this->chartSWF['stackedcolumn2d'][0]="StackedColumn2D";
		$this->chartSWF['stackedcolumn2d'][1]=2;
		$this->chartSWF['stackedcolumn3d'][0]="StackedColumn3D";
		$this->chartSWF['stackedcolumn3d'][1]=2;		
		$this->chartSWF['stackedcolumn3dlinedy'][0]="StackedColumn3DLineDY";
		$this->chartSWF['stackedcolumn3dlinedy'][1]=2;	
		$this->chartSWF['msstackedcolumn2d'][0]="MSStackedColumn2D";
		$this->chartSWF['msstackedcolumn2d'][1]=2;
		
		// FusionCharts charts added since v3.2
		$this->chartSWF['stackedcolumn2dline'][0]="StackedColumn2DLine";
		$this->chartSWF['stackedcolumn2dline'][1]=2;
		$this->chartSWF['stackedcolumn3dline'][0]="StackedColumn3DLine";
		$this->chartSWF['stackedcolumn3dline'][1]=2;
		$this->chartSWF['marimekko'][0]="Marimekko";
		$this->chartSWF['marimekko'][1]=2;
		$this->chartSWF['zoomline'][0]="ZoomLine";
		$this->chartSWF['zoomline'][1]=2;
		// PowerCharts
		$this->chartSWF['msspline'][0]="MSSpline";
		$this->chartSWF['msspline'][1]=1;	
		$this->chartSWF['mssplinearea'][0]="MSSplineArea";
		$this->chartSWF['mssplinearea'][1]=1;	
		
		$this->chartSWF['errorbar2d'][0]="ErrorBar2D";
		$this->chartSWF['errorbar2d'][1]=2;
		$this->chartSWF['errorline'][0]="ErrorLine";
		$this->chartSWF['errorline'][1]=2;
		$this->chartSWF['inversemsarea'][0]="InverseMSArea";
		$this->chartSWF['inversemsarea'][1]=2;
		$this->chartSWF['inversemscolumn2d'][0]="InverseMSColumn2D";
		$this->chartSWF['inversemscolumn2d'][1]=2;
		$this->chartSWF['inversemsline'][0]="InverseMSLine";
		$this->chartSWF['inversemsline'][1]=2;
		$this->chartSWF['radar'][0]="Radar";
		$this->chartSWF['radar'][1]=2;
		$this->chartSWF['logmscolumn2d'][0]="LogMSColumn2D";
		$this->chartSWF['logmscolumn2d'][1]=2;
		$this->chartSWF['logmsline'][0]="LogMSLine";
		$this->chartSWF['logmsline'][1]=2;
		$this->chartSWF['candlestick'][0]="CandleStick";
		$this->chartSWF['candlestick'][1]=2;
		$this->chartSWF['multilevelpie'][0]="MultiLevelPie";
		$this->chartSWF['multilevelpie'][1]=2;
		$this->chartSWF['multiaxisline'][0]="MultiAxisLine";
		$this->chartSWF['multiaxisline'][1]=2;
		$this->chartSWF['msstepline'][0]="MSStepLine";
		$this->chartSWF['msstepline'][1]=2;
		$this->chartSWF['boxandwhisker2d'][0]="BoxAndWhisker2D";
		$this->chartSWF['boxandwhisker2d'][1]=2;
		$this->chartSWF['heatmap'][0]="HeatMap";
		$this->chartSWF['heatmap'][1]=2;
		$this->chartSWF['dragcolumn2D'][0]="DragColumn2D";
		$this->chartSWF['dragcolumn2D'][1]=2;
		$this->chartSWF['dragline'][0]="DragLine";
		$this->chartSWF['dragline'][1]=2;
		$this->chartSWF['dragarea'][0]="DragArea";
		$this->chartSWF['dragarea'][1]=2;
		$this->chartSWF['dragnode'][0]="DragNode";
		$this->chartSWF['Dragnode'][1]=2;

		// FusionWidgets
		$this->chartSWF['realtimearea'][0]="RealTimeArea";
		$this->chartSWF['realtimearea'][1]=2;
		$this->chartSWF['realtimecolumn'][0]="RealTimeColumn";
		$this->chartSWF['realtimecolumn'][1]=2;
		$this->chartSWF['realtimeline'][0]="RealTimeLine";
		$this->chartSWF['realtimeline'][1]=2;
		$this->chartSWF['realtimestackedarea'][0]="RealTimeStackedArea";
		$this->chartSWF['realtimestackedarea'][1]=2;
		$this->chartSWF['realtimestackedcolumn'][0]="RealTimeStackedColumn";
		$this->chartSWF['realtimestackedcolumn'][1]=2;
		$this->chartSWF['realtimelinedy'][0]="RealTimeLineDY";
		$this->chartSWF['realtimelinedy'][1]=2;
		$this->chartSWF['gantt'][0]="Gantt";
		$this->chartSWF['gantt'][1]=2;
		
		$this->chartSWF['sparkline'][0]="SparkLine";
		$this->chartSWF['sparkline'][1]=2;
		$this->chartSWF['sparkcolumn'][0]="SparkColumn";
		$this->chartSWF['sparkcolumn'][1]=2;
		$this->chartSWF['sparkwinloss'][0]="SparkWinLoss";
		$this->chartSWF['sparkwinloss'][1]=2;
		$this->chartSWF['angulargauge'][0]="AngularGauge";
		$this->chartSWF['angulargauge'][1]=2;
		$this->chartSWF['bulb'][0]="Bulb";
		$this->chartSWF['bulb'][1]=2;
		$this->chartSWF['cylinder'][0]="Cylinder";
		$this->chartSWF['cylinder'][1]=2;
		$this->chartSWF['hled'][0]="HLED";
		$this->chartSWF['hled'][1]=2;
		$this->chartSWF['hlinearGauge'][0]="HLinearGauge";
		$this->chartSWF['hlinearGauge'][1]=2;
		$this->chartSWF['thermometer'][0]="Thermometer";
		$this->chartSWF['thermometer'][1]=2;
		$this->chartSWF['vled'][0]="VLED";
		$this->chartSWF['vled'][1]=2;
		$this->chartSWF['hbullet'][0]="HBullet";
		$this->chartSWF['hbullet'][1]=2;
		$this->chartSWF['vbullet'][0]="VBullet";
		$this->chartSWF['vbullet'][1]=2;

		$this->chartSWF['drawingpad'][0]="DrawingPad";
		$this->chartSWF['drawingpad'][1]=2;
		
		
		# Series Type #3
		$this->chartSWF['bubble'][0]="Bubble";
		$this->chartSWF['bubble'][1]=3;
		$this->chartSWF['scatter'][0]="Scatter";
		$this->chartSWF['scatter'][1]=3;
		//PowerCharts
		$this->chartSWF['selectscatter'][0]="SelectScatter";
		$this->chartSWF['selectscatter'][1]=3;
		$this->chartSWF['errorscatter'][0]="ErrorScatter";
		$this->chartSWF['errorscatter'][1]=3;


		# Series Type #4
		$this->chartSWF['msstackedcolumn2dlinedy'][0]="MSStackedColumn2DLineDY";
        $this->chartSWF['msstackedcolumn2dlinedy'][1]=4;	
	    
		

		
	 }
	 
	/** 
	 * renderChart renders FusionCharts with JavaScript + HTML code required to embed a chart.
	 *
	 * This function returns an HTML with a DIV and a SCRIPT tag. The DIV contains an ID which 
	 * is set in this format $chartId + "DIV". Thd DIV is followed by a JavaScript snippet
	 * that contains FusionCharts JavaScript class' constructor function - new FusionCharts(),
	 *
	 * @param	chartSWF				String  - SWF File Name (and Path) of the chart which you intend to plot
	 * @param	dataUrl				String  - If you intend to use dataUrl method (XML or JSON as Url), pass the URL 
	 *											as this parameter. Otherwise, set it to "" (in case of dataStr method)
	 * @param	dataStr				String  - If you intend to use dataStr method (embedded XML or JSON), pass the 
	 *											XML/JSON data as this parameter. Otherwise, set it to "" (in case of dataUrl method)
	 * @param	chartId				String  - Id for the chart, using which it will be recognized in the HTML page. 
	 *														Each chart on the page needs to have a unique Id.
	 * @param	chartWidth			String  - Intended width for the chart  (in pixels WITHOUT px suffix or in percent)
	 * @param	chartHeight			String  - Intended height for the chart (in pixels WITHOUT px suffix or in percent)
	 * @param	debugMode			Boolean - Whether to start the chart in debug mode
	 * @param	registerWithJS		Boolean - Whether to ask chart to register itself with JavaScript
	 * @param	allowTransparent	Boolean - Whether to allow the chart to have transparent background. Additionally this set the chart to get rendered in opaque mode
	 *
	 * @return	Chart HTML + JavaScript code to be added into web page as String
	 */

	function renderChartJS( $chartSWF, $dataUrl, $dataStr, $chartId, $chartWidth, $chartHeight, $debugMode=false, $registerWithJS=true, $allowTransparent=false )
	{

		// select data format xml/json/xmlurl/jsonurl 
		$dataFormat = ( $this->FC_GetConfiguration ("dataFormat")=="" ? "xml" : $this->FC_GetConfiguration ("dataFormat") ) . ($dataStr=="" ? "url" : "");
		
		// if renderAt is specified explicitely skip creation of DIV 
		$renderAtDiv = "<!-- The chart will be rendered inside div having id = ". @($this->FC_GetConfiguration ("renderAt")) ." -->";
		if ($this->FC_GetConfiguration ("renderAt")=="") 
		{
			$renderAtDiv = "<div id=\"{$chartId}Div\">Chart</div>";
			$this->FC_SetConfiguration("renderAt", "{$chartId}Div" );
		}
		
		// set swf
		$this->FC_SetConfiguration("swfUrl" , $chartSWF); 
		// set dataformat
		$this->FC_SetConfiguration("dataFormat" , $dataFormat);
		// set id
		$this->FC_SetConfiguration("id", $chartId); 
		//set width
		$this->FC_SetConfiguration("width", $chartWidth); 
		// set height
		$this->FC_SetConfiguration("height", $chartHeight);

		// if debug mode is set on set it on
		if ($debugMode) $this->FC_SetConfiguration("debugMode", $this->boolToNum($debugMode) );

		// selection of wmode
		// If wmode is set explicitely using setWindowMode it sets a reserved setting called forcedwmode
		// The setting forcedwmode takes priority over $allowTransparent parameter
		// If $allowTransparent parameter is set to true take transparent as wmode and set it
		$wmode = $this->FC_GetConfiguration("forcedwmode" , "constants"); 
		if ( $wmode == "" ) $wmode = $allowTransparent ? "transparent" : "";
		if ( $wmode != "" ) $this->FC_SetConfiguration("wmode", $wmode);

		// remove all nwelines from dataSource
		$dataSource =  @(preg_replace("/[\n\r]/","",($dataStr == "" ? $dataUrl : $dataStr )));
		// Set dataSource JS property. If JSON string is passed the value is set directly without enclosing in quoted
		// This would provide a proper JSON to the chart
		// For the rest of the cases the values are set enclosed with quotes
	   $datasource_json  =  " \"dataSource\" : "  . ( $dataFormat == "json" ? $dataSource : "\"$dataSource\"" );
		// parse all chart config and set it to JSON Object for new JavaScript constructor 
		$chart_config_json = "{ " .$this->fc_encode_json( $this->FC_GetConfiguration("params") ) . ", $datasource_json }";
			
		//First we create a new DIV for each chart. We specify the id of DIV as "chartId" + Div.			
		//DIV names are case-sensitive.

		// The Steps in the script block below are:
		//
		//  1) In the DIV the text "Chart" is shown to users before the chart has started loading
		//    (if there is a lag in relaying SWF from server). This text is also shown to users
		//    who do not have Flash Player installed. You can configure it as per your needs.
		//
		//  2) The chart is rendered using FusionCharts Class. Each chart's instance (JavaScript) Id 
		//     is named as chart_"chartId".		
		//
		//  3) Check whether we've to provide data using dataStr method or dataUrl method
		//     save the data for usage below 
		
		
		// generate js code for data provider

		
		// build FusionCharts HTML + JS
		$chart_HTML_JS = <<<HTML_JS
<!-- START Code Block for Chart {$chartId} -->
{$renderAtDiv}
<script type="text/javascript" ><!--
	// Instantiate the Chart 
	if ( FusionCharts("{$chartId}") && FusionCharts("{$chartId}").dispose ) FusionCharts("{$chartId}").dispose();
	var chart_{$chartId} = new FusionCharts( {$chart_config_json} ).render();
// --></script>
<!-- END Script Block for Chart {$chartId} -->
HTML_JS;


		$this->FC_INITIALIZE();
		// return HTML + JS
		return $chart_HTML_JS;
	}



	/** 
	 * renderChartHTML renders FusionCharts using HTML embedding method.
	 *
	 * This function does NOT embed the chart using JavaScript class. Instead, it uses
	 * direct HTML embedding using OBJECT/EMBED HTML tags.
	 *
	 * @param	chartSWF				String  - SWF File Name (and Path) of the chart which you intend to plot
	 * @param	dataUrl				String  - If you intend to use dataUrl method (XML as Url only, JSON not supported), pass the URL 
	 *												as this parameter. Otherwise, set it to "" (in case of dataStr method)
	 * @param	dataStr				String  - If you intend to use dataStr method (embedded XML), pass the XML (JSON not supported) 
	 *														data as this parameter. Otherwise, set it to "" (in case of dataUrl method)
	 * @param	chartId				String  - Id for the chart, using which it will be recognized in the HTML page. Each chart on the page needs to have a unique Id.
	 * @param	chartWidth			String  - Intended width for the chart  (in pixels WITHOUT px suffix or in percent)
	 * @param	chartHeight			String  - Intended height for the chart (in pixels WITHOUT px suffix or in percent)
	 * @param	debugMode			Boolean - Whether to start the chart in debug mode
	 * @param	registerWithJS		Boolean - Whether to ask chart to register itself with JavaScript
	 * @param	allowTransparent	Boolean - Whether to allow the chart to have transparent background. Additionally this set the chart to get rendered in opaque mode
	 *
	 * @return	Chart HTML code to be added into web page as String
	 */
	function renderChartHTML($chartSWF, $dataUrl, $dataStr, $chartId, $chartWidth, $chartHeight, $debugMode=false, $registerWithJS=false, $allowTransparent="") 
	{
		
		// detect ssl and rename http:// with https:// 
		if ($this->FC_DetectSSL()) 
		{
			$this->FC_SetConfiguration("pluginspage", str_replace("http://", "https://", $this->FC_GetConfiguration("pluginspage")));
			$this->FC_SetConfiguration("codebase", str_replace("http://", "https://", $this->FC_GetConfiguration("codebase")));
		}
		
		
		// set wmode
		$wmode = $this->FC_GetConfiguration("forcedwmode" , "constants"); 
		if ( $wmode == "" ) $wmode = $allowTransparent ? "transparent" : "opaque";

		// refer to global configuration storage
		$this->FC_SetConfigurations( 
			array ( 
				"movie" 				=> $chartSWF, 
				"src"					=> $chartSWF, 
				"dataXML" 				=> $dataStr,
				"dataURL" 				=> $dataUrl,
				"width" 				=> $chartWidth, 
				"height" 				=> $chartHeight, 
				"chartWidth"			=> $chartWidth, 
				"chartHeight" 			=> $chartHeight, 
				"DOMId" 				=> $chartId,
				"id" 					=> $chartId,
				"debugMode" 			=> boolToNum($debugMode),
				"wmode" 				=> $wmode
			)
		);
		
		// Generate the FlashVars string based on whether dataUrl has been provided
		// or dataXML.
		$strFlashVars = $this->FC_Transform($this->FC_GetConfiguration("fvars"), "&{key}={value}");
		$this->FC_SetConfiguration ("flashvars", $strFlashVars );

		$strObjectNode = "<object " . $this->FC_Transform(FC_GetConfiguration("object"), " {key}=\"{value}\"") . " >\n" ;
		$strObjectParamsNode = $this->FC_Transform(FC_GetConfiguration("objparams"), "\t<param name=\"{key}\" value=\"{value}\">\n") ;
		$strEmbedNode = "<embed " . $this->FC_Transform(FC_GetConfiguration("embed"), " {key}=\"{value}\"") . " />\n" ;
		
		
		 
	$HTML_chart = <<<HTMLCHART
<!-- START Code Block for Chart $chartId -->
{$strObjectNode}
{$strObjectParamsNode}
{$strEmbedNode}
</object>
<!-- END Code Block for Chart $chartId -->
HTMLCHART;

		$this->FC_INITIALIZE();
		return $HTML_chart;
	}




	/**
	 * Enables Print Manager for Mozilla browsers
	 * 
	 * This function adds a small JavaScript snippet to the page which enables the Managed Print option for Mozilla based browsers
	 * 
	 * There is an optional parameter $directWriteToPage which if set to true would write the code directly to page. Otherwise the 
	 * code snippet is returned as string 
	 * 
	 * @param	directWriteToPage	Boolean  - Whether to write the JavaScript code directly to page or return as string
	 *
	 * @return	A blank string when the code is directly written to page, otherwize, the JavaScript as string.
	 */
	function enablePrintManager($directWriteToPage = true )
	{
		$strHTML = "<script type=\"text/javascript\"><!--\n if(FusionCharts && FusionCharts.printManager) FusionCharts.printManager.enabled(true);\n// -->\n</script>";
		if ($directWriteToPage==true) { echo $strHTML; return "" ; } else return $strHTML;
	}

	/**
	 * sets the dataformat to be provided to charts (json/xml)
	 *
	 * @param	format String  - data format. Default is 'xml'. Other format is 'json'
	 *
	 */
	function setDataFormat($format="xml")
	{
		// stores the dataformat in global configuration store
		$this->FC_SetConfiguration ("dataformat", $format);
	}

	/**
	 * sets renderer type (flash/javascript)
	 *
	 * @param	renderer String  - Name of the renderer. Default is 'flash'. Other possibility is 'javascript'
	 *
	 */
	function setRenderer( $renderer="flash" )
	{
		// stores the renderer name in global configuration store
		$this->FC_SetConfiguration ("renderer", strtolower($renderer));
	}

	/**
	 * explicitely sets window mode (window(detault)/transpatent/opaque)
	 *
	 * @param	mode String  - Name of the mode. Default is 'window'. Other possibilities are 'transparent'/'opaque'
	 *
	 */
	function setWindowMode( $mode="window" )
	{
		// stores the window mode to configuration store to a separare 
		$this->FC_SetConfiguration ( "forcedwmode", $mode, "constants" );
	}


	/**
	 * FC_SetConfiguration sets various configurations of FusionCharts
	 *
	 * It takes configuration names as first parameter and its value a second parameter
	 * There are config groups which can contain common configuration names. All config names in all groups gets set with this value
	 * unless group is specified explicitely
	 *
	 * @param	name		String  - name of configuration 
	 * @param	value 	String  - value of configuration
	 * @param	group	 	String  - Name of the configuration group (params/fvars/object/objparams/embed/constants)
	 * @param	addNew 	Boolean - Whether a new configuration can be added
	 *	
	 *	@return 	true if values is set. False if value is not set. In case of a new configuration it sets and returns true.
	 */
	function FC_SetConfiguration ( $name="", $value="" , $group = "", $addNew = true )
	{
				
		$isSet=false;
		
		if ( $group != ""  && isset($this->FC_DEFAULT_CONFIG[strtolower($group)]) )
		{
			// set in global configuration store
			foreach ($this->FC_DEFAULT_CONFIG[strtolower($group)] as $skey => $svalue)
			{
				if( strtolower($skey) == strtolower($name) ) 
				$this->FC_DEFAULT_CONFIG[strtolower($group)][ $skey ] = $value;
										
				$isSet = true;
			}
			
		}
		else
		{
			foreach ( $this->FC_DEFAULT_CONFIG as $ckey => $cvalue)
			{
				foreach ($cvalue as $skey => $svalue)
				{
					if ( strtolower($skey) == strtolower($name) )
					{
						$this->FC_DEFAULT_CONFIG[$ckey][$skey] = $value;
						$isSet = true;
					}
				}
			}

		}

		if (!$isSet && $group != "" && $addNew)
		{
				$this->FC_DEFAULT_CONFIG[$group][$name] = $value;
				$isSet = true;
		}
		return $isSet;

	}


	/**
	 * FC_GetConfiguration retrives the values stored for various FusionCharts Configurations
	 * Configuration name is to be pased as parameter. Optional group name can be passed.
	 *
	 * The Configuration groups are - (params/fvars/object/objparams/embed/constants)
	 * 
	 * 
	 * @param	name	String  	- name of configuration or configuration group (params/fvars/object/objparams/embed/constants)
	 * @param	group		String  	- name of configuration group to search for (params/fvars/object/objparams/embed/constants)
	 *
	 * @return	value of configuration as String or if seeting not defined NULL
	 *				If a settig group name is specified it returns	an Array containg all configurations of that group
	 *
	 */
	function FC_GetConfiguration( $name, $group ="" )
	{
		
		if ( $group == "" )
		{
			// if the configuration is in store
			foreach ( $this->FC_DEFAULT_CONFIG as $ckey => $cvalue )
			{
				if ( strtolower($name) == strtolower($ckey) )
				{
					return $cvalue;
				}
				else
				{

					foreach ($cvalue as $skey => $svalue)
					{
						if ( strtolower($skey) == strtolower($name) )
						{
							return $svalue;
						}
					}
				}
			}
		}
		else
		{
			if( isset($this->FC_DEFAULT_CONFIG[$group]) )
			{
				foreach ($this->FC_DEFAULT_CONFIG[$group] as $skey => $svalue)
				{
					if ( strtolower($skey) == strtolower($name) )
					{
						$this->FC_DEFAULT_CONFIG[$group][$skey] = $svalue;
					}
				}
			}
			
		}
		
		return NULL;
	}


	/**
	 * Sets a collection of configurations
	 * 
	 * @param	objConfig	Array  - An Array of configurations with key as configuration name and values as  configuration value
	 */
	function FC_SetConfigurations( $objConfig )
	{
		// iterate through array
		foreach ($objConfig as $skey => $svalue)
		{
			// set config
			$this->FC_SetConfiguration  ( $skey, $svalue );
		}
		
	}


	/**
	 * ---------------------------------------------------------------------------------------
	 * Helper functions
	 * ---------------------------------------------------------------------------------------
	 */ 

	/**
	 * boolToNum function converts boolean values to numeric (1/0)
	 * Converts Boolean true to 1 and false to 0
	 *
	 * @param	value	Varient  - Can be Boolean true or false or numeric value 
	 *
	 * @return	1/0. true returns 1. false returns 0.
	 */
	function boolToNum($bVal) {
		return (($bVal==true || $bVal==1 ||  $bVal=="true" ) ? 1 : 0);
	}

	/**
	 * encodedataUrl function encodes the dataUrl before it's served to FusionCharts.
	 * If you've parameters in your dataUrl, you necessarily need to encode it.
	 *
	 * @param	strdataUrl 		String - dataUrl to be fed to chart
	 * @param	addNoCacheStr 	Boolean - Whether to add aditional string to URL to disable caching of data
	 *
	 * @return	URLEncoded Url
	 */
	function encodedataUrl($strdataUrl, $addNoCacheStr=false) {
		//Add the no-cache string if required
		if ($addNoCacheStr==true) {
			// We add ?FCCurrTime=xxyyzz
			// If the dataUrl already contains a ?, we add &FCCurrTime=xxyyzz
			// We replace : with _, as FusionCharts cannot handle : in URLs
			if (strpos($strdataUrl,"?")<>0)
				$strdataUrl .= "&FCCurrTime=" . Date("H_i_s");
			else
				$strdataUrl .= "?FCCurrTime=" . Date("H_i_s");
		}
		// URL Encode it
		return urlencode($strdataUrl);
	}

	/**
	 * datePart function converts MySQL date based on requested mask
	 *
	 * @param	mask			String - what part of the date to return "m' for month,"d" for day, and "y" for year
	 * @param	dateTimeStr String - MySQL date/time format (yyyy-mm-dd HH:ii:ss)
	 *
	 * @return	converted date
	 */
	function datePart($mask, $dateTimeStr) {
		@list($datePt, $timePt) = explode(" ", $dateTimeStr);
		$arDatePt = explode("-", $datePt);
		$dataStr = "";
		// Ensure we have 3 parameters for the date
		if (count($arDatePt) == 3) {
			list($year, $month, $day) = $arDatePt;
			// determine the request
			switch ($mask) {
			case "m": return $month;
			case "d": return $day;
			case "y": return $year;
			}
			// default to mm/dd/yyyy
			return (trim($month . "/" . $day . "/" . $year));
		}
		return $dataStr;
	}


	/**
	 * Converts associative array to To JSON String 
	 *
	 * @param	mask			String - what part of the date to return "m' for month,"d" for day, and "y" for year
	 * @param	dateTimeStr String - MySQL date/time format (yyyy-mm-dd HH:ii:ss)
	 *
	 * @return	converted date
	 */
	function fc_encode_json( $json , $enclosed = false)
	{

		$strjson = "";
		if($enclosed) $strjson .= "{";
		
		$strjson .= $this->FC_Transform ($json, " \"{key}\" : \"{value}\", ");
		
		$strjson = preg_replace("/, $/","", $strjson );
		
		if($enclosed)$strjson  .= "}";
		
		return $strjson ;
	}


	/**
	 *  Transforms an associaitive array to string
	 * 
	 *
	 * @param	arr			Array - Associative array
	 * @param	tFormat 		String - String builder format. The format is a string with placeholder for key and value.
	 * 									The function iterated through the array 
	 * 									replaces all "{key}" (placeholder for key) in the String with the key name of the array element
	 * 									replaces all "{value}"  (placeholder for value) in the String with the value associated with the above key
	 *
	 * @param	ignoreBlankValues		Boolean - If true it igonores all elements with blank values
	 *
	 * @return	converted date
	 */
	function FC_Transform($arr, $tFormat="", $ignoreBlankValues = true)
	{
		$converted = "";
		foreach ($arr as $skey => $svalue)
		{

			if($ignoreBlankValues && $svalue== "" ) continue;

			$TFApplied = preg_replace("/{key}/",$skey, $tFormat);
			$TFApplied = preg_replace("/{value}/",$svalue, $TFApplied);
			$converted .= $TFApplied;

		}
		return $converted;
	}



	/**
	 * Initializes FusionCharts generic configurations
	 *
	 * Prepares the wrapper to load default chart configurations
	 *
	 */
	function FC_INITIALIZE()
	{

		/** 
		 * Global storage of chart configurations
		 *
		 * debugMode				: Sets debug mode of chart on
		 * RegisterWithJS 		: sets the chart to communicate with JavaScript
		 * wmode 					: sets window mode - possible values "window"/"transparent"/opaque
		 * scaleMode				: 'default value is 'NoScale', other values are 'ExactFit', 'showAll', 'NoBorder'
		 * bgColor 					: set the flash player's background color. This gets shown up if the chart
		 *										background alpha is set less than 100 and wmode is not transparent
		 * lang 						: language, default is english
		 * detectflashversion 	: sets FusionCharts JavaScript class to check version of Flash Player >= 8
		 * AutoInstallRedirect 	: redirects to Flash Player installation page if version is > 8
		 * renderer 				: sets the current FusionCharts renderer. It can be "flash" or "javascript"
		 * dataformat				: sets the data format of FusionCharts. Can be "xml" or "json"
		 */
		
		$this->FC_DEFAULT_CONFIG["params"][ "swfUrl" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "width" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "height" ] =  "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "renderAt" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "renderer" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "dataSource" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "dataFormat" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "id" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "lang" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "debugMode" ] = "" ; 
		$this->FC_DEFAULT_CONFIG["params"][ "registerWithJS" ] = ""  ;
		$this->FC_DEFAULT_CONFIG["params"][ "detectFlashVersion" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "autoInstallRedirect" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "wMode" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "scaleMode" ] = ""  ;
		$this->FC_DEFAULT_CONFIG["params"][ "menu" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "bgColor" ] = "" ;
		$this->FC_DEFAULT_CONFIG["params"][ "quality" ] = "" ;


		$this->FC_DEFAULT_CONFIG["fvars"][ "dataURL" ] = "" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "dataXML" ] = "" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "chartWidth" ] = "" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "chartHeight" ] = "" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "DOMId" ] = "" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "registerWithJS" ] = "1" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "debugMode" ] = "0" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "scaleMode" ] = "noScale" ;
		$this->FC_DEFAULT_CONFIG["fvars"][ "lang" ] = "EN" ;
		
		$this->FC_DEFAULT_CONFIG["object"][ "height" ] = "" ;
		$this->FC_DEFAULT_CONFIG["object"][ "width" ] = "" ;
		$this->FC_DEFAULT_CONFIG["object"][ "id" ] = "" ;
		$this->FC_DEFAULT_CONFIG["object"][ "classid" ] = "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" ;
		$this->FC_DEFAULT_CONFIG["object"][ "codebase" ] = "http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" ;
		
		$this->FC_DEFAULT_CONFIG["objparams"][ "movie" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "FlashVars" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "scaleMode" ] = "noScale" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "wmode" ] = "opaque" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "bgColor" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "quality" ] = "best" ;
		$this->FC_DEFAULT_CONFIG["objparams"]["allowScriptAccess"] = "always" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "swLiveConnect" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "base" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "align" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "salign" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "scale" ] = "" ;
		$this->FC_DEFAULT_CONFIG["objparams"][ "menu" ] = "" ;


		$this->FC_DEFAULT_CONFIG["embed"][ "height" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "width" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "id" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "src" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "flashvars" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "name" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "scaleMode" ] = "noScale" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "wmode" ] = "opaque" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "bgColor" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "quality" ] = "best" ;
		$this->FC_DEFAULT_CONFIG["embed"]["allowScriptAccess"] = "always" ;
		$this->FC_DEFAULT_CONFIG["embed"]["type"] = "application/x-shockwave-flash";
		$this->FC_DEFAULT_CONFIG["embed"]["pluginspage"]= "http://www.macromedia.com/go/getflashplayer" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "swLiveConnect" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "base" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "align" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "salign" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "scale" ] = "" ;
		$this->FC_DEFAULT_CONFIG["embed"][ "menu" ] = "" ;

		$this->FC_DEFAULT_CONFIG["constants"][ "forcedwmode" ] = "" ;

	}

	/**
	 * Initializes FusionCharts Static configurations
	 *
	 * Prepares the wrapper to load default chart configurations
	 *
	 */
	function FC_INITSTATIC()
	{
		
		$this->FC_DEFAULT_CONFIG["constants"][ "scriptBaseUri" ] = "" ;

	}

	/**
	 * Detects SSL 
	 *
	 * Returns true if SSL is found
	 *
	 */
	function FC_DetectSSL(){
		if(@$_SERVER["https"] == "on"){
			return true;
		} elseif (@$_SERVER["https"] == 1){
			return true;
		} elseif (@$_SERVER['SERVER_PORT'] == 443) {
			return true;
		} else {
			return true;
		}
	} 


}
?>