package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;

public class Beam {
	private BeamSpec spec;
	
	public Beam(BeamSpec spec) {
		this.spec = spec;
	}
	
	public Vector2 getMinPos() { return spec.min; }
	public Vector2 getMaxPos() { return spec.max; }
	public BeamSpec.Direction getDirection() { return spec.dir; }
}
