package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import entities.Camera;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import terrains.Terrain;

public class MainGameLoop {

	private static List<Terrain> terrains = new ArrayList<Terrain>();
	
	public static void main(String[] args) {

		// Initializations of the display and model loader
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		// Creates terrains and adds them to the terrain list
		Terrain terrain = new Terrain(0, -1, loader);
		Terrain terrain2 = new Terrain(0, 0, loader);
		terrains.add(terrain);
		terrains.add(terrain2);
		
		// Initializes camera at the average midpoint of all terrains in the list
		Camera camera = new Camera(averageLocation().x, averageLocation().y);
		//
		Renderer renderer = new Renderer(loader, camera);
		
		
		// While the program is not closed, perform main calculations

		while (!Display.isCloseRequested()) {

			// Moves the camera
			camera.move();
			
			// Renders the terrains using the cameras POV
			renderer.render(terrains, camera);
			
			// Checks for inputs to change terrain
			checkInputs();

			DisplayManager.updateDisplay();
		}

		// Cleans up the memory before closing the display
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	
	private static void checkInputs() {
		// Uses basic LWJGL keyboard commands to control the program
		while (Keyboard.next()) { // While the keyboard has an event (button being pressed)
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) { // If the event detected is escape
				DisplayManager.closeDisplay();	// close the program and exit
				System.exit(1);
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_1) { // If the event is at 1
				if (Keyboard.getEventKeyState()) { // Makes sure that this event is a new event (prevents from holding down a key and rapidly generating terrains)
					
					for(Terrain t : terrains){
					t.setGenerator(0); // Change the noise function the generator uses
				}}
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_2) { // Same as above but for key 2
				if (Keyboard.getEventKeyState()) {
					for(Terrain t : terrains){
					t.setGenerator(1);
					}
				}
			}
		}

	}
	
	private static Vector2f averageLocation(){
		// Simple method for setting the camera location to the average of the centers of all drawn terrains
		Vector2f position = new Vector2f();
		for(Terrain t : terrains){
			position.x += t.getX() + (t.getSize() / 2);
			position.y -= t.getZ() + (t.getSize() / 2);
		}
		position.x /= terrains.size();
		position.y /= terrains.size();
		
		
		return position;
	}

}