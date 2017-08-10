package week.of.awesome.framework;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsService implements Disposable {
	
	private World b2dSim = new World(new Vector2(0, -10), true);
	private Collection<Body> bodiesScheduledForRemoval = new ArrayList<>();
	private boolean paused = false;
	
	public void setContactListener(ContactListener contactListener) {
		b2dSim.setContactListener(contactListener);
	}
	
	public void pause(boolean paused) {
		this.paused = paused;
	}
	
	public Body createBody(BodyDef bodyDef) {
		return b2dSim.createBody(bodyDef);
	}
	
	public Joint createJoint(JointDef jointDef) {
		return b2dSim.createJoint(jointDef);
	}

	public void removeBody(Body body) {
		bodiesScheduledForRemoval.add(body);
	}
	
	public void update(float dt) {
		removeDeadBodies();
		if (!paused) {
			b2dSim.step(dt, 8, 3);
		}
	}
	
	@Override
	public void dispose() {
		removeDeadBodies();
		
		if (b2dSim.getBodyCount() != 0) {
			System.err.println(b2dSim.getBodyCount() + " undisposed bodies!");
		}
		
		b2dSim.dispose();
	}

	private void removeDeadBodies() {
		for (Body b : bodiesScheduledForRemoval) {
			b2dSim.destroyBody(b);
		}
		bodiesScheduledForRemoval.clear();
	}
}
