package week.of.awesome.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class GraphicsResources implements Disposable {

	private Map<String, Texture> textureCache = new HashMap<>();
	private Map<String, BitmapFont> fontCache = new HashMap<>();
	private Map<String, ShaderProgram> shaderCache = new HashMap<>();
	private Collection<FrameBuffer> frameBuffers = new ArrayList<>();
	
	public Texture newTexture(String path) {
		Texture t = textureCache.get(path);
		if (t == null) {
			t = new Texture(path);
			t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			textureCache.put(path, t);
		}
		return t;
	}
	
	public BitmapFont newFont(String path) {
		BitmapFont f = fontCache.get(path);
		if (f == null) {
			f = new BitmapFont(Gdx.files.internal(path));
			fontCache.put(path, f);
		}
		return f;
	}

	public ShaderProgram newShader(String path) {
		ShaderProgram s = shaderCache.get(path);
		if (s == null) {
			s = new ShaderProgram(Gdx.files.internal("default.vert"), Gdx.files.internal(path + ".frag"));
			
			if (!s.isCompiled()) {
				throw new RuntimeException("Failed to compile shader '" + path + "': " + s.getLog());
			}
			
			shaderCache.put(path, s);
		}
		return s;
	}
	
	public FrameBuffer newFrameBuffer(Pixmap.Format format, int width, int height) {
		FrameBuffer fb = new FrameBuffer(format, width, height, false);
		frameBuffers.add(fb);
		return fb;
	}
	
	@Override
	public void dispose() {
		textureCache.values().forEach(Disposable::dispose);
		fontCache.values().forEach(Disposable::dispose);
		shaderCache.values().forEach(Disposable::dispose);
		frameBuffers.forEach(Disposable::dispose);
	}
}
