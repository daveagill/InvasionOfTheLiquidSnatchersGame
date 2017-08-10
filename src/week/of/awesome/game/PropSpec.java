package week.of.awesome.game;

import com.badlogic.gdx.math.Vector2;

public class PropSpec {
	public static enum Type { BALL, DOMINO, BLOCK }
	
	public Vector2 position;
	public Type type;
}
