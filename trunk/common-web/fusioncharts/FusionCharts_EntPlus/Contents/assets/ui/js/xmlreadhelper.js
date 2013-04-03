
function XmlReadWriteHelper()
{
	this.strFilePath = null;

	this.bSucceed = false;				//out

	this.xmlDoc = null;

	this.xmlHttp = null;

	this.loadFromFile = function( bAsync )
	{
		this.loadDataXML( this.strFilePath, bAsync);
	}

	this.getElementsByTagName = function(a_strTagName)
	{
		return this.xmlDoc.getElementsByTagName(a_strTagName);
    }

    this.getXmlDoc = function() {
        if (this.bSucceed) {
            if (!gbIE5 && !gbNav6)
                this.xmlDoc = this.xmlHttp.responseXML;
            return this.xmlDoc;
        }
        return null;
    }

	this.loadDataXML = function (sFileName, bAsync)
	{
		
		
		try
		{
			var sCurrentDocPath=_getPath(document.location.href);
			var bAsyncReq = true ;
			if (typeof(bAsync) !='undefined' )
				bAsyncReq = bAsync ;
			sdocPath=_getFullPath(sCurrentDocPath,sFileName);
			
			if(gbIE5)
			{	
				this.xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
				this.xmlDoc.async=bAsyncReq;
				this.xmlDoc.load(sdocPath);
			}
			else if(gbNav6)
			{
				var req=new XMLHttpRequest();
				req.open("GET", sdocPath, false);   
				req.send(null);   
				if (req.responseXML != null)
				{
					this.xmlDoc = req.responseXML;
				}
				else
				{
					this.onLoadXMLError();
					return ;
				}
			}
			else /*if(gbSafari)*/
			{
				if(window.XMLHttpRequest && !(window.ActiveXObject)) 
				{
					this.xmlHttp = new XMLHttpRequest();
					if(this.xmlHttp)
					{
						this.xmlHttp.onreadystatechange= this.onXMLResponse;
						this.xmlHttp.open("GET", sdocPath, false);
						this.xmlHttp.send(null);
					}
				}
			}
			this.bSucceed = true;
		}
		catch(e)
		{
			this.onLoadXMLError();
		}
	}

	this.onXMLResponse = function()
	{
	    if (this.readyState == 4) {
	        if (this.responseXML == null) 
	        {
	            this.onLoadXMLError();
	        }
	    }	
	};

	this.onLoadXMLError = function ()
	{
		
		this.bSucceed = false;
	};
	
	this.AJAXCall = function()
	{
		
		var xmlhttp;
		if (window.XMLHttpRequest)
		{// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		}
		else
		{// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		xmlhttp.open("GET","books.xml",false);
		xmlhttp.send();
		xmlDoc=xmlhttp.responseXML; 
		
		
	};
	
	
	this.loadDataXML_DELETED = function (sFileName, bAsync)
	{
		try
		{
		var sCurrentDocPath=_getPath(document.location.href);
		var bAsyncReq = true ;
		if (typeof(bAsync) !='undefined' )
			bAsyncReq = bAsync ;
		sdocPath=_getFullPath(sCurrentDocPath,sFileName);
		//alert(sdocPath);
		if(gbIE5)
		{	
			this.xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
			this.xmlDoc.async=bAsyncReq;
			this.xmlDoc.load(sdocPath);
		}
		else if(gbNav6)
		{
			var req=new XMLHttpRequest();
     			req.open("GET", sdocPath, false);   
			req.send(null);   
			if (req.responseXML != null)
				this.xmlDoc = req.responseXML;
			else
			{
				this.onLoadXMLError();
				return ;
			}
		}
		else /*if(gbSafari)*/
		{
       		if(window.XMLHttpRequest && !(window.ActiveXObject)) 
    		{
    		    this.xmlHttp = new XMLHttpRequest();
			    if(this.xmlHttp)
			    {
    			    this.xmlHttp.onreadystatechange= this.onXMLResponse;
    		        this.xmlHttp.open("GET", sdocPath, false);
    		        this.xmlHttp.send(null);
		        }
    		}
		}
		this.bSucceed = true;
		}
		catch(e)
		{
    			this.onLoadXMLError();
		}
	};

	
}
