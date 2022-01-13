package particles;

public class ParticleTexture {
	private int textureID;
	private int numberofRows;
	private boolean additive;
	
	public ParticleTexture(int textureID, int numberofRows, boolean additive) {
		this.textureID = textureID;
		this.numberofRows = numberofRows;
		this.additive = additive;
	}
	
	public boolean isAdditive() {
		return additive;
	}

	public int getTextureID() {
		return textureID;
	}
	public int getNumberofRows() {
		return numberofRows;
	}
	
}
