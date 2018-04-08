package core.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import core.kernel.Camera;
import core.kernel.Window;
import core.model.Model;
import core.utils.Constants;
import modules.entities.Entity;
import modules.entities.EntityRenderer;
import modules.entities.Light;
import modules.entities.StaticShader;

public class MasterRenderer {
	
	private Matrix4f projectionMatrix;
	
	private StaticShader staticShader;
	private EntityRenderer entityRenderer;
	
	private Map<Model, List<Entity>> entities = new HashMap<Model, List<Entity>>();
	
	public MasterRenderer() {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
	}
	
	public void renderScene(List<Entity> entities, List<Light> lights, Camera camera) {
		for(Entity entity : entities) {
			processEntity(entity);
		}
	}
	
	private void processEntity(Entity entity) {
		// TODO Auto-generated method stub
		Model model = entity.getModel();
		List<Entity> batch = entities.get(model);
		if(batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(model, newBatch);
		}
	}

	public void render(List<Light> lights, Camera camera) {
		prepare();
		// Statische objekte rendern
		staticShader.bind();
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		staticShader.unbind();
		
		// Alle listen leeren
		entities.clear();
	}
	
	private void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);	//TODO SkyColor als farbe einfügen
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Window.getInstance().getWidth() / (float) Window.getInstance().getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(Constants.FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = Constants.FAR_PLANE - Constants.NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((Constants.FAR_PLANE + Constants.NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * Constants.NEAR_PLANE * Constants.FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
}
