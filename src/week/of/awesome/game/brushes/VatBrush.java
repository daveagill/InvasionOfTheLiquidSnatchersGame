package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Droplet;
import week.of.awesome.game.Level;
import week.of.awesome.game.VatSpec;

public class VatBrush implements Brush {
	
	private int initialX, initialY;
	private Droplet.Type type;
	
	public VatBrush(Droplet.Type type) {
		this.type = type;
	}

	@Override
	public void beginBrush(Level level, int x, int y) {
		this.initialX = x;
		this.initialY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y) {
		int minX = Math.min(initialX, x);
		int minY = Math.min(initialY, y);
		
		int maxX = Math.max(initialX, x) + 1;
		int maxY = Math.max(initialY, y) + 1;
		
		VatSpec spec = new VatSpec();
		spec.min = new Vector2(minX, minY);
		spec.max = new Vector2(maxX, maxY);
		spec.type = type;
		
		level.vats.add(spec);
	}

}
