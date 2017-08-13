package week.of.awesome.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;

public class FluidSystem implements Disposable {

	private PhysicsFactory physics;
	private List<Droplet> fluid = new ArrayList<>();
	
	public FluidSystem(PhysicsFactory physics) {
		this.physics = physics;
	}
	
	public List<Droplet> getDroplets() {
		return fluid;
	}
	
	
	public void update(float dt) {
		for (Droplet w : fluid) {
			w.update(dt);
		}
		
		for (int i = 0; i < fluid.size();) {
			Droplet d = fluid.get(i);
			if (d.isDead()) { // swap-back-and-pop trick
				fluid.remove(i);
				
				// dispose of the body
				physics.destroy(d.getBody());
			}
			else {
				++i;
			}
		}
	}
	
	
	public void spawnParticle(Droplet.Type type, Vector2 position, Vector2 velocity, boolean persistent) {
		if (!persistent && fluid.size() > 2000) { return; }
		
		Color colour = type.COLOUR;
		// special case magma to include some blackened particles, it looks cool!
		if (type == Droplet.Type.MAGMA) {
			if (fluid.size() % 3 == 0) {
				colour = colour.cpy().mul(0.5f, 0.1f, 0.1f, 1f);
			}
		}
		
		Body b = physics.createWaterParticle(position, velocity);
		Droplet d = new Droplet(b, type, colour, persistent);
		fluid.add(d);
		b.setUserData(d);
	}

	@Override
	public void dispose() {
		for (Droplet d : fluid) { physics.destroy(d.getBody()); }
	}
	

	
}