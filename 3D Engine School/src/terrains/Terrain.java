package terrains;

import org.lwjgl.util.vector.Vector2f;

import models.RawModel;
import renderEngine.Loader;

public class Terrain {

	private static final int VERTEX_COUNT = 128;
	private static final float SIZE = 500;
	private float x;
	private float z;
	
	private RawModel model;
	private Loader loader;
	
	private float[][] heights = DiamondSquare.createGrid(VERTEX_COUNT);

	// Creates a terrain of the proper size, and creates the model of the terrain based on the heights
	public Terrain(int gridX, int gridZ, Loader loader) {
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.loader = loader;
		this.model = generateTerrain(loader);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
	
	public void setX(int x){
		this.x = x;
	}

	public void setZ(int z){
		this.z = z;
	}
	
	public RawModel getModel() {
		return model;
	}
	
	public Vector2f getAmplitude(){
		
		float max = 0f;
		float min = 100f;
		
		for(int j = 0; j < heights.length; j++){
			for(int i = 0; i < heights[0].length; i++){
				if(heights[i][j] > max){
					max = heights[i][j];
				}
				if(heights[i][j] < min){
					min = heights[i][j];
				}
			}
		}
		
		return new Vector2f(max, min);
	}
	public float getSize(){
		return SIZE;
	}

	// Transforms the heights generated by the noise functions into vertices, and then loads them into models
	// Indices are created to prevent any duplicate vertices
	private RawModel generateTerrain(Loader loader) {

		// Each vertex has 3 floats, x,y, and z, and so the x and y are created on the fly from iterating from an index of 0
		// x is in position 1, so it is placed in the index * 3 + 0, y is in position 2, so it is placed in the index * 3 + 1 and z is placed at index * 3 + 2
		// The terrain lies on the xz plane and the height is represented by y.
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT * 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = heights[j][i];
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				vertexPointer++;
			}
		}
		// Next the index is created. This method looks complicated but all it is doing is creating an order in which to read the vertices while ensuring that each one makes a complete triangle
		int pointer = 0;
		for (int z = 0; z < VERTEX_COUNT - 1; z++) {
			for (int x = 0; x < VERTEX_COUNT - 1; x++) {
				int topLeft = (z * VERTEX_COUNT) + x;
				int topRight = topLeft + 1;
				int bottomLeft = ((z + 1) * VERTEX_COUNT) + x;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, indices);
	}
	
	public void setGenerator(int type){
		switch (type){
			case 0: heights = DiamondSquare.createGrid(VERTEX_COUNT);
					break;
			case 1: heights = Simplex.createGrid(VERTEX_COUNT);
			
		}
		this.model = generateTerrain(loader);
	}
}

