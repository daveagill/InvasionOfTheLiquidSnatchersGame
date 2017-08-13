package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Gear implements Activatable {
	
	private GearSpec spec;
	private Body body;
	private boolean isActive = false;
	private float timeSpinning = 0;

	public Gear(GearSpec spec, Body body) {
		this.spec = spec;
		this.body = body;
	}
	
	public Vector2 getPosition() { return body.getPosition(); }
	public float getRotation() { return body.getAngle(); }
	public float getRadius() { return spec.radius; }
	
	@Override
	public void activate(WorldEvents events) {
		isActive = true;
		events.gearActivated();
	}
	
	public void update(float dt) {
		if (isActive) {
			body.setType(BodyType.DynamicBody);
			body.applyTorque(spec.rotatesRight ? -1000 : 1000, true);
			timeSpinning += dt;
		}
	}
	
	public boolean hasSpunEnough() {
		return timeSpinning >= 3;
	}

}
