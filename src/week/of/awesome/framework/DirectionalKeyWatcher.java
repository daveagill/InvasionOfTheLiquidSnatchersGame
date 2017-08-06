package week.of.awesome.framework;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

import com.badlogic.gdx.Input.Keys;

public class DirectionalKeyWatcher implements KeyWatcher {
	
	public static enum Direction {
		UP, DOWN, RIGHT, LEFT
	}
	
	private Deque<Direction> directionStack = new ArrayDeque<>();

	@Override
	public boolean keyDown(int keycode) {
		Direction direction = mapToDirection(keycode);
		if (direction != null) {
			directionStack.addLast(direction);
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		Direction direction = mapToDirection(keycode);
		if (direction != null) {
			directionStack.remove(direction);
		}
		return false;
	}
	
	public Collection<Direction> pollSequence() {
		return Collections.unmodifiableCollection(directionStack);
	}
	
	public Direction pollLast() {
		if (directionStack.isEmpty()) { return null; }
		return directionStack.getLast();
	}
	
	private static Direction mapToDirection(int keycode) {
		if (keycode == Keys.RIGHT || keycode == Keys.D) {
			return Direction.RIGHT;
		}
		if (keycode == Keys.LEFT || keycode == Keys.A) {
			return Direction.LEFT;
		}
		if (keycode == Keys.UP || keycode == Keys.W) {
			return Direction.UP;
		}
		if (keycode == Keys.DOWN || keycode == Keys.S) {
			return Direction.DOWN;
		}
		
		return null;
	}
}
