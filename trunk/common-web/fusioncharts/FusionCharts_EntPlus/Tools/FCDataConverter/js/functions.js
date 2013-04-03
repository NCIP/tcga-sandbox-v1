// Small function to verify the type of a variable in easy notation.
var tmp;
function updatejson(dynamic) {
    var xml = $("#dataxml"),
    prexml = $("#pre-dataxml"),
    json = $("#datajson"),
    prejson = $("#pre-datajson"),
    res = '',
    error;

    try {
        eval("tmp = " + json.val().replace(/\n|\r/ig, '') + ";");
    }
    catch (e) {
        $("td#json .no-error").hide();
        $("td#json .error").show();
        $("td#json").css("border-color", "#F00");
        return;
    }


    res = FusionCharts.transcodeData(tmp, 'json', 'xml').replace(/\>\n*/ig, '>\n');

    xml.val(res);
    prexml.html(res.replace(/\>/ig, '&gt;').replace(/\</ig, '&lt;'));
    prejson.html(json.val());

    $("td#json .no-error").show().fadeOut(1000)
    $("td#json .error").hide();
    $("td#xml .error").hide();
    $("td#json").css("border-color", "#ccc");
    $("td#xml").css("border-color", "#ccc");

    prettyPrint();
}

function updatexml (dynamic) {
    var xml = $("#dataxml"),
    prexml = $("#pre-dataxml"),
    datajson = $("#datajson"),
    predatajson = $("#pre-datajson"),
    res = '';

    res = JSON.stringify( FusionCharts.transcodeData(xml.val(), 'xml', 'json') );

    if (res == '{"text":"Xml error"}') {
        $("td#xml .no-error").hide();
        $("td#xml .error").show();
        $("td#xml").css("border-color", "#F00");
        prexml.html(xml.val().replace(/\>/ig, '&gt;').replace(/\</ig, '&lt;'));
        return;
    }

    eval("res = " + res.replace(/\n|\r/ig, '') + ";");
    res = JSON.stringify( res, undefined, 2 );
    datajson.val(res);
    predatajson.html(res);
    prexml.html(xml.val().replace(/\>/ig, '&gt;').replace(/\</ig, '&lt;'));


    $("td#xml .error").hide();
    $("td#json .error").hide();
    $("td#xml").css("border-color", "#ccc");
    $("td#json").css("border-color", "#ccc");
    $("td#xml .no-error").show().fadeOut(1000);

    prettyPrint();
}

var showEditMsg = function (type) {

    var width = $(document).width();
    var height = 50;
    //var width = e.pageX;
    //var height = e.pageY;
    if (type == "xml") {
        //$("#edit-msg2").fadeOut('fast');
        $("#edit-msg").css ({
            'left' : width/2 - 120,
            "top": height-5,
            "display": "block"
        });
    }
    if (type == "json") {
        //$("#edit-msg").fadeOut('fast');
        $("#edit-msg2").css ({
            'left' : width/2 + 330,
            "top": height-5,
            "display": "block"
        });
    }
}

var hideEditMsg = function (type) {
    //$("#edit-msg").stop().fadeOut(1500);
    if (type == "xml") {
        $("#edit-msg").hide();
    }
    if (type == "json") {
        $("#edit-msg2").hide();
    }
}

function stringParser (obj, num) {
    if (!num) {
        num = 0;
    }
    var x, str = '', space_count = num * 4, space = '', newline = '';
    for(x=0; x < space_count; x++) {
    //space +=' ';
    }
    if (typeof obj === 'object') {
        str += '{';
        for (x in obj) {
            if (obj[x] instanceof Array) {
                str +='[';
                for (var i in obj[x]) {
                    str += stringParser (obj[x][i], num + 1) + ',';
                }
                str += ']';
            }else{
                str += newline + space + x + ': ' + stringParser(obj[x], num + 1) + ',';
            }
        }
        str += newline + space + '}';
    }else{
        str = "\"" + obj + "\"";
    }
    //console.log (str);
    return str;
}
