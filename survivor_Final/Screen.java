package survivor_Final;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Screen {
	
	private GraphicsDevice vc;    //Video card
	
	//access vc to monitor screen
	public Screen()
	{
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		vc = e.getDefaultScreenDevice();
	}
	
	//gets all compatible display modes
	public DisplayMode[] getCompatibleDisplayModes()
	{
		return vc.getDisplayModes();
	}
	
	//Compares display modes passed into video card and see if they match
	
	public DisplayMode findFirstCompatibleMode(DisplayMode modes[])
	{
		DisplayMode goodModes[] = vc.getDisplayModes();
		for(int x=0;x<modes.length;x++)
		{
			for(int y=0;y<goodModes.length;y++)
			{
				if(displayModesMatch(modes[x],goodModes[y]))
				{
					return modes[x];
				}
			}
		}
		return null;
	}
	
	//get current Display modes
	
	public DisplayMode getCurrentDisplayMode()
	{
		return vc.getDisplayMode();
	}
	
	//checks if two modes match each other
	
	public boolean displayModesMatch(DisplayMode m1, DisplayMode m2)
	{
		if(m1.getWidth()!=m2.getWidth()||m1.getHeight()!=m2.getHeight())
		{
			return false;
		}
		if(m1.getBitDepth()!= DisplayMode.BIT_DEPTH_MULTI &&m2.getBitDepth()!=DisplayMode.BIT_DEPTH_MULTI&&m1.getBitDepth()!=m2.getBitDepth())
		{
			return false;
		}
		if(m1.getRefreshRate()!=DisplayMode.REFRESH_RATE_UNKNOWN&&m2.getRefreshRate()!=DisplayMode.REFRESH_RATE_UNKNOWN&&m1.getRefreshRate()!=m2.getRefreshRate())
		{
			return false;
		}
		return true;
	}
	
	//make frame full screen
	
	public void setFullScreen(DisplayMode dm)
	{
		JFrame f = new JFrame();
		f.setUndecorated(true);
		//f.setVisible(true);
		f.setIgnoreRepaint(true);
		f.setResizable(false);
		vc.setFullScreenWindow(f);
		
		if(dm!=null&&vc.isDisplayChangeSupported())
		{
			try
			{
				vc.setDisplayMode(dm);
			}
			catch(Exception ex)
			{
				System.out.println(ex);
			}
		}
		f.createBufferStrategy(2);
	}
	
	//setting graphics object to following method's return
	
	public Graphics2D getGraphics()
	{
		Window w = vc.getFullScreenWindow();
		if(w!=null)
		{
			BufferStrategy s = w.getBufferStrategy();
			return (Graphics2D) s.getDrawGraphics();
		}
		else
		return null;
	}
	
	//updates display
	
	public void update()
	{
		Window w = vc.getFullScreenWindow();
		if(w != null)
		{
			BufferStrategy s = w.getBufferStrategy();
			if(!s.contentsLost())
			{
				s.show();
			}
		}
	}
	
	//returns full screen window
	
	public Window getFullScreenWindow()
	{
		return vc.getFullScreenWindow();
	}
	
	public int getWidth()
	{
		Window w = vc.getFullScreenWindow();
		if(w!=null)
		{
			return w.getWidth();
		}
		else
		{
			return 0;
		}
	}
	
	public int getHeight()          //returns height
	{
		Window w = vc.getFullScreenWindow();
		if(w!=null)
		{
			return w.getHeight();
		}
		else
		{
			return 0;
		}
	}
	
	//get out of full screen
	
	public void restoreScreen()
	{
		Window w= vc.getFullScreenWindow();
		if(w!=null)
		{
			w.dispose();
		}
		vc.setFullScreenWindow(null);
	}
	
	//create image compatible with monitor
	
	public BufferedImage createCompatibleImage(int w, int h, int t)
	{
		Window win = vc.getFullScreenWindow();
		if(win!=null)
		{
			GraphicsConfiguration gc = win.getGraphicsConfiguration();
			return gc.createCompatibleImage(w, h, t);
		}
		else
			return null;
	}
}
