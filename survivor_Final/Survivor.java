package survivor_Final;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import survivor_Final.Animation;

public class Survivor extends Core implements KeyListener,MouseMotionListener {

	public static void main(String[] args) {
		new Survivor().run();
	}
	
	private Animation a;
	private String message="";
	private boolean left,right,up,down,brake;
	private double dor=0.0;						//degree of rotation for spaceship
	private double dor_delta=3;					//degree delta
	private double s_x,s_y;						//coordinates of spaceship
	private double s_vx,s_vy;					//speed of spaceship
	private double s_speed=0.45; 			
	private double speedModifier=1;				
	private double speedModifierConstant=0.015;	//changing constant of speed
	private File audioFile;
	private Clip audioClip;
	private final long nextMissileTime=8000;	//interval between missiles
	private final long missileDuration=20000;
	private boolean boom = false,begin=false,pressI=false,dodega=true,highScoreBreak=false,mouseMove=false;
	private long gameTime=0,missileTime,starTime;
	private final long nextStarTime=8000;
	private final long starDuration=10000;
	private int missileCollided,starsCollected,f,h,highscore;
	private int score=0;
	private double my,mx; 
	Random r=new Random();
	private BufferedImage ss,msRef,ms_out,bg,go,st,start,infor,end,pause;
	private long clipTime;
	Missiles[] list=new Missiles[8];
	Stars[] starsList=new Stars[3];
	BufferedImage[] ms=new BufferedImage[5];
	
