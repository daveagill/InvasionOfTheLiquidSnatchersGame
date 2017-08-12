package week.of.awesome.game;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import week.of.awesome.framework.FlipFlopTweener;
import week.of.awesome.framework.GameState;
import week.of.awesome.framework.MouseWatcher;
import week.of.awesome.framework.Services;
import week.of.awesome.game.brushes.BeamBrush;
import week.of.awesome.game.brushes.BgTextBrush;
import week.of.awesome.game.brushes.Brush;
import week.of.awesome.game.brushes.DominosBrush;
import week.of.awesome.game.brushes.GearBrush;
import week.of.awesome.game.brushes.LeftSlopeBrush;
import week.of.awesome.game.brushes.PlatformBrush;
import week.of.awesome.game.brushes.MinionBrush;
import week.of.awesome.game.brushes.PropBrush;
import week.of.awesome.game.brushes.RightSlopeBrush;
import week.of.awesome.game.brushes.ShiftBrush;
import week.of.awesome.game.brushes.SolidBrush;
import week.of.awesome.game.brushes.SpaceshipBrush;
import week.of.awesome.game.brushes.TrapDoorBrush;
import week.of.awesome.game.brushes.VatBrush;
import week.of.awesome.game.brushes.WellBrush;
import week.of.awesome.game.brushes.WiringBrush;

public class PlayGameState implements GameState {
	
	private static final float FADE_SPEED = 1f;
	
	private Services services;
	private Renderer renderer;
	
	private int levelNum=1;
	private Level level;
	private Scene scene;
	private Rectangle levelSize;
	
	private float scaleX = 30;
	private float scaleY = 30;
	
	private boolean isStarting, isEnding;
	private FlipFlopTweener screenFader = new FlipFlopTweener();
	private Texture screenDarkFadeTex;
	
	private boolean buildModeEnabled = false;
	private MouseWatcher inGameMouseWatcher;
	private MouseWatcher buildModeMouseWatcher;
	private List<Brush> brushes = Arrays.asList(
			new ShiftBrush(),
			new SolidBrush(),
			new LeftSlopeBrush(),
			new RightSlopeBrush(),
			new WiringBrush(),
			new WellBrush(Droplet.Type.WATER),
			new WellBrush(Droplet.Type.MAGMA),
			new WellBrush(Droplet.Type.OIL),
			new VatBrush(Droplet.Type.WATER),
			new VatBrush(Droplet.Type.MAGMA),
			new VatBrush(Droplet.Type.OIL),
			new VatBrush(Droplet.Type.BLOOD),
			new PlatformBrush(),
			new TrapDoorBrush(),
			new BeamBrush(),
			new PropBrush(PropSpec.Type.BALL),
			new DominosBrush(),
			new GearBrush(),
			new SpaceshipBrush(),
			new MinionBrush(Droplet.Type.WATER),
			new MinionBrush(Droplet.Type.OIL),
			new MinionBrush(Droplet.Type.WASTE),
			new BgTextBrush()
	);
	private int currentBrushIdx = 0;
	
	private WorldEvents worldEvents = new WorldEvents() {

		@Override
		public void captureDroplet(Droplet droplet, Well well) {
			
		}

		@Override
		public void minionDeath(Minion minion) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void gameOver() {
			//isEnding = true;
		}

		@Override
		public void levelCompleted() {
			advanceLevel();
		}
		
	};

	@Override
	public void onEnter(Services services) {
		this.services = services;
		this.renderer = new Renderer(services.gfx, services.gfxResources);
		
		this.screenDarkFadeTex = services.gfxResources.newTexture("screens/darkFade.png");
		
		reloadScene();
		
		this.inGameMouseWatcher = new MouseWatcher() {
			@Override
			public void buttonDown(int screenX, int screenY, int button) {
				scene.spray(true);
			}

			@Override
			public void buttonUp(int screenX, int screenY, int button) {
				scene.spray(false);
			}
			
			@Override
			public void movedOrDragged(int screenX, int screenY) {
				float worldX = screenX / scaleX + levelSize.x;
				float worldY = screenY / scaleY + levelSize.y;
				scene.aim(worldX, worldY);
			}
		};
		
		this.buildModeMouseWatcher = new MouseWatcher() {
			
			int initialMapX;
			int initialMapY;
			
			@Override
			public void buttonDown(int screenX, int screenY, int button) {
				initialMapX = (int) (screenX / scaleX);
				initialMapY = (int) (screenY / scaleY);
				if (button == Buttons.LEFT) {
					brushes.get(currentBrushIdx).beginBrush(level, initialMapX, initialMapY);
				}
			}

			@Override
			public void buttonUp(int screenX, int screenY, int button) {
				int mapX = (int) (screenX / scaleX);
				int mapY = (int) (screenY / scaleY);
				
				if (button == Buttons.LEFT) {
					brushes.get(currentBrushIdx).endBrush(level, mapX, mapY);
				} else { // delete stuff in range
					int minX = Math.min(initialMapX, mapX);
					int maxX = Math.max(initialMapX, mapX);
					int minY= Math.min(initialMapY, mapY);
					int maxY = Math.max(initialMapY, mapY);
					int xRange = maxX - minX;
					int yRange = maxY - minY;
					if (xRange > yRange) {
						for (int x = minX; x <= maxX; ++x) {
							level.removeAt(x, mapY);
						}
					}
					else {
						for (int y = minY; y <= maxY; ++y) {
							level.removeAt(mapX, y);
						}
					}
					
				}
				
				// reload level
				scene.dispose();
				scene = new Scene(worldEvents, level, services.physics);
			}
			
			@Override
			public void scrolled(int amount) {
				scaleX -= amount;
				scaleY -= amount;
			}
		};
		
		services.input.addMouseWatcher(inGameMouseWatcher);
		
		isStarting = true;
		isEnding = false;
	}
	
