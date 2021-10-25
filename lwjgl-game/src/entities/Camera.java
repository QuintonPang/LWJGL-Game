package entities;

//import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	// initial position
	private Vector3f position = new Vector3f(100,35,50);
	private float pitch=20; // how high or low it is aimed
	private float yaw; // how left or right the camera is aiming
	private float roll; // how much it is rotated
	
	public Camera() {}
	
	
	public void move() {/*
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) position.z -= 0.1f;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) position.x += 0.1f;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) position.x -= 0.1f;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) position.z += 0.1f;*/
	}
	

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
}
