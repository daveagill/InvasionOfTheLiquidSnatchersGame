package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;

public class BeamSpec {
	public static enum Direction { LEFT, RIGHT };
	
	public Vector2 min, max;
	public Direction dir;
}
