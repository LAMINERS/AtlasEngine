package modules.entities;

import org.lwjgl.util.vector.Vector3f;

import core.model.Model;

public class Entity {

	private Model model;
	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	private int textureIndex = 0;
	
	public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public Entity(Model model, int textureIndex, Vector3f position, Vector3f rotation, Vector3f scale) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.textureIndex = textureIndex;
	}
	
	public float getTextureXOffset(){
		int column = textureIndex % model.getMaterial().getDiffusemap().getNumberOfRows();
		return (float)column / (float)model.getMaterial().getDiffusemap().getNumberOfRows();
	}
	
	public float getTextureYOffset(){
		int row = textureIndex / model.getMaterial().getDiffusemap().getNumberOfRows();
		return (float)row / (float)model.getMaterial().getDiffusemap().getNumberOfRows();
	}
	
	public void increasePosition(Vector3f deltaPosition) {
		position.x += deltaPosition.x;
		position.y += deltaPosition.y;
		position.z += deltaPosition.z;
	}
	
	public void increaseRotation(Vector3f deltaRotation) {
		rotation.x += deltaRotation.x;
		rotation.y += deltaRotation.y;
		rotation.z += deltaRotation.z;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}
}
