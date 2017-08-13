package week.of.awesome.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class TrapDoor implements Activatable {
	
	private TrapDoorSpec spec;
	private Body body;
	
	public TrapDoor(TrapDoorSpec spec, Body body) {
		this.spec = spec;
		this.body = body;
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public float getRotation() {
		return body.getAngle() * MathUtils.radiansToDegrees;
	}
	
	public float getWidth() {
		return spec.width;
	}

	@Override
	public void activate(WorldEvents events) {
		body.setType(BodyType.DynamicBody);
		events.trapdoorActivated();
	}

}
