/**
 * @class NumberFormatting
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
*/
class com.fusioncharts.helper.NumberFormatting {
	/**
	*/
	function NumberFormatting() {
	}
	/**
	* formatNumber method helps format a number as per the user
	* requirements.
	*	@param		intNum				Number to be formatted
	*	@param		bFormatNumber		Flag whether we've to format
	*									decimals and add commas
	*	@param		decimalPrecision	Number of decimal places we need to
	*									round the number to.
	*	@param		forceDecimals		Whether we force decimal padding.
	*	@param		bFormatNumberScale	Flag whether we've to format number
	*									scale
	*	@param		defaultNumberScale	Default scale of the number provided.
	*	@param		numScaleValues		Array of values (for scaling)
	*	@param		numScaleUnits		Array of Units (for scaling)
	*	@param		numberPrefix		Number prefix to be added to number.
	*	@param		numberSuffix		Number Suffix to be added.
	*	@return						Formatted number as string.
	*
	*/
	public static function formatNumber (intNum : Number, bFormatNumber : Boolean, decimalPrecision : Number, forceDecimals : Boolean, bFormatNumberScale : Boolean, defaultNumberScale : String, numScaleValues : Array, numScaleUnits : Array, numberPrefix : String, numberSuffix : String, decimalSeparator:String, thousandSeparator:String, numberScaleDefined:Boolean) : String 
	{
		//First, if number is to be scaled, scale it
		//Number in String format
		var strNum : String = String (intNum);
		//Number Scale
		var strScale : String
		if (bFormatNumberScale)
		{
			strScale = defaultNumberScale;
		}else
		{
			strScale = "";
		}
		if (bFormatNumberScale && numberScaleDefined)
		{
			//Get the formatted scale and number
			var objNum : Object = formatNumberScale (intNum, defaultNumberScale, numScaleValues, numScaleUnits);
			//Store from return in local primitive variables
			strNum = String (objNum.value);
			intNum = objNum.value;
			strScale = objNum.scale;
		}
		//Now, if we've to format the decimals and commas
		if (bFormatNumber)
		{
			//Format decimals
			strNum = formatDecimals (intNum, decimalPrecision, forceDecimals);
			//Format commas now
			strNum = formatCommas (strNum, decimalSeparator, thousandSeparator);
		}
		//Now, add scale, number prefix and suffix
		strNum = numberPrefix + strNum + strScale + numberSuffix;
		return strNum;
	}
	/**
	* formatNumberScale formats the number as per given scale.
	* For example, if number Scale Values are 1000,1000 and
	* number Scale Units are K,M, this method will divide any
	* value over 1000000 using M and any value over 1000 (<1M) using K
	* so as to give abbreviated figures.
	* Number scaling lets you define your own scales for numbers.
	* To clarify further, let's consider an example. Say you're plotting
	* a chart which indicates the time taken by a list of automated
	* processes. Each process in the list can take time ranging from a
	* few seconds to few days. And you've the data for each process in
	* seconds itself. Now, if you were to show all the data on the chart
	* in seconds only, it won't appear too legible. What you can do is
	* build a scale of yours and then specify it to the chart. A scale,
	* in human terms, would look something as under:
	* 60 seconds = 1 minute
	* 60 minute = 1 hr
	* 24 hrs = 1 day
	* 7 days = 1 week
	* First you would need to define the unit of the data which you're providing.
	* Like, in this example, you're providing all data in seconds. So, default
	* number scale would be represented in seconds. You can represent it as under:
	* <graph defaultNumberScale='s' ...>
	* Next, the scale for the chart is defined as under:
	* <graph numberScaleValue='60,60,24,7' numberScaleUnit='min,hr,day,wk' >
	* If you carefully see this and match it with our range, whatever numeric
	* figure was present on the left hand side of the range is put in
	* numberScaleValue and whatever unit was present on the right side of
	* the scale has been put under numberScaleUnit - all separated by commas.
	
	*	@param	intNum				The number to be scaled.
	*	@param	defaultNumberScale	Scale of the number provided.
	*	@param	numScaleValues		Incremental list of values (divisors) on
	*								which the number will be scaled.
	*	@param
	*/
	public static function formatNumberScale (intNum : Number, defaultNumberScale : String, numScaleValues:Array, numScaleUnits:Array) : Object 
	{
		//Create an object, which will be returned
		var objRtn : Object = new Object ();
		//Scale Unit to be stored (assume default)
		var strScale : String = defaultNumberScale;
		var i : Number = 0;
		//If the scale unit or values have something fed in them
		//we manipulate the scales.
		for (i = 0; i < numScaleValues.length; i ++)
		{
			if (Math.abs (Number (intNum)) >= Number(numScaleValues [i]))
			{
				strScale = numScaleUnits [i];
				intNum = Number (intNum) / numScaleValues [i];
			} else 
			{
				break;
			}
		}
		//Set the values as properties of objRtn
		objRtn.value = intNum;
		objRtn.scale = strScale;
		return objRtn;
	}
	/**
	* formatDecimals method formats the decimal places of a number.
	* Requires the following to be defined:
	*	@param	intNum				Number on which we've to work.
	*	@param	decimalPrecision	Number of decimal places to which we've
	*								to format the number to.
	*	@param	forceDecimals		Boolean value indicating whether to add decimal
	*								padding to numbers which are falling as whole
	*								numbers?
	*	@return					A number with the required number of decimal places
	*								in String format. If we return as Number, Flash will remove
	*								our decimal padding or un-wanted decimals.
	*/
	public static function formatDecimals (intNum : Number, decimalPrecision : Number, forceDecimals : Boolean) : String 
	{
		//If no decimal places are needed, just round the number and return
		if (decimalPrecision <= 0)
		{
			return String (Math.round (intNum));
		}
		//Round the number to specified decimal places
		//e.g. 12.3456 to 3 digits (12.346)
		//Step 1: Multiply by 10^decimalPrecision - 12345.6
		//Step 2: Round it - i.e., 12346
		//Step 3: Divide by 10^decimalPrecision - 12.346
		var tenToPower : Number = Math.pow (10, decimalPrecision);
		var strRounded : String = String (Math.round (intNum * tenToPower) / tenToPower);
		//Now, strRounded might have a whole number or a number with required
		//decimal places. Our next job is to check if we've to force Decimals.
		//If yes, we add decimal padding by adding 0s at the end.
		if (forceDecimals)
		{
			//Add a decimal point if missing
			//At least one decimal place is required (as we split later on .)
			//10 -> 10.0
			if (strRounded.indexOf (".") == - 1)
			{
				strRounded += ".0";
			}
			//Finally, we start add padding of 0s.
			//Split the number into two parts - pre & post decimal
			var parts : Array = strRounded.split (".");
			//Get the numbers falling right of the decimal
			//Compare digits in right half of string to digits wanted
			var paddingNeeded : Number = decimalPrecision - parts [1].length;
			//Number of zeros to add
			for (var i = 1; i <= paddingNeeded; i ++)
			{
				//Add them
				strRounded += "0";
			}
		}
		return (strRounded);
	}
	/**
	* formatCommas method adds proper commas to a number in blocks of 3
	* i.e., 123456 would be formatted as 123,456
	*	@param	strNum	The number to be formatted (as string).
	*					Why are numbers taken in string format?
	*					Here, we are asking for numbers in string format
	*					to preserve the leading and padding 0s of decimals
	*					Like as in -20.00, if number is just passed as number,
	*					Flash automatically reduces it to -20. But, we've to
	*					make sure that we do not disturb the original number.
	*	@return		Formatted number with commas.
	*/
	public static function formatCommas (strNum : String, decimalSeparator:String, thousandSeparator:String) : String 
	{
		//intNum would represent the number in number format
		var intNum : Number = Number (strNum);
		//If the number is invalid, return an empty value
		if (isNaN (intNum))
		{
			return "";
		}
		var strDecimalPart : String = "";
		var boolIsNegative : Boolean = false;
		var strNumberFloor : String = "";
		var formattedNumber : String = "";
		var startPos : Number = 0;
		var endPos : Number = 0;
		//Define startPos and endPos
		startPos = 0;
		endPos = strNum.length;
		//Extract the decimal part
		if (strNum.indexOf (".") != - 1)
		{
			strDecimalPart = strNum.substring (strNum.indexOf (".") + 1, strNum.length);
			endPos = strNum.indexOf (".");
		}
		//Now, if the number is negative, get the value into the flag
		if (intNum < 0)
		{
			boolIsNegative = true;
			startPos = 1;
		}
		//Now, extract the floor of the number
		strNumberFloor = strNum.substring (startPos, endPos);
		//Now, strNumberFloor contains the actual number to be formatted with commas
		// If it's length is greater than 3, then format it
		if (strNumberFloor.length > 3)
		{
			// Get the length of the number
			var lenNumber : Number = strNumberFloor.length;
			for (var i : Number = 0; i <= lenNumber; i ++)
			{
				//Append proper commans
				if ((i > 2) && ((i - 1) % 3 == 0))
				{
					formattedNumber = strNumberFloor.charAt (lenNumber - i) + thousandSeparator + formattedNumber; 
				} else 
				{
					formattedNumber = strNumberFloor.charAt (lenNumber - i) + formattedNumber; 
				}
			}
		} else 
		{
			formattedNumber = strNumberFloor; 
		}
		// Now, append the decimal part back
		if (strDecimalPart != "")
		{
			formattedNumber = formattedNumber + decimalSeparator + strDecimalPart; 
		}
		//Now, if neg num
		if (boolIsNegative == true)
		{
			formattedNumber = "-" + formattedNumber; 
		}
		//Return
		return formattedNumber;
	}
	/**
	* unescapeChar method helps to unescape certain escape characters
	* which might have got through the XML. Like, %25 is escaped back to %.
	* This function would be used to format the number prefixes and suffixes.
	*	@param	strChar		The character or character sequence to be unescaped.
	*	@return			The unescaped character
	*/
	public static function unescapeChar (strChar : String) : String
	{
		//Perform only if strChar is defined
		if (strChar == "" || strChar == undefined)
		{
			return "";
		}
		//If it doesnt contain a %, return the original string
		if (strChar.indexOf ("%") == - 1)
		{
			return strChar;
		}
		//We're not doing a case insensitive search, as there might be other
		//characters provided in the Prefix/Suffix, which need to be present in lowe case.
		//Create the conversion table.
		var cTable : Array = new Array ();
		cTable.push (
		{
			char : "%", encoding : "%25"
		});
		cTable.push (
		{
			char : "&", encoding : "%26"
		});
		cTable.push (
		{
			char : "£", encoding : "%A3"
		});
		cTable.push (
		{
			char : "€", encoding : "%E2%82%AC"
		});
		//v2.3 Backward compatible Euro
		cTable.push (
		{
			char : "€", encoding : "%80"
		});
		cTable.push (
		{
			char : "¥", encoding : "%A5"
		});
		cTable.push (
		{
			char : "¢", encoding : "%A2"
		});
		cTable.push (
		{
			char : "₣", encoding : "%E2%82%A3"
		});
		cTable.push (
		{
			char : "+", encoding : "%2B"
		});
		cTable.push (
		{
			char : "#", encoding : "%23"
		});
		//Loop variable
		var i : Number;
		//Return string (escaped)
		var rtnStr : String = strChar;
		for (i = 0; i < cTable.length; i ++)
		{
			if (strChar == cTable [i].encoding)
			{
				//If the given character matches the encoding, convert to character
				rtnStr = cTable [i].char;
				break;
			}
		}
		//Return it
		return rtnStr;
		//Clean up
		delete cTable;
	}
}
