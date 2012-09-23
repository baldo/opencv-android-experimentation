/**
 * 
 */
package facepoint;

import org.opencv.core.Point;

/**
 * @author Jan Girlich
 * Dieses Suchmuster springt in großen Schritten durch das Bild und bildet dabei
 * ein Suchgitter, das immer enger gespannt wird. Jede weitere Iteration halbiert
 * die Schrittgröße, d.h. falls mit 20 Pixeln begonnen wird, dann wird im nächsten
 * Schritt mit 10 Pixeln Abstand gesucht.
 */
public class GridPattern extends SearchPattern {

	private int initStepWidth;
	
	public GridPattern(int dimX, int dimY) {
		super(dimX, dimY);
	}
	
	/**
	 * Initale Suchschrittgröße angeben
	 */
	public void setInitalStepWidth(int stepWidth) {
		this.initStepWidth = stepWidth;
	}
	
	/* (non-Javadoc)
	 * @see facepoint.SearchPattern#()
	 */
	@Override
	public void buildPattern() {
		int counter = 0;
		int stepWidth = initStepWidth;
		
		for (int k = 0; k < initStepWidth; k++) {
			int nextStepWidth = (int) (stepWidth * 0.5);

			// Ausfüllen des Musters in normaler Schrittweite
			counter = insertSteps(counter, stepWidth, stepWidth);
			
			// Ausfüllen des Musters in X-Richtung mit halber Schrittweite
			counter = insertSteps(counter, nextStepWidth, stepWidth);
			
			// Ausfüllen des Musters in Y-Richtung mit halber Schrittweite
			counter = insertSteps(counter, stepWidth, nextStepWidth);
			
			stepWidth = nextStepWidth;
		}
	}
	
	/**
	 * Hilfsfunktion, die die Punktkoordinaten in unterschiedlichen Schrittweiten
	 * für X und Y in das pattern Array einfügt.
	 * @param counter Position ab der in das pattern Array eingefügt werden soll
	 * @param stepWidthX Schrittweite für X
	 * @param stepWidthY Schrittweite für Y
	 * @return int nächste unbeschriebene Position im pattern Array
	 */
	private int insertSteps(int counter, int stepWidthX, int stepWidthY) {
		for (int i = 0; i < dimX; i += stepWidthX) {
			for (int j = 0; j < dimY; j += stepWidthY) {
				pattern[counter++] = new Point(i, j);
			}
		}
		return counter;
	}
}
