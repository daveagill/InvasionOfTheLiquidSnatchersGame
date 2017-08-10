package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import week.of.awesome.framework.PhysicsService;

public class PhysicsFactory {
	
	public static final short LIQUID = 0x0001;   // 0000000000000001
	public static final short SCENERY = 0x0002;  // 0000000000000010
	public static final short ENTITY = 0x0004;   // 0000000000000100
	public static final short KILLSPOT = 0x0008; // 0000000000001000
	public static final short PROPLIKE = 0x0010; // 0000000000010000
	
	public static class BodySet {
		public Body main;
		public Collection<Body> all = new ArrayList<>();
		
		public BodySet(Body mainBody, Body... supportingBodies) {
			this.main = mainBody;
			all.add(mainBody);
			all.addAll(Arrays.asList(supportingBodies));
		}
	}
	
	private PhysicsService physics;

	public PhysicsFactory(PhysicsService physics) {
		this.physics = physics;
	}
	
	public void destroy(Body b) {
		physics.removeBody(b);
	}
	
	public Body createSolidBlock(Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position);
		
		Body b = physics.createBody(bodyDef);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.5f, new Vector2(0.5f, 0.5f), 0f);
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.filter.categoryBits = SCENERY;
		fixtureDef.friction = 1f;
		//fixtureDef.restitution = 1;
		
		b.createFixture(fixtureDef);
		
		return b;
	}
	
	public Body createSlope(Vector2 position, boolean toTheRight) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position);
		
		Body b = physics.createBody(bodyDef);
		
		PolygonShape triangle = new PolygonShape();
		if (toTheRight) {
			triangle.set(new Vector2[] {
					new Vector2(0, 0),
					new Vector2(1, 0),
					new Vector2(1, 1)
					
			});
		}
		else {
			triangle.set(new Vector2[] {
					new Vector2(0, 0),
					new Vector2(1, 0),
					new Vector2(0, 1)
			});
		}
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = triangle;
		fixtureDef.filter.categoryBits = SCENERY;
		fixtureDef.friction = 1f;
		//fixtureDef.restitution = 1;
		
		b.createFixture(fixtureDef);
		
		return b;
	}
	
	public Body createWaterParticle(Vector2 position, Vector2 velocity) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		bodyDef.position.set(position);
		bodyDef.linearVelocity.set(velocity);
		
		Body b = physics.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.1f);
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = LIQUID;
		fixtureDef.filter.maskBits = LIQUID | SCENERY | PROPLIKE;
		//fixtureDef.density = 1;
		fixtureDef.friction = 0.01f;
		fixtureDef.restitution = 0.2f;
		
		b.createFixture(fixtureDef);
		
		return b;
	}
	
	public Body createWell(Vector2 minPosition, Vector2 maxPosition) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(minPosition);
		
		Body b = physics.createBody(bodyDef);
		
		float width = maxPosition.x - minPosition.x;
		float height = maxPosition.y - minPosition.y;
		PolygonShape box = new PolygonShape();
		box.setAsBox(width/2, height/2, new Vector2(width/2, height/2), 0f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = LIQUID;
		
		b.createFixture(fixtureDef);
		
		return b;
	}
	
	public Body createPlatform(Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(position);
		
		Body b = physics.createBody(bodyDef);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.1f, new Vector2(0.5f, 0.9f), 0f);
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 0;
		fixtureDef.friction = 1f;
		//fixtureDef.restitution = 1;
		fixtureDef.filter.categoryBits = SCENERY;
		fixtureDef.filter.maskBits = LIQUID | ENTITY | PROPLIKE;
		b.createFixture(fixtureDef);
		
		return b;
	}
	
	public BodySet createTrapDoor(Vector2 position) {
		BodyDef hingeBodyDef = new BodyDef();
		hingeBodyDef.type = BodyDef.BodyType.StaticBody;
		hingeBodyDef.position.set(position).add(0, 1f);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position).add(0f, 1f);
		bodyDef.angularDamping = 10f;
		
		Body hingeBody = physics.createBody(hingeBodyDef);
		Body b = physics.createBody(bodyDef);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = hingeBody;
		jointDef.bodyB = b;
		
		physics.createJoint(jointDef);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.1f, new Vector2(0.5f, -0.1f), 0f);
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 0.3f;
		fixtureDef.filter.categoryBits = SCENERY;
		fixtureDef.filter.maskBits = LIQUID | ENTITY | PROPLIKE;
		b.createFixture(fixtureDef);
		
		return new BodySet(b, hingeBody);
	}
	
	public Body createMinion(Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		bodyDef.position.set(position).add(0, 0.25f);
		
		Body b = physics.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.25f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = ENTITY;
		fixtureDef.filter.maskBits = SCENERY | PROPLIKE;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0f;
		
		CircleShape headShape = new CircleShape();
		headShape.setRadius(0.1f);
		headShape.setPosition(new Vector2(0f, 0.3f));
		
		FixtureDef headFixtureDef = new FixtureDef();
		headFixtureDef.shape = headShape;
		headFixtureDef.filter.categoryBits = KILLSPOT;
		headFixtureDef.filter.maskBits = SCENERY | PROPLIKE;
		headFixtureDef.isSensor=true;
		
		b.createFixture(fixtureDef);
		b.createFixture(headFixtureDef);
		
		return b;
	}

	public Body createBallProp(Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position);
		
		Body b = physics.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.4f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = PROPLIKE;
		fixtureDef.density = 9999999;
		fixtureDef.friction = 0.9f;
		
		b.createFixture(fixtureDef);
		
		return b;
	}

	public Body createDominoProp(Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position);
		
		Body b = physics.createBody(bodyDef);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.05f, 0.5f, new Vector2(0.05f, 0.5f), 0f);
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.filter.categoryBits = PROPLIKE;
		fixtureDef.density = 9999999;
		fixtureDef.friction = 0.9f;
		
		b.createFixture(fixtureDef);
		
		return b;
	}
	
	public BodySet createGear(Vector2 position, float radius) {
		BodyDef hingeBodyDef = new BodyDef();
		hingeBodyDef.type = BodyDef.BodyType.StaticBody;
		hingeBodyDef.position.set(position);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position);
		
		Body b = physics.createBody(bodyDef);
		Body hingeBody = physics.createBody(hingeBodyDef);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = hingeBody;
		jointDef.bodyB = b;
		
		physics.createJoint(jointDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = PROPLIKE;
		fixtureDef.filter.maskBits = LIQUID | ENTITY;
		fixtureDef.friction = 1f;
		fixtureDef.density = 0.1f;
		
		b.createFixture(fixtureDef);
		
		return new BodySet(b, hingeBody);
	}
}
