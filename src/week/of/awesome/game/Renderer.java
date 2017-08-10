package week.of.awesome.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import week.of.awesome.framework.GraphicsResources;
import week.of.awesome.framework.RenderService;

public class Renderer {
	
	private RenderService gfx;
	
	private FluidRenderer fluidRenderer;
	
	private Texture grikTex;
	private Texture ballPropTex;
	private Texture gearTex;
	private Texture aimTex;
	
	private Texture floorTex;
	private Texture leftSlopeTex;
	private Texture rightSlopeTex;
	
	public Renderer(RenderService gfx, GraphicsResources resources) {
		this.gfx = gfx;
		this.fluidRenderer = new FluidRenderer(gfx, resources);
		
		this.grikTex = resources.newTexture("sprites/person.png");
		this.ballPropTex = resources.newTexture("sprites/ballprop.png");
		this.gearTex = resources.newTexture("sprites/gear.png");
		this.aimTex = resources.newTexture("sprites/aim.png");
		
		this.floorTex = resources.newTexture("tiles/floor.png");
		this.leftSlopeTex = resources.newTexture("tiles/leftSlope.png");
		this.rightSlopeTex = resources.newTexture("tiles/rightSlope.png");
	}
	
	public void preDraw(Scene scene) {
		fluidRenderer.updateRendering(scene.getDroplets(), scene.getWells());
	}
	
	public void draw(Scene scene) {
		for (Well well : scene.getWells()) {
			int width = (int) (well.getMaxPosition().x - well.getMinPosition().x);
			int height = (int) (well.getMaxPosition().y - well.getMinPosition().y);
			gfx.draw(floorTex, well.getMinPosition(), width, height, 0.05f);
		}
		
		fluidRenderer.compositeToScreen();
		
		if (scene.getActiveSprayPosition() != null) {
			gfx.drawRotated(aimTex, scene.getActiveSprayPosition(), 2f, 2f, scene.getSprayDirection().angle(), new Vector2(-1f, -1f));
		}
		
		for (Minion m : scene.getMinions()) {
			gfx.drawCentered(grikTex, m.getPosition(), 0.5f, 0.5f, false);
		}
		
		for (Vector2 pos : scene.getSolids()) {
			gfx.draw(floorTex, pos, 1f, 1f, 1f);
		}
		for (Vector2 pos : scene.getLeftSlopes()) {
			gfx.draw(leftSlopeTex, pos, 1f, 1f, 1f);
		}
		for (Vector2 pos : scene.getRightSlopes()) {
			gfx.draw(rightSlopeTex, pos, 1f, 1f, 1f);
		}
		
		for (Platform platform : scene.getPlatforms()) {
			gfx.draw(floorTex, platform.getBasePosition(), 1f, platform.getHeadPosition().y - platform.getBasePosition().y + 1, 0.5f);
		}
		
		
		for (TrapDoor trapdoor : scene.getTrapDoors()) {
			gfx.drawRotated(floorTex, trapdoor.getPosition(), 1f, 0.2f, trapdoor.getRotation(), new Vector2(0, -0.2f));
		}
		
		for (Gear gear : scene.getGears()) {
			gfx.drawRotated(gearTex, gear.getPosition(), gear.getRadius()*2, gear.getRadius()*2, gear.getRotation(), new Vector2(-gear.getRadius() + 0.1f, -gear.getRadius()));
		}
		
		for (Prop prop : scene.getProps()) {
			final float radius = 0.4f;
			
			switch (prop.getType()) {
			case BALL:
				
				gfx.drawRotated(ballPropTex, prop.getPosition(), radius*2f, radius*2f, prop.getRotation(), new Vector2(-radius, -radius));
				break;
			case BLOCK: // TODO
				gfx.drawRotated(ballPropTex, prop.getPosition(), radius*2f, radius*2f, prop.getRotation(), new Vector2(-radius, -radius));
				break;
			case DOMINO:
				gfx.drawRotated(floorTex, prop.getPosition(), 0.1f, 1f, prop.getRotation(), new Vector2(-0.05f, 0));
				break;
			}
			
		}
	}

}
