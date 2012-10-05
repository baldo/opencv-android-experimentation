

import org.opencv.core.Point;

public abstract class SearchPattern
{
	protected int dimX = 0;
	protected int dimY = 0;
	protected Point[] pattern;
	protected int nextPixel = 0;

	public SearchPattern(int dimX, int dimY)
	{
		this.dimX = dimX;
		this.dimY = dimY;
		this.pattern = new Point[dimX*dimY];
	}
	
	/**
	 * Baut das Suchmuster auf, das dann für alle Bilder verwendet werden kann.
	 * Dadurch kann die Suchmuster-Berechnung verhältnismäßig aufwändig gestaltet
	 * werden und es wird danach nur noch das gespeicherte Suchmuster effizient ausgelesen.
	 */
	public abstract void buildPattern();
	
	/**
	 * Setzt das Suchmuster wieder an den Anfang zurück.
	 */
	public void reset()	{
		nextPixel = 0;
	}
	
	/**
	 * Gibt den nächsten zu untersuchenden Pixel gemäß dem implementierten Suchmuster an.
	 * Nachdem der letzte Pixel dieses Suchmusters abgerufen wird, wird als nächstes wieder
	 * der erste Pixel herausgegeben.
	 * @return Point nächster zu untersuchender Pixel
	 */
	public Point nextPixel() {
		++nextPixel;
		if (nextPixel >= dimX * dimY)
			nextPixel = 0;
		return pattern[nextPixel]; 
	}
}
