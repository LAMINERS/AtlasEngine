package modules.terrain;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import core.kernel.Camera;
import core.math.Maths;
import core.shader.Shader;
import core.utils.Constants;
import core.utils.ResourceLoader;
import modules.entities.Light;

public class TerrainShader extends Shader{

	private static final String VERTEX_FILE = "/res/shader/terrain.vert";
	private static final String FRAGMENT_FILE = "/res/shader/terrain.frag";
	
	public TerrainShader() {
		super();
		
		addVertexShader(ResourceLoader.loadShader(VERTEX_FILE));
		addFragmentShader(ResourceLoader.loadShader(FRAGMENT_FILE));
		compileShader();
		
		addUniform("transformationMatrix");
		addUniform("projectionMatrix");
		addUniform("viewMatrix");
		for(int i = 0; i < Constants.MAX_LIGHTS; i++) {
			addUniform("lightPosition[" + i + "]");
			addUniform("lightColor[" + i + "]");
			addUniform("attenuation[" + i + "]");
		}
		addUniform("shineDamper");
		addUniform("reflectivity");
		addUniform("skyColor");
		
		for(int i = 0; i < Constants.MAX_TERRAIN_MAT; i++) {
			addUniform("materials[" + i + "].diffusemap");
			addUniform("materials[" + i + "].normalmap");
			addUniform("materials[" + i + "].specularmap");
		}
	}
	
	public void connectTextureUnits() {
		for(int i = 0; i < Constants.MAX_TERRAIN_MAT; i++) {
			loadUniformi("materials[" + i + "].diffusemap", 1 + i);
			loadUniformi("materials[" + i + "].normalmap", Constants.MAX_TERRAIN_MAT * 2 + i);
			loadUniformi("materials[" + i + "].normalmap", Constants.MAX_TERRAIN_MAT * 3 + i);
		}		
	}
	
	public void loadTransformationMatrix(Matrix4f transformationMatrix) {
		loadUniformMatrix4f("transformationMatrix", transformationMatrix);
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		loadUniformf("shineDmaper", damper);
		loadUniformf("reflectivity", reflectivity);
	}
	
	public void loadSkyColor(Vector3f skyColor) {
		loadUniform3f("skyColor", skyColor);
	}
	
	public void loadLights(List<Light> lights) {
		for(int i = 0; i < Constants.MAX_LIGHTS; i++) {
			if(i < lights.size()) {
				loadUniform3f("lightPosition[" + i + "]", lights.get(i).getPosition());
				loadUniform3f("lightColor[" + i + "]", lights.get(i).getColor());
				loadUniform3f("attenuation[" + i + "]", lights.get(i).getAttenuation());
			} else {
				loadUniform3f("lightPosition[" + i + "]", new Vector3f(0,0,0));
				loadUniform3f("lightColor[" + i + "]",  new Vector3f(0,0,0));
				loadUniform3f("attenuation[" + i + "]",  new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadUniformMatrix4f("viewMatrix", viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		loadUniformMatrix4f("projectionMatrix", projection);
	}
}
