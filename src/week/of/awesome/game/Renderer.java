package week.of.awesome.game;

import java.util.Collection;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import week.of.awesome.framework.GraphicsResources;
import week.of.awesome.framework.RenderService;

public class Renderer {
	
	private static final int CURSOR_SIZE = 40;
	
	private static final Color COLOUR_CAST = new Color(247f/255f, 170f/255f, 255f/255f, 1f);
	private static final Color ALIEN_GREEN = new Color(0.5f, 1, 0.5f, 1);
	
	private Vector2 tmpPos = new Vector2();
	private GraphicsResources resources;
	private RenderService gfx;
	
	private FluidRenderer fluidRenderer;
	
	private Vector2 cursorPos = new Vector2();
	private Texture cursorTex;
	
	private Texture minionTex;
	private Texture minionLookingDownTex;
	private Texture minionSquashedTex;
	private Texture ballPropTex;
	private Texture gearTex;
	private Texture aimTex;
	private Texture indicatorTex;
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
	
	private BitmapFont bgTextFont;
	private BitmapFont dialogTextFont;
	
	public Renderer(RenderService gfx, GraphicsResources resources) {
		this.gfx = gfx;
		this.resources = resources;
		this.fluidRenderer = new FluidRenderer(gfx, resources);
		
		this.bgTex = resources.newTexture("screens/background.png");
		bgTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.vignetteTex = resources.newTexture("screens/vignette.png");
		this.vignetteTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		this.cursorTex = resources.newTexture("sprites/cursor.png");
		
		this.minionTex = resources.newTexture("sprites/minion.png");
		this.minionLookingDownTex = resources.newTexture("sprites/minion_lookDown.png");
		this.minionSquashedTex = resources.newTexture("sprites/squashedMinion.png");
		this.ballPropTex = resources.newTexture("sprites/ballprop.png");
		this.gearTex = resources.newTexture("sprites/gear.png");
		this.aimTex = resources.newTexture("sprites/aim.png");
		this.indicatorTex = resources.newTexture("sprites/indicator.png");
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
		
		this.bgTextFont = resources.newFont("fonts/QUANTIFIER NBP.fnt");
		this.dialogTextFont = resources.newFont("fonts/CASTWO1.fnt");
	}
	
	public void setMouse(int x, int y) {
		cursorPos.set(x, y - CURSOR_SIZE);
	}
	
	public void preDraw(Scene scene) {
		fluidRenderer.updateRendering(scene.getDroplets(), scene.getWells());
	}
	
