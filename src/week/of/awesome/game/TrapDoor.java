package week.of.awesome.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class TrapDoor implements Activatable {
	
	private Body body;
	
	public TrapDoor(Body body) {
		this.body = body;
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public float getRotation() {
		return body.getAngle() * MathUtils.radiansToDegrees;
	}

	@Override
	public void activate() {
		body.setType(BodyType.DynamicBody);
	}

}
