package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ContactListenerImpl implements ContactListener {
	
	private Scene scene;
	
	private Collection<Runnable> queuedActions = new ArrayList<>();
	
	public ContactListenerImpl(Scene s) {
		this.scene = s;
	}
	
	public void dispatchCollisionEvents() {
		for (Runnable r : queuedActions) { r.run(); }
		queuedActions.clear();
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Object objectA = fixtureA.getBody().getUserData();
		Object objectB = fixtureB.getBody().getUserData();
		
		Runnable action = null;
		
		// Test: killspot <--> anything
		if (fixtureA.getFilterData().categoryBits == PhysicsFactory.KILLSPOT && objectA instanceof Minion) {
			action = () -> scene.onPlayerDeath((Minion)objectA);
		}
		else if (fixtureB.getFilterData().categoryBits == PhysicsFactory.KILLSPOT && objectB instanceof Minion) {
			action = () -> scene.onPlayerDeath((Minion)objectB);
		}
		
		// Test: droplet <-> Well
		else if (objectA instanceof Droplet && objectB instanceof Well) {
			action = () -> scene.onCaptureDroplet((Droplet)objectA, (Well)objectB);
		}
		else if (objectA instanceof Well && objectB instanceof Droplet) {
			action = () -> scene.onCaptureDroplet((Droplet)objectB, (Well)objectA);
		}
		
		if (action != null) {
			queuedActions.add(action);
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
