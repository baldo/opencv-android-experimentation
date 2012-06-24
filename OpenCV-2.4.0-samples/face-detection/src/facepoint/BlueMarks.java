package facepoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import android.util.Log;

/**
 * Description: Detects the blue points in the frame<br>
 * Author: T. Tews & H. Faasch <br>
 * Last change: 02.07.2010 <br>
 * Version: 1.2 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class BlueMarks {
	long minimumRegions; // Minimum number of blue cluster that has to be found
	long startThreshold;// Start of threshold; will be reduced until
	long stopThreshold;// FIXME Start of threshold; will be reduced until
	long stepThreshold;// FIXME Start of threshold; will be reduced until
	// enough marks have been found
	Mat image; // Actual image
	long width, height;
	Rect frameWindow;// Region to be searched for marks; important if other
	List<PointCluster> cluster = new ArrayList<PointCluster>(); // cluster points
	Set<PointCluster> deleteSet = new HashSet<PointCluster>(); // Delete
	// points
	// list

	Point searchLostZeroClusterWindow;
	Point searchLostOneClusterWindow;

	// blue regions are within the entire picture
	public void setBlueMarksValues(Mat image, long startThr, long stopThr, long stepThr, long minimumMark, Rect frameWindow) {
		this.image = image;
		this.width = image.width();
		this.height = image.height();
		minimumRegions = minimumMark;
		startThreshold = startThr;
		stopThreshold = stopThr;
		stepThreshold = stepThr;
		this.frameWindow = frameWindow;
	}

	/**
	 * Try to locate the minimum number of blue marks. The start threshold will
	 * be reduced until enough marks have been found. Adjusting points will be
	 * merged to a single region.
	 * 
	 * @return List of cluster representing a blue mark
	 */
	public List<PointCluster> firstLocate() {

		long threshold = startThreshold;
		//cluster.removeAll(cluster);
		
		while (cluster.size() < minimumRegions) {
			//System.out.println("BM: " + threshold);

			if (threshold < stopThreshold) {
				System.out.println("No chance to find blue marks!");
				return null;
			}

			threshold -= stepThreshold;

			cluster = new ArrayList<PointCluster>();
			

//			for (long y = frameWindow.y; y < frameWindow.height; y++)
//				for (long x = frameWindow.x; x < frameWindow.width; x++) {
					for (long y = frameWindow.y; y < (frameWindow.y + frameWindow.height); y++)
						for (long x = frameWindow.x; x < (frameWindow.x + frameWindow.width); x++) {

							
					long sig = significance1(x, y);
					// System.out.println("BM: SIG: " + sig);

					if (sig >= threshold) {
						PointCluster inserted = null; // create instance of PC
						for (PointCluster pc : cluster) {
							if (pc.isNeighbor(x, y)) {
								if (inserted == null) { // include new data
														// inside the new
														// cluster
									pc.insert(x, y, significance1(x, y));
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
//							 System.out.println("BM: New cluster: " +
//							 cluster.size()
//							 + " " + x + "," + y);
						}
						for (PointCluster pc : deleteSet)
							cluster.remove(pc);
						deleteSet.clear();
					}
				}
		}
		
		
		
	
		return cluster;
	}

	/**
	 * Try to track a cluster along the sequence of frames.
	 * 
	 * @param region
	 *            Pointcluster representing a blue mark.
	 */
	public void follow(PointCluster region) {

		long[] locxarr = { 3,// 0
				3, 3, 3, 3, 3, // 1
				3, 3, 3, 3, 3, // 6
				3, 2, 2, 2, 3, // 11
				3, 3, 3, 3 };// 16

		long[] locyarr = { 3,// 0
				3, 3, 3, 3, 3, // 1
				3, 8, 3, 3, 3, // 6
				3, 3, 1, 3, 1, // 11
				3, 3, 3, 3 };// 16

		long loctolx = locxarr[region.nr];
		long loctoly = locyarr[region.nr];

		long th = region.minSig;
		//System.out.println("BM: Region.minSig " + region.minSig);

		// calc direction of point
		region.getPredictCenter();
		PointCluster newCluster = null;
		long minPoints = 1;

		long size = region.getSize();

		while (newCluster == null
				|| (newCluster.getSize() < minPoints | newCluster.getSize() > 80)) {

			// Thr. modifer
			if (size < 4 & th > 1) {
				th -= 1;
				if (th <= 0)
					th = 1;
				// System.out.println("(" + seq + ") Reduced to " + th +
				// " (Region "
				// + region.nr + ", size=" + size + ")");
			} else if (size > 80) {
				th += 1;
				// System.out.println("(" + seq + ") Enhanced to " + th +
				// " (Region "
				// + region.nr + ", size=" + size + ")");
			}

			newCluster = null;
			for (long y = region.minRow - loctoly; y <= region.maxRow + loctoly; y++) {
				for (long x = region.minCol - loctolx; x <= region.maxCol
						+ loctolx; x++) {
//					 System.out.println("BM1: " + y + ", "+ region.minRow +
//					 ", " + region.maxRow + ", " + loctoly);
//					 System.out.println("BM2: " + y + ", "+ region.minCol +
//					 ", " + region.maxCol + ", " + loctolx);

					long sig = significance1(x, y);
					// System.out.println("BM: SIG: " + sig);
					// System.out.print(f.format(sig) + ", ");

					if (sig >= th) {
						if (newCluster == null) {
							newCluster = new PointCluster(new Point(x, y), sig);
						} else {
							newCluster.insert(x, y, sig);
						}
					}
				}
			}

			if (newCluster != null)
				size = newCluster.getSize();
			else
				size = 0;

			 //System.out.println("BM: TH: " + th + " size " + size +
			// " minPoints " + minPoints);
			if (th == 1 & size < minPoints) { // old
				// if (size < minPoints | size > 80) { //TODO: Clustergr��e
				// ver�ndern...???
				break;
			}
		} // while

		region.minSig = th;
		region.modify(newCluster);
		//cluster = null;
		
		if (newCluster == null)  {
			searchMissingCluster(region);
		} else {

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
	private long significance1(long x, long y) {
		double[] pixel = image.get((int) y, (int) x); // FIXME typen aufraeumen
		

		long red = (long)pixel[0]; // val >> 16 & 0xff; // Rotanteil
		long green = (long)pixel[1]; // val >> 8 & 0xff; // Gruen
		long blue = (long)pixel[2]; // val & 0xff; // blau

		// Farbspektrum

		long sig = 0;
		if (blue > red)
			sig = blue - red;
		if (blue > green)
			sig += blue - green;

		return sig;
	}
	
	
	public void searchMissingCluster(PointCluster region) {

		long[] locxarr = { 3,// 0
				3, 3, 3, 3, 3, // 1
				3, 3, 3, 3, 3, // 6
				3, 2, 2, 2, 3, // 11
				3, 3, 3, 3 };// 16

		long[] locyarr = { 3,// 0
				3, 3, 3, 3, 3, // 1
				3, 8, 3, 3, 3, // 6
				3, 3, 1, 3, 1, // 11
				3, 3, 3, 3 };// 16

		long loctolx = locxarr[region.nr];
		long loctoly = locyarr[region.nr];

		long th = region.minSig;
		//System.out.println("BM: Region.minSig " + region.minSig);

		// calc direction of point
		region.getPredictCenter();
		
		if(region.nr != 0 && region.nr != 1) {
			//long disX = region.distanceZeroCluster.x + PointCluster.zeroClusterCenter.x;
			//long disY = region.distanceZeroCluster.y + PointCluster.zeroClusterCenter.y;
			searchLostZeroClusterWindow = new Point(region.distanceZeroCluster.x + PointCluster.zeroClusterCenter.x, region.distanceZeroCluster.y + PointCluster.zeroClusterCenter.y);
			searchLostOneClusterWindow  = new Point(region.distanceOneCluster.x + PointCluster.oneClusterCenter.x, region.distanceOneCluster.y + PointCluster.oneClusterCenter.y);
			
			System.out.println("BM: " + region.nr + ": (" + region.distanceZeroCluster.x + ", " + PointCluster.zeroClusterCenter.x + "," + PointCluster.zeroClusterCenter.y + ", " + region.distanceZeroCluster.y + ")");
			//System.out.println("BM: " + region.nr + ": ("+ distanceOneCluster[region.nr].x + "," + distanceOneCluster[region.nr].y + ")");
		
		PointCluster newCluster = null;
		long minPoints = 1;

		long size = region.getSize();

		while (newCluster == null
				|| (newCluster.getSize() < minPoints | newCluster.getSize() > 80)) {

			// Thr. modifer
			if (size < 4 & th > 1) {
				th -= 1;
				if (th <= 0)
					th = 1;
				// System.out.println("(" + seq + ") Reduced to " + th +
				// " (Region "
				// + region.nr + ", size=" + size + ")");
			} else if (size > 80) {
				th += 1;
				// System.out.println("(" + seq + ") Enhanced to " + th +
				// " (Region "
				// + region.nr + ", size=" + size + ")");
			}

			newCluster = null;
			for (long y = Math.round(searchLostZeroClusterWindow.y) - loctoly; y <= searchLostZeroClusterWindow.y + loctoly; y++) {
				for (long x = Math.round(searchLostZeroClusterWindow.x) - loctolx; x <= searchLostZeroClusterWindow.x
						+ loctolx; x++) {
//					 System.out.println("BM1: " + y + ", "+ region.minRow +
//					 ", " + region.maxRow + ", " + loctoly);
//					 System.out.println("BM2: " + y + ", "+ region.minCol +
//					 ", " + region.maxCol + ", " + loctolx);

					long sig = significance1(x, y);
					// System.out.println("BM: SIG: " + sig);
					// System.out.print(f.format(sig) + ", ");

					if (sig >= th) {
						if (newCluster == null) {
							newCluster = new PointCluster(new Point(x, y), sig);
						} else {
							newCluster.insert(x, y, sig);
							System.out.println("BM: insert");
						}
					}
				}
			}

			if (newCluster != null)
				size = newCluster.getSize();
			else
				size = 0;

			 //System.out.println("BM: TH: " + th + " size " + size +
			// " minPoints " + minPoints);
			if (th == 1 & size < minPoints) { // old
				// if (size < minPoints | size > 80) { //TODO: Clustergr��e
				// ver�ndern...???
				break;
			}
		} // while

		region.minSig = th;
		region.modify(newCluster);
		//cluster = null;
		}
	}
	
	
	
	
}
