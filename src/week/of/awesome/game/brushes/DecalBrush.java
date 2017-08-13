package week.of.awesome.game.brushes;

import javax.swing.JOptionPane;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.DecalSpec;
import week.of.awesome.game.Level;

public class DecalBrush implements Brush {

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) { }

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		String filename =  JOptionPane.showInputDialog(null, "Texture filename");
		if (filename == null) { return; }
		
		DecalSpec decalSpec = new DecalSpec();
		decalSpec.position = new Vector2(x, y);
		decalSpec.texture = "decals/" + filename;
		
		level.decals.add(decalSpec);
		level.undoHistory.add(decalSpec);
	}

}
