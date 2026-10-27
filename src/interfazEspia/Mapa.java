package interfazEspia;

import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

public class Mapa extends JMapViewer{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxZoomLevel;
		
	public Mapa() {
		this.maxZoomLevel = 12;
	}
	
	@Override
    public void setZoom(int zoom, Point mapPoint) {
        if (zoom < maxZoomLevel)
            super.setZoom(maxZoomLevel, mapPoint);
    }
}
