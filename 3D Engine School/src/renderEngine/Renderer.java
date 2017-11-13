package renderEngine;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.RawModel;
import shaders.ShaderProgram;
import terrains.Terrain;
import toolbox.Maths;

public class Renderer {

	// Field of view stuff
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 10000;

	private Matrix4f projectionMatrix;
	
	private ShaderProgram shader = new ShaderProgram();

	// Loads the projection matrix to the shader
	public Renderer(Loader loader, Camera camera) {
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	// Binds the vertices of the model to the necessary VAO
	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
	}
	
	// Disables and unbinds the VAOs
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	// Creates a transformation for the terrain based on its current position
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

	// Shortcut to all the rendering and shading
	public void render(List<Terrain> terrains, Camera camera) {
		prepare();
		for(Terrain terrain: terrains){
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadTerrainAmplitude(terrain.getAmplitude());
		prepareTerrain(terrain);
		loadModelMatrix(terrain);
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
				GL11.GL_UNSIGNED_INT, 0);
		unbindTexturedModel();
		shader.stop();
		}
	}
	
	// Removes the shader from memory
	public void cleanUp() {
		shader.cleanUp();
	}
	
	// Prepares the screen by removing the background color, and then removing the previous images from the frame
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	// Creates the projection matrix, responsible for making things that are farther away look smaller.
	 private void createProjectionMatrix(){
	    	projectionMatrix = new Matrix4f();
	    	// Adjusts the x and y scale using the field of view and the screens aspect ratio to prevent distortion
			float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
			float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
			float x_scale = y_scale / aspectRatio;
			float frustum_length = FAR_PLANE - NEAR_PLANE;
			
			// A matrix is created using these values
			// The basic idea is that all matrices are multiplied by this matrix, resulting in all of them being projected onto a bounded cone (frustum), biasing all things closer to be larger and vice versa
			// Basically all vectors have values [x, y, z, scale] and so multiplying this by the following matrix scales all the values appropriately
			// There are plenty of resources online on how this works
			
			projectionMatrix.m00 = x_scale;
			projectionMatrix.m11 = y_scale;
			projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
			projectionMatrix.m33 = 0;
	    }
}

