package core.kernel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import core.utils.Constants;

public class CoreEngine {
	
	private static int fps;
	private static float framerate = 144;
	private static float frameTime = 1.0f / framerate;
	private boolean isRunning;
	private RenderEngine renderEngine;
	
	public void createWindow(int width, int height) {
		Window.getInstance().create(width, height);
		renderEngine = new RenderEngine();
		
		getDeviceProperties();
	}
	
	public void init() {
		core.configs.Default.init();
		renderEngine.init();
	}
	
	public void run() {
		
		this.isRunning = true;
		
		int frames = 0;
		long frameCounter = 0;
		
		long lastTime = System.nanoTime();
		double unprocessedTime = 0;
		
		// render Loop
		while(isRunning)
		{
			boolean render = false;
			
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;
			
			unprocessedTime += passedTime / (double) Constants.NANOSECOND;
			frameCounter += passedTime;
		
			
			while(unprocessedTime > frameTime)
			{

				render = true;
				unprocessedTime -= frameTime;
				
				if(Window.getInstance().isCloseRequested())
					stop();
				
				update();
				
				if(frameCounter >= Constants.NANOSECOND)
				{
					setFps(frames);
					frames = 0;
					frameCounter = 0;
				}
			}
			if(render)
			{
				render();
				frames++;
			}
			else
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
		}
		
		cleanUp();	
	}
	
	public void start() {
		if(isRunning)
			return;
		run();
	}
	
	public void stop() {
		if(!isRunning)
			return;
		isRunning = false;
	}
	
	private void render() {
		renderEngine.render();
	}
	
	private void update() {
		renderEngine.update();
		//TODO Camera update
	}
	
	private void cleanUp() {
		Window.getInstance().closeDisplay();
	}
	
	private void getDeviceProperties() {
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		System.out.println("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS+ " bytes");
		System.out.println("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		System.out.println("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		System.out.println("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		System.out.println("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");		
	}

	public static int getFps() {
		return fps;
	}

	public static void setFps(int fps) {
		CoreEngine.fps = fps;
	}

	public static float getFrameTime() {
		return frameTime;
	}

	
}
