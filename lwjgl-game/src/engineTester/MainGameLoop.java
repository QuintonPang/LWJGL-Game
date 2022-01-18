package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
			
		ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture2"));
		playerTexture.setUseFakeLighting(true);
		Player player = new Player(new TexturedModel(OBJLoader.loadObjModel("person", loader),playerTexture),new Vector3f(-500,100,-500),0,0,0,1);
	
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		
		// StaticShader shader = new StaticShader();
		
		// Renderer renderer = new Renderer(shader);
		
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
		
		/* PARTICLE SYSTEM */
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		ParticleSystem particleSystem = new ParticleSystem(new ParticleTexture(loader.loadTexture("particles/particleAtlas"),4,true), 40, 10, 0.1f, 1, 10);
		
		// ********** FONT **********
		TextMaster.init(loader);
		FontType font = new FontType(loader.loadTexture("fonts/candara"), "candara");
		GUIText text = new GUIText("This is a text!",5 , font, new Vector2f(0,0), 1f, true);
		text.setColour(0, 0, 0);
		// ********************
		
		// ********** TERRAIN TEXTURE STUFF **********
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		// *******************************************
		
		
		Terrain terrain = new Terrain(-1,-1,loader,texturePack,blendMap,"heightMap");
		Terrain terrain2 = new Terrain(-1,0,loader,texturePack,blendMap,"heightMap");
		Terrain terrain3 = new Terrain(0,-1,loader,texturePack,blendMap,"heightMap");
		Terrain terrain4 = new Terrain(0,0,loader,texturePack,blendMap,"heightMap");
		
		Terrain[][] terrains = new Terrain[2][2];
		terrains[0][0] = terrain;
		terrains[0][1] = terrain2;
		terrains[1][0] = terrain3;
		terrains[1][1] = terrain4;
		
		List<Terrain> terrainList = new ArrayList<Terrain>();
		terrainList.add(terrain);
		
		
		//********** RENDERING ENTITIES **********
		
		//RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
		//TexturedModel texturedModel = new TexturedModel(model,texture);
		RawModel model = OBJLoader.loadObjModel("monkey",loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("monkeyTexture"));
		
		// specular lighting
		texture.setShineDamper(10);
		texture.setReflectivity(1);	
		TexturedModel texturedModel = new TexturedModel(model,texture);
		
		List<Entity>monkeys = new ArrayList<Entity>();
		
		List<Entity>normalMapEntities = new ArrayList<Entity>();

		
		//******************NORMAL MAP MODELS************************
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		/*
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		
		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);
		*/
		
		Random random = new Random();
		
		//************ENTITIES*******************
		
		Entity barrelEntity = new Entity(barrelModel, new Vector3f(0, 10, 0), 0, 0, 0, 1f);
		//Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
		//Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
		normalMapEntities.add(barrelEntity);
		//normalMapEntities.add(entity2);
		//normalMapEntities.add(entity3);
		
		for(int i=0;i<10;i++) {
			float x = random.nextFloat()*500-250;
			float z = random.nextFloat()*-300;
			int gridX = (int) (Math.floor(x/Terrain.getSize())) + 1;
			int gridZ = (int) (Math.floor(z/Terrain.getSize())) + 1;
			float y = terrains[gridX][gridZ].getHeightOfTerrain(x, z);
			Entity entity = new Entity(texturedModel,new Vector3f(x,y+5,z),0,0,0,2);	
			monkeys.add(entity);
		}
		
		List<Entity>ferns = new ArrayList<Entity>();
		
		//ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("flowers"));
		fernTexture.setHasTransparency(true);
		fernTexture.setUseFakeLighting(true); // no dark side
		fernTexture.setNumberOfRows(2);
		
		for(int i=0;i<1500;i++) {
			float x = random.nextFloat()*-500;
			float z = random.nextFloat()*-500;
			int gridX = (int) (Math.floor(x/Terrain.getSize())) + 1;
			int gridZ = (int) (Math.floor(z/Terrain.getSize())) + 1;
			float y = terrains[gridX][gridZ].getHeightOfTerrain(x, z);
			Entity entity = new Entity(new TexturedModel(OBJLoader.loadObjModel("fern", loader),fernTexture),random.nextInt(4),new Vector3f(x,y,z),0,0,0,1);		
			ferns.add(entity);
		}
		
		List<Entity>trees = new ArrayList<Entity>();
		
		ModelTexture treeTexture = new ModelTexture(loader.loadTexture("tree"));
		treeTexture.setHasTransparency(true);
		treeTexture.setUseFakeLighting(true);
		
		for(int i=0;i<100;i++) {
			float x = random.nextFloat()*1000-500;
			float z = random.nextFloat()*-300;
			int gridX = (int) (Math.floor(x/Terrain.getSize())) + 1;
			int gridZ = (int) (Math.floor(z/Terrain.getSize())) + 1;
			float y = terrains[gridX][gridZ].getHeightOfTerrain(x, z);
			Entity entity = new Entity(new TexturedModel(OBJLoader.loadObjModel("tree", loader),treeTexture),new Vector3f(x,y,z),0,0,0,8);		
			trees.add(entity);
		}
		
		List<Entity>lowPolyTrees = new ArrayList<Entity>();

		ModelTexture lowPolyTreeTexture = new ModelTexture(loader.loadTexture("lowPolyTree"));
		lowPolyTreeTexture.setHasTransparency(true);
		lowPolyTreeTexture.setUseFakeLighting(true);
		
		for(int i=0;i<75;i++) {
			float x = random.nextFloat()*1000-500;
			float z = random.nextFloat()*-300;
			int gridX = (int) (Math.floor(x/Terrain.getSize())) + 1;
			int gridZ = (int) (Math.floor(z/Terrain.getSize())) + 1;
			float y = terrains[gridX][gridZ].getHeightOfTerrain(x, z);
			Entity entity = new Entity(new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),lowPolyTreeTexture),new Vector3f(x,y,z),0,0,0,1);		
			lowPolyTrees.add(entity);
		}
		
		// ********************
		
		// ********** LIGHTS **********
		List<Light> lights = new ArrayList<Light>();
		Light light = new Light(new Vector3f(1000000,1500000,-1000000), new Vector3f(1.3f,1.3f,1.3f),new Vector3f(1,0,0)); // smaller the value of attenuation, the greater the range of illumination
		lights.add(light);
		//lights.add(new Light(new Vector3f(-185,10,-293), new Vector3f(10,0,0),new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(270,17,-300), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		// ********************
		
		// ********** GUIs **********
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"),new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
		// image of map of shadows to be rendered
		// GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f,0.5f), new Vector2f(0.5f,0.5f));
		
		guis.add(gui);
		// guis.add(shadowMap);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		// ********************
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(),terrains);
		
		// *********** ENTITIES **********
		List<Entity> entities = new ArrayList<Entity>();
		for(Entity monkey:monkeys) {
			entities.add(monkey);
		}
		for(Entity fern:ferns) {
			entities.add(fern);	
		}
		for(Entity tree:trees) {
			entities.add(tree);	
		}
		for(Entity lowPolyTree:lowPolyTrees) {
			entities.add(lowPolyTree);		
		}
		entities.add(player);
		
		TexturedModel cherryModel = new TexturedModel(OBJLoader.loadObjModel("specular/cherry", loader), new ModelTexture(loader.loadTexture("specular/cherry")));
		cherryModel.getTexture().setHasTransparency(true);
		cherryModel.getTexture().setShineDamper(10);
		cherryModel.getTexture().setReflectivity(0.5f);
		cherryModel.getTexture().setExtraInfoMap(loader.loadTexture("specular/cherryS"));
		Entity cherry = new Entity(cherryModel, new Vector3f(player.getPosition().x,terrains[0][0].getHeightOfTerrain(player.getPosition().x, player.getPosition().z), player.getPosition().z), 0, 0, 0, 10);
		entities.add(cherry);
		// ********************
		
		// ********** WATER **********
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader,renderer.getProjectionMatrix(), fbos);
		List<WaterTile>waters = new ArrayList<WaterTile>();
		// height indicates how high the water surface should be from the terrain
		WaterTile water = new WaterTile(player.getPosition().x+4,player.getPosition().z,0);
		WaterTile water2 = new WaterTile(player.getPosition().x,player.getPosition().z,0);
		WaterTile water3 = new WaterTile(player.getPosition().x-4,player.getPosition().z,0);
		waters.add(water);
		waters.add(water2);
		waters.add(water3);
		
		// GuiTexture reflection= new GuiTexture(fbos.getReflectionTexture(), new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
		// GuiTexture refraction= new GuiTexture(fbos.getRefractionTexture(), new Vector2f(-0.5f,0.5f), new Vector2f(0.25f,0.25f));
