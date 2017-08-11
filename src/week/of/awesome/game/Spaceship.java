package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;

public class Spaceship implements Activatable {
	
	private static final float VERTICAL_ACCELLERATION = 6f;
	private static final float WIGGLE_SPEED = 0.5f;
	private static final float WIGGLE_AMPLITUDE = 2f;
	
	private Vector2 position;
	private float xOffset;
	private float yOffset;
	private float ySpeed;
	private boolean activated;
	
	public Spaceship(SpaceshipSpec spec) {
		this.position = spec.position;
	}
	
	public Vector2 getPosition() {
		return position.cpy().add(xOffset, yOffset);
	}

	@Override
	public void activate() {
		activated = true;
	}

	
	public void update(float dt) {
		if (activated && xOffset < 1000f) {
			ySpeed += dt * VERTICAL_ACCELLERATION;
			yOffset += dt * ySpeed;
			xOffset = (float) (Math.sin(yOffset * WIGGLE_SPEED) * WIGGLE_AMPLITUDE);
		}
	}
}
