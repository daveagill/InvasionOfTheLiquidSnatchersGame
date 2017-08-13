package week.of.awesome.game.brushes;

import javax.swing.JOptionPane;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Droplet;
import week.of.awesome.game.Level;
import week.of.awesome.game.MinionSpec;

public class MinionBrush implements Brush {
	
	private Droplet.Type type;
	
	public MinionBrush(Droplet.Type type) {
		this.type = type;
	}
	
	public String getName() { return "MinionBrush(" + type + ")"; }

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) { }

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		MinionSpec spec = new MinionSpec();
		spec.position = new Vector2(x, y);
		spec.fluidType = type;
		spec.dialog = JOptionPane.showInputDialog(null, "Enter Dialog");
		level.minions.add(spec);
		level.undoHistory.add(spec);
	}

}