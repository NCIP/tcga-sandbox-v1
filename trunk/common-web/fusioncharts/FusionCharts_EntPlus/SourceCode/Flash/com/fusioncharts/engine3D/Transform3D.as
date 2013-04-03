/**
* @class Transform3D
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* Transform3D class groups a bunch of 3D space 
* transformation related functions.
*/
//
// import the Manager class
import com.fusioncharts.engine3D.Manager;
// import the Matrix3D class
import com.fusioncharts.engine3D.Matrix3D;
// import the MathExt class
import com.fusioncharts.extensions.MathExt;
// class definition
class com.fusioncharts.engine3D.Transform3D {
	/**
	 * Constructor of Transform3D class
	 */
	private function Transform3D() {
	}
	/**
	 * ptScreenToModel method converts a 2D point to its 
	 * respective 3D point in model space. 
	 * @param	arrPt2D			the 2D point
	 * @param	objAxes			chart axes metrics 
	 * @param	objCam			camera angles 
	 * @param	arrView3D		all data item vertices in view space
	 * @param	arrMap			map of face vertices
	 * @param	mcBlockId		data item block id
	 * @param	seriesId		data item series id
	 * @param	arrModelCenter	chart model space center
	 * @returns					3D point in model space corresponding to
	 *							the supplied 2D point in screen plane
	 */
	public static function ptScreenToModel(arrPt2D:Array, objAxes:Object, objCam:Object, arrView3D:Array, arrMap:Array, mcBlockId:Number, seriesId:Number, arrModelCenter:Array):Array {
		// array of coordinates defining the face plane defined by 'arrMap'
		var arrPlane3D:Array = Manager.getPlaneDefiningPoints(arrView3D, arrMap, mcBlockId, seriesId);
		// blank array to hold the final 3D point corresponding to the 2D point provided
		var arrPt3D:Array = [];
		// coordinates of the 2D point provided
		var xScreen:Number = arrPt2D[0];
		var yScreen:Number = arrPt2D[1];
		// 
		// screen plane to view space
		var xView:Number = arrPt2D[0] - objAxes.w / 2;
		var yView:Number = objAxes.h / 2 - arrPt2D[1];
		// missing z component of the 3D point need be found;
		// concept: For any plane, a given (x,y) will correspond to an unique "z".
		var zView:Number = Manager.getPlanePointZ([xView, yView], arrPlane3D);
		// so, the final 3D point in view space is framed as an array
		var arrPt3DView:Array = [xView, yView, zView];
		// conversion of 3D point from view space to world space follows;
		// matrix for transformation from world to view space
		var viewMatrix:Matrix3D = Transform3D.getViewTransMatrix(objCam.xAng, objCam.yAng);
		// inverse the above obtained matrix to use for transformation from view to world space;
		// view to world transformation
		var arrPt3DWorld:Array = viewMatrix.inverse.concat(arrPt3DView);
		// world to model transformation
		for (var i = 0; i < 3; ++i) {
			arrPt3D.push(arrModelCenter[i] + arrPt3DWorld[i]);
		}
		// 3D point in model space returned
		return arrPt3D;
	}
	/**
	 * ptScreenToModel2DMode method converts a 2D point to its 
	 * respective 3D point in model space, when chart is in 2D
	 * view angles.
	 * @param	arrPt2D			the 2D point
	 * @param	objAxes			chart axes metrics
	 * @param	arrView3D		all data item vertices in view space
	 * @param	seriesId		data item series id
	 * @param	arrModelCenter	chart model space center
	 * @returns					3D point in model space corresponding to
	 *							the supplied 2D point in screen plane
	 */
	public static function ptScreenToModel2DMode(arrPt2D:Array, objAxes:Object, arrView3D:Array, seriesId:Number, arrModelCenter:Array):Array {
		// getting the z-origins for stacked series data items
		var arrZs:Array = Manager.getZOrigins(arrView3D);
		// blank container to hold the final 3D point coordinates
		var arrPt3D:Array = [];
		// coordinates of the 2D point provided
		var xScreen:Number = arrPt2D[0];
		var yScreen:Number = arrPt2D[1];
		//
		// screen plane to view space
		var xView:Number = arrPt2D[0] - objAxes.w / 2;
		var yView:Number = objAxes.h / 2 - arrPt2D[1];
		// missing z component of the 3D point need be found; 
		// since it 2D view, so z value for all points on "front" face planes will be same
		var zView:Number = arrZs[seriesId][2];
		// so, the final 3D point in view space is framed as an array
		var arrPt3DView:Array = [xView, yView, zView];
		// view to world transformation
		var arrPt3DWorld:Array = arrPt3DView;
		// world to model transformation
		for (var i = 0; i < 3; ++i) {
			arrPt3D.push(arrModelCenter[i] + arrPt3DWorld[i]);
		}
		// 3D point in model space returned
		return arrPt3D;
	}
	/**
	 * worldTrans method returns the set of vertices passed
	 * after their transformation from model to world space.
	 * Its a recursive function.
	 * @param	arrModel		set of vertices in model space
	 * @param	arrModelCenter	chart model center coordinates
	 * @returns					the set of vertices in world space
	 */
	public static function worldTrans(arrModel:Array, arrModelCenter:Array):Array {
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arrWorld:Array = (arguments[2] == undefined) ? [] : arguments[2];
		// number of entries to iterate over
		var num:Number = arrModel.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			// if final level to work on is yet to dig out
			if (typeof arrModel[u] == 'object') {
				// recursive call to dig to the next lower level
				arrWorld[u] = arguments.callee(arrModel[u], arrModelCenter, arrWorld[u]);
			} else {
				// level to work on found
				// all 3 (x, y and z) coordinate component shifted about the chart model center
				arrWorld[u] = arrModel[u] - arrModelCenter[u];
			}
		}
		return arrWorld;
	}
	/**
	 * viewTrans method returns the set of vertices passed
	 * after their transformation from world to view space.
	 * Its a recursive function.
	 * @param	arrWorld		set of vertices in world space
	 * @param	viewMatrix		transformation matrix
	 * @returns					the set of vertices in view space
	 */
	public static function viewTrans(arrWorld:Array, viewMatrix:Matrix3D):Array {
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arrView:Array = (arguments[2] == undefined) ? [] : arguments[2];
		var arrWorld_u:Array, arrVertex:Array;
		// number of entries to iterate over
		var num:Number = arrWorld.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			arrWorld_u = arrWorld[u];
			// if final level to work on is yet to dig out
			if (typeof arrWorld_u == 'object' && typeof arrWorld_u[0] == 'object') {
				// recursive call to dig to the next lower level
				arrView[u] = arguments.callee(arrWorld_u, viewMatrix, arrView[u]);
			} else {
				// level to work on found
				// transformation by matrix multiplication
				arrView[u] = viewMatrix.concat(arrWorld_u);
			}
		}
		return arrView;
	}
	/**
	 * screenTrans method returns the set of vertices passed
	 * after their transformation from view space to screen 
	 * plane. Its a recursive function.
	 * @param	arrView		set of vertices in view space
	 * @param	xAdjust		screen mapping x adjustment
	 * @param	yAdjust		screen mapping y adjustment
	 * @returns				the set of vertices in screen plane
	 */
	public static function screenTrans(arrView:Array, xAdjust:Number, yAdjust:Number):Array {
		var roundUp:Function = MathExt.roundUp;
		var x:Number, y:Number, arrView_u;
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arr2D:Array = (arguments[3] == undefined) ? [] : arguments[3];
		// number of entries to iterate over
		var num:Number = arrView.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			arrView_u = arrView[u];
			// if final level to work on is yet to dig out
			if (typeof arrView_u == 'object' && typeof arrView_u[0] == 'object') {
				// recursive call to dig to the next lower level
				arr2D[u] = arguments.callee(arrView_u, xAdjust, yAdjust, arr2D[u]);
			} else {
				// level to work on found
				// screen transformation
				x = roundUp(xAdjust + arrView_u[0]);
				y = roundUp(yAdjust - arrView_u[1]);
				// plot point
				arr2D[u] = [x, y];
			}
		}
		return arr2D;
	}
	/**
	 * ptWorldToView method returns the vertex passed after
	 * its transformation from world to view space.
	 * @param	arrWorld		the vertex in world space
	 * @param	viewMatrix		transformation matrix
	 * @returns					the vertex in view space
	 */
	public static function ptWorldToView(arrWorld:Array, viewMatrix:Matrix3D):Array {
		// transformation by matrix multiplication
		var arrView:Array = viewMatrix.concat(arrWorld);
		return arrView;
	}
	/**
	 * modelToViewTrans method returns the set of vertices 
	 * passed after their transformation from model to view 
	 * space. Its a recursive function.
	 * @param	arrModel		set of vertices in model space
	 * @param	arrModelCenter	chart model center coordinates
	 * @param	viewMatrix		transformation matrix
	 * @returns					the set of vertices in view space
	 */
	public static function modelToViewTrans(arrModel:Array, arrModelCenter:Array, viewMatrix:Matrix3D):Array {
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arrView:Array = (arguments[3] == undefined) ? [] : arguments[3];
		// number of entries to iterate over
		var num:Number = arrModel.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			// if final level to work on is yet to dig out
			if (typeof arrModel[u] == 'object' && typeof arrModel[u][0] == 'object') {
				// recursive call to dig to the next lower level
				arrView[u] = arguments.callee(arrModel[u], arrModelCenter, viewMatrix, arrView[u]);
			} else {
				// level to work on found
				var arrWorld_u:Array = [];
				// iterate
				for (var t = 0; t < 3; ++t) {
					// all 3 (x, y and z) coordinate component shifted about the chart model center
					arrWorld_u[t] = arrModel[u][t] - arrModelCenter[t];
				}
				// transformation by matrix multiplication
				arrView[u] = viewMatrix.concat(arrWorld_u);
			}
		}
		return arrView;
	}
	/**
	 * modelToScreenTrans method returns the set of vertices 
	 * passed after their transformation from model space to
	 * screen plane. Its a recursive function.
	 * @param	arrModel		set of vertices in model space
	 * @param	arrModelCenter	chart model center coordinates
	 * @param	viewMatrix		transformation matrix
	 * @param	xAdjust			screen mapping x adjustment
	 * @param	yAdjust			screen mapping y adjustment
	 * @returns					the set of vertices in screen plane
	 */
	public static function modelToScreenTrans(arrModel:Array, arrModelCenter:Array, viewMatrix:Matrix3D, xAdjust:Number, yAdjust:Number):Array {
		var roundUp:Function = MathExt.roundUp;
		var x:Number, y:Number;
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arrPlot:Array = (arguments[5] == undefined) ? [] : arguments[5];
		// number of entries to iterate over
		var num:Number = arrModel.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			// if final level to work on is yet to dig out
			if (typeof arrModel[u] == 'object' && typeof arrModel[u][0] == 'object') {
				// recursive call to dig to the next lower level
				arrPlot[u] = arguments.callee(arrModel[u], arrModelCenter, viewMatrix, xAdjust, yAdjust, arrPlot[u]);
			} else {
				// level to work on found
				var arrWorld_u:Array = [];
				// iterate
				for (var t = 0; t < 3; ++t) {
					// all 3 (x, y and z) coordinate component shifted about the chart model center
					arrWorld_u[t] = arrModel[u][t] - arrModelCenter[t];
				}
				// transformation by matrix multiplication
				var arrView_u = viewMatrix.concat(arrWorld_u);
				// screen transformation
				x = roundUp(xAdjust + arrView_u[0]);
				y = roundUp(yAdjust - arrView_u[1]);
				// plot point
				arrPlot[u] = [x, y];
			}
		}
		return arrPlot;
	}
	/**
	 * worldToScreenTrans method returns the set of vertices 
	 * passed after their transformation from world space to
	 * screen plane. Its a recursive function.
	 * @param	arrWorld		set of vertices in world space
	 * @param	viewMatrix		transformation matrix
	 * @param	xAdjust			screen mapping x adjustment
	 * @param	yAdjust			screen mapping y adjustment
	 * @returns					the set of vertices in screen plane
	 */
	public static function worldToScreenTrans(arrWorld:Array, viewMatrix:Matrix3D, xAdjust:Number, yAdjust:Number):Array {
		var roundUp:Function = MathExt.roundUp;
		var arrViewVertex:Array, arrWorld_u, x:Number, y:Number;
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arrView:Array = (arguments[4] == undefined) ? [] : arguments[4];
		// number of entries to iterate over
		var num:Number = arrWorld.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			//
			arrWorld_u = arrWorld[u];
			// if final level to work on is yet to dig out
			if (typeof arrWorld_u == 'object' && typeof arrWorld_u[0] == 'object') {
				// recursive call to dig to the next lower level
				arrView[u] = arguments.callee(arrWorld_u, viewMatrix, xAdjust, yAdjust, arrView[u]);
			} else {
				// level to work on found
				// transformation by matrix multiplication
				arrViewVertex = viewMatrix.concat(arrWorld_u);
				// screen transformation
				x = roundUp(xAdjust + arrViewVertex[0]);
				y = roundUp(yAdjust - arrViewVertex[1]);
				// plot point
				arrView[u] = [x, y];
			}
		}
		return arrView;
	}
	/**
	 * worldToScreenTrans_entryWise method returns the set of  
	 * vertices passed after their transformation from world 
	 * space to screen plane, taking care of blank entries in
	 * the vertices set. Its a recursive function.
	 * @param	arrWorld		set of vertices in world space
	 * @param	viewMatrix		transformation matrix
	 * @param	xAdjust			screen mapping x adjustment
	 * @param	yAdjust			screen mapping y adjustment
	 * @returns					the set of vertices in screen plane
	 */
	public static function worldToScreenTrans_entryWise(arrWorld:Array, viewMatrix:Matrix3D, xAdjust:Number, yAdjust:Number):Array {
		var roundUp:Function = MathExt.roundUp;
		var arrViewVertex:Array, arrWorld_u, x:Number, y:Number;
		// container to hold the transformed points created if not a recursive call whereby the container is passed
		var arrView:Array = (arguments[4] == undefined) ? [] : arguments[4];
		// number of entries to iterate over
		var num:Number = arrWorld.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			//
			arrWorld_u = arrWorld[u];
			// if final level to work on is yet to dig out
			if (typeof arrWorld_u == 'object' && Transform3D.firstData_typeof(arrWorld_u) == 'object') {
				// recursive call to dig to the next lower level
				arrView[u] = arguments.callee(arrWorld_u, viewMatrix, xAdjust, yAdjust, arrView[u]);
			} else {
				// level to work on found
				if (arrWorld_u == undefined) {
					continue;
				}
				// transformation by matrix multiplication              
				arrViewVertex = viewMatrix.concat(arrWorld_u);
				// screen transformation
				x = roundUp(xAdjust + arrViewVertex[0]);
				y = roundUp(yAdjust - arrViewVertex[1]);
				// plot point
				arrView[u] = [x, y];
			}
		}
		return arrView;
	}
	/**
	 * worldToViewAndScreenTrans method populates
	 * both passed blank arrays after view and screen 
	 * transformations of the set of vertices passed.
	 * Its a recursive function.
	 * @param	arrWorld		set of vertices in world space
	 * @param	viewMatrix		transformation matrix
	 * @param	xAdjust			screen mapping x adjustment
	 * @param	yAdjust			screen mapping y adjustment
	 * @param	arrView			blank array to get populated with 
	 *							transformed vertices to view space
	 * @param	arrScreen		blank array to get populated with 
	 *							transformed vertices to screen plane
	 */
	public static function worldToViewAndScreenTrans(arrWorld:Array, viewMatrix:Matrix3D, xAdjust:Number, yAdjust:Number, arrView:Array, arrScreen:Array) {
		var roundUp:Function = MathExt.roundUp;
		var arrViewVertex:Array, arrWorld_u, x:Number, y:Number;
		// number of entries to iterate over
		var num:Number = arrWorld.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			//
			arrWorld_u = arrWorld[u];
			// if final level to work on is yet to dig out
			if (typeof arrWorld_u == 'object' && typeof arrWorld_u[0] == 'object') {
				// blank sub-containers created to pass over in recursive call
				arrView[u] = [];
				arrScreen[u] = [];
				// recursive call to dig to the next lower level
				arguments.callee(arrWorld_u, viewMatrix, xAdjust, yAdjust, arrView[u], arrScreen[u]);
			} else {
				// level to work on found
				// transformation by matrix multiplication
				arrViewVertex = viewMatrix.concat(arrWorld_u);
				// view tarnsformed 3D point
				arrView[u] = arrViewVertex;
				// screen transformation
				x = roundUp(xAdjust + arrViewVertex[0]);
				y = roundUp(yAdjust - arrViewVertex[1]);
				// plot point
				arrScreen[u] = [x, y];
			}
		}
	}
	/**
	 * worldToViewAndScreenTrans_entryWise method populates
	 * both passed blank arrays after view and screen 
	 * transformations of the set of vertices passed, taking
	 * care of blank entries in the vertices set. Its a 
	 * recursive function.
	 * @param	arrWorld		set of vertices in world space
	 * @param	viewMatrix		transformation matrix
	 * @param	xAdjust			screen mapping x adjustment
	 * @param	yAdjust			screen mapping y adjustment
	 * @param	arrView			blank array to get populated with 
	 *							transformed vertices to view space
	 * @param	arrScreen		blank array to get populated with 
	 *							transformed vertices to screen plane
	 */
	public static function worldToViewAndScreenTrans_entryWise(arrWorld:Array, viewMatrix:Matrix3D, xAdjust:Number, yAdjust:Number, arrView:Array, arrScreen:Array) {
		var roundUp:Function = MathExt.roundUp;
		var arrViewVertex:Array, arrWorld_u, x:Number, y:Number;
		// number of entries to iterate over
		var num:Number = arrWorld.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			//
			arrWorld_u = arrWorld[u];
			// if final level to work on is yet to dig out
			if (typeof arrWorld_u == 'object' && Transform3D.firstData_typeof(arrWorld_u) == 'object') {
				arrView[u] = [];
				arrScreen[u] = [];
				// recursive call to dig to the next lower level
				arguments.callee(arrWorld_u, viewMatrix, xAdjust, yAdjust, arrView[u], arrScreen[u]);
			} else {
				// level to work on found
				if (arrWorld_u == undefined) {
					continue;
				}
				// transformation by matrix multiplication              
				arrViewVertex = viewMatrix.concat(arrWorld_u);
				// view transformed 3D point
				arrView[u] = arrViewVertex;
				// screen transformation
				x = roundUp(xAdjust + arrViewVertex[0]);
				y = roundUp(yAdjust - arrViewVertex[1]);
				// plot point
				arrScreen[u] = [x, y];
			}
		}
	}
	/**
	 * lightTrans method returns the set of vertices passed
	 * after their transformation from world to light space.
	 * @param	arrWorld			set of vertices in world space
	 * @param	transMatrixLight	transformation matrix
	 * @returns						the set of vertices in light space
	 */
	public static function lightTrans(arrWorld:Array, transMatrixLight:Matrix3D):Array {
		var arrView:Array = Transform3D.viewTrans(arrWorld, transMatrixLight);
		return arrView;
	}
	/**
	 * firstData_typeof method finds the first defined entry 
	 * in the passed array and returns back its data type.
	 * @param	arrData		data container to check in
	 * @returns				data type of the first defined entry
	 */
	private static function firstData_typeof(arrData:Array):String {
		// number of entries to iterate over
		var num:Number = arrData.length;
		// iterate
		for (var u = 0; u < num; ++u) {
			// if entry at this index of the array is defined
			if (arrData[u] != undefined) {
				// check for its data type and return the same
				return typeof arrData[u];
			}
		}
		// if no defined entry found ... return null
		return null;
	}
	/**
	 * getViewTransMatrix method returns the matrix for 
	 * multiplication so as to remap any 3D point when
	 * rotated by the passed angles.
	 * @param	angX	rotation about x-axis
	 * @param	angY	rotation about y-axis
	 * @returns			transformation matrix
	 */
	public static function getViewTransMatrix(angX:Number, angY:Number):Matrix3D {
		/*
		 In this "Engine", all transformation between world space and view space (or light space) works to rotate
		 the OBJECT itself to the observer fixed to the view port, while 3D-axes orientation of world space and 
		 view space are both same to observer (like, x-axis directs to right, y-axis upwards and z-axis towards 
		 the observer).
		 
		 For rotation of axes (w.r.t. observer), we need to rotate about x-axis first and then rotate 
		 about y-axis, to maintain verticals of object as vertical or parallel to y-axis.
		 But, we will rotate OBJECT keeping axes static. So we need to take the	opposite course of actions, 
		 i.e. rotation about y-axis first and then about x-axis.
		 */
		// camera orientation angles
		var cameraAngX:Number = MathExt.toRadians(angX);
		var cameraAngY:Number = MathExt.toRadians(angY);
		// matrix entries
		var a:Number, b:Number, c:Number, d:Number, e:Number, f:Number, g:Number, h:Number, i:Number;
		//----ABOUT Y-AXIS ------//
		// setting matrix entries to have a unit matrix
		a = e = i = 1;
		b = c = d = f = g = h = 0;
		// manipulating specific entries so as to achieve the matrix for rotation about y-axis
		a = Math.cos(cameraAngY);
		c = -Math.sin(cameraAngY);
		g = Math.sin(cameraAngY);
		i = Math.cos(cameraAngY);
		// new Matrix3D instance created passing its 9 entries (3 x 3 matrix)
		var matrixY:Matrix3D = new Matrix3D(a, b, c, d, e, f, g, h, i);
		//----ABOUT X-AXIS ------//
		// resetting matrix entries to have a unit matrix
		a = e = i = 1;
		b = c = d = f = g = h = 0;
		// manipulating specific entries so as to achieve the matrix for rotation about x-axis
		e = Math.cos(cameraAngX);
		f = Math.sin(cameraAngX);
		h = -Math.sin(cameraAngX);
		i = Math.cos(cameraAngX);
		// new Matrix3D instance created passing its 9 entries (3 x 3 matrix)
		var matrixX:Matrix3D = new Matrix3D(a, b, c, d, e, f, g, h, i);
		// matrix multiplication (concatenation): [matrixY] x [matrixX] (the order required); a new matrix returns
		var matrixYX:Matrix3D = matrixY.concatGeom(matrixX);
		// return
		return matrixYX;
	}
}
