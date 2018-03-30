package core.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.model.Material;
import core.model.Mesh;
import core.model.Model;
import core.model.Vertex;
import core.textures.Texture2D;

public class OBJLoader {

	private static final String RES_LOC = "res/";
	
	public static Model load(String objFile, String mtlFile) {
		long time = System.currentTimeMillis();
		
		BufferedReader meshReader = null;
		BufferedReader mtlReader = null;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		Material material = null;
		
		// Read obj File
		try {
			meshReader = new BufferedReader(new FileReader(RES_LOC + objFile));
			String line;
			while(true) {
				line = meshReader.readLine();
				String[] tokens = line.split(" ");
				tokens = Util.removeEmptyStrings(tokens);
				if(tokens.length == 0 || tokens[0].equals("#")) {
					continue;
				}
				if(tokens[0].equals("v")) {
					Vector3f vertex = new Vector3f(Float.valueOf(tokens[1]),
												   Float.valueOf(tokens[2]),
												   Float.valueOf(tokens[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);
				}
				if(tokens[0].equals("vn")) {
					normals.add(new Vector3f(Float.valueOf(tokens[1]),
							   Float.valueOf(tokens[2]),
							   Float.valueOf(tokens[3])));
				}
				if(tokens[0].equals("vt")) {
					textures.add(new Vector2f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2])));
				}
				if(tokens[0].equals("f")) {
					break;
				}
			}
			while((line = meshReader.readLine()) != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				Vertex v0 = processVertex(vertex1, vertices, indices);
				Vertex v1 = processVertex(vertex2, vertices, indices);
				Vertex v2 = processVertex(vertex3, vertices, indices);
				calculateTangents(v0, v1, v2, textures);
				line = meshReader.readLine();
			}
			meshReader.close();
		} catch (Exception e) {
			System.err.println("Error: Failed to Read File: " + objFile);
			e.printStackTrace();
			System.exit(-1);
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] tangentsArray = new float[vertices.size() * 3];
		int[] indicesArray = convertIndicesListToArray(indices);
		
		// Read mtl File
		if(mtlFile != null) {
			try {
				mtlReader = new BufferedReader(new FileReader(RES_LOC + mtlFile));
				String line;
				
				while((line = mtlReader.readLine()) != null) {
					
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					
					if(tokens.length == 0)
						continue;
					if(tokens[0].equals("newmtl")) {
						material = new Material();
						material.setName(tokens[1]);
					}
					if(tokens[0].equals("Kd")) {
						if(tokens.length > 1) {
							Vector3f color = new Vector3f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3]));
							material.setColor(color);
						}
					}
					if(tokens[0].equals("map_Kd")) {
						if(tokens.length > 1) {
							material.setDiffusemap(new Texture2D(RES_LOC + tokens[1]));
						}
					}
					if(tokens[0].equals("map_Ks")){
						if (tokens.length > 1){
							material.setSpecularmap(new Texture2D(RES_LOC + tokens[1]));
						}
					}
					if(tokens[0].equals("map_bump")){
						if (tokens.length > 1){
							material.setNormalmap(new Texture2D(RES_LOC + tokens[1]));
						}
					}
					if(tokens[0].equals("illum")){
						if (tokens.length > 1)
							material.setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("Ns")){
						if (tokens.length > 1)
							material.setShininess(Float.valueOf(tokens[1]));
					}
					mtlReader.close();
				}
			} catch (Exception e) {
				System.err.println("Error: Failed to read mtlFile: " + mtlFile);
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		System.out.println("Message: Obj loading time: " + (System.currentTimeMillis() - time) + "ms");
		
		Mesh mesh = loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
		return new Model(mesh, material);
		
	}
	
	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	public static Mesh loadToVAO(float[] positions, float[] textureCoords, float[] normals,
			int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new Mesh(vaoID, indices.length);
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2,
			List<Vector2f> textures) {
		Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
		Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
		Vector2f uv0 = textures.get(v0.getTextureIndex());
		Vector2f uv1 = textures.get(v1.getTextureIndex());
		Vector2f uv2 = textures.get(v2.getTextureIndex());
		Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
		Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

		float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		delatPos1.scale(deltaUv2.y);
		delatPos2.scale(deltaUv1.y);
		Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
		tangent.scale(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}

	private static Vertex processVertex(String[] vertex, List<Vertex> vertices,
			List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
					vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}
	
	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray, float[] tangentsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			Vector3f tangent = currentVertex.getAverageTangent();
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			tangentsArray[i * 3] = tangent.x;
			tangentsArray[i * 3 + 1] = tangent.y;
			tangentsArray[i * 3 + 2] = tangent.z;

		}
		return furthestPoint;
	}

	private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,
						newNormalIndex, indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}

		}
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			vertex.averageTangents();
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
}
