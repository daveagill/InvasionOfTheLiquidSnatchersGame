package week.of.awesome.framework;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsService implements Disposable {
	
	private World b2dSim = new World(new Vector2(0, -10), true);
	private Collection<Body> bodiesScheduledForRemoval = new ArrayList<>();
	
	public void setContactListener(ContactListener contactListener) {
		b2dSim.setContactListener(contactListener);
	}

	public void removeBody(Body body) {
		bodiesScheduledForRemoval.add(body);
	}
	
	public void update(float dt) {
		removeDeadBodies();
		b2dSim.step(dt, 8, 3);
	}
	
	@Override
	public void dispose() {
		removeDeadBodies();
		b2dSim.dispose();
	}

	private void removeDeadBodies() {
		for (Body b : bodiesScheduledForRemoval) {
			b2dSim.destroyBody(b);
		}
		bodiesScheduledForRemoval.clear();
	}
}
