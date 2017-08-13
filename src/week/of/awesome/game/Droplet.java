package week.of.awesome.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Droplet {
	private static final float FROTH_LIFE = 2f;
	private static final float MAX_LIFE = 20f;
	private static final float SLOW_DECAY_SPEED = 0.3f;
	private static final float FAST_DECAY_SPEED = 4f;
	
	public static enum Type {
		WATER(FluidRenderer.WATER_COLOUR, 0.2f),
		BLOOD(FluidRenderer.BLOOD_COLOUR, 0.2f),
		MAGMA(FluidRenderer.MAGMA_COLOUR, 0.2f),
		OIL(FluidRenderer.OIL_COLOUR, 0.05f),
		WASTE(FluidRenderer.NUCLEAR_WASTE_COLOUR, 0.2f);
		
		public final Color COLOUR;
		public final float FROTH_FACTOR;
		
		private Type(Color colour, float frothFactor) {
			this.COLOUR = colour;
			this.FROTH_FACTOR = frothFactor;
		}
	}
	
	private Body body;
	private float life;
	private float decay = 1f;
	private float decaySpeed = 0f;
	private Type type;
	private Color colour;
	private boolean persistent;
	private float speedSquared;
	
	public Droplet(Body body, Type type, Color colour, boolean persistent) {
		this.body = body;
		this.type = type;
		this.colour = colour;
		this.persistent = persistent;
	}
	
	public Body getBody() { return body; }
	public Type getType() { return type; }
	public Vector2 getPosition() { return body.getPosition(); }
	public Color getColour() { return colour; }
	public float getAlpha() { return decay; }
	public float getFrothiness() {
		float speedFactor = speedSquared / 200f;
		speedFactor = speedFactor < 0 ? 0 : speedFactor;
		speedFactor = speedFactor > 1 ? 1 : speedFactor;
		
		float lifeFactor = persistent || life > FROTH_LIFE ? 0 : (1f - life / FROTH_LIFE);
		
		return Math.max(lifeFactor, speedFactor);
	}
	
	public boolean isDead() { return decay <= 0; }
	
	public void beginCaptureDecay() {
		persistent = false;
		decaySpeed = SLOW_DECAY_SPEED;
	}
	
	public void update(float dt) {
		speedSquared = body.getLinearVelocity().len2();
		if (speedSquared < 0.0001) { body.setAwake(false); }
		
		if (persistent) { return; }
		life += dt;
		decay -= dt * decaySpeed;
		
		if (life >= MAX_LIFE) { // fast death for long-lived particles, looks better that way
			decaySpeed = FAST_DECAY_SPEED;
		}
		else {
			boolean isIdle = speedSquared < 0.0000001f;
			if (isIdle) { // slow death for idle particles, looks better that way
				decaySpeed = SLOW_DECAY_SPEED;
				body.setActive(false);
			}
		}
	}
}
