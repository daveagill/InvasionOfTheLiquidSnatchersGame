package week.of.awesome.game;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

import week.of.awesome.framework.GraphicsResources;
import week.of.awesome.framework.RenderService;

public class FluidRenderer {

	public static final Color BLOOD_COLOUR = new Color(34f/255f, 174f/255f, 34f/255f, 1f);
	public static final Color NUCLEAR_WASTE_COLOUR = new Color(34f/255f, 240f/255f, 34f/255f, 1f);
	public static final Color WATER_COLOUR = new Color(50f/255f, 132f/255f, 255f/255f, 1f);
	public static final Color MAGMA_COLOUR = Color.RED;
	public static final Color OIL_COLOUR = Color.BLACK;
	
	private RenderService gfx;
	
	private FrameBuffer frameBuffer;
	private Texture particleTex;
	private Texture wellFillTex;
	private Texture wellFillSolidTex;
	private ShaderProgram shader;
	
	
	
	public FluidRenderer(RenderService gfx, GraphicsResources resources) {
		this.gfx = gfx;
		
		this.frameBuffer = resources.newFrameBuffer(Pixmap.Format.RGBA8888, gfx.getWidth() / 4, gfx.getHeight());
		this.particleTex = resources.newTexture("sprites/particle.png");
		this.wellFillTex = resources.newTexture("tiles/wellfill.png");
		this.wellFillSolidTex = resources.newTexture("tiles/wellfillSolid.png");
		this.shader = resources.newShader("fluid");
	}
	
	public void updateRendering(Collection<Droplet> particles, Collection<Well> wells) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		gfx.batch.setBlendFunction(-1, -1); // disable blend func
		Gdx.gl.glBlendFuncSeparate(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_DST_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
		gfx.begin(frameBuffer);
		Color c = new Color();
		
		for (Droplet p : particles) {
			c.set(typeToColour(p.getType()));
			c.lerp(Color.WHITE, p.getFrothiness() * typeToFrothFactor(p.getType()) + 0.05f);
			c.a = p.getAlpha();
			gfx.drawCenteredTinted(particleTex, p.getPosition(), 0.5f, 0.5f, false, c);
		}
				
		final float wellOverfitting = 0.1f;
		for (Well well : wells) {
			if (well.getPercentFull() <= 0) { continue; }
			float width = well.getMaxPosition().x - well.getMinPosition().x;
			float height = (well.getMaxPosition().y - well.getMinPosition().y) * well.getPercentFull();
			c.set(typeToColour(well.getDropletAffinity()));
			
			final float surfaceHeight = 0.5f;
			if (height < surfaceHeight) {
				gfx.drawTinted(wellFillTex, new Vector2(well.getMinPosition().x-wellOverfitting, well.getMinPosition().y -wellOverfitting), width + wellOverfitting*2f, height + wellOverfitting*2f, c);
			}
			else {
				gfx.drawTinted(wellFillTex, new Vector2(well.getMinPosition().x-wellOverfitting, well.getMinPosition().y + height-surfaceHeight), width + wellOverfitting*2f, surfaceHeight + wellOverfitting*2f, c);
				gfx.drawTinted(wellFillSolidTex, new Vector2(well.getMinPosition().x-wellOverfitting, well.getMinPosition().y -wellOverfitting), width + wellOverfitting*2f, height - surfaceHeight + wellOverfitting, c);
			}
		}
		
		gfx.end(frameBuffer);
		gfx.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClearColor(0, 0, 0, 1);
	}
	
	public void compositeToScreen() {
		ShaderProgram.pedantic = false;
		gfx.drawToScreen(frameBuffer, shader, 2);
		ShaderProgram.pedantic = true;
	}
	
	public static Color typeToColour(Droplet.Type type) {
		switch (type) {
			case WATER: return WATER_COLOUR;
			case BLOOD: return BLOOD_COLOUR;
			case MAGMA: return MAGMA_COLOUR;
			case OIL:   return OIL_COLOUR;
			case WASTE: return NUCLEAR_WASTE_COLOUR;
		}
		throw new RuntimeException("Unhandled droplet type: " + type);
	}
	
	private static float typeToFrothFactor(Droplet.Type type) {
		switch (type) {
		case WATER: return 0.2f;
		case BLOOD: return 0.2f;
		case MAGMA: return 0.2f;
		case OIL:   return 0.02f;
		case WASTE: return 0.2f;
	}
	throw new RuntimeException("Unhandled droplet type: " + type);
	}
}
