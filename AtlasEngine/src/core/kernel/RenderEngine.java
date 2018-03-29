package core.kernel;

import core.configs.Default;

public class RenderEngine {

	private Window window;
	
	public RenderEngine() {
		window = Window.getInstance();
	}
	
	public void init() {
		window.init();
	}
	
	public void render() {
		Default.clearScreen();
		
		// Alle Rendercalls
		
		// Update Window
		
		window.update();
	}
	
	public void update() {
		
	}
	
	public void shutdown() {
		
	}
}
