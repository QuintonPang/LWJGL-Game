package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class Loader {
	
	// memory management, delete VAO and VBO after closing game
	// keeps track of VAOs and VBOs created
	private List<Integer> vaos = new ArrayList <Integer>();
	private List<Integer> vbos = new ArrayList <Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		// parameters: index in VAO, number of vectors, array to be stored
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,textureCoords);
		storeDataInAttributeList(2,3,normals);
		unbindVAO();
		//return new RawModel(vaoID,positions.length/3); // divide by 3 because there are x,y and z
		return new RawModel(vaoID,indices.length);
	}
	
	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		/* useless
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		*/
		   
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
		
	}
	
	// delete all VAOs and VBOs
	public void cleanUp() {
		for (int vao:vaos) GL30.glDeleteVertexArrays(vao);
		for (int vbo:vbos) GL30.glDeleteVertexArrays(vbo);
		for (int texture:textures) GL11.glDeleteTextures(texture);
	}
	
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays(); // create empty VAO
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID); // activate VAO
		return vaoID;
	}
	
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers(); // create VBO
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // 3rd parameter is what are we going to do with the vbo
		// static draw means do nothing with it
		
		// 1st arg is number of attribute lists, 2nd is length of vertex (3 for 3d vectors x, y, z), 3rd is type of data, 4th is normalize or not, 5th is length between vertices, 6th is offset of vertices
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);
	}
	
	// when finish using VAO
	private void unbindVAO() {
		GL30.glBindVertexArray(0); // unbind currently binded VAO
	}
	
	// index buffer, makes rendering more efficient
	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers(); // create empty vbo;#
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	// convert float array into float buffer
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data); // put data
		buffer.flip(); // indicates finished writing and preparing to be read
		return buffer;
	}

}