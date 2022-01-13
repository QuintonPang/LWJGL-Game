package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import renderEngine.DisplayManager;

public class Particle {

	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private float elapsedTime = 0;
	private float distance; // distance from camera to determine which particle show up in front
	
	private Vector3f reusableChange = new Vector3f();

	private ParticleTexture texture;
	
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;
	
	private boolean alive = false;
	
	public float getDistance() {
		return distance;
	}
	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlend() {
		return blend;
	}


	public Particle() {
//		this.texture = texture;
//		this.position = position;
//		this.velocity = velocity;
//		this.gravityEffect = gravityEffect;
//		this.lifeLength = lifeLength;
//		this.rotation = rotation;
//		this.scale = scale;
//		ParticleMaster.addParticle(this);
	}
	
	public void setActive(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation,
			float scale) {
		alive = true;
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		ParticleMaster.addParticle(this);
	}

	public float getRotation() {
		return rotation;
	}


	public float getScale() {
		return scale;
	}


	public Vector3f getPosition() {
		return position;
	}
	
	// check if particle is alive
	protected boolean update(Camera camera) {
		velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		// Vector3f change = new Vector3f(velocity);
//		change.scale(DisplayManager.getFrameTimeSeconds());
		// more efficient
		reusableChange.set(velocity);
		reusableChange.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(reusableChange, position, position);
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		updateTextureCoordInfo();
		return elapsedTime < lifeLength;
	}
	
	private void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		// total stages 
		int stageCount = texture.getNumberofRows() * texture.getNumberofRows();
		// current stage
		float atlasProgession = lifeFactor * stageCount; 
		int index1 = (int) Math.floor(atlasProgession);
		// subsequent index
		int index2 = index1<stageCount-1? index1+1 : index1;
		this.blend = atlasProgession%1;
		setTextureOffset(texOffset1,index1);
		setTextureOffset(texOffset2,index2);
	}
	
	private void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberofRows();
		int row = index / texture.getNumberofRows();
		offset.x = (float)column / texture.getNumberofRows();
		offset.y = (float)row / texture.getNumberofRows();
	}

	public ParticleTexture getTexture() {
		return texture;
	}
	
}
