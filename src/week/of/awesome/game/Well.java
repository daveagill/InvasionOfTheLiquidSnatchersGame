package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.math.Vector2;

public class Well {
	private WellSpec spec;
	private int amountFull = 0;
	
	private Collection<Activatable> activatables = new ArrayList<>();
	
	public Well(WellSpec spec) {
		this.spec = spec;
	}
	
	public Droplet.Type getDropletAffinity() { return spec.affinity; }
	public Vector2 getMinPosition() { return spec.min; }
	public Vector2 getMaxPosition() { return spec.max; }
	public float getPercentFull() { return ((float)amountFull) / 100f; }
	
	public void notifyCapturedDroplets(int amount) {
		boolean alreadyTriggered = amountFull == 100;
		
		amountFull += amount;
		amountFull = Math.min(amountFull, 100);
		
		if (!alreadyTriggered && amountFull == 100) {
			for (Activatable a : activatables) {
				a.activate();
			}
		}
	}
	
	public void registerActivatable(Activatable a) {
		activatables.add(a);
	}
}
