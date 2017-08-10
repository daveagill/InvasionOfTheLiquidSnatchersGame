package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Minion {
	private int fluidRemaining;
	private boolean alive = true;
	private Body body;
	private Vector2 positionWhenDead;
	private float ghostAscention = 0f;
	
	public Minion(MinionSpec spec, Body b) {
		fluidRemaining = spec.fluidRemaining;
		this.body = b;
	}
	
	public int getFluidRemaining() { return fluidRemaining; }
	public boolean isGhost() { return !alive; }
	
	public boolean deathSceneComplete() { return ghostAscention >= 3f; }
	
	public Vector2 getPosition() {
		if (isGhost()) {
			return positionWhenDead.cpy().add(0.1f * ghostAscention, 1.5f * ghostAscention);
		}
		return body.getPosition();
	}
	
	public boolean notifyDeath() {
		if (alive) {
			alive = false;
			positionWhenDead = body.getPosition().cpy();
			return true;
		}
		return false;
	}
	
	public void update(float dt) {
		if (isGhost()) {
			ghostAscention += dt;
		}
	}
}
