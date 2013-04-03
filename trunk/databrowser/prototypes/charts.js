//js charts used in clickable model for clinical search CS_clinicalSearch.html
ID=0;
AGE=1;
SEX=2;
TUMORSITE=3;
SURVIVAL=4;
RADIATION=5;

patientData = new Array();
var patientCount = 0;
//header row
patientData[patientCount++] = ["Patient ID", "Age", "Sex", "Tumor Site", "Survival (months)", "Radiation"];
patientData[patientCount++] = ["TCGA-01-0001", 64, "MALE", "CORTEX", 12, false]
patientData[patientCount++] = ["TCGA-01-0002", 57, "FEMALE", "CEREBELLUM", 15, true];
patientData[patientCount++] = ["TCGA-01-0003", 69, "MALE", "CORTEX", 4, false];
patientData[patientCount++] = ["TCGA-01-0004", 73, "MALE", "AMYGDALA", 18, false];
patientData[patientCount++] = ["TCGA-01-0005", 61, "FEMALE", "CORTEX", -1, true];
patientData[patientCount++] = ["TCGA-01-0006", 76, "MALE", "CEREBELLUM", 16, false];
patientData[patientCount++] = ["TCGA-01-0007", 83, "FEMALE", "BRAIN STEM", -1, true];

function anotherJS()
{
	alert('from another js');
}

function drawAge() {
    var myData = [["< 30",0], ["30-39",0], ["40-49",0], ["50-59",0], ["60-69",0], ["70-79",0], ["80-89",0],[">= 90",0]];

    for (var igroup=0; igroup<myData.length; igroup++) {
        for (var ipatient=1; ipatient<patientData.length; ipatient++) {
            var patientAge = patientData[ipatient][AGE];
            var increment = false;
            switch (myData[igroup][0]) {
            case "< 30":
                increment = (patientAge < 30);
                break;
            case "30-39":
                increment = (patientAge >= 30 && patientAge < 40);
                break;
            case "40-49":
                increment = (patientAge >= 40 && patientAge < 50);
                break;
            case "50-59":
                increment = (patientAge >= 50 && patientAge < 60);
                break;
            case "60-69":
                increment = (patientAge >= 60 && patientAge < 70);
                break;
            case "70-79":
                increment = (patientAge >= 70 && patientAge < 80);
                break;
            case "80-89":
                increment = (patientAge >= 80 && patientAge < 90);
                break;
            case ">= 90":
                increment = (patientAge >= 90);
                break;
            }
            if (increment) {
                myData[igroup][1]++;
            }
        }
    }
    ageGraph = new JSChart("agegraph", "bar");
    ageGraph.setTitle("Age Distribution");
    ageGraph.setAxisNameX("Age");
    ageGraph.setAxisNameY("# Patients");
    ageGraph.setAxisValuesDecimals(1);
    ageGraph.setDataArray(myData);
    //can put in more code here to set colors and other visuals
    ageGraph.draw();

    agestats.innerHTML = calcStats(AGE);
}

function calcStats(column) {
    var ret = "Average: " + calcAverage(column) + "<br>";
    ret += "Median: " + calcMedian(column);
    return ret;
}

function numOrdA(a, b){ return (a-b); }

function calcMedian(column) {
    var numbers = Array();
    var count = 0;
    for (var ipatient=1; ipatient<patientData.length; ipatient++) {
        if (patientData[ipatient][column] >= 0) {
            numbers[count++] = patientData[ipatient][column];
        }
    }
    numbers.sort(numOrdA);
    var iseven = (numbers.length % 2 == 0);
    var ret;
    if (iseven) {  //average the two middle numbers
        var lowermiddleidx = parseInt(numbers.length/2);
        ret = (numbers[lowermiddleidx] + numbers[lowermiddleidx+1])/2;
    } else {
        var middleidx = Math.ceil(numbers.length/2);
        ret = numbers[middleidx];
    }
    return ret;
}

function calcAverage(column) {
    var total=0;
    var count=0;
    for (var ipatient=1; ipatient<patientData.length; ipatient++) {
        if (patientData[ipatient][column] >= 0) {
            total += patientData[ipatient][column];
            count++;
        }
    }
    return total/count;
}

function drawSurvival() {
    var myData = [["< 4",0], ["4-7",0], ["8-11",0], ["12-14",0], ["15-17",0], ["18-20",0], ["21-23",0],[">= 24",0], ["Living",0]];

    for (var igroup=0; igroup<myData.length; igroup++) {
        for (var ipatient=1; ipatient<patientData.length; ipatient++) {
            var survival = patientData[ipatient][SURVIVAL];
            var increment = false;
            switch (myData[igroup][0]) {
            case "< 4":
                increment = (survival < 4 && survival >= 0);
                break;
            case "4-7":
                increment = (survival >= 4 && survival < 7);
                break;
            case "8-11":
                increment = (survival >= 8 && survival < 11);
                break;
            case "12-14":
                increment = (survival >= 12 && survival < 14);
                break;
            case "15-17":
                increment = (survival >= 15 && survival < 17);
                break;
            case "18-20":
                increment = (survival >= 18 && survival < 20);
                break;
            case "21-23":
                increment = (survival >= 21 && survival < 23);
                break;
            case ">= 24":
                increment = (survival >= 24);
                break;
            case "Living":
                increment = (survival < 0);
                break;
            }
            if (increment) {
                myData[igroup][1]++;
            }
        }
    }
    survivalGraph = new JSChart("survivalgraph", "bar");
    survivalGraph.setTitle("Survival Distribution");
    survivalGraph.setAxisNameX("Survival (months)");
    survivalGraph.setAxisNameY("# Patients");
    survivalGraph.setAxisValuesDecimals(1);
    survivalGraph.setDataArray(myData);
    //can put in more code here to set colors and other visuals
    survivalGraph.draw();

    survivalstats.innerHTML = calcStats(SURVIVAL);
}

