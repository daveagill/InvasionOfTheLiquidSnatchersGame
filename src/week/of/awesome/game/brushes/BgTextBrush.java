package week.of.awesome.game.brushes;

import javax.swing.JOptionPane;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.BgTextSpec;
import week.of.awesome.game.Level;

public class BgTextBrush implements Brush {


	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) { }

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		BgTextSpec spec = new BgTextSpec();
		spec.position = new Vector2(x, y);
		spec.text = JOptionPane.showInputDialog(null, "Enter Text");
		
		if (spec.text != null) {
			level.bgTexts.add(spec);
			level.undoHistory.add(spec);
		}
	}

}
