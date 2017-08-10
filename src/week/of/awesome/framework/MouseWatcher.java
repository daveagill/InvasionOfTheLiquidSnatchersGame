package week.of.awesome.framework;

public interface MouseWatcher {
	public void buttonDown (int screenX, int screenY, int button);
	public void buttonUp (int screenX, int screenY, int button);
	
	public default void moved (int screenX, int screenY) { }
	public default void dragged (int screenX, int screenY) { }
	public default void movedOrDragged (int screenX, int screenY) { }
	public default void scrolled (int amount) { }
}
