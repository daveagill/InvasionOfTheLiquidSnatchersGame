package week.of.awesome.framework;

public class FlipFlopTweener {

	private float alpha = 0f;
	
	public float getValue() {
		return Math.abs(alpha);
	}
	
	public float sampleValueAtOffset(float timeOffset) {
		float offsetAlpha = alpha + timeOffset;
		if (offsetAlpha < 0) {
			offsetAlpha = -offsetAlpha;
		}
		if (((int)offsetAlpha) % 2 == 1) {
			offsetAlpha = 1 - (offsetAlpha - (int)offsetAlpha);
		}
		else {
			offsetAlpha = offsetAlpha - (int)offsetAlpha;
		}
		return offsetAlpha;
	}
	
	public void resetToMin() {
		alpha = 0f;
	}
	
	public void resetToMax() {
		alpha = -1f;
	}
	
	public boolean updateTillEdgeChange(float dt) {
		alpha += dt;
		if (alpha >= 1) {
			alpha = -1f;
			return true;
		}
		if (alpha >= 0 && alpha-dt < 0) {
			alpha = 0f;
			return true;
		}
		return false;
	}
	
	public boolean updateContinuously(float dt) {
		alpha += dt;
		if (alpha >= 1) {
			alpha = -1 + (alpha - (int)alpha);
		}
		return alpha >= 0 && alpha-dt < 0;
	}
}
