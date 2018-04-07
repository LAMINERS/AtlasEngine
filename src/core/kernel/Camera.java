package core.kernel;

import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	
	private Vector3f forwardVector;
	private Vector3f rightVector;
	
	private static Camera instance = null;
	
	public static Camera getInstance() {
		if(instance == null)
			instance = new Camera();
		return instance;
	}
	
	protected Camera() {
		position = new Vector3f(0,0,0);
	}
	
	public void update() {
		move();
	}
	
	public void move() {
		checkInputs();
	}
	
	private void checkInputs() {
		
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public Vector3f getForwardVector() {
		return forwardVector;
	}

	public Vector3f getRightVector() {
		return rightVector;
	}
}
