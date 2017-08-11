package week.of.awesome.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.math.Vector2;
import week.of.awesome.framework.GraphicsResources;
import week.of.awesome.framework.RenderService;

public class Renderer {
	
	private static final Color COLOUR_CAST = new Color(242f/255f, 214f/255f, 255f/255f, 1f);
	private static final Color ALIEN_GREEN = new Color(0.5f, 1, 0.5f, 1);
	
	private Vector2 tmpPos = new Vector2();
	private RenderService gfx;
	
	private FluidRenderer fluidRenderer;
	
	private Texture minionTex;
	private Texture minionLookingDownTex;
	private Texture minionSquashedTex;
	private Texture ballPropTex;
	private Texture gearTex;
	private Texture aimTex;
	private Texture dominoTex;
	private Texture spaceshipTex;
	
	private Texture floorTex;
	private Texture leftSlopeTex;
	private Texture rightSlopeTex;
	private Texture trapdoorTex;
	private Texture platformTex;
	private Texture platformShaftTex;
	private Texture platformBaseTex;
	private Texture platformRangeTex;
	private Texture platformRangeTopTex;
	private Texture wellEmptyTex;
	private Texture wellHighlightTex;
	private Texture beamTex;
	private Texture beamBaseTex;
	
	private Texture bgTex;
	private Texture vignetteTex;
	
	public Renderer(RenderService gfx, GraphicsResources resources) {
		this.gfx = gfx;
		this.fluidRenderer = new FluidRenderer(gfx, resources);
		
		this.bgTex = resources.newTexture("screens/background.png");
		bgTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.vignetteTex = resources.newTexture("screens/vignette.png");
		this.vignetteTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		this.minionTex = resources.newTexture("sprites/minion.png");
		this.minionLookingDownTex = resources.newTexture("sprites/minion_lookDown.png");
		this.minionSquashedTex = resources.newTexture("sprites/squashedMinion.png");
		this.ballPropTex = resources.newTexture("sprites/ballprop.png");
		this.gearTex = resources.newTexture("sprites/gear.png");
		this.aimTex = resources.newTexture("sprites/aim.png");
		this.dominoTex = resources.newTexture("sprites/domino.png");
		this.spaceshipTex = resources.newTexture("sprites/spaceship.png");
		
		this.floorTex = resources.newTexture("tiles/floor.png");
		this.leftSlopeTex = resources.newTexture("tiles/leftSlope.png");
		this.rightSlopeTex = resources.newTexture("tiles/rightSlope.png");
		this.trapdoorTex = resources.newTexture("tiles/trapdoor.png");
		this.platformTex = resources.newTexture("tiles/platform.png");
		this.platformShaftTex = resources.newTexture("tiles/platformShaft.png");
		this.platformBaseTex = resources.newTexture("tiles/platformBase.png");
		this.platformRangeTex = resources.newTexture("tiles/platformRange.png");
		this.platformRangeTopTex = resources.newTexture("tiles/platformRangeTop.png");
		this.wellEmptyTex = resources.newTexture("tiles/wellempty.png");
		this.wellHighlightTex = resources.newTexture("tiles/wellHighlighter.png");
		this.beamTex = resources.newTexture("tiles/beam.png");
		this.beamBaseTex = resources.newTexture("tiles/beamBase.png");
	}
	
	public void preDraw(Scene scene) {
		fluidRenderer.updateRendering(scene.getDroplets(), scene.getWells());
	}
	
