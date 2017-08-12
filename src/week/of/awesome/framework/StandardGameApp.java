package week.of.awesome.framework;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.TimeUtils;

public class StandardGameApp implements ApplicationListener {
	
	private static final float FIXED_TIMESTEP = 1f / 60f;
	private static final long FIXED_TIMESTEP_NANOS = (long)(FIXED_TIMESTEP * 1000000000L);
	
	
	private Services services = new Services();

	private GameState currentState;
	
	private long lastFrameTime = -1;
	private long accumulatedTime;
	
	public StandardGameApp(GameState initialGameState) {
		currentState = initialGameState;
	}
	
	@Override
	public void create () {
		// setup input system
		services.input = new InputService();
		
		// setup graphics system
		services.gfx = new RenderService();
		services.gfxResources = new GraphicsResources();
		
		// setup audio system
		services.jukebox = new JukeboxService();
		services.sfxResources = new SoundResources();
		
		// setup physics system
		services.physics = new PhysicsService();
	}

	@Override
	public void render () {
		// 1st frame setup
		if (lastFrameTime <= 0) {
			lastFrameTime = TimeUtils.nanoTime();
			currentState.onEnter(services);
		}
		
		long time = TimeUtils.nanoTime();
		accumulatedTime += (time - lastFrameTime);
		lastFrameTime = time;
		
		float frameSimulationTime = 0;
		int numIterations = 0;
		
		if (accumulatedTime >= FIXED_TIMESTEP_NANOS) {
			++numIterations;
			services.physics.update(FIXED_TIMESTEP);
			GameState nextState = currentState.update(FIXED_TIMESTEP);
			if (nextState != null) {
				services.input.removeAllWatchers();
				services.physics.setContactListener(null);
				
				currentState.onExit();
				currentState = nextState;
				nextState.onEnter(services);
			}
			
			accumulatedTime -= FIXED_TIMESTEP_NANOS;
			frameSimulationTime += FIXED_TIMESTEP;
			services.input.postUpdate();
		}
		
		services.jukebox.update(frameSimulationTime);
		currentState.render(frameSimulationTime);
	}
	


	@Override
	public void resize(int width, int height) {
		services.gfx.resizeViewport(width, height);
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }
	
	@Override
	public void dispose () {
		currentState.onExit();
		
		services.gfx.dispose();
		services.gfxResources.dispose();
		services.jukebox.dispose();
		services.sfxResources.dispose();
		services.physics.dispose();
	}
}
