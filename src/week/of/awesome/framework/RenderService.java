package week.of.awesome.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

public class RenderService implements Disposable {
	private GL20 gl;
	private SpriteBatch batch = new SpriteBatch();
	
	private OrthographicCamera camera  = new OrthographicCamera();
	private int width, height;

	public RenderService() {
		gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
	}
	
	public void resizeViewport(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.update();
		
		this.width = width;
		this.height = height;
	}
	
	public int getMidX() {
		return width / 2;
	}
	
	public int getMidY() {
		return height / 2;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void beginFrame() {
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
	}
	
	public void begin(FrameBuffer fb) {
		fb.begin();
		beginFrame();
	}
	
	public void endFrame() {
		batch.end();
	}
	
	public void end(FrameBuffer fb) {
		endFrame();
		fb.end();
	}
	
	public void setTransformMatrix(Matrix4 m) {
		batch.setTransformMatrix(m);
	}
	
	public void drawScreen(Texture t, float alpha) {
		batch.setColor(new Color(1f, 1f, 1f, alpha));
		batch.draw(t, 0, 0, width, height);
		batch.setColor(Color.WHITE);
	}
	
	public void drawToScreen(FrameBuffer fb, ShaderProgram s) {
		batch.setShader(s);
		batch.draw(fb.getColorBufferTexture(), 0, height, width, -height);
		batch.setShader(null);
	}
	
	public void draw(Texture t, Vector2 pos, float width, float height, float alpha) {
		batch.setColor(new Color(1f, 1f, 1f, alpha));
		batch.draw(t, pos.x, pos.y, width, height);
		batch.setColor(Color.WHITE);
	}
	
	public void drawCentered(Texture t, Vector2 pos, float width, float height, boolean flipX) {
		drawCenteredTinted(t, pos, width, height, flipX, Color.WHITE);
	}
	
	public void drawCenteredTinted(Texture t, Vector2 pos, float width, float height, boolean flipX, Color colour) {
		float actualWidth = flipX ? -width : width;
		batch.setColor(colour);
		batch.draw(t, pos.x - actualWidth/2, pos.y - height/2, actualWidth, height);
		batch.setColor(Color.WHITE);
	}
	
	public GlyphLayout drawFont(BitmapFont font, String str, float x, float y, float alpha) {
		font.setColor(1, 1, 1, alpha);
		return font.draw(batch, str, x, y);
	}
	
	public GlyphLayout drawWrappedFont(BitmapFont font, String str, float x, float y, float width, float alpha) {
		font.setColor(1, 1, 1, alpha);
		return font.draw(batch, str, x, y, width/2f, Align.topLeft, true);
	}
	
	public GlyphLayout drawCenteredFont(BitmapFont font, String str, float midX, float y, float alpha) {
		font.setColor(1, 1, 1, alpha);
		GlyphLayout glyphLayout = new GlyphLayout(font, str);
		return font.draw(batch, str, midX - glyphLayout.width/2f, y);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
