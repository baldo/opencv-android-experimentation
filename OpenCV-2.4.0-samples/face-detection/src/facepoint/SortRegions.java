package facepoint;
import java.util.ArrayList;
import java.util.List;


/**
 * Sort detected clusters on the face by x-coordinate.
 * 
 * @param lower
 *            Lower bound of regions to be sorted
 * @param upper
 *            Upper bound of regions to be sorted
 */
public class SortRegions {

	static final int[][] sortRegionValues = { { 0, 0 }, { 0, 0 }, { 0, 0 }};
	static List<PointCluster> cluster;
//	static Point[] cluster = { new Point(100, 200), new Point(120, 210), new Point(180, 460), new Point(80, 175), new Point(220, 160),
//		new Point(100, 320), new Point(170, 300), new Point(180, 170), new Point(230, 360), new Point(180, 350)};
	static int cNumber; //= cluster.length;
	static int [] fourSmallestY;
	//static int [] fourSmallestX;
	static double [] clusterValesY;
	static double [] clusterValesY2;
	static double [] clusterValesX;
	static int [] clusterSorted;
	static int [] secondSearch;
	static int smallValue = 0;
	//private static PointCluster z;

	public List<PointCluster> sortRegionsCalc(int cNo, int[][] sRValue, List<PointCluster> clust) { // int lower, int upper
		cluster = clust;
		cNumber = cNo;
	
		cluster = sortlist(cluster);
				
		return cluster; 
	}
	
