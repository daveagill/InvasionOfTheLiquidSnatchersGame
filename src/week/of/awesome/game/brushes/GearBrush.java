package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.GearSpec;
import week.of.awesome.game.Level;

public class GearBrush implements Brush {
	
	private float gearX, gearY;

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) {
		this.gearX = worldX;
		this.gearY = worldY;
	}

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		float radius = Vector2.dst(gearX, gearY, worldX, worldY);
		if (radius == 0) { return; }
		
		GearSpec spec = new GearSpec();
		spec.position = new Vector2(gearX, gearY);
		spec.radius = radius;
		spec.rotatesRight = true;
		
		level.gears.add(spec);
		level.undoHistory.add(spec);
	}

}
