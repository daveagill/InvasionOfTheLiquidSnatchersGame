package week.of.awesome.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import week.of.awesome.game.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Invasion of the Liquid Snatchers!";
		config.width = 1024;
		config.height = 768;
		config.resizable = false;
		
		config.addIcon("icon128.png", FileType.Internal);
		config.addIcon("icon32.png", FileType.Internal);
		config.addIcon("icon16.png", FileType.Internal);
		
		new LwjglApplication(Game.create(), config);
	}
}