	public void draw(Scene scene, Matrix4 worldTransform) {
		gfx.setTransformMatrix(new Matrix4());
		
		gfx.drawScreen(bgTex, true, COLOUR_CAST);
		for (BgTextSpec bgText : scene.getLevel().bgTexts) {
			Vector3 textPos = new Vector3(bgText.position.x + 0.5f, bgText.position.y + 0.5f, 0).mul(worldTransform);
			gfx.drawFont(bgTextFont, bgText.text, textPos.x, textPos.y, 0.8f);
		}
		
		
		for (DecalSpec decal : scene.getLevel().decals) {
			Vector3 decalPos = new Vector3(decal.position.x, decal.position.y, 0).mul(worldTransform);
			Texture decalTex = resources.newTexture(decal.texture);
			gfx.draw(decalTex, new Vector2(decalPos.x, decalPos.y), decalTex.getWidth(), decalTex.getHeight(), 1f);
		}
		
		
		gfx.drawScreen(vignetteTex, false, 0.3f);
		
		
		
		gfx.setTransformMatrix(worldTransform);
		
		float wellHeightFudge = 0.3f;
		float wellIndicatorThickness = 0.1f;
		for (Well well : scene.getWells()) {
			float width = well.getMaxPosition().x - well.getMinPosition().x;
			float height = well.getMaxPosition().y - well.getMinPosition().y - wellHeightFudge;
			gfx.draw(wellEmptyTex, well.getMinPosition(), width, height, 0.06f);
			gfx.drawTinted(wellHighlightTex, well.getMinPosition().cpy().add(0, height - wellIndicatorThickness), width, wellIndicatorThickness, well.getDropletAffinity().COLOUR);
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
			gfx.draw(platformRangeTex, platform.getBasePosition(), 1f, platform.getMaxHeight(), 0.2f);
			gfx.draw(platformRangeTopTex, platform.getBasePosition().cpy().add(0, platform.getMaxHeight()-1), 1f, 1f, 0.2f);
			gfx.draw(platformShaftTex, platform.getBasePosition(), 1f, platform.getHeadPosition().y - platform.getBasePosition().y + 0.8f, 1f);
			gfx.draw(platformTex, platform.getHeadPosition(), 1f, 1f, 1f);
		}
		
		
		fluidRenderer.compositeToScreen();
		
		// SOLIDS
		Random floorRand = new Random(1);
		for (SolidSpec solid : scene.getLevel().solids) {
			for (float y = solid.min.y; y < solid.max.y; ++y) {
				for (float x = solid.min.x; x < solid.max.x; ++x) {
					float topBloat = 0f;
					float bottomFloat = floorRand.nextFloat() * 0.2f;
					float leftBloat = floorRand.nextFloat() * 0.2f + 0f;
					float rightBloat = floorRand.nextFloat() * 0.2f + 0f;
					gfx.draw(floorTex, tmpPos.set(x, y).add(-leftBloat, topBloat-bottomFloat), 1f + leftBloat + rightBloat, 1f + topBloat + bottomFloat, 1f);
				}
			}
		}
		
		for (Vector2 pos : scene.getLevel().leftSlopes) {
			float topBloat = 0f;
			float bottomFloat = floorRand.nextFloat() * 0.2f;
			float leftBloat = floorRand.nextFloat() * 0.2f + 0f;
			float rightBloat = floorRand.nextFloat() * 0.2f + 0f;
			gfx.draw(leftSlopeTex, tmpPos.set(pos).add(-leftBloat, topBloat-bottomFloat), 1f + leftBloat + rightBloat, 1f + topBloat + bottomFloat, 1f);
		}
		for (Vector2 pos : scene.getLevel().rightSlopes) {
			float topBloat = 0f;
			float bottomFloat = floorRand.nextFloat() * 0.2f;
			float leftBloat = floorRand.nextFloat() * 0.2f + 0f;
			float rightBloat = floorRand.nextFloat() * 0.2f + 0f;
			gfx.draw(rightSlopeTex, tmpPos.set(pos).add(-leftBloat, topBloat-bottomFloat), 1f + leftBloat + rightBloat, 1f + topBloat + bottomFloat, 1f);
		}
		
		for (Gear gear : scene.getGears()) {
			gfx.drawRotated(gearTex, gear.getPosition(), gear.getRadius()*2, gear.getRadius()*2, gear.getRotation(), new Vector2(-gear.getRadius() + 0.1f, -gear.getRadius()));
		}
		
		// platform elements that are in-front of water
		for (Platform platform : scene.getPlatforms()) {
			gfx.draw(platformBaseTex, platform.getBasePosition(), 1f, 1f, 1f);
		}
		
		for (TrapDoor trapdoor : scene.getTrapDoors()) {
			tmpPos.set(trapdoor.getPosition()).sub(0.1f, 0.15f);
			gfx.drawRotated(trapdoorTex, tmpPos, trapdoor.getWidth() + 0.5f, 0.6f, trapdoor.getRotation(), new Vector2(0, -0.4f));
		}
		
		for (Minion m : scene.getMinions()) {
			boolean isLookingDown = m.getPosition().y > scene.getAimPos().y;
			Texture t = m.isDead() ? minionSquashedTex : isLookingDown ? minionLookingDownTex : minionTex;
			Color c = ALIEN_GREEN.cpy();
			c.a = m.deathAnimationTween();
			boolean flipX = !m.isDead() && m.getPosition().x > scene.getAimPos().x;
			gfx.drawCenteredTinted(t, m.getPosition(), 1,1, flipX, c);
		}
				
		for (Prop prop : scene.getProps()) {
			if (prop.getType() == PropSpec.Type.BALL) {
				final float radius = 0.6f;
				gfx.drawRotated(ballPropTex, prop.getPosition(), radius*2f, radius*2f, prop.getRotation(), new Vector2(-radius, -radius));
			}
		}
		
		for (Prop prop : scene.getProps()) {
			if (prop.getType() == PropSpec.Type.DOMINO) {
				gfx.drawRotated(dominoTex, prop.getPosition(), 0.4f, 2f, prop.getRotation(), new Vector2(-0.2f, 0));
			}
		}
		
		for (Spaceship s : scene.getSpaceships()) {
			gfx.draw(spaceshipTex, s.getPosition(), 8f, 4f, 1f);
		}
		
		if (scene.getActiveMinion() != null) {
			gfx.drawRotated(aimTex, scene.getActiveMinion().getPosition(), 2f, 2f, scene.getSprayDirection().angle(), new Vector2(-1f, -1f));
		}
				
		// MINION SPEECH
		gfx.setTransformMatrix(new Matrix4());
		for (Minion m : scene.getMinions()) {
			Collection<String> dialogLines = m.getDialog();
			if (dialogLines == null) { continue; }
			
			Vector2 pos = m.getPosition();
			Vector3 textPos = new Vector3(pos.x + 0.5f, pos.y, 0).mul(worldTransform);
			float lineOffsetY = 0;
			float dialogOffsetY = dialogLines.size() * dialogTextFont.getLineHeight();
			
			for (String line : dialogLines) {
				gfx.drawFont(dialogTextFont,line, textPos.x, textPos.y + dialogOffsetY - lineOffsetY, m.deathAnimationTween() * m.dialogFadeTween());
				lineOffsetY += dialogTextFont.getLineHeight();
			}
		}
		
		gfx.draw(cursorTex, cursorPos, CURSOR_SIZE, CURSOR_SIZE, 1);
		if (scene.getActiveMinion() != null) {
			gfx.drawTinted(indicatorTex, cursorPos, 24, 24, scene.getActiveMinion().getDropletType().COLOUR);
		}
	}

	public void drawDebugging(String text) {
		gfx.drawFont(bgTextFont, text, 10,  gfx.getHeight() - 10, 1);
	}

}
