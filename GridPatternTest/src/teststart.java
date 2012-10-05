import java.util.HashMap;

import org.opencv.core.Point;


public class teststart {

	public static void main(String[] args)
	  {
		GridPattern grid = new GridPattern(10, 10);
		grid.buildPattern();
		
		System.out.println("Size: " + grid.pattern.length);
		
		HashMap<Point, Integer> coordinateEvaluation = new HashMap<Point, Integer>();
				
		for (int i = 0; i < 100; i++) {
			Point a = grid.nextPixel();
			if (coordinateEvaluation.containsKey(a))
				coordinateEvaluation.put(a, coordinateEvaluation.get(a) + 1);
			else
				coordinateEvaluation.put(a, 1);
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				Point a = new Point(i, j);
				int num = 0;
				if (coordinateEvaluation.containsKey(a))
					num = coordinateEvaluation.get(a);
				System.out.format(" %2d", num);
			}
			System.out.print("\n");
		}
	  }
	}
