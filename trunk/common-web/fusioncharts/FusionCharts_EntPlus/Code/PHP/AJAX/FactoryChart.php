<?php
//We've included ../Includes/FusionCharts.php, which contains functions
//to help us easily embed the charts.
include("../Includes/FusionCharts.php");
include("../Includes/DBConn.php");
?>
<?php
    //This page is invoked from Default.php. When the user clicks on a pie
    //slice in Default.php, the factory Id is passed to this page. We need
    //to get that factory id, get information from database and then show
    //a detailed chart.

    //Request the factory Id from Querystring
    $FactoryId = @$_GET['FactoryId'] ? $_GET['FactoryId'] : 1 ;

    //Generate the chart element string
    $strXML = "<chart palette='2' caption='Factory " . $FactoryId ." Output ' subcaption='(In Units)' xAxisName='Date' showValues='1' >";

    // Connet to the DB
    $link = connectToDB();

    //Now, we get the data for that factory
    $strQuery = "select * from Factory_Output where FactoryId=" . $FactoryId;
    $result = mysql_query($strQuery) or die(mysql_error());
    
    //Iterate through each factory
    if ($result) {
        while($ors = mysql_fetch_array($result)) {
            //Here, we convert date into a more readable form for set label.
            $strXML .= "<set label='" . datePart("d",$ors['DatePro']) . "/" . datePart("m",$ors['DatePro']) . "' value='" . $ors['Quantity'] . "'/>";
        }
    }
    mysql_close($link);

    //Close <chart> element
    $strXML .= "</chart>";
	
    //Create the chart - Column 2D Chart with data from strXML
    echo renderChart("../../FusionCharts/Column2D.swf", "", $strXML, "FactoryDetailed", 600, 300, false);
?>