	public void draw(Scene scene) {
		gfx.drawScreen(bgTex, true, COLOUR_CAST);
		gfx.drawScreen(vignetteTex, false, 0.3f);
		
		float wellHeightFudge = 0.3f;
		float wellIndicatorThickness = 0.1f;
		for (Well well : scene.getWells()) {
			float width = well.getMaxPosition().x - well.getMinPosition().x;
			float height = well.getMaxPosition().y - well.getMinPosition().y - wellHeightFudge;
			gfx.draw(wellEmptyTex, well.getMinPosition(), width, height, 0.05f);
			gfx.drawTinted(wellHighlightTex, well.getMinPosition().cpy().add(0, height - wellIndicatorThickness), width, wellIndicatorThickness, FluidRenderer.typeToColour(well.getDropletAffinity()));
		}
		
		
		for (Beam beam : scene.getBeams()) {
			gfx.draw(beamTex, beam.getMinPos(), beam.getMaxPos().x - beam.getMinPos().x + 1f, 1f, 1f);
			switch (beam.getDirection()) {
				case RIGHT:
					for (float x = beam.getMinPos().x; x <= beam.getMaxPos().x; x += 2f) {
						gfx.draw(beamBaseTex, new Vector2(x, beam.getMinPos().y), 1f, 1f, 1f);
					}
					
					break;
				case LEFT:
					for (float x = beam.getMinPos().x; x <= beam.getMaxPos().x; x += 2f) {
						gfx.draw(beamBaseTex, new Vector2(x+1, beam.getMinPos().y), -1f, 1f, 1f);
					}
					
					break;
			}
		}
		
		// platform elements that are behind water
		for (Platform platform : scene.getPlatforms()) {
			gfx.draw(platformRangeTex, platform.getBasePosition(), 1f, platform.getMaxHeight(), 0.5f);
			gfx.draw(platformRangeTopTex, platform.getBasePosition().cpy().add(0, platform.getMaxHeight()-1), 1f, 1f, 0.5f);
		}
		
		
		fluidRenderer.compositeToScreen();
		
		Random floorRand = new Random(1);
		for (Vector2 pos : scene.getSolids()) {
			float topBloat = 0f;
			float bottomFloat = floorRand.nextFloat() * 0.2f;
			float leftBloat = floorRand.nextFloat() * 0.2f + 0f;
			float rightBloat = floorRand.nextFloat() * 0.2f + 0f;
			gfx.draw(floorTex, pos.cpy().add(-leftBloat, topBloat-bottomFloat), 1f + leftBloat + rightBloat, 1f + topBloat + bottomFloat, 1f);
		}
		for (Vector2 pos : scene.getLeftSlopes()) {
			gfx.draw(leftSlopeTex, pos, 1f, 1f, 1f);
		}
		for (Vector2 pos : scene.getRightSlopes()) {
			gfx.draw(rightSlopeTex, pos, 1f, 1f, 1f);
		}
		
		// platform elements that are in-front of water
		for (Platform platform : scene.getPlatforms()) {
			gfx.draw(platformShaftTex, platform.getBasePosition(), 1f, platform.getHeadPosition().y - platform.getBasePosition().y + 0.8f, 1f);
			gfx.draw(platformBaseTex, platform.getBasePosition(), 1f, 1f, 1f);
			gfx.draw(platformTex, platform.getHeadPosition(), 1f, 1f, 1f);
		}
		
		for (TrapDoor trapdoor : scene.getTrapDoors()) {
			tmpPos.set(trapdoor.getPosition()).sub(0.1f, 0.15f);
			gfx.drawRotated(trapdoorTex, tmpPos, 2.5f, 0.6f, trapdoor.getRotation(), new Vector2(0, -0.4f));
		}
		
		for (Gear gear : scene.getGears()) {
			gfx.drawRotated(gearTex, gear.getPosition(), gear.getRadius()*2, gear.getRadius()*2, gear.getRotation(), new Vector2(-gear.getRadius() + 0.1f, -gear.getRadius()));
		}
		
		for (Minion m : scene.getMinions()) {
			boolean isLookingDown = m.getPosition().y > scene.getAimPos().y;
			Texture t = m.isDead() ? minionSquashedTex : isLookingDown ? minionLookingDownTex : minionTex;
			Color c = ALIEN_GREEN.cpy();
			c.a = m.deathAnimationTween();
			boolean flipX = !m.isDead() && m.getPosition().x > scene.getAimPos().x;
			gfx.drawCenteredTinted(t, m.getPosition(), 1,1, flipX, c);
		}
		
		if (scene.getActiveSprayPosition() != null) {
			gfx.drawRotated(aimTex, scene.getActiveSprayPosition(), 2f, 2f, scene.getSprayDirection().angle(), new Vector2(-1f, -1f));
		}
		
		for (Prop prop : scene.getProps()) {
			final float radius = 0.5f;
			
			switch (prop.getType()) {
			case BALL:
				gfx.drawRotated(ballPropTex, prop.getPosition(), radius*2f, radius*2f, prop.getRotation(), new Vector2(-radius, -radius));
				break;
			case BLOCK: // TODO
				gfx.drawRotated(ballPropTex, prop.getPosition(), radius*2f, radius*2f, prop.getRotation(), new Vector2(-radius, -radius));
				break;
			case DOMINO:
				gfx.drawRotated(dominoTex, prop.getPosition(), 0.4f, 2f, prop.getRotation(), new Vector2(-0.2f, 0));
				break;
			}
			
		}
		
		for (Spaceship s : scene.getSpaceships()) {
			gfx.draw(spaceshipTex, s.getPosition(), 8f, 4f, 1f);
		}
	}

}
