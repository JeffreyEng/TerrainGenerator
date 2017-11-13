package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	// LWJGL standard class to set up a display and ensure that the program doesnt run faster than 60 frames per second. Any more is just wasteful
	
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 60;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay(){		
		// Calls on the correct version of OpenGL (3.2)
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			// Sets the display to a predetermined width and height, and assigns the OpenGL attributes to it
			//Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			
			// Enables V-Sync to prevent vertical screen tearing
			Display.setVSyncEnabled(true);
			
			Display.setFullscreen(false);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Terrain Generator");
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		// Creates the view port and checks the time
		GL11.glViewport(0,0, Display.getWidth(), Display.getHeight());
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay(){
		
		Display.sync(FPS_CAP);
		Display.update();
		
		// LWJGL 2 uses a version of Java where system times were not necessarily in the same units, this ensures that they all sync up to the correct units so that camera motion doesnt happen faster on some systems then others
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}
	
	// Returns the time since last frame in seconds, mostly to ensure that the camera moves at a steady rate
	public static float getFrameTimeSeconds(){
		return delta;
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
	
	// Finds a standardized time
	private static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	
	
	

}
