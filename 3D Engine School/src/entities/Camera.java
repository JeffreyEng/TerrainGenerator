package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import toolbox.SmoothFloat;

public class Camera{
	
	/*  Standard camera class. Uses a specific camera position to calculate the necessary matrices
		to properly display the terrain. Currently set up to rotate around an unmoving anchor point
	*/
	
	private SmoothFloat distanceFromOrigin = new SmoothFloat(350, 8.0f);
	private SmoothFloat angleAroundOrigin = new SmoothFloat(0, 8.0f);

	private Matrix4f viewMatrix = new Matrix4f();
	
	private Vector3f anchor;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private Vector3f rotation = new Vector3f(0,0,0);
	private SmoothFloat pitch = new SmoothFloat(35, 8.0f);
	private float yaw = 0;
	private float roll;
	
	public Camera(float x, float z){
		anchor = new Vector3f(x, -z, 50);
	}
	
	public void move(){
		// Moves the camera based on user controlled zoom, pitch and angle
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		// Keeps the camera at the proper position based on its distance from the anchor
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		
		// Keeps the yaw properly oriented, making the camera always face the anchor
		this.yaw = 180 - (rotation.y + angleAroundOrigin.get());
		yaw%=360;
		
		updateViewMatrix();
	}
	

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch.get();
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		
		// Simple trig operations to calculate the x and y position of the camera based on the angle and the distance to the anchor
		float theta = rotation.y + angleAroundOrigin.get();
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = anchor.x - offsetX;
		position.z = anchor.y - offsetZ;
		position.y = anchor.z + verticDistance + 4;
	}
	
	private float calculateHorizontalDistance(){
		// Simple cosine operation to get the horizontal distance
		return (float) (distanceFromOrigin.get() * Math.cos(Math.toRadians(pitch.get() + 4)));
	}
	
	private float calculateVerticalDistance(){
		// Simple sine operation to get the vertical distance
		return (float) (distanceFromOrigin.get() * Math.sin(Math.toRadians(pitch.get() + 4)));
	}
	
	private void calculateZoom(){
		// Uses the mouse wheel input to adjust the distance from the anchor
		float zoomLevel = Mouse.getDWheel() * 0.03f;
		distanceFromOrigin.setTarget(distanceFromOrigin.get() - zoomLevel);
		if(distanceFromOrigin.get() < 10f){
			distanceFromOrigin.setTarget(10.1f);
		}
		distanceFromOrigin.update(DisplayManager.getFrameTimeSeconds());
		
	}
	
	private void calculatePitch(){
		// Allows the user to change the pitch by moving the mouse when the user right clicks
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch.setTarget(pitch.get() - pitchChange);
			if(pitch.get() < -20){
				pitch.force(0);
			}else if(pitch.get() > 90){
				pitch.force(90);
			}
		}
		pitch.update(DisplayManager.getFrameTimeSeconds());
	}
	
	private void calculateAngleAroundPlayer(){
		// Provides constant rotation around the anchor and allows the user to change this angle
		if(Mouse.isButtonDown(1)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundOrigin.setTarget(angleAroundOrigin.get() - angleChange);
		}else{
			angleAroundOrigin.setTarget(angleAroundOrigin.get() );
		}
		angleAroundOrigin.update(DisplayManager.getFrameTimeSeconds());
	}

	private void updateViewMatrix() {
		// Matrix math to create the view plane "from" the camera.
		
		// Sets the view matrix as the identity
		viewMatrix.setIdentity();
		
		// Rotates the matrix vertically
		Matrix4f.rotate((float) Math.toRadians(pitch.get()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		// Rotates the matrix horizontally
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		
		// Translates the camera coordinates to OpenGL coordinates (flipped)
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}
}