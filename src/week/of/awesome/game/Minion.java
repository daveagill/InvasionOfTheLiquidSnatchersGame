package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Minion implements Beamable {
	private boolean alive = true;
	private Body body;
	private Vector2 positionWhenDead;
	private float deathAnimation = 1f;
	private boolean inBeam;
	
	public Minion(MinionSpec spec, Body b) {
		this.body = b;
	}
	
	public Body getBody() { return body; }
	public boolean isDead() { return !alive; }
	
	public float deathAnimationTween() { return deathAnimation; }
	public boolean deathSceneComplete() { return deathAnimation <= 0f; }
	
	public Vector2 getPosition() {
		if (isDead()) {
			return positionWhenDead;
		}
		return body.getPosition();
	}
	
	public boolean notifyDeath() {
		if (alive) {
			alive = false;
			positionWhenDead = body.getPosition().cpy();
			body = null;
			return true;
		}
		return false;
	}
	
	public void update(float dt) {
		if (isDead()) {
			deathAnimation -= dt * 0.3f;
		}
		
		if (inBeam) {
			body.setLinearVelocity(4, body.getLinearVelocity().y * 0.5f);
		}
	}

	@Override
	public void onEnterBeam(Beam b) {
		inBeam = true;
		body.setGravityScale(0);
		body.setLinearVelocity(0, 0);
	}

	@Override
	public void onExitBeam() {
		inBeam = false;
		body.setGravityScale(1);
	}
}
