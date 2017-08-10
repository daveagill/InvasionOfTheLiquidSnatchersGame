package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Droplet {
	private static final float MAX_LIFE = 20f;
	private static final float SLOW_DECAY_SPEED = 0.5f;
	private static final float FAST_DECAY_SPEED = 4f;
	
	public static enum Type { WATER, BLOOD, MAGMA, OIL }
	
	private Body body;
	private float life;
	private float decay = 1f;
	private float decaySpeed = 0f;
	private Type type;
	private boolean persistent;
	
	public Droplet(Body body, Type type, boolean persistent) {
		this.body = body;
		this.type = type;
		this.persistent = persistent;
	}
	
	public Body getBody() { return body; }
	public Type getType() { return type; }
	public Vector2 getPosition() { return body.getPosition(); }
	public float getAlpha() { return decay; }
	
	public boolean isDead() { return decay <= 0; }
	
	public void beginCaptureDecay() {
		persistent = false;
		decaySpeed = SLOW_DECAY_SPEED;
	}
	
	public void update(float dt) {
		if (persistent) { return; }
		life += dt;
		decay -= dt * decaySpeed;
		
		if (life >= MAX_LIFE) { // fast death for long-lived particles, looks better that way
			decaySpeed = FAST_DECAY_SPEED;
		}
		
		if (!body.isAwake()) { // slow death for idle particles, looks better that way
			decaySpeed = SLOW_DECAY_SPEED;
		}
	}
}