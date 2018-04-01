package modules.entities;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import core.shader.Shader;
import core.utils.Constants;
import core.utils.ResourceLoader;

public class StaticShader extends Shader {
	
	private static StaticShader instance = null;
	
	public static StaticShader getInstance() {
		if(instance == null) 
			instance = new StaticShader();
		return instance;
	}
	
	public StaticShader() {
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/static_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/static_FS.glsl"));
		compileShader();
		
		addUniform("transformationMatrix");
		addUniform("projectionMatrix");
		addUniform("viewMatrix");
		for(int i = 0; i < Constants.MAX_LIGHTS; i++) {
			addUniform("lightPosition[" + i + "]");
			addUniform("lightColour[" + i + "]");
			addUniform("attenuation[" + i + "]");
		}
		addUniform("shineDamper");
		addUniform("reflectivity");
		addUniform("useFakeLighting");
		addUniform("skyColor");
		addUniform("numberOfRows");
		addUniform("offset");
		addUniform("specularMap");
		addUniform("useSpecularMap");
		addUniform("skyColor");
		addUniform("diffusemap");
		addUniform("cameraPosition");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		loadUniformMatrix4f("transformationMatrix", matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		loadUniformMatrix4f("projectionMatrix", matrix);
	}
	
	public void loadViewMatrix(Matrix4f matrix) {
		loadUniformMatrix4f("viewMatrix", matrix);
	}
	
	public void loadCameraPosition(Vector3f position) {
		loadUniform3f("cameraPosition", position);
	}
	
	public void loadNumberOfRows(int numberOfRows) {
		loadUniformi("numberOfRows", numberOfRows);
	}
	
	public void loadOffset(float x, float y) {
		loadUniform2f("offset", new Vector2f(x, y));
	}
	
	public void loadSkyColor(Vector3f skyColor) {
		loadUniform3f("skyColor", skyColor);
	}
	
	public void loadShineVariables(float shineDamper, float reflectivity) {
		loadUniformf("shineDamper", shineDamper);
		loadUniformf("reflectivity", reflectivity);
	}
	
	public void loadLights(List<Light> lights) {
		for(int i = 0; i < Constants.MAX_LIGHTS; i++) {
			if(i < lights.size()) {
				loadUniform3f("lightColor[" + i + "]", lights.get(i).getColor());
				loadUniform3f("lightPosition[" + i + "]", lights.get(i).getPosition());
				loadUniform3f("attenuation[" + i + "]", lights.get(i).getAttenuation());
			} else {
				loadUniform3f("lightColor[" + i + "]", new Vector3f(0,0,0));
				loadUniform3f("lightPosition[" + i + "]", new Vector3f(0,0,0));
				loadUniform3f("attenuation[" + i + "]", new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadUseFakeLighting(boolean useFakeLighting) {
		loadUniformb("useFakeLighting", useFakeLighting);
	}
	
	public void loadUseSpecularMap(boolean useSpecularmap) {
		loadUniformb("useSpecularMap", useSpecularmap);
	}
	
}