	public static List<PointCluster> sortlist(List<PointCluster> clust) {
		cluster = clust;
		//cNumber = cNo;


		boolean sorted;
		//PointCluster z;

		double tmpMin = 0;
		clusterSorted = new int[cNumber];
		fourSmallestY = new int[cNumber];
		//fourSmallestX = new int[cNumber];
		clusterValesY = new double[cNumber];
		clusterValesY2 = new double[cNumber];
		clusterValesX = new double[cNumber];
		secondSearch = new int[cNumber];

		//	// tmpMin = cluster.get(k).getCenter().x + cluster.get(k).getCenter().y;
		tmpMin = cluster.get(0).getCenter().x + cluster.get(0).getCenter().y;
		//tmpMin = cluster [0].x + cluster [0].y;

		//System.out.println("SR: Test");

		do {

			sorted = true;

			//System.out.println("SR: TestK");
			//System.out.println(cNumber); //10
			for(int k = 0; k < cNumber; k ++ ) // search for the smallest cluster
			{
				//System.out.println("SR: X" + k + ": "+ cluster.get(k).getCenter().x + ", Y: "+ cluster.get(k).getCenter().y + ", TMPMIN: " + tmpMin);
				if ((cluster.get(k).getCenter().x + cluster.get(k).getCenter().y) < tmpMin) { // search for the smallest cluster
					tmpMin = cluster.get(k).getCenter().x + cluster.get(k).getCenter().y;
					clusterSorted[0] = k;
					//System.out.println("SR_Smallest: X" + k + ": "+ cluster.get(k).getCenter().x + ", Y: "+ cluster.get(k).getCenter().y);
					sorted = false;
				}
				//System.out.println("SR: TestL");

			}	

		} while (!sorted);

		//System.out.println("SR: Test2");

		smallValue = clusterSorted[0];
		//System.out.println("SR: Smallest: " + clusterSorted[0]);

		boolean unsorted = true;

		for (int g = 0; g < cNumber ; g++){
			clusterValesY [g] = cluster.get(g).getCenter().y;
			clusterValesY2 [g] = cluster.get(g).getCenter().y;
			clusterValesX [g] = cluster.get(g).getCenter().x;
			fourSmallestY[g] = g;
			//fourSmallestX[g] = g; //TODO brauch ich das hier?
		}


		do {	

			unsorted = true;
			for (int g = 0; g < cNumber-1; g++) {	

				if (clusterValesY [g] > clusterValesY [g+1]) {   // search for the four smallest y => eye brow points                   
					double temp  = clusterValesY [g];
					clusterValesY [g]  = clusterValesY [g+1];
					clusterValesY [g+1]     = temp;
					//System.out.println(clusterValesY [g] + ">"+ clusterValesY [g+1]);

					//System.out.println("T> " + g);
					int temp2  = fourSmallestY[g]; 
					fourSmallestY[g]  = fourSmallestY[g+1];
					fourSmallestY[g+1]     = temp2;

					unsorted = false;
				}
				else {
					//System.out.println("T2> " + g);
					fourSmallestY[g] = fourSmallestY[g]; // is sorted
				}
				if (g == cNumber-1) { // print last number
					fourSmallestY[g+1] = fourSmallestY[g+1];
				}
			}
		} while (!unsorted);

		//for(int j = 0; j < cNumber; j++)   //einsortieren der anderen drei Cluster in die Liste
			//System.out.println("sortiert nach y Werten> " +j + " "+ fourSmallestY[j]); // Nach y-Werten sortiert

		//System.out.println("TR3> "+ fourSmallestY[9]);
		int lock = 0;
		for(int j = 0; j < cNumber; j++)  { //einsortieren der anderen drei Cluster in die Liste
			if((fourSmallestY[j] != clusterSorted[0]) && (lock == 0)){

				if (j != (cNumber-1)) { 
					clusterSorted[j+1] = fourSmallestY[j];
					//System.out.println("ST3> " + j + " "+ clusterSorted[j+1]);
				} else {
					//if (fourSmallestY[j] != smallValue)
					clusterSorted[j] = fourSmallestY[j];
				}
			} else { // found the smallest value
				if (j != (cNumber-1)) // not to far
					clusterSorted[j+1] = fourSmallestY[j+1]; //include all values
				lock++; //found value
			}

		}
		//					for(int j = 0; j < cNumber; j++ )                                 //found the three points
		//						System.out.println("ST> " + j + " "+ clusterSorted[j]);

		do {	

			for (int g = 1; g < 3; g++) {	// sortieren der eye brow cluster nach der richtigen Reihenfolge
				unsorted = true;

				//System.out.println("TR> " + clusterSorted[g] + ", " + clusterSorted[g+1]);

				if (clusterValesX [clusterSorted[g]] > clusterValesX [clusterSorted[g+1]]) {   // search for the four smallest y => eye brow points                   

					int temp2  = clusterSorted[g];  // einsortieren der Werte
					clusterSorted[g]  = clusterSorted[g+1];
					clusterSorted[g+1]     = temp2;

					unsorted = false;
				}
			}
		} while (!unsorted);

		//				for(int j = 0; j < cNumber; j++ )                                 //found the three points
		//					System.out.println("ST> " + j + " "+ clusterSorted[j]);

		int resort[] = new int[cNumber];
		lock = 0;
		do {	


			for (int i = 0; i < 4; i++) { // nur augenbrauen punkte finden.

				unsorted = true;
				//System.out.println(clusterSorted[i] +", "+ smallValue);
				if (clusterSorted[i] == smallValue) {

					resort[0]  = clusterSorted[i]; 

					for(int h = 0 ; h < 3; h++) {
						if((h != smallValue) && (lock == 0)) { // den kleinsten Wert nach vorne holen und abspeichern

							resort[h+1]  = clusterSorted[h+1]; 
							//System.out.println(h + ", "+resort[1]);
						}
						else {
							lock = 1;
							resort[h+1]  = clusterSorted[h+1];
						}

						if (h == 2) {

						}
					}		


					unsorted = false;
				} 
			}	
		} while (!unsorted);	


		//					for(int j = 0; j < cNumber; j++)   //einsortieren der anderen drei [1-3] Cluster in die Liste
		//						System.out.println(clusterSorted[j]);
		//					
		//for(int f = 0; f < 4; f++)
		//	System.out.println("Cluster (" + f +   "):" + resort[f]); // cluster nummierung 1-4 fertig einsortiert

		/////////////////////////////////// resort => aktuelle liste mit Punkten 1-4
		// searching for the nose points // clusterSorted => aktueller Stand der Punkten
		///////////////////////////////////



		// Sortieren der drei Punkte um die Nase nach der Groesse der X Werte
		do {	

			unsorted = true;
			int g;
			for (int i = 0; i < 6; i++) {  //TODO only 10 Points 	
				g = clusterSorted[i+4]; 

				//System.out.println("RT> " + g); // found all last numbers
				if (g != 9)
					if (clusterValesY2 [g] > clusterValesY2 [g+1]) {   // search for the four smallest y => eye brow points                   
						double temp  = clusterValesY2 [g];
						clusterValesY2 [g]  = clusterValesY2 [g+1];
						clusterValesY2 [g+1]     = temp;
						//System.out.println(clusterValesY [g] + ">"+ clusterValesY [g+1]);

						//System.out.println("T> " + g);
						unsorted = false;
					}

			}
		} while (!unsorted);

		//				for(int j = 0; j < cNumber; j++)   //einsortieren der anderen drei Cluster in die Liste
		//					System.out.println("TR2> " +j + " "+ fourSmallestX[j]); // drei noch nicht vorkommenden punkte raussuchen
		//				


		// Searching for the nose points
		int k = 0;
		for(int j = 0; j < cNumber-1; j++) {

			//System.out.println("KR "+  fourSmallestX[0] + ", "+ fourSmallestX[1] + ", "+ fourSmallestX[2] + ", "+  fourSmallestX[3]);

			//if(((fourSmallestX[j]+4) != (fourSmallestX[0]+4) && (fourSmallestX[j]+4) != (fourSmallestX[1]+4)) && ((fourSmallestX[j]+4) != (fourSmallestX[2]+4)) && ((fourSmallestX[j]+4) != (fourSmallestX[3]+4)))	{		
			if( (fourSmallestY[j] != resort[0])&& (fourSmallestY[j] != resort[1]) && (fourSmallestY[j] != resort[2]) && (fourSmallestY[j] != resort[3]) && k < 3)	{		
				//System.out.println("Nose vales> " +j + " "+ fourSmallestY[j]); // drei noch nicht vorkommenden punkte raussuchen
				secondSearch[k] = fourSmallestY[j];
				k++;
			}
		}
		
		// Searching for the nose points order
		k = 0;
		if (cluster.get(secondSearch[k]).getCenter().x > cluster.get(secondSearch[k+1]).getCenter().x && cluster.get(secondSearch[k]).getCenter().x > cluster.get(secondSearch[k+2]).getCenter().x) {
			resort[6] = secondSearch[k];	
			if (cluster.get(secondSearch[k+1]).getCenter().x > cluster.get(secondSearch[k+2]).getCenter().x) {
				resort[4] = secondSearch[k+2];
				resort[5] = secondSearch[k+1];
			} else {
				resort[5] = secondSearch[k+2];
				resort[4] = secondSearch[k+1];
			}	
		}	
		if (cluster.get(secondSearch[k+1]).getCenter().x > cluster.get(secondSearch[k]).getCenter().x && cluster.get(secondSearch[k+1]).getCenter().x > cluster.get(secondSearch[k+2]).getCenter().x) {
			resort[6] = secondSearch[k+1];	
			if (cluster.get(secondSearch[k]).getCenter().x > cluster.get(secondSearch[k+2]).getCenter().x) {
				resort[4] = secondSearch[k+2];
				resort[5] = secondSearch[k];
			} else {
				resort[5] = secondSearch[k+2];
				resort[4] = secondSearch[k];
			}	
		}
		if (cluster.get(secondSearch[k+2]).getCenter().x > cluster.get(secondSearch[k]).getCenter().x && cluster.get(secondSearch[k+2]).getCenter().x > cluster.get(secondSearch[k+1]).getCenter().x) {
			resort[6] = secondSearch[k+2];	
			if (cluster.get(secondSearch[k]).getCenter().x > cluster.get(secondSearch[k+1]).getCenter().x) {
				resort[4] = secondSearch[k+1];
				resort[5] = secondSearch[k];
			} else {
				resort[5] = secondSearch[k+1];
				resort[4] = secondSearch[k];
			}	
		}	


		k = 0;
		for(int j = 0; j < cNumber-1; j++) { // Searching for the mouth points left and right

			//System.out.println("KR "+  fourSmallestX[0] + ", "+ fourSmallestX[1] + ", "+ fourSmallestX[2] + ", "+  fourSmallestX[3]);

			//if(((fourSmallestX[j]+4) != (fourSmallestX[0]+4) && (fourSmallestX[j]+4) != (fourSmallestX[1]+4)) && ((fourSmallestX[j]+4) != (fourSmallestX[2]+4)) && ((fourSmallestX[j]+4) != (fourSmallestX[3]+4)))	{		
			if( (fourSmallestY[j] != resort[0])&& (fourSmallestY[j] != resort[1]) && (fourSmallestY[j] != resort[2]) && (fourSmallestY[j] != resort[3]) && (fourSmallestY[j] != resort[4])&& (fourSmallestY[j] != resort[5]) && (fourSmallestY[j] != resort[6]) && k < 3)	{		
				//System.out.println("Mouth vales> " +j + " "+ fourSmallestY[j]); // drei noch nicht vorkommenden punkte raussuchen
				secondSearch[k] = fourSmallestY[j];
				k++;
			}
		}

		// Mundpunkte zuweisen
		if (cluster.get(secondSearch[0]).getCenter().x < cluster.get(secondSearch[1]).getCenter().x) {
			resort[7] = secondSearch[0];
			resort[8] = secondSearch[1];
		} else {
			resort[7] = secondSearch[1];
			resort[8] = secondSearch[0];
		}

		resort[9] = fourSmallestY[9]; // last value 


		for(int f = 0; f < cNumber; f++)
			System.out.println("Cluster (sorted): " + resort[f]); // all nose points are sorted

		System.out.println("System notice: done");

		
		List<PointCluster> z = new ArrayList<PointCluster>(); // Liste zum Zwischenspeichern der  Cluster

		for(int j = 0; j < cNumber-1; j++) {
			z.add(cluster.get(j));
		}
		for(int j = 0; j < cNumber-1; j++) {
			cluster.set(j, z.get(resort[j])); // Cluster neuzuordnen nach dem vorherigen schema
			
		}

		return cluster; 
	 }

}
