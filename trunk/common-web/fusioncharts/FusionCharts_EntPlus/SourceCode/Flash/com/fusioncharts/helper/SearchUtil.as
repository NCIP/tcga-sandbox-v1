/**
 * @class SearchUtil
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 
 * SearchUtil class bunches a group of search utilities.At present only 
 * Divide & conquer method implemented.
 *
 * NOTE -  1. The class by default now searches for the nearest higher or lower value.There should be a flag set on this.
 *		   2. sortArray methods at present only sorts array containing objects with sorting property of each object specified.
			 	The normal Array sorting has not yet been implemented.
 */
 
 class com.fusioncharts.helper.SearchUtil{
	 
	 //The value to decide whether to loop or divide the array.
	 private var MAX_RANGE_LENGTH : Number = 5;
	 //This is the map to make the search progressive and effiecient.
	 private var VALUE_MAP : Object = new Object();
	 
	 private var iterateTab : String = "";
	 
	 /**
	 * Though we have very little to do in constructor.We have to  
	 * make this class instantiable.As we have a map implementation
	 * this should be unique to each instatnce.
	 */
	 public function SearchUtil(){
		//create the map;
		VALUE_MAP = new Object();
	 }
	 
	 /**
	 * storeToValueMap functions stores all the random value that we parsed
	 * at the time of running our divide & conquer algorithm.This table will 
	 * further help us to find the values without going through divide & conquer 
	 * algorith in a single session.
	 *	@param	value	Number	The number as the key to the map
	 *	@param	index	Number	The index of the passed number in the actual 
	 *							data array.
	 **/
	 private function storeToValueMap(value : Number, index:Number):Void{
		 
		 if(isNaN(value) || isNaN(index) || index < 0){
			return; 
		 }
		 //No of total reluts stored.
		 VALUE_MAP.num_result = (VALUE_MAP.num_result == undefined) ? 0 : VALUE_MAP.num_result;
		 //increment the number of result stored.
		 VALUE_MAP.num_result++;
		 //store index with value key.
		 VALUE_MAP[value] = index;
	 }
	 
	 /**
	 *	searchByDivide&Conquer method search one array on the basis of
	 *	divide and conquer algorithm and returns an array with elements
	 * 	in the range of min and max value.
	 *
	 *	@param		sourceArr		Array	The source array of objects or elements 
	 *	@param		min				Number	The min value of the range
	 *	@param		max				Number	The Max value of the range
	 *	@param		isSorted		Boolean	Whether the source array is already sorted
	 *	@param		sortType		Number	The type to sort the source array.These values could 
	 *										be only the Array sort on methods in ActionScript 2.0
	 *	@param		sortProperty	String	If the array holds objects.This property defines the 
	 *										property of those objects with which to sort. 
	 *
	 *	@return						Array	Array with the searched elements in the defined ranged.
	 *
	 **/
	 
	 public function searchByDivideAndConquer(soureceArr:Array, min:Number, max:Number, isSorted:Boolean, 
													sortProperty:String , sortType:Number ) : Array {
		 //make a duplicate of the source array
		 var rawArray : Array = soureceArr.slice();
		 //the array to return;
		 var searchedArr : Array;
		 //the range starting and end value
		 var startRange : Number;
		 var endRange : Number;
		 
		 //sort the duplicated source array; if it is already not been sorted.
		 if(!isSorted){
			rawArray = sortArray(rawArray, sortProperty, sortType);
			//if the sorted array has no elements returns
			if(rawArray.length <= 0){
				return searchedArr;
			}
		 }
		 //Before we go into the Divide & conquer algorithm; we should check whether
		 //the VALUE_MAP has already stored value on this targets.
		 if(VALUE_MAP.num_result > 0){
			 //either we will get the exact index number of this value or the 
			 //index of a nearest lower or higher number.
			 startRange = getIndexFromTheMap(min,true);
			 endRange = getIndexFromTheMap(max,false);
		 }
		 if(startRange == undefined || endRange == undefined){
			 // in case the start & end range is undefined (when the map is likely to be empty)
			 // assign the full range
			 startRange = (startRange == undefined) ? 0 : startRange;
			 endRange = (endRange == undefined) ? (rawArray.length-1) : endRange;
		 }
		 // now if the value has not been found from the map go for the divide & conquer
		 // with the narrowed down range or the full range from the map
		 var firstTarget : Number = (rawArray[startRange] == min) ? startRange : getIndex(rawArray, min, startRange,  endRange, sortProperty, false, true);
		 var lastTarget : Number = (rawArray[endRange] == max) ? endRange : getIndex(rawArray, max, startRange,  endRange, sortProperty, true, false);
		 // if the last index retured is the last index in the data array; Return the
		 // remaining items from the first index.
		 if(lastTarget == (rawArray.length-1) && firstTarget != undefined){
			 searchedArr = rawArray.slice(firstTarget);
		 }else if(firstTarget != undefined && lastTarget != undefined){
			// in the last target we tries to get the nearest lower number.So the value of the 
			// return index must be included in the return array.
			searchedArr = rawArray.slice(firstTarget, (lastTarget+1));
		 }
		 
		 searchedArr["firstIndex"] = firstTarget;
		 searchedArr["lastIndex"] = lastTarget;
		 return searchedArr;
	 }
	 
	 
	 /**
	 *	getIndexFromTheMap looks into the search table and tries to get the associated
	 *	index for the value.In case it could not find the exact index; it tries to narrowed 
	 *	down the range, with which we have to go to divide & conquer algorithm, by finding 
	 *	the index of the nearest lower or higher value stored in the Map.
	 *
	 *	@param	val						Number		The number of which we have to find the index.
	 *	@param	isLowestNumberIndex		Boolean 	The boolean value which decides whether we have
	 *												to find the nearest lower or higher number(in case
	 *												we do not find the exact number).
	 *
	 *	@return 						Number		The index number if the value exist in the Map.
	 *
	 **/
	 private function getIndexFromTheMap(val : Number, isLowerNumberIndex : Boolean):Number{
		 //check whether this value has an index associated
		  var target : Number = VALUE_MAP[val];
		  //only if the exact number has no index associated.
		  if(target == undefined){
			  
			  /** lets try to get the index of the nearest value stored to get a narrowed
		  	  *	  down range.There could be 2 possibilities.
			  *		1. Find the index of the closest lower Number
			  *		2. Find the index of the closest higher Number.
			  **/
			   var prevStoredNo : Number;
			  // loop over items stored..
			   for(var prop in VALUE_MAP){
				   var no : Number = Number(prop);
				   var doUpdate : Boolean = false;
				   //disregard all the non-numeric properties..
				   if(!isNaN(no)){
					   // Case 1.
					   if(isLowerNumberIndex){
						   if(prevStoredNo == undefined && no < val){
								doUpdate = true;
						   }else if(no < val && no > prevStoredNo){
								doUpdate = true;
						   }
					   // Case 2.
					   }else{
						  if(prevStoredNo == undefined && no > val){
							 doUpdate = true;
						  }else if(no > val && no < prevStoredNo){
							 doUpdate = true;
						  }
					   }
					   if(doUpdate){
						   prevStoredNo = no;
						   target = VALUE_MAP[prop];
					   }
				   }
			   } // end of for
		  }
		  
		  return target;
	 }
	 
	 /**
	 * getIndex is the principal method for the Divide & Conquer algorith.
	 * it accepts a target value and iterates over the array to find the index 
	 * of that value.In case the value is not present, based on the Occuerence 
	 * type it returns the index of the nearest higher or lower value find.
	 *
	 *	@param	arrayToSearch		Array		The actual sorted array to work upon
	 *	@param	targetValue			Number		The traget value of which we need to find the index.
	 *	@param	lowerEnd			Number		The starting index number
	 *	@param	upperEnd			Number		The end index number
	 *	@param	propName			String		The name of the property if the array holds object
	 *											in its indices.
	 *
	 *	@param	findLowerNumber		Booelan		The booelan value to decide whether to get the nearest lower number
	 *											or the nearest higher number if the exact number does not exist.
	 *
	 *	@param	forFirstOccurence	Boolean		If the Array has multiple entries with the same value
	 *											this Boolean value decided whether to store the first occurence
	 *											or the last occurnce of that value in the array.Now when we are looking 
	 *											for the first occurence that means we will be ignoring that value from our
	 *											calculation and start from the very next of it.And when we are looking for 
	 *											the last occurence that means we will be including this value in our calculation.
	 *
	 *	@returns					Number		The index number of the value or the index number of the nearest higher or lower
	 *											Value.
	 **/
	 
	 private function getIndex(arrayToSearch : Array, targetValue : Number, lowerEnd : Number, upperEnd : Number, 
									  propName : String, findLowerNumber : Boolean, forFirstOccurence : Boolean) : Number{
		 //target index to return
		 var targetIndex : Number;
		 //get the middle index
		 var midIndex : Number = Math.floor((upperEnd - lowerEnd) / 2 + lowerEnd);
		 //get the mid value
		 var val : Number = (propName != undefined && propName != "") ? arrayToSearch[midIndex][propName] : arrayToSearch[midIndex];
		 // Whenever we are deriving a value we must store it in our VALUE_MAP table
		 // this will ease out the search load as we continue searching.
		 storeToValueMap(val, midIndex);
		 //if the value is a valid number check..
		 if(!isNaN(val)){
			 /**
			 *	The possible search results are as follows
			 *	1. We LUCKILY find the number that we are looking for
			 *	2. We get to the predefined minimum range and we will loop on these items to find the number.
			 *	   In case 2 when we will loop through the last few items we have 2 possibilties.
			 *	   2.A	We will find the number we are looking for.
			 *	   2.B	The required number does not exist in the array. We will store the nearest lowest or higher 
			 *			Number found in these items.
			 *	3. We go for further recursions based on the value find.
			 **/
			 // CASE 1 - EXACT NUMBER FOUND
			 if(val == targetValue){
				// The exact value has been found.We also need to check whether multiple indices has the same value and 
				// depending on the occurence type we have to get the first or the last occurence index.
				targetIndex = findOccurrence(arrayToSearch, targetValue, midIndex, propName, forFirstOccurence);
			 // CASE 2 - NARROW REGION 
			 }else if(Math.abs(upperEnd-lowerEnd) <= MAX_RANGE_LENGTH){
				 var i : Number;
				 //There is also a possibility of the exact value does not exist.
				 var exactValueFound : Boolean = false;
				 var nearestLowerNum : Number;
				 var nearestHigherNum : Number;
				 var tempIndex : Number;
				 //loop over items....
				 for(i = lowerEnd; i<=upperEnd; i++){
					 val = (propName != undefined && propName != "") ? arrayToSearch[i][propName] : arrayToSearch[i];
					 //store to map
					 storeToValueMap(val, i);
					 // CASE 2.A
					 if(val == targetValue){
						//now check whether more indices has the same value
						exactValueFound = true;
						targetIndex = findOccurrence(arrayToSearch, targetValue, i, propName, forFirstOccurence);
						break;
					 // CASE 2.B
					 }else{
						  var doUpdate : Boolean = false;
						 //nearest lower number
						 if(findLowerNumber){
							// if no nearest number stored store the first one.Should be the lowest value
							// for this section of the array
							if(nearestLowerNum == undefined && val < targetValue){
								nearestLowerNum = val;
								tempIndex = i;
								doUpdate = true;
							// if the current value is greater than the previous nearest lower number & lower than 
							// the target value we update
							}else if(val > nearestLowerNum && val < targetValue){
								nearestLowerNum = val;
								tempIndex = i;
								doUpdate = true;
							}
						 // nearest higher number 
						 }else{
							// if no nearset value is stored. store the one that is greater than the target value.
							// this will be the nearest higher number of our target value as the array is already 
							// sorted
							if(nearestHigherNum == undefined && val > targetValue){
								nearestHigherNum = val;
								tempIndex = i;
								doUpdate = true;
							}
						 }
					 }
					 //if the exact value has not been found.Store nearest
					 if(!exactValueFound && doUpdate){
						 if(findLowerNumber){
							targetIndex = findOccurrence(arrayToSearch, nearestLowerNum, tempIndex, propName, forFirstOccurence);
						 }else{
							targetIndex = findOccurrence(arrayToSearch, nearestHigherNum, tempIndex, propName, forFirstOccurence);
						 }
					 }
				 }
				 if(targetIndex == undefined){
					if(findLowerNumber){
						nearestLowerNum = (propName != undefined && propName != "") ? arrayToSearch[(lowerEnd-1)][propName] : arrayToSearch[(lowerEnd-1)];
						tempIndex = (lowerEnd-1);
						targetIndex = findOccurrence(arrayToSearch, nearestLowerNum, tempIndex, propName, forFirstOccurence);
					}else{
						nearestHigherNum = (propName != undefined && propName != "") ? arrayToSearch[(upperEnd+1)][propName] : arrayToSearch[(upperEnd+1)];
						tempIndex = (upperEnd+1);
						targetIndex = findOccurrence(arrayToSearch, nearestHigherNum, tempIndex, propName, forFirstOccurence);
					}
				 }
			 }else if(val > targetValue){
				 //the found value is greater than the target value go for recursions with the lower range ..
				 targetIndex = getIndex(arrayToSearch, targetValue, lowerEnd, (midIndex-1), propName, findLowerNumber, forFirstOccurence);
				 
			 }else{
				//the found value is greater than the target value go for recusions with the higher range .
				targetIndex = getIndex(arrayToSearch, targetValue, (midIndex+1), upperEnd, propName, findLowerNumber, forFirstOccurence);
			 }
		 }
		 
		 return targetIndex;
	 }
	 
	 /**
	 * findOccurrence method finds the last or the first index of a given value in the
	 * Data array.
	 * @param	arrayToSearch		Array	The array in which we have to search.
	 * @param	val					Number	The initial value to search.
	 * @param	targetVal			Number	The Target value to search.
	 * @param	index				Number	The index from which we have to start checking.
	 * @param	propName			String	The name of the property if the array holds objects 
	 *										in its indices
	 * @param	forFirstOccurrence	Boolean	The boolean value which decides whether we need to find the
	 *										The last or first index of the value
	 *
	 *	@return						Number	The index number of the first or the last occurence
	 **/
	 
	 private function findOccurrence(arrayToSearch:Array, targetVal:Number, index:Number, propName:String, forFirstOccurrence : Boolean):Number{
		 //index to return 
		 var targetIndex : Number;
		 var val : Number = (propName != undefined && propName != "") ? arrayToSearch[index][propName] : arrayToSearch[index];
		
		 if(forFirstOccurrence){
			//traverse downward
			while(val == targetVal){
				targetIndex = index;
				index --; 
				val = (propName != undefined && propName != "") ? arrayToSearch[index][propName] : arrayToSearch[index];
				//store to map
				storeToValueMap(val, index);
			}
		}else{
			//traverse upward
			while(val == targetVal){
				targetIndex = index;
				index ++; 
				val = (propName != undefined && propName != "") ? arrayToSearch[index][propName] : arrayToSearch[index];
				//store to map
				storeToValueMap(val, index);
			}
		}
		
		return targetIndex;
	 }
	 
	 /**
	 * sortArray method sorts an array.It deals with array with objects  
	 * in its each index or just numeric values in index. 
	 *	
	 *	@param	unSortedArr		The raw source array to be sorted
	 *	@param	sortProperty	On which property we have to sort the array.
	 *	@param	sortType		Flash built in sorting types.
	 **/
	 private function sortArray(unSortedArr : Array,  sortProperty:String , sortType:Number):Array{
		 //array to return
		 var sortedArr : Array = new Array();
	 	//check whether each elemnt is an object = [Array - object combination]
		var testObj : Object = unSortedArr[1];
		if(typeof testObj == "object"){
			//if object and sort property is not defined return
			if(sortProperty == undefined){
				return sortedArr;
			}
			//sort array on the property
			sortedArr = unSortedArr.sortOn(sortProperty,sortType);
			
		}else{
			// TO BE IMPLEMENTED ---
		}
		
		return sortedArr;
	 }
	 
 }