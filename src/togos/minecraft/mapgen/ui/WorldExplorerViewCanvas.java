package togos.minecraft.mapgen.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

import togos.minecraft.mapgen.world.gen.WorldGenerator;

public abstract class WorldExplorerViewCanvas extends Canvas implements WorldExplorerView
{
    private static final long serialVersionUID = 1L;
    
	protected WorldGenerator wg;
	protected double wx=0, wy=0, zoom=1;
	public boolean showZoom = true;
	
	public WorldExplorerViewCanvas() {
		addComponentListener(new ComponentListener() {
			public void componentShown( ComponentEvent arg0 ) {
				stateUpdated();
			}
			public void componentResized( ComponentEvent arg0 ) {
				stateUpdated();
			}
			public void componentMoved( ComponentEvent arg0 ) {
			}
			public void componentHidden( ComponentEvent arg0 ) {
			}
		});
    }
	
	public double getWorldX() {  return wx;  }
	public double getWorldY() {  return wy;  }
	public double getZoom() {  return zoom;  }
	public double getWorldXStepSize() {  return getWidth()/(4*zoom);  }
	public double getWorldYStepSize() {  return getHeight()/(4*zoom);  }
	
	protected abstract void stateUpdated();
	
	public void setState( WorldGenerator wg, double wx, double wy, double zoom ) {
		if( zoom == 0 ) {
			throw new RuntimeException("Zoom cannot be zero!");
		}
		if( Double.isInfinite(zoom) ) {
			throw new RuntimeException("Zoom cannot be infinite!");
		}
		if( Double.isNaN(zoom) ) {
			throw new RuntimeException("Zoom must be a number!");
		}
		if( zoom == 0 ) {
			throw new RuntimeException("Zoom must not be zero!");
		}
		this.wg = wg;
		this.wx = wx;
		this.wy = wy;
		this.zoom = zoom;
		stateUpdated();
	}
	
	public void setWorldGenerator( WorldGenerator wg ) {
		setState( wg, wx, wy, zoom );
	}
	public void setWorldPos( double wx, double wy, double zoom ) {
		setState( wg, wx, wy, zoom );
	}
	
	protected void paintOverlays(Graphics g) {
		if( showZoom ) {
			String zoomText = "MPP: "+(1/zoom)+",    ("+wx+", "+wy+")";
			
			int textX = 16;
			int textY = getHeight()-16;
			
			Rectangle2D b = g.getFontMetrics().getStringBounds(zoomText, g);
			g.setColor(Color.BLACK);
			g.fillRect( textX+(int)b.getMinX()-8, textY+(int)b.getMinY()-4,
				(int)b.getWidth()+16, (int)b.getHeight()+8 );
			
			g.setColor(Color.GREEN);
			g.drawString( zoomText, textX, textY );
		}
	}
}
