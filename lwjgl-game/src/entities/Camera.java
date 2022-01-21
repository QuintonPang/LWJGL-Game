package entities;

import org.lwjgl.input.Mouse;
//import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	// initial position
	 private Vector3f position = new Vector3f(100,35,50);
	// for looking at cube map reflections
//	private Vector3f position = new Vector3f(0,0,0);
	private float pitch=20; // how high or low it is aimed
	private float yaw; // how left or right the camera is aiming
	private float roll; // how much it is rotated
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer;
	private Player player;
	
	public Camera() {
	}
	
	public Camera(Player player) {
		this.player = player;
	}
	
	
	public void move() {/*
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) position.z -= 0.1f;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) position.x += 0.1f;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) position.x -= 0.1f;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) position.z += 0.1f;*/
		
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
//		this.yaw = 360 - angleAroundPlayer;
		this.yaw%=360;
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
//		float theta = angleAroundPlayer;
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y +  verticalDistance + 4;
		position.z = player.getPosition().z - offsetZ;
//		position.x =   offsetX;
//		position.y =   verticalDistance + 4;
//		position.z =   offsetZ;
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
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f; // 0.1f affects sensitivity
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		if(Mouse.isButtonDown(1)) {
			// right button
			float pitchChange = Mouse.getDY() * 0.1f; // how much position of mouse is moved up and down
			pitch -= pitchChange;
			if(pitch < 0){
				pitch = 0;
			}else if(pitch > 90){
				pitch = 90;
			} 
		}
	}
	
	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f; // how much position of mouse is moved left and right
			angleAroundPlayer -= angleChange;
		}
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}


	public void invertPitch() {
		this.pitch = -pitch;	
	}
	
}
