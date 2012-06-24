package facepoint;

import java.util.List;

import android.util.Log;
import android.graphics.Point;

public class calculateGeometry {
	
	public calculateGeometry(List<PointCluster> cluster)
	{
		// Distance 0 zu 3
		double dist1 = calcDistance(cluster.get(0).getCenter(), cluster.get(3).getCenter());
			
		// Distance 4 zu 6
		double dist2 = calcDistance(cluster.get(4).getCenter(), cluster.get(6).getCenter());
		
		// Distance 7 zu 8
		double dist3 = calcDistance(cluster.get(7).getCenter(), cluster.get(8).getCenter());
		
		// Trapez 0, 1, 2 und 3
		double trapez1 = calcArea(cluster.get(0).getCenter(),
				cluster.get(3).getCenter(),
				cluster.get(2).getCenter(),
				cluster.get(1).getCenter());
		
		// Triangle 4, 5 und 6
		double tri1 = calcTriangle(cluster.get(4).getCenter(),
				cluster.get(5).getCenter(),
				cluster.get(6).getCenter());
		
		// Triangle 5, 7 und 8
		double tri2 = calcTriangle(cluster.get(5).getCenter(),
				cluster.get(7).getCenter(),
				cluster.get(8).getCenter());

		// Triangle 7, 8 und 9
		double tri3 = calcTriangle(cluster.get(7).getCenter(),
				cluster.get(8).getCenter(),
				cluster.get(9).getCenter());

		Log.i("calculateGeometry::calculateGeometry", "Distanzen: " + dist1 + " " + dist2 + " " + dist3);
		Log.i("calculateGeometry::calculateGeometry", "Dreiecke: " + tri1 + " " + tri2 + " " + tri3);
		Log.i("calculateGeometry::calculateGeometry", "Trapez: " + trapez1);
	}
	
	/**
	 * calculate the distance of two points
	 * 
	 * @param int x of point 1
	 * @param int y of point 1
	 * @param int x of point 2
	 * @param int y of point 2
	 * @return double distance 
	 */
	private double calcDistance(org.opencv.core.Point p1,
			org.opencv.core.Point p2) {
		// wurzel((X1 - X2)^2 + (Y1 - Y2)^2)
		double x1 = p1.x, x2 = p2.x, y1 = p1.y, y2=p2.y;
		double retval = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));

		// TODO: two decimalPlaces only
		// retval = setTwoDecimalPlaces(retval, 2);

		return retval;
	}

	/**
	 * calculate the area of a triangle of three dots
	 * 
	 * TODO: Doku calcTriangle
	 * @param colIdValue1
	 * @param colIdValue2
	 * @param colIdValue3
	 * @return
	 */
	private double calcTriangle(org.opencv.core.Point p1,
			org.opencv.core.Point p2,
			org.opencv.core.Point p3)
	{
		double x1 = p1.x, x2 = p2.x, x3 = p3.x;
		double y1 = p1.y, y2 = p2.y, y3 = p3.y;
		
		// A = 1/2 |x1*(y2-y3)+x2(y3-y1)+x3(y1-y2)|
		double retval = Math.abs(0.5 * (x1*(y2-y3)+x2*(y3-y1)+x3*(y1-y2))); 

		// TODO: two decimalPlaces only
		// setTwoDecimalPlaces(retval, 0);
		return retval;
	}
	
	/**
	 * calculate the area of four dots
	 * TODO: Doku calcArea
	 * @return
	 */
	private double calcArea(org.opencv.core.Point p1,
			org.opencv.core.Point p2,
			org.opencv.core.Point p3,
			org.opencv.core.Point p4)
	{
		double x1 = p1.x, x2 = p2.x, x3 = p3.x, x4 = p4.x;
		double y1 = p1.y, y2 = p2.y, y3 = p3.y, y4 = p4.y;

		// A = 1/2 |(y1 -y3)*(x4-x2)+(y2-y4)*(x1-x3)| // gaussschen Trapezformel
		double retval = 0.5* ( (y2+y3)*(x2-x3)+(y3+y4)*(x3-x4)-(y2+y1)*(x2-x1)-(y1+y4)*(x1-x4) );
		

		// TODO: two decimalPlaces only
		// setTwoDecimalPlaces(retval, 0);
		return retval;
	}
}
