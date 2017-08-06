package week.of.awesome.framework;

public interface GameState {
	public void onEnter(Services services);
	public default void onExit() { }

	public GameState update(float dt);
	public void render(float dt);
}