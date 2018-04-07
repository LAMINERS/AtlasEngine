package core.renderer;

import core.configs.RenderConfig;
import core.shader.Shader;

public class RenderInfo {

	private Shader shader;
	private RenderConfig config;
	
	public RenderInfo(Shader shader, RenderConfig config) {
		this.shader = shader;
		this.config = config;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public RenderConfig getConfig() {
		return config;
	}

	public void setConfig(RenderConfig config) {
		this.config = config;
	}
}