function drawSex() {
    var myData = [["Male",0], ["Female",0]];

    for (var igroup=0; igroup<myData.length; igroup++) {
        for (var ipatient=1; ipatient<patientData.length; ipatient++) {
            var patientSex = patientData[ipatient][SEX];
            var increment = false;
            switch (myData[igroup][0]) {
            case "Male":
                increment = (patientSex == "MALE");
                break;
            case "Female":
                increment = (patientSex == "FEMALE");
                break;
            }
            if (increment) {
                myData[igroup][1]++;
            }
        }
    }
    sexGraph = new JSChart("sexgraph", "pie");
    sexGraph.setTitle("Sex Distribution");
    sexGraph.setDataArray(myData);
    //can put in more code here to set colors and other visuals
    sexGraph.draw();
}

function drawTumorSite() {
    var myData = [["Cortex",0], ["Amygdala",0], ["Brain Stem",0], ["Cerebellum",0]];

    for (var igroup=0; igroup<myData.length; igroup++) {
        for (var ipatient=1; ipatient<patientData.length; ipatient++) {
            var site = patientData[ipatient][TUMORSITE];
            var increment = false;
            switch (myData[igroup][0]) {
            case "Cortex":
                increment = (site == "CORTEX");
                break;
            case "Amygdala":
                increment = (site == "AMYGDALA");
                break;
            case "Brain Stem":
                increment = (site == "BRAIN STEM");
                break;
            case "Cerebellum":
                increment = (site == "CEREBELLUM");
                break;
            }
            if (increment) {
                myData[igroup][1]++;
            }
        }
    }
    siteGraph = new JSChart("sitegraph", "pie");
    siteGraph.setTitle("Tumor Sites");
    siteGraph.setDataArray(myData);
    //can put in more code here to set colors and other visuals
    siteGraph.draw();
}

function drawRadiation() {
    var myData = [["True",0], ["False",0]];

    for (var igroup=0; igroup<myData.length; igroup++) {
        for (var ipatient=1; ipatient<patientData.length; ipatient++) {
            var radiation = patientData[ipatient][RADIATION];
            var increment = false;
            switch (myData[igroup][0]) {
            case "True":
                increment = radiation;
                break;
            case "False":
                increment = !radiation;
                break;
            }
            if (increment) {
                myData[igroup][1]++;
            }
        }
    }
    radiationGraph = new JSChart("radiationgraph", "pie");
    radiationGraph.setTitle("Patient had Radiation");
    radiationGraph.setDataArray(myData);
    //can put in more code here to set colors and other visuals
    radiationGraph.draw();
}

function drawPatientTable() {
    var table = "<table border=1 rules=below><tr>";
    table += "<th><input type=checkbox>" + patientData[0][0] + "</th>";
    for (var ii=1; ii<patientData[0].length; ii++) {
        table += "<th>" + patientData[0][ii] + "</th>";
    }
    for (var i=1; i<patientData.length; i++) {
        var row = "<tr>";
        row += "<td><input type=checkbox>" + patientData[i][0] + "</td>";
        for (ii=1; ii<patientData[i].length; ii++) {
            row += ("<td>" + patientData[i][ii] + "</td>");
        }
        row += "</tr>";
        table += row;
    }
    table += "</table>";
    patienttable.innerHTML = table;
}

function findHitsExact(column, exactval) {
    var hits = new Array();
    for (var ipatient=1; ipatient<patientData.length; ipatient++) {
        hits[ipatient-1] = (patientData[ipatient][column] == exactval);
    }
    return hits;
}

function findHitsRange(column, low, high) {
    var hits = new Array();
    for (var ipatient=1; ipatient<patientData.length; ipatient++) {
        var val = patientData[ipatient][column]
        hits[ipatient-1] = (val >= low && val <= high);
    }
    return hits;
}

function countHits(hits) {
    var count = 0;
    for (var i=0; i<hits.length; i++) {
        if (hits[i]) {
            count++;
        }
    }
    return count;
}

function intersectHits(hits1, hits2) {
    var hits3 = new Array();
    for (var i=0; i<hits1.length; i++) {
        hits3[i] = (hits1[i] && hits2[i]);
    }
    return hits3;
}

function countSex(sex) {
    countsex.innerHTML = "<br>Count of " + sex + ": <b>" + countHits(findHitsExact(SEX, sex)) + "</b>";
}

function countAge(low, high) {
    countage.innerHTML = "<br>Count of between " + low + " and " + high + ": <b>" + countHits(findHitsRange(AGE, low, high)) + "</b>";
}

function countAgeAndSex(sex, lowage, highage) {
    var hits1 = findHitsExact(SEX, sex);
    var hits2 = findHitsRange(AGE, lowage, highage);
    var hits3 = intersectHits(hits1, hits2);
    countintersection.innerHTML = "<br>Count intersection: <b>" + countHits(hits3) + "</b>";
}


funtion showGraphs() {

	alert('here');
	drawAge();
	alert('there');

}

