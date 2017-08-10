package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.GearSpec;
import week.of.awesome.game.Level;

public class GearBrush implements Brush {
	
	private int gearX, gearY;

	@Override
	public void beginBrush(Level level, int x, int y) {
		this.gearX = x;
		this.gearY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y) {
		float radius = Vector2.dst(gearX, gearY, x, y);
		
		GearSpec spec = new GearSpec();
		spec.position = new Vector2(gearX, gearY);
		spec.radius = radius;
		spec.rotatesRight = true;
		
		level.gears.add(spec);
	}

}
