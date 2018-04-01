package core.utils;

import java.io.BufferedReader;
import java.io.FileReader;

public class ResourceLoader {

	public static String loadShader(String fileName) {
		StringBuilder shaderResource = new StringBuilder();
		BufferedReader shaderReader = null;
		
		try {
			shaderReader = new BufferedReader(new FileReader("./res/" + fileName));
			String line = shaderReader.readLine();
			while((line = shaderReader.readLine()) != null) {
				shaderResource.append(line).append("\n");
			}
			shaderReader.close();
		} catch (Exception e) {
			System.err.println("Error: Failed to load Shader: " + fileName);
			e.printStackTrace();
			System.exit(1);
		}
		
		return shaderResource.toString();
	}
}