	@Override
	public void onExit() {
		scene.dispose();
	}

	@Override
	public GameState update(float dt) {		
		// toggle the mouse control scheme on space key
		if (services.input.isJustDown(Keys.SPACE)) {
			buildModeEnabled = !buildModeEnabled;
			
			this.scene.dispose();
			this.scene = new Scene(worldEvents, level, services.physics);
			this.levelSize = level.calculateSize();
			
			if (buildModeEnabled) {
				services.physics.pause(true);
				services.input.removeMouseWatcher(inGameMouseWatcher);
				services.input.addMouseWatcher(buildModeMouseWatcher);
			}
			else {
				services.physics.pause(false);
				services.input.removeMouseWatcher(buildModeMouseWatcher);
				services.input.addMouseWatcher(inGameMouseWatcher);
			}
		}
		
		// cycle brushes
		if (services.input.isJustDown(Keys.X)) {
			++currentBrushIdx;
			currentBrushIdx = currentBrushIdx % brushes.size();
		}
		else if (services.input.isJustDown(Keys.Z)) {
			--currentBrushIdx;
			currentBrushIdx = currentBrushIdx < 0 ? brushes.size()-1 : currentBrushIdx;
		}
		
		if (services.input.isJustDown(Keys.S)) {
			Level.save(level, "level" + levelNum + ".txt");
		}
		
		if (services.input.isJustDown(Keys.R)) {
			isEnding = true;
		}
		
		if (services.input.isJustDown(Keys.TAB)) {
			advanceLevel();
		}
		
		if (!buildModeEnabled) {
			scene.update(dt);
		}
		
		// screen fade in/out
		if (isEnding || isStarting) {
			if (screenFader.updateTillEdgeChange(dt * FADE_SPEED)) {
				if (isStarting) {
					isStarting = false;
				}
				else if (isEnding) {
					isEnding = false;
					reloadScene();
					isStarting = true;
				}
			}
		}
		
		return null;
	}

	@Override
	public void render(float dt) {
		if (!buildModeEnabled) {
			int scaleXY = (int) Math.min(services.gfx.getWidth() / (levelSize.x + levelSize.width), services.gfx.getHeight() / (levelSize.y + levelSize.height));
			scaleX = scaleXY;
			scaleY = scaleXY;
		}
		Matrix4 worldTransform = new Matrix4().scale(scaleX, scaleY, 1f);
		
		if (!buildModeEnabled) {
			worldTransform.translate(-levelSize.x/2f, -levelSize.y/2f, 0);
		}
		
		services.gfx.setTransformMatrix(worldTransform);
		renderer.preDraw(scene);
		
		services.gfx.beginFrame();
		
		renderer.draw(scene, worldTransform);

		if (buildModeEnabled) {
			services.gfx.setTransformMatrix(new Matrix4());
			renderer.drawDebugging(brushes.get(currentBrushIdx).getName());
		}
		
		// screen fade in/out
		if (isStarting || isEnding) {
			services.gfx.drawScreen(screenDarkFadeTex, false, 1 - screenFader.getValue());
		}
		
		services.gfx.endFrame();
	}
	
	private void reloadScene() {
		if (scene != null) {
			scene.dispose();
		}
		this.level = Level.load("level" + levelNum + ".txt");
		this.levelSize = level.calculateSize();
		this.scene = new Scene(worldEvents, level, services.physics);
	}
	
	private void advanceLevel() {
		++levelNum;
		
		// wrap around the end
		if (!Gdx.files.internal("level" + levelNum + ".txt").exists()) {
			levelNum = 1;
		}
		
		isEnding = true;
	}
}
