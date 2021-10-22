package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);
		
		/*
		// list of vertices of quad to be rendered
		// openGL expects vertices to be defined anti-clockwise by default
		float[] vertices = {
				// Left bottom triangle
				-0.5f, 0.5f, 0f, //V0
				-0.5f, -0.5f, 0f, //V1
				//0.5f, -0.5f, 0f,
				// Right top triangle
				0.5f, -0.5f, 0f, //V2
				0.5f, 0.5f, 0f, //V3
				//-0.5f, 0.5f, 0f
		};
		
		// order of vertices to be rendered
		int[] indices = {
				0,1,3, // Top left triangle (V0,V1,V3)
				3,1,2	// Bottom right triangle (V3,V1,V2)
		};
		
		// coordinates of corners of texture
		float[] textureCoords = {
				0,0, //V0
				0,1, //V1
				1,1, //V2
				1,0 //V3
		};
		*/
		
		
		//RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
		//TexturedModel texturedModel = new TexturedModel(model,texture);
		RawModel model = OBJLoader.loadObjModel("dragon", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("white"));
		
		// specular lighting
		texture.setShineDamper(10);
		texture.setReflectivity(1);	
		
		TexturedModel texturedModel = new TexturedModel(model,texture);
		
		Entity entity = new Entity(texturedModel,new Vector3f(0,0,-50),0,0,0,1);
		Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));
		
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()) {
			// game logic
			// render
			//entity.increasePosition(0, 0, -0.1f);
			entity.increaseRotation(0, 1, 0);
			camera.move(); // gets input of keyboard
			renderer.prepare();
			shader.start();
			shader.loadLight(light);
			shader.loadViewMatrix(camera); // load view matrix
			//renderer.render(model);
			renderer.render(entity,shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		// after exiting, close display
		DisplayManager.closeDisplay();
	}

}
