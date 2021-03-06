package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3,3) // takes in version of opengl
		//settings
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		 try {
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			// Display.create(new PixelFormat(),attribs);
			// anti-aliasing
			Display.create(new PixelFormat()/*.withSamples(8)*/,attribs); // the higher the samples, the better the quality, the more expensive
			Display.setTitle("My First OpenGL Game!"); //title of screen
		} catch (LWJGLException e) {
			e.printStackTrace();
		}	
		 
		 // set position of display
		 GL11.glViewport(0, 0, WIDTH, HEIGHT);
		 
		 lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		
		// display runs at steady fps
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f; // in seconds
		lastFrameTime = getCurrentTime();
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		
		Display.destroy();
		
	}
	
	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution(); // in milliseconds
	}
}
