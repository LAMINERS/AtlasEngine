package core.model;

public class Mesh {

	private int vaoID;
	private int vertexCount;
	
	public Mesh(int vaoId,  int vertexCount) {
		this.vaoID = vaoId;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
}
