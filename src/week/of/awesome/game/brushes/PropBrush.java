package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.PropSpec;

public class PropBrush implements Brush {
	
	private PropSpec.Type type;
	
	public PropBrush(PropSpec.Type type) {
		this.type = type;
	}
	
	@Override
	public void beginBrush(Level level, int x, int y) { }

	@Override
	public void endBrush(Level level, int x, int y) {
		PropSpec spec = new PropSpec();
		spec.position = new Vector2(x, y);
		spec.type = this.type;
		level.props.add(spec);
	}

}
