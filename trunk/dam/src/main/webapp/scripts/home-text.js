
	var homeFirst = "<b>The TCGA Data Portal does not host lower levels of sequence data. NCI's <a href=\"https://cghub.ucsc.edu/\" target=\"_blank\" class=\"newWinIcon\">Cancer Genomics Hub (CGHub)</a> is the new secure repository for storing, cataloging, and accessing sequence related data. New users must still apply for authorized access through NCBI's <a href=\"http://www.ncbi.nlm.nih.gov/projects/gap/cgi-bin/study.cgi?study_id=phs000178.v5.p5\" target=\"_blank\" class=\"newWinIcon\">Database of Genotypes and Phenotypes (dbGaP)</a>.</b>";
	var homeLast  = "";
	$(document).ready(function(){
		var homeFirstDom = document.getElementById("homeFirst");
		var homeLastDom = document.getElementById("homeLast");
		if(homeFirstDom != null){
			homeFirstDom.innerHTML = homeFirst;
		}
		if(homeLastDom != null){
			homeLastDom.innerHTML = homeLast;
		}

        var cbiitDiv = document.getElementById("cbiit-contact");
        if (cbiitDiv != null) {
            cbiitDiv.innerHTML = "<p>" +
                "<b>Local Phone:</b><br />240-276-5541" +
            //"</p><p><b>Toll Free Phone:</b><br />1-888-478-4423" +
            "</p><p>" +
                "Telephone support is available:<br />" +
                "Monday to Friday, 8 am - 8 pm Eastern Time, excluding government holidays." +
            "</p><p>" +
                "<b>Email:</b><br />" +
                "<a href=\"mailto:ncicbiit@mail.nih.gov\">ncicbiit@mail.nih.gov</a>" +
            "</p><p>" +
                "When submitting support requests via email, please include:" +
                "<ul>" +
                    "<li>your contact information</li>" +
                    "<li>the name of the TCGA application/tool you were using</li>" +
                    "<li>the URL of the page where you experienced the problem</li>" +
                    "<li>a description of the problem and the steps to recreate it</li>" +
                    "<li>the text of any error messages you received</li>" +
                "</ul>" +
            "</p>";
        }

        var cbiitReportProblem = document.getElementById("cbiit-report-problem");
        if (cbiitReportProblem != null) {
            cbiitReportProblem.innerHTML = "Please report TCGA data issues to [NCICB support | " +
                    "<a href=\"mailto:ncicbiit@mail.nih.gov\" title=\"NCICB support\">ncicbiit@mail.nih.gov</a>]";
        }
	});