	int noOfMissile=0;
	int noOfStars=0;
	
	
	public void init(){
		super.init();
		up=left=right=down=brake=false;
		s_x=s.getWidth()/2;
		s_y=s.getHeight()/2;
		boom =begin=pressI=false;
		score=0;
		gameTime=noOfMissile=noOfStars=0;
		try
		{
		audioFile = new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Sound//shoot.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		audioClip = (Clip) AudioSystem.getLine(info);
		audioClip.open(audioStream);
		audioClip.start();
		audioClip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch(Exception e)
		{		}
		
		missileCollided =0;
		starsCollected=0;
		
		s_vx=s_vy=s_speed;
		s_x=s.getWidth()/2;
		s_y=s.getHeight()/2;
		
		Window w=s.getFullScreenWindow();
		w.setFocusTraversalKeysEnabled(false);
		w.addKeyListener(this);
		w.addMouseMotionListener(this);
		try
		{
		infor = ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//infor.jpg"));		
		bg = ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//backup.jpg"));
		//bg = ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//war.jpg"));
		ss=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//nebula1.gif"));
		msRef=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//black.jpg"));
		ms_out=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//red.jpg"));
		go=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//gameover.jpg"));		
		//missile list
				ms[0]=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//missile.gif"));			
				ms[1]=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//missile1.gif"));
				ms[2]=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//missile2.gif"));
				ms[3]=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//missile3.gif"));
				ms[4]=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//missile4.gif"));
				st=	ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//star.gif"));
				start=ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//bark1.jpg"));
				end = ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//exit2.jpg"));
				pause = ImageIO.read(new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Images//paused.jpg"));
		}
		catch(Exception e)
		{
			
		}
		}
	
	public void keyPressed(KeyEvent e) {
		int keyCode=e.getKeyCode();
	
	if(!boom&&running)
	{
		if(!begin)
		{
			if(keyCode==KeyEvent.VK_ESCAPE){
				audioClip.close();
				stop();
			}
			if(keyCode==KeyEvent.VK_ENTER||keyCode==KeyEvent.VK_SPACE)
			{
				begin=true;
			}
			
			if(keyCode==KeyEvent.VK_I)
			{
				pressI=true;
			}
		}
			else if(keyCode==KeyEvent.VK_ESCAPE){
				audioClip.close();
				stop();
			}
		else{
			switch(keyCode) {
			case KeyEvent.VK_LEFT:
				left=true;
				break;
			case KeyEvent.VK_RIGHT:
				right=true;
				break;
			case KeyEvent.VK_UP:
				up=true;
				break;
			case KeyEvent.VK_DOWN:
				down=true;
				break;
			case KeyEvent.VK_SPACE:
				brake=true;
				break;
			case KeyEvent.VK_P:
				if(dodega){
					dodega=false;
					clipTime= audioClip.getMicrosecondPosition();
					audioClip.stop();
				}
				else{
					dodega=true;
					audioClip.setMicrosecondPosition(clipTime);
					audioClip.start();
				}
				break;
			}	
			message = "Pressed : "+KeyEvent.getKeyText(keyCode);
		}	
	}
	
	if(!running)
	{
		if(keyCode==KeyEvent.VK_ESCAPE)
			{
				System.exit(0);
			}
	}
}	


	public void keyReleased(KeyEvent e) {
		int keyCode=e.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_I:			pressI=false;
									break;
		case KeyEvent.VK_LEFT:
			left=false;
			break;
		case KeyEvent.VK_RIGHT:
			right=false;
			break;
		case KeyEvent.VK_UP:
			up=false;
			break;
		case KeyEvent.VK_DOWN:
			down=false;
			break;
		case KeyEvent.VK_SPACE:
			brake=false;
			break;
		}
		message = "Released : "+KeyEvent.getKeyText(keyCode);
	}

	public void keyTyped(KeyEvent e) {
		e.consume();
	}

	
	//to calculate degree
	void setDegree(){	
		if(up && right)
			degCalculate(45);
		else if(up && left)
			degCalculate(135);
		else if(down && left)
			degCalculate(225);
		else if(down && right)
			degCalculate(315);
		else if(right)
			degCalculate(0);
		else if(up)
			degCalculate(90);
		else if(left)
			degCalculate(180);
		else if(down)
			degCalculate(270);
		
		
		if(brake)speedModifier-=speedModifierConstant;
		else speedModifier+=speedModifierConstant;
		
		if(speedModifier<=0)speedModifier=0;
		else if(speedModifier>1)speedModifier=1;
			
	}

	void degCalculate(double deg){
		
		double op_deg=(deg+180)%360;
		if(deg==dor){	
		}
		
		else if(deg<180){
			if(dor<deg){
				dor+=dor_delta;
			}
			else if(dor<op_deg){
				dor-=dor_delta;
			}
			else if(dor<360){
				dor+=dor_delta;
			}
		}
		else{
			if(dor<op_deg){
				dor-=dor_delta;
			}
			else if(dor<deg){
				dor+=dor_delta;
			}
			else if(dor<360){
				dor-=dor_delta;
			}
			
		}
		
		if(dor>=360){						//if degree of rotation greater than 360
			dor-=360;
		}
		else if(dor<0){
			dor+=360;
		}
	}
	
	private double bounce(double x,double y,double rot,Image relative){
		
		if(y<=0){							//above horizontal
			if(rot>0&&rot<90){
				return 360-rot;
			}
			else if(rot>=90&&rot<180){
				return 360-rot;
			}
		}
		else if(x<=0){						
			if(rot>90&&rot<=180){
				return 180-rot;
			}
			else if(rot>180&&rot<270){
				return 540-rot;
			}
		}
		else if(y>=s.getHeight()-relative.getHeight(null)){			//below horizontal
			
			if(rot>180&&rot<=270){
				return 360-rot;
			}
			else if(rot>270&&rot<360){
				return 360-rot;
			}
			
		}
		else if(x>=s.getWidth()-relative.getWidth(null)){
		
			if(rot>=0&&rot<90){
				return 180-rot;
			}
			else if(rot>270&&rot<360){
				return 540-rot;
			}
			
		}
			
		
		return dor;
	}
	int myRandom(int r1,int r2,int spawnArea,int nonSpawn){
		
		int a;
		r1-=nonSpawn;
		r2+=nonSpawn;
		do{a=r.nextInt(2*spawnArea+r1+r2)-spawnArea;
			
		}while((a>r1&&a<r2));
		return a;
	}

	void createMissiles(){
		int m_x,m_y,choice;
		choice=r.nextInt(2);
		
		m_x=myRandom(0,s.getWidth(),500,300);
		m_y=myRandom(0,s.getHeight(),500,300);
		if(choice==0){
			m_x=r.nextInt(s.getWidth());
		}
		else
			m_y=r.nextInt(s.getHeight());
		
		if(missileTime>=nextMissileTime&&noOfMissile<5){
			missileTime-=nextMissileTime;
			noOfMissile++;
			choice=r.nextInt(5);
			list[noOfMissile-1]=new Missiles(m_x,m_y,gameTime,choice);
		}
	}
	void TimelyDestroyMissile(){
		for(int i=0;i<noOfMissile;i++){
			if(gameTime-list[i].lifeTime>=missileDuration){
				destroyMissile(i);
			}
			
		}
	
	}
	void destroyMissile(int pos){
		
		for(int i=pos;i<noOfMissile-1;i++)
		{	list[i]=list[i+1];
			
		}
		noOfMissile--;
		
	}
	void createStars(){
		int m_x,m_y;
		m_x=r.nextInt(s.getWidth()-st.getWidth(null));
		m_y=r.nextInt(s.getHeight()-st.getHeight(null));
		
		if(starTime>=nextStarTime&&noOfStars<1){
			starTime-=nextStarTime;
			noOfStars++;
			starsList[noOfStars-1]=new Stars(m_x,m_y,gameTime);
			
		}
		
	}
	void timelyRemoveStars(){
		for(int i=0;i<noOfStars;i++){
			if(gameTime-starsList[i].lifeTime>=starDuration){
				destroyStars(i);
				starTime=0;
				//System.out.println("From here");
			}
			
		}
	}
	void destroyStars(int pos){
		
		for(int i=pos;i<noOfStars-1;i++)
		{	starsList[i]=starsList[i+1];
			starTime=0;
		}
		noOfStars--;
		
	}
	boolean collision(Image first,int x1,int y1,Image second,int x2,int y2){
		int width=first.getWidth(null);
		int height=first.getHeight(null);
		int width2=second.getWidth(null);
		int height2=second.getHeight(null);
		
		int imageUnit=12;
		for(int i=x1+width/imageUnit;i<=x1+width-width/imageUnit;i+=width/imageUnit )//
			for(int j=y1+height/imageUnit;j<=y1+height-height/imageUnit;j+=height/imageUnit )// 
				if(i>x2+width2/imageUnit&&i<x2+width2-width2/imageUnit&&j>y2+height2/imageUnit&&j<y2+height2-height2/imageUnit)
					{		
					return true;
					}
		
		return false;
	}
	@Override
	public synchronized void update(long timePassed){
	if(dodega)
	{
		if(begin&&running)
		{
			
			if(mouseMove)
			{
			//	mouseDegCalculate();
				mouseMove=false;
			}
		
		// for spaceship
		gameTime+=timePassed;
		missileTime+=timePassed;
		starTime+=timePassed;
		createMissiles();
		TimelyDestroyMissile();
		
		createStars();
		
		
		timelyRemoveStars();
		
		//rotate the ship
		setDegree();
		
		//bouncing off the wall
		dor=bounce(s_x,s_y,dor,ss);
		
		//coordinates of spaceship
		s_x+= (timePassed*s_vx*Math.cos(Math.toRadians(dor)))*speedModifier;
		s_y+= (timePassed*s_vy*Math.sin(Math.toRadians(dor)))*-1*speedModifier;
		
		//rotation calculation of missile
		for(int i=0;i<noOfMissile;i++){
			list[i].CalDegree(s_x, s_y,timePassed,dor);					
		}
		
		//missiles colliding with each other
		for(int i=0;i<noOfMissile;i++)					
			for(int j=i+1;j<noOfMissile;j++){
				try {
					if(i!=j&&collision(msRef,(int)list[i].x,(int)list[i].y,msRef,(int)list[j].x,(int)list[j].y)){
						missileCollided++;
						destroyMissile(j);
						destroyMissile(i);
										//
					
}
				} catch (Exception e) {
					
				}
		}
		//missiles colliding with spaceship
		for(int i=0;i<noOfMissile;i++){
			try {
				if(collision(msRef,(int)list[i].x,(int)list[i].y,ss,(int)s_x,(int)s_y)) 
					{
					audioClip.close();
					f=(int) list[i].x-135; h=(int) list[i].y-120; 			///////////////////////////////////
					loadImages();
					boom=true;
					long startingTime = System.currentTimeMillis();
					long cumTime = startingTime;    //cumulative time
					File audioFile1 = new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Sound//boom.wav");
					AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(audioFile1);
					AudioFormat format1 = audioStream1.getFormat();
					DataLine.Info info1 = new DataLine.Info(Clip.class, format1);
					Clip audioClip1 = (Clip) AudioSystem.getLine(info1);
					audioClip1.open(audioStream1);
					audioClip1.start();
					while(cumTime - startingTime<1000)
					{
						timePassed = System.currentTimeMillis()-cumTime;
						cumTime += timePassed;
						a.update(timePassed);
						Graphics2D g = s.getGraphics();
						draw(g);
						g.dispose();
						s.update();
					}
					audioClip1.close();
					boom=false;
						stop();
					}
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		//missiles collecting stars
				for(int i=0;i<noOfStars;i++){
					try {
						if(collision(st,starsList[i].x,starsList[i].y,ss,(int)s_x,(int)s_y)){
							starsCollected++;
							destroyStars(i);
						}
					} catch (Exception e) {
							} 
				}
		}
		//System.out.println(timePassed);
		}
	}
	
	public synchronized void draw(Graphics2D g) {
		Window w = s.getFullScreenWindow();
		AffineTransform tx;
		AffineTransformOp op;
		Font myFont = new Font("Showcard Gothic", Font.BOLD, 50);
		w.setForeground(Color.DARK_GRAY);
		g.setFont(myFont);
		
	if(!running&&exit)
	{
		
		g.drawImage(end,0,0,null);
		g.drawString(score+"", 960, 710); ///////////////
		if(highScoreBreak)
			highscore=score;
		g.drawString(highscore+"", 960, 870);
		if(highScoreBreak)
			g.drawString("NEW HIGH SCORE!",1050,710);
		return;	
	}
	
	if(dodega)
	{
	
	if(!begin)
	{
	g.drawImage(start, 0, 0, null);
		if(pressI)
		{
			g.drawImage(infor, 0,0, null);
		}
	}
	else
	{		
		
		if(boom)
		{
			g.drawImage(bg, 0, 0, null);
	//		g.drawImage(ss,(int)s_x,(int)s_y,null);
			g.drawImage(a.getImage(), f, h, null);
			g.drawImage(go, s.getWidth()/2-250, s.getHeight()/2-150, null);
			return;
		}

		g.drawImage(bg, 0, 0, null);
	//	g.drawImage(border,0,,0,null);
	//spaceship drawing
		tx = AffineTransform.getRotateInstance(Math.toRadians(-dor),ss.getWidth(null)/2,ss.getHeight(null)/2);
		op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		g.drawImage(op.filter(ss,null),(int)s_x,(int)s_y,null);
	//	g.drawString(""+noOfMissile, 50, 50);
		//missile drawing
		for(int i=0;i<noOfStars;i++){
			g.drawImage(st, starsList[i].x,	starsList[i].y,null );
		}
		for(int i=0;i<noOfMissile;i++){
			int ch_x,ch_y;				//ch
			ch_x=(int)list[i].x;
			ch_y=(int)list[i].y;
			if(ch_x>0&&ch_x<s.getWidth()&&ch_y>0&&ch_y<s.getHeight())
			{
				tx = AffineTransform.getRotateInstance(Math.toRadians(list[i].dor),ms[i].getWidth(null)/2,ms[i].getHeight(null)/2);
				op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				g.drawImage(op.filter(ms[list[i].choice],null),(int)list[i].x,(int)list[i].y,null);
			//	g.drawImage(ms[list[i].choice],(int)list[i].x, (int)list[i].y,null);
			}
			else{
				if(ch_x<0){
					ch_x=0;
				}
				else if(ch_x>s.getWidth()){
					ch_x=s.getWidth()-ms_out.getWidth(null);
				}
				if(ch_y<0){
					ch_y=0;
				}
				else if(ch_y>s.getHeight()){
					ch_y=s.getHeight()-ms_out.getHeight(null);
				}
			g.drawImage(ms_out, ch_x, ch_y, null);	
			}
		}
		g.drawString((int) gameTime/60000+":"+(int) (gameTime/1000)%60 ,50 ,100 );
		g.drawImage(st,s.getWidth()-250,60,null);
		g.drawString(" :"+starsCollected,s.getWidth()-200,100);
	}
	}	
	else
	{
		g.drawImage(pause,0,0,null);
	}
	}
	
	public void loadImages(){
		Image i1 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\1(1).gif").getImage();
		Image i2 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\1(2).gif").getImage();
		Image i4 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\2(2).gif").getImage();
		Image i5 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\3(2).gif").getImage();
		Image i6 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\3.gif").getImage();
		Image i8 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\5.gif").getImage();
		Image i9 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\6.gif").getImage();
		Image i10 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\7.gif").getImage();
		Image i11 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\8.gif").getImage();
		Image i12 =new ImageIcon("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Explosion Animationi\\9.gif").getImage();
		
		a = new Animation();
		a.addScene(i1, 100);
		a.addScene(i2, 100);
		a.addScene(i4, 100);
		a.addScene(i5, 100);
		a.addScene(i6, 100);
		a.addScene(i8, 100);
		a.addScene(i9, 100);
		a.addScene(i10, 100);
		a.addScene(i11, 100);
		a.addScene(i12, 100);
	}

	
	public void stop()
	{
		exit=true;
		score=(int) ((gameTime/1000)+missileCollided*10+starsCollected*10);
		try
		{
			File audioFile1 = new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Sound//Wha-Wha.wav");
			AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(audioFile1);
			AudioFormat format1 = audioStream1.getFormat();
			DataLine.Info info1 = new DataLine.Info(Clip.class, format1);
			Clip audioClip1 = (Clip) AudioSystem.getLine(info1);
			audioClip1.open(audioStream1);
			audioClip1.start();
			Thread.sleep(4000);
			}catch(Exception ex) { }
			running = false;
			try {
				highScoreCheck();
			} catch (IOException e) {
				
			}
	}
	
	public void highScoreCheck() throws IOException
	{
		File file = new File("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Data//highscore.txt");
		Scanner scanner = new Scanner(file);
		highscore=scanner.nextInt();
		if(highscore<score)
		{
			FileOutputStream fos = new FileOutputStream("C://Users//Esteev//workspace//Survivor//src//survivor_Final//Data//highscore.txt"); 
			PrintStream out;
			out = new PrintStream(fos);
			out.println(score);
			out.close();
			highScoreBreak=true;
			//highscore=score;
		}
		scanner.close();
	}

	public void mouseDragged(MouseEvent e) {
		//mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		
		mouseMove = true;
		 mx = e.getX();
		 my = e.getY();
		}
	void mouseDegCalculate(){

		double m=(my-s_y)/(mx-s_x);
		double degree=Math.toDegrees(Math.atan(m));
		//for precise following	
		/*if(degree<0){
			degree+=180;
			if(s_y<my&&s_x>mx) degree+=180;						//third quadrant
		}
		else if(s_y<=my&&s_x<=mx)degree+=180;					//fourth quadrant
		*/
		if(degree<0){
			degree+=180;
			if(my<s_y&&mx>s_x) degree+=180;						//third quadrant
		}
		else if(my<=s_y&&mx<=s_x)degree+=180;					//fourth quadrant

		//for quantise following	
		double op_deg=(degree+180)%360;
	if(degree==dor){	
	}

	else if(degree<180){
		if(dor<degree){
			dor+=dor_delta;
		}
		else if(dor<op_deg){
			dor-=dor_delta;
		}
		else if(dor<360){
			dor+=dor_delta;
		}
	}
	else{
		if(dor<op_deg){
			dor-=dor_delta;
		}
		else if(dor<degree){
			dor+=dor_delta;
		}
		else if(dor<360){
			dor-=dor_delta;
		}
		
	}

	if(dor>=360){						//if degree of rotation greater than 360
		dor-=360;
	}
	else if(dor<0){
		dor+=360;
	}

	}
}

//////////////////////////////////////////////////////////////////////////////////////////////
class Missiles{
	double x,y;
	double vx,vy;
	double m,degree,dor;
	double dor_delta;
	double speed;
	int choice;
	int follow;
	
	double[] rotationDeltaList={1.3,3,2,1,4};
	double[] speedList={0.6,0.31,0.40,0.35,0.3};
	int[] followType={1,1,2,2,2};				
	/*
	 *follow type 1= quantize following ,follows spaceship 
	 *follow type 2= ahead following, smart missiles ,route is ahead of ship  d/2
	 * 
	 *rotationDeltaList : for higher values ,missiles changes direction very fast 
	 * 
	 *speedList : speed of missiles 
	 * 
	  */
	long lifeTime;
	Missiles(){};
	Missiles(int x,int y,long time,int choice){
		this.x=x;//
		this.y=y;//
		this.choice=choice;
		dor=0;
		speed=speedList[choice];
		dor_delta=rotationDeltaList[choice];
		follow=followType[choice];
		
		vx=vy=speed;
		lifeTime=time;
	}
	void show(){
		System.out.println("x "+x+" y"+y);
	}
	void CalDegree(double sx,double sy,long timePassed,double slopeAngle){
		double d;
		if(choice==2){
		
		d=Math.sqrt((sx-x)*(sx-x)+(sy-y)*(sy-y));
		d/=2;
		sx=sx+d*Math.cos(Math.toRadians(slopeAngle));
		sy=sy+d*Math.sin(Math.toRadians(slopeAngle))*-1;
		}
		
		m=(sy-y)/(sx-x);
		degree=Math.toDegrees(Math.atan(m));
		//for precise following	
		if(degree<0){
			degree+=180;
			if(sy<y&&sx>x) degree+=180;						//third quadrant
		}
		else if(sy<=y&&sx<=x)degree+=180;					//fourth quadrant
		
		//for quantize following	
		double op_deg=(degree+180)%360;
		if(degree==dor){	
		}
		
		else if(degree<180){
			if(dor<degree){
				dor+=dor_delta;
			}
			else if(dor<op_deg){
				dor-=dor_delta;
			}
			else if(dor<360){
				dor+=dor_delta;
			}
		}
		else{
			if(dor<op_deg){
				dor-=dor_delta;
			}
			else if(dor<degree){
				dor+=dor_delta;
			}
			else if(dor<360){
				dor-=dor_delta;
			}
			
		}
		
		if(dor>=360){						//if degree of rotation greater than 360
			dor-=360;
		}
		else if(dor<0){
			dor+=360;
		}
		
		 
		x+= (timePassed*vx*Math.cos(Math.toRadians(dor)));
		y+= (timePassed*vy*Math.sin(Math.toRadians(dor)));
			
	}
	
	
	double getx(){
		return x;		
	}
	double gety(){
		return y;
	}

}
///////////////////////////////////////////////////////////////////////////////////////
class Stars{
int  x,y;
long lifeTime;
Stars(){};
Stars(int x,int y,long time ){
this.x=x;
this.y=y;
lifeTime=time;
}
}
