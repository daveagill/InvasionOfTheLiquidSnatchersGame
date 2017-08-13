package week.of.awesome.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Minion implements Beamable {
	private static final float TIME_BETWEEN_CHARS = 0.05f;
	private static final float TIME_TO_FIRST_CHAR = 1.5f;
	
	private static final float DIALOG_RETENTION_DURATION = 2f;
	private static final float DIALOG_FADE_SPEED = 0.3f;
	
	private MinionSpec spec;
	private boolean alive = true;
	private Body body;
	private Vector2 positionWhenDead;
	private float deathAnimation = 1f;
	private boolean inBeam;
	private float beamSpeed;
	
	private List<String> dialogLines = new ArrayList<>();
	private int dialogCharIdx = 0;
	private float timeToNextChar = TIME_TO_FIRST_CHAR;
	private boolean justBegunDialog = false;
	private float dialogFadeAnimation = 1f;
	private float dialogRetainDuration = DIALOG_RETENTION_DURATION;
	
	public Minion(MinionSpec spec, Body b) {
		this.spec = spec;
		this.body = b;
		
		if (spec.dialog != null) {
			dialogLines.add("");
		}
	}
	
	public Body getBody() { return body; }
	public boolean isDead() { return !alive; }
	public boolean isEssential() { return spec.essential; }
	public Droplet.Type getDropletType() { return spec.fluidType; }
	public boolean isInteractive() { return spec.fluidType != null; }
	
	public float dialogFadeTween() { return dialogFadeAnimation; }
	public float deathAnimationTween() { return deathAnimation; }
	public boolean deathSceneComplete() { return deathAnimation <= 0f; }
	
	public Vector2 getPosition() {
		if (isDead()) {
			return positionWhenDead;
		}
		return body.getPosition();
	}
	
	public List<String> getDialog() {
		if (spec.dialog == null) { return null; }
		return dialogLines;
	}
	
	public boolean notifyDeath() {
		if (alive) {
			alive = false;
			positionWhenDead = body.getPosition().cpy();
			body = null;
			return true;
		}
		return false;
	}
	
	public void skipDeathAnimation() { deathAnimation = 0f; }
	
	public void update(float dt) {
		if (isDead()) {
			deathAnimation -= dt * 0.3f;
			return;
		}
		
		if (inBeam) {
			body.setLinearVelocity(beamSpeed, body.getLinearVelocity().y * 0.5f);
		}
	}
	
	public boolean updateDialog(float dt) {
		if (spec.dialog == null) { return false; }
		
		if (dialogCharIdx == spec.dialog.length()) {
			if (dialogRetainDuration < 0) {
				if (dialogFadeAnimation > 0) { dialogFadeAnimation -= dt * DIALOG_FADE_SPEED; } else { dialogFadeAnimation = 0; }
			}
			else {
				dialogRetainDuration -= dt;
			}
			
			return false;
		}
		
		timeToNextChar -= dt;
		justBegunDialog = false;
		if (timeToNextChar <= 0) {
			timeToNextChar = TIME_BETWEEN_CHARS;
			char latestChar = spec.dialog.charAt(dialogCharIdx);

			String latestLine = dialogLines.isEmpty() ? "" : dialogLines.get(dialogLines.size()-1);
			if (latestLine.length() >= 20 && Character.isWhitespace(latestChar) || latestChar == ';') {
				dialogLines.add("");
			}
			else {
				latestLine = latestLine + latestChar;
				dialogLines.set(dialogLines.size()-1, latestLine);
			}
			
			++dialogCharIdx;
			justBegunDialog = dialogCharIdx == 1;
		}
		return true;
	}
	
	public boolean justStartedTalking() {
		return justBegunDialog;
	}

	@Override
	public void onEnterBeam(Beam b) {
		inBeam = true;
		beamSpeed = b.getDirection() == BeamSpec.Direction.RIGHT ? 4 : -4;
		body.setGravityScale(0);
		body.setLinearVelocity(0, 0);
	}

	@Override
	public void onExitBeam() {
		if (isDead()) { return; }
		inBeam = false;
		body.setGravityScale(1);
	}
}
