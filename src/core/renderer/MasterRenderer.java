package core.renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import modules.entities.Entity;

public class MasterRenderer {

	private static List<Entity> entities = new ArrayList<Entity>();
	
	private RenderInfo renderInfo;
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
}
