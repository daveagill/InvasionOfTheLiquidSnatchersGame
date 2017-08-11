package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.PropSpec;

public class DominosBrush implements Brush {
	
	private int initialX;

	@Override
	public void beginBrush(Level level, int x, int y) {
		this.initialX = x;
	}

	@Override
	public void endBrush(Level level, int x, int y) {
		if (x < initialX) { return; }
		
		for (float dominoX = initialX; dominoX < x+1; dominoX += 0.8f) {
			PropSpec spec = new PropSpec();
			spec.position = new Vector2(dominoX, y);
			spec.type = PropSpec.Type.DOMINO;
			level.props.add(spec);
		}
	}

}
