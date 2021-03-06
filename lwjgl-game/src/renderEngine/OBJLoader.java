package renderEngine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

public class OBJLoader {
	public static RawModel loadObjModel(String fileName, Loader loader) {
//		FileReader fr = null;
//		try {
//			fr = new FileReader(new File("res/"+fileName+".obj"));
//		} catch (FileNotFoundException e) {
//			System.err.println("Couldn't load file!");
//			e.printStackTrace();
//		}
		InputStreamReader isr = new InputStreamReader(OBJLoader.class.getResourceAsStream("/res/"+fileName+".obj"));
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>(); // for lighting // line perpendicular to the surface
		List<Integer> indices = new ArrayList<Integer>();
		// eventually, we need data in float array
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		try {
			while(true) {
				line = reader.readLine(); // read obj file
				String[] currentLine = line.split(" "); // split each lines
				if(line.startsWith("v ")) {
					// if line is vertex position
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				}else if(line.startsWith("vt ")) {
					// if line is texture coordinates
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]));
					textures.add(texture);
				}else if(line.startsWith("vn ")) {
					// if line is normal
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3]));
					normals.add(normal);
				}else if(line.startsWith("f ")) {
					textureArray = new float[vertices.size()*2]; // 2 coordinates
					normalsArray = new float[vertices.size()*3]; // 3 coordinates
					break;
				}
				
			}
			while(line!=null) { // if not the end of obj file
				// skipping till reaches arrangement info
				if(!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				
				// arrangement info
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3= currentLine[3].split("/");
				
				processVertex(vertex1,indices,textures,normals,textureArray,normalsArray);
				processVertex(vertex2,indices,textures,normals,textureArray,normalsArray);
				processVertex(vertex3,indices,textures,normals,textureArray,normalsArray);
				line = reader.readLine();
			}
			
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		
		// copying data from vector to coords
		for(Vector3f vertex:vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		for(int i=0;i<indices.size();i++) {
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
	}
	
	// sort out the texture coords and normal vectors for current vertex and put them to correct positions
	private static void processVertex(String[] vertexData, List<Integer>indices,
			List<Vector2f>textures,List<Vector3f>normals,float[]textureArray,
			float[] normalsArray) {
		int currentVertexPointer = Integer.parseInt(vertexData[0])-1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
		textureArray[currentVertexPointer*2] = currentTex.x;
		textureArray[currentVertexPointer*2+1] = 1 - currentTex.y; //opengl starts from top left
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
		normalsArray[currentVertexPointer*3] = currentNorm.x;
		normalsArray[currentVertexPointer*3+1] = currentNorm.y;
		normalsArray[currentVertexPointer*3+2] = currentNorm.z;
	}
}
