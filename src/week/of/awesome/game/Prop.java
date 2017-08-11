package week.of.awesome.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Prop implements Beamable {
	private PropSpec spec;
	private Body body;
	
	public Prop(PropSpec spec, Body body) {
		this.spec = spec;
		this.body = body;
	}
	
	public Vector2 getPosition() { return body.getPosition(); }
	public float getRotation() {
		float angleInDegs = body.getAngle() * MathUtils.radiansToDegrees;
		return Math.abs(angleInDegs) < 2f ? 0f : angleInDegs;
	}
	public PropSpec.Type getType() { return spec.type; }
	
	public Body getBody() { return body; }

	@Override
	public void onEnterBeam(Beam b) {
		body.setLinearVelocity(5, 0);
	}

	@Override
	public void onExitBeam() {
		
	}
}
