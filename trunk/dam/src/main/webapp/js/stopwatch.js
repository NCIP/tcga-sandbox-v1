// based on stopwatch script by Brothercake - http://www.brothercake.com/ (format modified by Proft, 11 Sep 04)
var base = 60;
var clocktimer,dateObj,dh,dm,ds,ms;
var readout = '';
var h = 1;
var m = 1;
var tm = 1;
var s = 0;
var ts = 0;
var ms = 0;
var show = true;
var init = 0;
var mPLUS = new Array(
        'm0',
        'm1',
        'm2',
        'm3',
        'm4',
        'm5',
        'm6',
        'm7',
        'm8',
        'm9'
        );
var ii = 0;
var clock;
function startwatch( clockinput ) {
    clock = clockinput;
    dateObj = new Date();
    startTIME();
    clocktimer = setInterval("startTIME()", 1000);
}
function stopwatch() {
    clearInterval(clocktimer);
}
function startTIME() {
    var cdateObj = new Date();
    var t = (cdateObj.getTime() - dateObj.getTime()) - (s * 1000);
    if(t > 999) {
        s++;
    }
    if(s >= (m * base)) {
        ts = 0;
        m++;
    } else {
        ts = parseInt((ms / 100) + s);
        if(ts >= base) {
            ts = ts - ((m - 1) * base);
        }
    }
    if(m > (h * base)) {
        tm = 1;
        h++;
    } else {
        tm = parseInt((ms / 100) + m);
        if(tm >= base) {
            tm = tm - ((h - 1) * base);
        }
    }
    ms = Math.round(t / 10);
    if(ms > 99) {
        ms = 0;
    }
    if(ms == 0) {
        ms = '00';
    }
    if(ms > 0 && ms <= 9) {
        ms = '0' + ms;
    }
    if(ts > 0) {
        ds = ts;
        if(ts < 10) {
            ds = '0' + ts;
        }
    } else {
        ds = '00';
    }
    dm = tm - 1;
    if(dm > 0) {
        if(dm < 10) {
            dm = '0' + dm;
        }
    } else {
        dm = '00';
    }
    dh = h - 1;
    if(dh > 0) {
        if(dh < 10) {
            dh = '0' + dh;
        }
    } else {
        dh = '00';
    }
    readout = dh + ':' + dm + ':' + ds //+ '.' + ms;
    if(show) { //document.clockform.clock.value = readout; }
        clock.value = readout;
    }
}


