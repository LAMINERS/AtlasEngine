package modules.terrain;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import core.math.Maths;
import core.model.MaterialPack;
import core.model.Mesh;
import core.utils.Constants;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.bind();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.unbind();
	}
	
	public void render(List<Terrain> terrains) {
		for(Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getMesh().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindModel();
		}
	}
	
	private void prepareTerrain(Terrain terrain) {
		Mesh mesh = terrain.getMesh();
		GL30.glBindVertexArray(mesh.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}
	
	private void bindTextures(Terrain terrain) {
		MaterialPack materialPack = terrain.getMaterialPack();
		for(int i = 0; i < Constants.MAX_TERRAIN_MAT; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, materialPack.getMaterials().get(i).getDiffusemap().getId());
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + Constants.MAX_TERRAIN_MAT + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, materialPack.getMaterials().get(Constants.MAX_TERRAIN_MAT + i).getNormalmap().getId());
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + Constants.MAX_TERRAIN_MAT * 2 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, materialPack.getMaterials().get(Constants.MAX_TERRAIN_MAT * 2 + i).getNormalmap().getId());
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + Constants.MAX_TERRAIN_MAT * 3 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, materialPack.getMaterials().get(Constants.MAX_TERRAIN_MAT * 3 + i).getNormalmap().getId());
		}	
	}
	
	private void unbindModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0f, 0f, 0f, new Vector3f(1,1,1));
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
