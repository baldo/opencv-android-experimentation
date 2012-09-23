package facepoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class SearchPattern {
	
	int significance[][];
	long threshold;
	List<PointCluster> cluster; // cluster points
	Set<PointCluster> deleteSet;
	long minimumRegions; // Minimum number of blue cluster that has to be found
	Mat image; // Actual image
	int x, y;
	
/**
 * Try to locate the minimum number of blue marks. The start threshold will
 * be reduced until enough marks have been found. Adjusting points will be
 * merged to a single region.
 * 
 * @return List of cluster representing a blue mark
 */
public List<PointCluster> searchPixel(long tshold, Mat img, int xPixel, int yPixel) {	
	
	threshold = tshold;
	x = xPixel;
	y = yPixel;
	image = img;
	//for (int y = frameWindow.y; y < (frameWindow.y + frameWindow.height); y++)
		//for (int x = frameWindow.x; x < (frameWindow.x + frameWindow.width); x++) {

					
			int sig = significance2(x, y);
			significance[x][y] = sig;
			// System.out.println("BM: SIG: " + sig);

			if (sig >= threshold) {
				PointCluster inserted = null; // create instance of PC
				for (PointCluster pc : cluster) {
					if (pc.isNeighbor(x, y)) {
						if (inserted == null) { // include new data
												// inside the new
												// cluster
							pc.insert(x, y, sig);
							inserted = pc;
						} else { // join cluster
							inserted.join(pc);
							deleteSet.add(pc);
							System.out.println("Joined");
						}
						// System.out.println("--- Inserted: " + x + ","
						// + y);
					}
				}

				if (inserted == null & cluster.size() < minimumRegions) {
					final Point point = new Point(x, y);
					cluster.add(new PointCluster(point, sig));
//					 System.out.println("BM: New cluster: " +
//					 cluster.size()
//					 + " " + x + "," + y);
				}
				for (PointCluster pc : deleteSet)
					cluster.remove(pc);
				deleteSet.clear();
			}
		}

}

/**
 * Compute significance of a single pixel. A simple measure is used to
 * determine the significance of a single pixel: Just add the posiive
 * differences between blue and red/green.
 * 
 * @param x
 * @param y
 * @return Value of significance of the selected pixel
 */
private int significance2(long x, long y) {
	double[] pixel = image.get((int) y, (int) x); 
	

	long red = (long)pixel[0]; // val >> 16 & 0xff; // Rotanteil
	long green = (long)pixel[1]; // val >> 8 & 0xff; // Gruen
	long blue = (long)pixel[2]; // val & 0xff; // blau

	// Farbspektrum

	long sig = 0;
	if (blue > red)
		sig = blue - red;
	if (blue > green)
		sig += blue - green;

	return (int)sig;
}

}
