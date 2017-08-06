package week.of.awesome.game;

import com.badlogic.gdx.math.Matrix4;
import week.of.awesome.framework.GameState;
import week.of.awesome.framework.Services;

public class EmptyGameState implements GameState {
	
	private Services services;

	@Override
	public void onEnter(Services services) {
		this.services = services;
	}

	@Override
	public GameState update(float dt) {
		return null;
	}

	@Override
	public void render(float dt) {
		services.gfx.setTransformMatrix(new Matrix4());
		
		services.gfx.beginFrame();
		
		
		services.gfx.endFrame();
	}
}
