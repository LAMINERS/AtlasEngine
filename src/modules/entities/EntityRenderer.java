package modules.entities;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import core.math.Maths;
import core.model.Material;
import core.model.Mesh;
import core.model.Model;
import core.renderer.MasterRenderer;

public class EntityRenderer {

	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.bind();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.unbind();
	}
	
	public void render(Map<Model, List< Entity>> entities) {
		for(Model model : entities.keySet()) {
			prepareModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getMesh().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			unbindModel();
		}
	}
	
	private void prepareModel(Model model) {
		Mesh rawModel = model.getMesh();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		Material material = model.getMaterial();
		shader.loadNumberOfRows(material.getDiffusemap().getNumberOfRows());
		if(material.getAlphamap() != null && material.getAlpha() == 0){
			MasterRenderer.disableCulling();
		}
		shader.loadUseFakeLighting(material.isUseFakeLighting());
		shader.loadShineVariables(material.getShineDamper(), material.getShininess());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMaterial().getDiffusemap().getId());
		shader.loadUseSpecularMap(material.getSpecularmap() != null);
		if(material.getSpecularmap() != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getSpecularmap().getId());
		}
	}

	private void unbindModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotation().x, entity.getRotation().y, entity.getRotation().z, entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	
}
