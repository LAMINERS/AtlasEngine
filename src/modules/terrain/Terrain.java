package modules.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import core.model.MaterialPack;
import core.model.Mesh;
import core.utils.OBJLoader;

public class Terrain {

	private static final float SIZE = 1024;
	private static final float MAX_HEIGHT = 70;
	private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
	
	private float x;
	private float z;
	private Mesh mesh;
	private MaterialPack materialPack;
	
	private float[][] heights;

	public static float getSize() {
		return SIZE;
	}
	
	public Terrain(float x, float z, MaterialPack materialPack, String heightmapFile) {
		this.x = x;
		this.z = z;
		this.materialPack = materialPack;
		this.mesh = generateTerrain(heightmapFile);
	}
	
	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public MaterialPack getMaterialPack() {
		return materialPack;
	}

	private Mesh generateTerrain(String heightmap) {
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightmap + ".png"));
		} catch (IOException e) {
			System.err.println("Error: Failed to load Heightmap");
			e.printStackTrace();
		}
		int VERTEX_COUNT = image.getHeight();
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, image);
				vertices[vertexPointer * 3 + 1] = height;
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return OBJLoader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getHeight()){
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOUR/2f;
		height /= MAX_PIXEL_COLOUR/2f;
		height *= MAX_HEIGHT;
		return height;
	}

	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x-1, z, image);
		float heightR = getHeight(x+1, z, image);
		float heightD = getHeight(x, z-1, image);
		float heightU = getHeight(x, z+1, image);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
}
