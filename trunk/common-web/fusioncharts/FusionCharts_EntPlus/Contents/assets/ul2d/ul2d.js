// JavaScript Document

// todo ... support more node params
var d ;

var sectionCount = 0;

var menuDataStore = {};

var ul2d = { }

ul2d.g = function(id, ob) { return (ob || document).getElementById(id); };
ul2d.a = function(id, ob) { return ob.getAttribute(id); };
ul2d.t = function(tg, ob) { return (ob || document).getElementsByTagName(tg); };
ul2d.t0 = function(tg, ob) { nl=ul2d.t(tg, ob); return (nl.length) ? (nl[0].parentNode==ob) ? nl[0] : null : null; };

ul2d.ln = 0;
ul2d.parse = function(id, root)
{
	
	var ob;
	try	{ ob = ul2d.g(id); } catch (e) { }
	if(typeof ob != 'object') return;
	
	d = new dTree(id+'_d');
	d.add(ul2d.ln, -1, root);
	ul2d._parseul(ob, d, ul2d.ln, 0);

	ob.style.display='none';
	// append to dom
	return d;
};

ul2d._parseul = function(ul, d, pid)
{
	
	var nl=ul.childNodes;
	for(var i=0;i<nl.length;i++)
		if(nl[i].nodeName.toLowerCase()!='li') continue;
		else {
			
			ul2d._parseli(nl[i], d, pid);
		}
};

ul2d._parseli = function(li, d, pid)
{ 
	var na=ul2d.t0('a', li);
	var ul=ul2d.t0('ul', li);
	
	ul2d.ln++;

	var pm={};
	
	//	id, pid, name, url, title, target, icon, iconOpen, open
	pm['id'] = ul2d.ln;
	pm['pid'] = pid;
	pm['name'] = na?na.innerHTML:li.childNodes[0].nodeValue.toString();
	pm['url'] = (""+(na?na:'')).replace(/^.+?contents\//i,"").replace(/^\s+?|\s+?$/g,"");
	pm['title'] = na?(ul2d.a('title',na)||ul2d.a('title',li)):(ul2d.a('title',li)); 
	pm['open'] = (sectionCount==0);
	
	d.add(pm);
	
	if(na){
		menuDataStore[pm['url'].toLowerCase()] = pm['id'];
	}else
	{
		menuDataStore[pm['id'].toString()] = ""+pm['id'];
	}
	
	if(ul) { ul2d._parseul(ul, d,ul2d.ln); sectionCount++  }
};

function dtree_addbase(d, val)
{
	for(var xx in d.icon)
		d.icon[xx] = val+d.icon[xx];
}

ul2d.pasrseOpenNodes = function (nodeIndex)
{
	var objOpenNodes = {};
	for (var i in nodeIndex)
		objOpenNodes[i+""] = true;
	return objOpenNodes;
};
