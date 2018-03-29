package core.kernel;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import core.utils.Constants;

public class Window {

	private static Window instance = null;
	
	private int width;
	private int height;
	
	private boolean resizable = true;
	
	public static Window getInstance() {
		if(instance == null)
			instance = new Window();
		return instance;
	}
	
	public void init() {
		
	}
	
	public void create(int width, int height) {
		this.width = width;
		this.height = height;
		
		ContextAttribs attribs = new ContextAttribs(3,3)
				.withForwardCompatible(true)
				.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
			Display.setTitle("Atlas Engine");
			Display.setResizable(resizable);
		} catch (LWJGLException e) {
			System.err.println(" Error: Failed to create: " + this.getClass().getName());
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, width, height);	
	}
	
	public void update() {
		if(Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			width = Display.getWidth();
			height = Display.getHeight();
		}
		
		Display.sync(Constants.FPS_CAP);
		Display.update();
	}
	
	public boolean isCloseRequested() {
		return Display.isCloseRequested();
	}
	
	public void closeDisplay() {
		Display.destroy();
	}
	
	public void SetWindowSize(int width, int height) {
		if(resizable) {
			this.width = width;
			this.height = height;
			GL11.glViewport(0, 0, width, height);
		}
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	
}















