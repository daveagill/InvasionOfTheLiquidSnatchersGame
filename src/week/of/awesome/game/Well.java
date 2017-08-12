package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.math.Vector2;

public class Well {
	private WellSpec spec;
	private int capacity = 0;
	private int amountFull = 0;
	
	private Collection<Activatable> activatables = new ArrayList<>();
	
	public Well(WellSpec spec) {
		this.spec = spec;
		int area = (int) ((spec.max.x - spec.min.x) * (spec.max.y - spec.min.y));
		capacity = area * 15;
	}
	
	public Droplet.Type getDropletAffinity() { return spec.affinity; }
	public Vector2 getMinPosition() { return spec.min; }
	public Vector2 getMaxPosition() { return spec.max; }
	public float getPercentFull() { return ((float)amountFull) / (float)capacity; }
	
	public void notifyCapturedDroplets(int amount) {
		boolean alreadyTriggered = amountFull == capacity;
		
		amountFull += amount;
		amountFull = Math.min(amountFull, capacity);
		
		if (!alreadyTriggered && amountFull == capacity) {
			for (Activatable a : activatables) {
				a.activate();
			}
		}
	}
	
	public void registerActivatable(Activatable a) {
		activatables.add(a);
	}
}
