package core.shader;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Shader {
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	private int program;
	private HashMap<String, Integer> uniforms;
	
	public Shader() {
		program = GL20.glCreateProgram();
		uniforms = new HashMap<String, Integer>();
		
		if(program == 0) {
			System.err.println("Shader creating failed");
			System.exit(1);
		}
	}
	
	public void bind() {
		GL20.glUseProgram(program);
	}
	
	public void addUniform(String uniform) {
		
		int uniformLocation = GL20.glGetUniformLocation(program, uniform);
		
		if(uniformLocation == 0xFFFFFFFF) {
			System.err.println(this.getClass().getName() + "Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		uniforms.put(uniform, uniformLocation);
	}
	
	public void addUniformBlock(String uniform) {
		
		int uniformLocation = GL31.glGetUniformBlockIndex(program, uniform);
				
		if(uniformLocation == 0xFFFFFFFF) {
			System.err.println(this.getClass().getName() + "Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		uniforms.put(uniform, uniformLocation);
	}
	
	public void addVertexShader(String text)
	{
		addProgram(text, GL20.GL_VERTEX_SHADER);
	}
	
	public void addGeometryShader(String text)
	{
		addProgram(text, GL32.GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShader(String text)
	{
		addProgram(text, GL20.GL_FRAGMENT_SHADER);
	}
	
	public void addTessellationControlShader(String text)
	{
		addProgram(text, GL40.GL_TESS_CONTROL_SHADER);
	}
	
	public void addTessellationEvaluationShader(String text)
	{
		addProgram(text, GL40.GL_TESS_EVALUATION_SHADER);
	}
	
	public void addComputeShader(String text)
	{
		addProgram(text, GL43.GL_COMPUTE_SHADER);
	}
	
	public void compileShader() {
		
		GL20.glLinkProgram(program);
		
		if(GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) {
			System.err.println(this.getClass().getName() + " " + GL20.glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
	}
	
	public void addProgram(String text, int type) {
		
		int shader = GL20.glCreateShader(type);
		
		if(shader == 0) {
			System.err.println(this.getClass().getName() + "Shader creating failed");
			System.exit(1);
		}
		
		GL20.glShaderSource(shader, text);
		GL20.glCompileShader(shader);
		
		if(GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
			System.err.println(this.getClass().getName() + " " + GL20.glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		
		GL20.glAttachShader(program, shader);
	}
	
	public void loadUniformi(String uniformName, int value) {
		GL20.glUniform1i(uniforms.get(uniformName), value);
	}
	
	public void loadUniformf(String uniformName, float value) {
		GL20.glUniform1f(uniforms.get(uniformName), value);
	}
	
	public void loadUniform2f(String uniformName, Vector2f value) {
		GL20.glUniform2f(uniforms.get(uniformName), value.x, value.y);
	}
	
	public void loadUniform3f(String uniformName, Vector3f value) {
		GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
	}
	
	public void loadUniformMatrix4f(String uniformName, Matrix4f value) {
		value.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(uniforms.get(uniformName), false, matrixBuffer);
	}
	
	public void bindUniformBlock(String uniformBlockName, int uniformBlockBinding )
	{
		GL31.glUniformBlockBinding(program, uniforms.get(uniformBlockName), uniformBlockBinding);
	}
	
	public int getProgram() {
		return this.program;
	}	
}






























