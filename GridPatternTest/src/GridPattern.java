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
		// Choose the square root of the smaller picture dimension as starting point
		// for finding a suitable (power of 2) stepwidth.
		// This gives a nice trade-off between stepwidth and number of steps
		// depending on the picture size.
		setInitalStepWidth((int) Math.sqrt(Math.min(dimX, dimY)));
	}
	
	/**
	 * Initale Suchschrittgröße angeben.
	 * Wird automatisch auf die nächste "power of 2" aufgerundet.
	 * Muss größer 0 sein, sonst wird der Default-Wert von 32 gewählt.
	 * @param stepWidth Gewünschte Größe
	 * @return int Wert, der tatsächlich gewählt wurde.
	 */
	public int setInitalStepWidth(int stepWidth) {
		if (stepWidth > 0)
			initStepWidth = (int) Math.pow(2, Math.ceil(Math.log(stepWidth)/Math.log(2)));

		return initStepWidth;			
	}

	/* (non-Javadoc)
	 * @see facepoint.SearchPattern#()
	 */
	@Override
	public void buildPattern() {
		int counter = 0;
		int stepWidth = initStepWidth;
		
		// Initi grid so that the borders are left for the last round
		counter = insertSteps(counter, stepWidth - 1, stepWidth - 1, stepWidth);

		do {
			int nextStepWidth = (int) (stepWidth * 0.5);

			// Ausfüllen des Musters in X-Richtung mit halber Schrittweite Offset
			counter = insertSteps(counter, stepWidth - 1, nextStepWidth -1, stepWidth);
			
			// Ausfüllen des Musters in Y-Richtung mit halber Schrittweite Offset
			counter = insertSteps(counter, nextStepWidth -1, stepWidth - 1, stepWidth);

			// Ausfüllen des Musters mit halber Schrittweite Offset in beide Richtungen
			counter = insertSteps(counter, nextStepWidth -1, nextStepWidth - 1, stepWidth);

			stepWidth = nextStepWidth;
		} while ((int) stepWidth/2 > 0);
	}
	
	/**
	 * Hilfsfunktion, die die Punktkoordinaten in unterschiedlichen Schrittweiten
	 * für X und Y in das pattern Array einfügt.
	 * @param counter Position ab der in das pattern Array eingefügt werden soll
	 * @param stepWidthX Schrittweite für X
	 * @param stepWidthY Schrittweite für Y
	 * @return int nächste unbeschriebene Position im pattern Array
	 */
	private int insertSteps(int counter, int offsetX, int offsetY, int stepWidth) {
		for (int i = offsetX; i < dimX; i += stepWidth)
			for (int j = offsetY; j < dimY; j += stepWidth)
				pattern[counter++] = new Point(i, j);
		return counter;
	}
}
