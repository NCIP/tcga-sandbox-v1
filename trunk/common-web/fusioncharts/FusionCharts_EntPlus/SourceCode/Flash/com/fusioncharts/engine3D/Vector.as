/**
* @class Vector
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* Vector class groups a bunch of vector mathematics
* related functions.
*/
// class definition
class com.fusioncharts.engine3D.Vector {
	/**
	 * Constructor of Vector class.
	 */
	private function Vector() {
	}
	/**
	 * getVectorAB method is used to get the vector
	 * between 2 points in 3D space.
	 * @param	A	starting position vector (array)
	 * @param	B	ending position vector (array)
	 * @returns		resultant vector (array)
	 */
	public static function getVectorAB(A:Array, B:Array):Array {
		// Operation: vAB = vB - vA
		// resultant vector array created
		var arrVector:Array = [];
		// iterated thrice, for x, y and z
		for (var i = 0; i < 3; ++i) {
			// respective elements subtracted and filled in
			arrVector[i] = B[i] - A[i];
		}
		// resultant returned
		return arrVector;
	}
	/**
	 * mod method returns the modulus of a vector.
	 * @param	arrV	vector (array) whose modulus is required
	 * @returns			modulus of the vector (number)
	 */
	public static function mod(arrV:Array):Number {
		// initialised to zero
		var modValueSq:Number = 0;
		// iterated thrice, for x, y and z
		for (var i = 0; i < 3; ++i) {
			// square of each entry added up
			modValueSq += arrV[i] * arrV[i];
		}
		// square root returned
		return Math.sqrt(modValueSq);
	}
	/**
	 * AdotB method returns the dot product of 2 vectors.
	 * @param	A	vector 1 (array)
	 * @param	B	vector 2 (array)
	 * @returns		dot product (number)
	 */
	public static function AdotB(A:Array, B:Array):Number {
		// to calculate the product value
		var product:Number = 0;
		// iterated thrice, for x, y and z
		for (var i = 0; i < 3; ++i) {
			// respective entries from both multiplied and added up
			product += A[i] * B[i];
		}
		// product value returned
		return product;
	}
	/**
	 * modAxB method returns the modulus of a vector obtained
	 * from cross product of 2 vectors.
	 * @param	A	vector 1 (array)
	 * @param	B	vector 2 (array)
	 * @returns		modulus of cross product (number)
	 */
	public static function modAxB(A:Array, B:Array):Number {
		// dot product of the 2 vectors
		var AdotB:Number = Vector.AdotB(A, B);
		// modulus of the 2 vectors
		var modA:Number = Vector.mod(A);
		var modB:Number = Vector.mod(B);
		// formula's first term under square root
		var a:Number = Number(String(Math.pow(modA * modB, 2)));
		// formula's second term under square root
		var b:Number = Number(String(Math.pow(AdotB, 2)));
		// formula applied and resultant returned
		return Math.sqrt(a - b);
	}
	/**
	 * AxB method returns the vector obtained from cross
	 * product of 2 vectors provided in order.
	 * @param	A	vector 1 (array)
	 * @param	B	vector 2 (array)
	 * @returns		cross product vector (array)
	 */
	public static function AxB(A:Array, B:Array):Array {
		var arrVector:Array = [];
		// set arrays to execute determinants;
		A.push(A[0], A[1]);
		B.push(B[0], B[1]);
		// row arrays
		var a:Array, b:Array;
		// iterated thrice, for x, y and z
		for (var i = 0; i < 3; ++i) {
			// first row formed
			a = [A[i + 1], A[i + 2]];
			// second row formed
			b = [B[i + 1], B[i + 2]];
			// determinant returned is the required vector component
			arrVector[i] = Vector.det2x2(a, b);
		}
		// croos product vector returned
		return arrVector;
	}
	/**
	 * unitVectorAxB method returns the direction vector of
	 * the vector obtained from cross product of 2 vectors 
	 * provided in order.
	 * @param	A	vector 1 (array)
	 * @param	B	vector 2 (array)
	 * @returns		cross product vector (array)
	 */
	public static function unitVectorAxB(A:Array, B:Array):Array {
		// modulus of the vectors
		var modAxB:Number = Vector.modAxB(A, B);
		// cross product vector 
		var arrAxB:Array = Vector.AxB(A, B);
		// checking for zero vector 
		if (modAxB == 0) {
			// unit vector of cross product impossible
			return;
		}
		// unit vector array created  
		var unitVector:Array = [];
		// iterated thrice, for x, y and z
		for (var i = 0; i < 3; ++i) {
			// getting respective vector component
			unitVector.push(arrAxB[i] / modAxB);
		}
		// unit vector returned
		return unitVector;
	}
	/**
	 * det2x2 method returns the determinant of 2 x 2 matrix.
	 * @param	a	row 1 (array)
	 * @param	b	row 2 (array)
	 * @returns		determinant (number)
	 */
	public static function det2x2(a:Array, b:Array):Number {
		//determinant of order 2
		return (a[0] * b[1] - a[1] * b[0]);
	}
}
