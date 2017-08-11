package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Platform implements Activatable {
	private PlatformSpec spec;
	private boolean isRaised;
	private Body body;
	
	private int activationCounter = 0;
	
	public Platform(PlatformSpec spec, Body body) {
		this.spec = spec;
		this.body = body;
		isRaised = false;
	}
	
	public Vector2 getBasePosition() { return spec.position; }
	public Vector2 getHeadPosition() { return body.getPosition(); }
	public float getMaxHeight() {
		return spec.height;
	}
	
	@Override
	public void activate() {
		++activationCounter;
		if (activationCounter == spec.wellActivatorIDs.size()) {
			isRaised = !isRaised;
		}
	}
	
	public void update(float dt) {
		if (isRaised && getHeadPosition().y < spec.position.y + spec.height-1) {
			body.setLinearVelocity(0f, 3f);
		}
		else if (!isRaised && getHeadPosition().y > spec.position.y) {
			body.setLinearVelocity(0f, -3f);
		}
		else {
			body.setLinearVelocity(0f, 0f);
		}
	}
}
