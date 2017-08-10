package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.math.Vector2;

public abstract class ActivatableSpec {
	public Vector2 position;
	public Collection<String> wellActivatorIDs = new ArrayList<>();
}