//		guis.add(reflection);
//		guis.add(refraction);
		
		// ********************
		
		// ********** POST-PROCESSING EFFECTS ***********
		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(),Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		// *******************
		
		while(!Display.isCloseRequested()) {
			
			// game logic
			// render
			//entity.increasePosition(0, 0, -0.1f);
			//entity.increaseRotation(0, 1, 0);
			
			// clip distance (any point with negative value is not rendered)
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			// for reflection
			float distance = 2 * (camera.getPosition().getY() - water.getHeight());
			camera.getPosition().y-=distance;
			camera.invertPitch();
			fbos.bindReflectionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrainList, lights, camera, new Vector4f(0,1,0,-water.getHeight()+1)); // pointing upwards
																																		// +1 to remove glitch
			camera.getPosition().y+=distance;
			camera.invertPitch();
			// for refraction
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrainList, lights, camera, new Vector4f(0,-1,0,water.getHeight())); // render everything under the water
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer(); // switch back to default buffer
			
			int gridX = (int) (Math.floor(player.getPosition().x/Terrain.getSize())) + 1;
			int gridZ = (int) (Math.floor(player.getPosition().z/Terrain.getSize())) + 1;
		
			camera.move();
			player.move(terrains[gridX][gridZ]);  // gets input of keyboard
			
			ParticleMaster.update(camera);
			picker.update();
			
			renderer.renderShadowMap(entities, light);
			
			if(picker.getCurrentTerrainPoint()!=null) { // if cursor is not pointing away from terrain
				//Entity monkeyFollowsCursor = new Entity(texturedModel,picker.getCurrentTerrainPoint(),0,0,0,2);	
				//renderer.processEntity(monkeyFollowsCursor);
				if(lights.size()==2) lights.remove(1); // update player's light
				Terrain terrainIlluminated = terrains[gridX][gridZ];
				lights.add(new Light(new Vector3f(picker.getCurrentTerrainPoint().x,terrainIlluminated.getHeightOfTerrain(picker.getCurrentTerrainPoint().x,picker.getCurrentTerrainPoint().z)+3,picker.getCurrentTerrainPoint().z), new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
			} 
			/*
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processTerrain(terrain3);
			renderer.processTerrain(terrain4);
			*/
			renderer.processEntity(player);

			
			
			
			for(Entity monkey:monkeys) {
				monkey.increaseRotation(0, 1, 0);
			}
			/*
			renderer.prepare();
			shader.start();
			shader.loadLight(light);
			shader.loadViewMatrix(camera); // load view matrix
			//renderer.render(model);
			renderer.render(entity,shader);
			shader.stop();
			*/
			//renderer.render(lights, camera);
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				// new vector3f of player so that player's position does not get affected
				//new Particle(new Vector3f(player.getPosition()),new Vector3f(0,30,0),1,4,0,1);
				particleSystem.generateParticles(player.getPosition());
			}
			
			// where user-defined fbo comes in
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrainList, lights, camera, new Vector4f(0, 1, 0, 1 -water.getHeight()+1));
			waterRenderer.render(waters, camera, light);
			ParticleMaster.renderParticles(camera);
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToScreen(); // straight to screen
			// multisampleFbo.resolveToFbo(outputFbo);
			// ostProcessing.doPostProcessing(outputFbo.getColourTexture());
			// any renderere after this does not get affected by the fbo
			
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		
		//shader.cleanUp();
		fbo.cleanUp();
		multisampleFbo.cleanUp();
		outputFbo.cleanUp();
		PostProcessing.cleanUp();
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		ParticleMaster.cleanUp();
		fbos.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		// after exiting, close display
		DisplayManager.closeDisplay();
	}

}
