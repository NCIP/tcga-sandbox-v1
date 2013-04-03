/**
 *
 * PDFEncoder_Flex is a minimalistic class to generate PDF from an image
 * in Flash CS3 or above(using ActionScript 3.0 - Flah 10 Player or above Required) . 
 * This class takes an image (as BitmapData object) and converts that to a PDF file. 
 * 
 * 
 * @author FusionCharts Labs
 * @version 1 [ Flex ]
 *
 * @see	flash.geom.Rectangle
 * @see	flash.display.BitmapData
 * @see	flash.display.BitmapData.getPixels()
 *
 * @see	flash.utils.ByteArray
 * @see	flash.utils.ByteArray.writeObject()
 * @see	flash.utils.ByteArray.writeUTFBytes()
 * @see	flash.utils.ByteArray.writeBytes()
 * @see	flash.utils.ByteArray.writeByte()
 *
 * @see	flash.net.FileReference
 * @see	flash.net.FileReference.save()
 *
 * @see RegExp
 * @see uint
 * @see String
 * @see Boolean
 *
 * Copyright (c) 2008 Infosoft Global Private Limited
 *
 */
package com.fusioncharts.components.codec
{
	
	import flash.display.BitmapData;
	import flash.geom.Rectangle;
	//import flash.net.*;
	import flash.utils.ByteArray;

	public class PDFEncoder {
		
		private var arrExportData:Array=[];
		private var numPages:uint = 1;
		
		//list of Address references of all Objects in PDF
		private var xRefList:Array=[];
		// instance variables
		// iWidth stores the width of the image
		private var iWidth:uint;
		// iHeight stores the height of the image
		private var iHeight:uint;
		// PDFBytes stores the whole output PDF in raw bytes
		private var PDFBytes:ByteArray=new ByteArray();


		/**
		* Constructor for objects of class PDFEncoder
		*/
 		public function PDFEncoder() {

		}
		/**		
		* function setBatchBitmapData()
		* This function gets the image data of chart(s) and stores in an array
		* 
		* 
		* @param  	arrData		array of associative arrays containing chart export data including imagedata etc.
		* The format is given below:
		*		Each element of the array contains data for each chart (if multiple)
		*		If single chart only one array element would be present
		*		
		*		arrData: [
		*		  { 
		*			image:ImageData - image data of the chart
		*			meta :Object {
		*						width:uint	- width of the chart
		*						height:uint  - height of the chart
		*					}
		*			},
		* 	...
		*	    ]			
		*/
/*		public function setBatchBitmapData(arrData:Object):void{
			for(var i in arrData)
				arrExportData[int(i)] = arrData[i];
		}
*/		
		
		/**		
		* function setBitmapData()
		* This function gets the image data of a specific chart and stores in an array
		* 
		* 
		* @param  	iData		associative array containing chart export data including imagedata etc.
		* The format is given below:
		*		Each element of the array contains data for each chart (if multiple)
		*		If single chart only one array element would be present
		*		
		*		iData: 
		*		  { 
		*			image:ImageData - image data of the chart
		*			meta :Object {
		*						bgColor:Sting - background color of the chart 						
		*						width:uint	- width of the chart
		*						height:uint  - height of the chart
		*					}
		*			}
		*/
		public function setBitmapData(idata:BitmapData, imgWidth:int, imgHeight:int):void{
			arrExportData.push({stream:idata,meta:{width:imgWidth, height:imgHeight}});
		}
		
		public function addPDFObjects(obj:String):void {
			//currently unused
		}
		
		
		//---------------------------commented for FLEX---------------------
		//save PDF file
		/*public function savePDF(fileName:String = 'FusionCharts.pdf'):void {
			var fPDF:FileReference = new FileReference();
			fPDF.save(PDFBytes,fileName);
		}*/
		
		//Add PDF formatted object that embeds chart image data
		private function addImageToPDF(id:int = 0, compress:Boolean=true):ByteArray {
			var imgObjNo:int = 6 + id*3;
			var tbArr:ByteArray= new ByteArray();
			var baImg:ByteArray=new ByteArray();
			
			baImg=getBitmapData24(arrExportData[id].stream);
			if (compress) baImg.compress();
			var len:int=baImg.length;
			var str:String= imgObjNo + " 0 obj\n<<\n/Subtype /Image /ColorSpace /DeviceRGB /BitsPerComponent 8 /HDPI 72 /VDPI 72 /Filter /FlateDecode /Width "+iWidth+" /Height "+iHeight+" /Length "+len+" >>\nstream\n";
			tbArr.writeUTFBytes(str);
			tbArr.writeBytes(baImg);
			str="endstream\nendobj\n";
			tbArr.writeUTFBytes(str);

			return tbArr;
		}
		
		//Main function that build the PDF binary
		public function encode(compress:Boolean=true):ByteArray {
			//Store all PDF objects in this temporary string to be written to ByteArray
			var strTmpObj:String="";
			
			//Number of Pages (charts)
			numPages=arrExportData.length;
			
			//start xref array
			xRefList.push("xref\n0 ");
			xRefList.push("0000000000 65535 f \n"); //Address Refenrece to obj 0
			
			//Build PDF objects sequentially
			//version and header
			strTmpObj="%PDF-1.3\n%{FC}\n";
			PDFBytes.writeUTFBytes(strTmpObj);

			//OBJECT 1 : info (optional)
			strTmpObj="1 0 obj<<\n/Author (FusionCharts)\n/Title (FusionCharts)\n/Creator (FusionCharts)\n>>\nendobj\n";
			xRefList.push(calculateXPos(PDFBytes.length)); //refenrece to obj 1
			PDFBytes.writeUTFBytes(strTmpObj);
			
			//OBJECT 2 : Starts with Pages Catalogue
			strTmpObj="2 0 obj\n<< /Type /Catalog /Pages 3 0 R >>\nendobj\n";
			xRefList.push(calculateXPos(PDFBytes.length));//refenrece to obj 2
			PDFBytes.writeUTFBytes(strTmpObj);
			
			//OBJECT 3 : Page Tree (reference to pages of the catalogue)
			strTmpObj="3 0 obj\n<<  /Type /Pages /Kids [";
			for(var i:int=0;i<numPages;i++){
				strTmpObj+=(((i+1)*3)+1)+" 0 R\n";
			}
			strTmpObj+="] /Count "+numPages+" >>\nendobj\n";
			
			xRefList.push(calculateXPos(PDFBytes.length)); //refenrece to obj 3
			PDFBytes.writeUTFBytes(strTmpObj);
			
			//Each image page
			for(var itr:int=0;itr<numPages;itr++){
				iWidth=arrExportData[itr].meta.width;
				iHeight=arrExportData[itr].meta.height;
				//OBJECT 4..7..10..n : Page config
				strTmpObj=(((itr+2)*3)-2)+" 0 obj\n<<\n/Type /Page /Parent 3 0 R \n/MediaBox [ 0 0 "+iWidth+" "+iHeight+" ]\n/Resources <<\n/ProcSet [ /PDF ]\n/XObject <</R"+(itr+1)+" "+((itr+2)*3)+" 0 R>>\n>>\n/Contents [ "+(((itr+2)*3)-1)+" 0 R ]\n>>\nendobj\n";
				xRefList.push(calculateXPos(PDFBytes.length)); //refenrece to obj 4,7,10,13,16...
				PDFBytes.writeUTFBytes(strTmpObj);

				//OBJECT 5...8...11...n : Page resource object (xobject resource that transforms the image)
				xRefList.push(calculateXPos(PDFBytes.length)); //refenrece to obj 5,8,11,14,17...
				PDFBytes.writeUTFBytes(getXObjResource(itr));

				//OBJECT 6...9...12...n : Binary xobject of the page (image)
				var imgBA:ByteArray=addImageToPDF(itr,compress);
				xRefList.push(calculateXPos(PDFBytes.length));//refenrece to obj 6,9,12,15,18...
				PDFBytes.writeBytes(imgBA);
			}
			
			//xrefs	compilation
			xRefList[0]+=((xRefList.length-1)+"\n");
			
			//get trailer
			var trailer:String=getTrailer(PDFBytes.length ,xRefList.length-1);
			
			
			//write xref and trailer to PDF
			PDFBytes.writeUTFBytes(xRefList.join(''));
			PDFBytes.writeUTFBytes(trailer);
			
			//write EOF
			PDFBytes.writeUTFBytes("%%EOF\n");
			
			return this.PDFBytes
		}
		//Function to generate Image resource object needed for PDF image object
		private function getXObjResource(itr:int):String {
			return (((itr+2)*3)-1)+" 0 obj\n<< /Length "+(24+((iWidth+""+iHeight).length))+" >>\nstream\nq\n"+iWidth+" 0 0 "+iHeight+" 0 0 cm\n/R"+(itr+1)+" Do\nQ\nendstream\nendobj\n";
		}
		//X Reference calculator for each PDF object
		private function calculateXPos(pos:int):String{
			return ("0000000000".replace((new RegExp("0{"+((pos+"").length)+"}$")),pos))+(" 00000 n \n");
		}
		//Returns trailer PDF object
		private function getTrailer(xrefpos:uint,numxref:uint=7):String {
			return "trailer\n<<\n/Size "+numxref+"\n/Root 2 0 R\n/Info 1 0 R\n>>\nstartxref\n"+xrefpos+"\n";
		}
		
		
		
		/**
		* This function takes a BitmapData objects (having 32 bit image) and returns
		* a byte array containing 24 bit image data
		*/
		private function getBitmapData24(imageData:BitmapData):ByteArray{
			var imageData32:ByteArray=imageData.getPixels(new Rectangle(0, 0, imageData.width, imageData.height));
			var imageData24:ByteArray=new ByteArray();
			var i:uint;
			for(i=0;i<imageData32.length;i++){
				if(i%4==0) continue;
				imageData24.writeByte(imageData32[i]);
			}
			return imageData24;
		}
/*		private function getBitmapData24(strImgData:String, bgColor:String):ByteArray{
			
			var byteArray:ByteArray = new ByteArray();
			var arrRows:Array = strImgData.split(';');

			var getPixelConglomerates:Function = function(strRow:String, id:int, arr:Array):Array{
				return strRow.split(',');
			};
			var arrAll:Array = arrRows.map(getPixelConglomerates);

			var workOnRow:Function = function(arrRow:Array, id:int, arr:Array){
				arrRow.forEach(workInRow);
			}
			var workInRow:Function = function(strPixels:String, id:int, arr:Array){
				var arrData:Array = strPixels.split('_');
				if(arrData[0] == ''){
					// todo
					arrData[0] = bgColor;
				}
				arrData[1] = int(arrData[1]);
				
				var colorValue:uint = parseInt(arrData[0], 16);
				
				var r:uint = (colorValue & 0xFF0000) >> 16;
				var g:uint = (colorValue & 0x00FF00) >> 8;
				var b:uint = (colorValue & 0x0000FF);
				
				for(var i=0; i<arrData[1]; ++i){
					byteArray.writeByte(r);
					byteArray.writeByte(g);
					byteArray.writeByte(b);
				}
			};
			arrAll.forEach(workOnRow);
			return byteArray;
		}
*/		
	}
}
