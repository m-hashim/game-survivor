package survivor_Final;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Window;

public abstract class Core {

	private static final DisplayMode modes[]= {
			new DisplayMode(1920,1080,32,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(1920,1080,16,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(800,600,32,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(800,600,24,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(800,600,16,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(640,480,32,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(640,480,24,DisplayMode.REFRESH_RATE_UNKNOWN),
			new DisplayMode(640,480,16,DisplayMode.REFRESH_RATE_UNKNOWN),
	};
	
	protected boolean running,exit;
	protected Screen s;
	protected long startTime,timePassed,cumTime;
	public abstract void stop();

	public void run()
	{
		try
		{
			init();
			gameLoop();
		}finally{
			s.restoreScreen();
		}
	}
	
	public void init()
	{
		s = new Screen();
		DisplayMode dm = s.findFirstCompatibleMode(modes);
		s.setFullScreen(dm);
		
		Window w = s.getFullScreenWindow();
		w.setFont(new Font("Arial",Font.PLAIN,30));
		w.setBackground(Color.GREEN);
		w.setForeground(Color.WHITE);
		running = true;
		exit=false;
	}
	
	public void gameLoop()
	{
		startTime = System.currentTimeMillis();
		cumTime = startTime;
		
		while(running||exit)
		{
			timePassed = System.currentTimeMillis()-cumTime;
			cumTime += timePassed;								//cum time is total time
			
			update(timePassed);
			
			Graphics2D g = s.getGraphics();
			draw(g);
			g.dispose();
			s.update();
			
			try
			{
				Thread.sleep(10);
			}catch(Exception e){}
		}
	}
	
	public void update(long timePassed){
		
	}
	
	public abstract void draw(Graphics2D g);
}
