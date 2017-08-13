package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;

import week.of.awesome.framework.PhysicsService;
import week.of.awesome.game.PhysicsFactory.BodySet;

public class Scene implements Disposable {
	private static final float SPRAY_SPEED = 10f;
	
	private Level level;
	private Rectangle levelSize;
	private WorldEvents events;
	
	private Collection<Body> persistentBodies = new ArrayList<>();
	private PhysicsFactory physics;
	private ContactListenerImpl contactListener = new ContactListenerImpl(this);
	private FluidSystem fluid;
	
	private Collection<Minion> minions = new ArrayList<>();
	private Collection<Prop> props = new ArrayList<>();
	private Collection<Beam> beams = new ArrayList<>();
	private Map<String, Well> wellsByID = new HashMap<>();
	
	// activatables
	private Collection<Platform> platforms = new ArrayList<>();
	private Collection<TrapDoor> trapdoors = new ArrayList<>();
	private Collection<Gear> gears = new ArrayList<>();
	private Collection<Spaceship> spaceships = new ArrayList<>();
	
	private boolean isSpraying = false;
	private Vector2 sprayDirection = new Vector2(1f, 1f);
	private Minion activeMinion;
	
	private Vector2 aimPos = new Vector2();
	
	private boolean levelComplete = false;
	
	
	public Scene(WorldEvents events, Level level, PhysicsService physicsService) {
		this.level = level;
		this.levelSize = level.calculateSize();
		this.events = events;
		this.physics = new PhysicsFactory(physicsService);
		this.fluid = new FluidSystem(physics);
		
		physicsService.setContactListener(contactListener);
		
		// SOLID BLOCKS
		for (SolidSpec solidSpec : level.solids) {
			Body b = physics.createSolidBlock(solidSpec.min, solidSpec.max);
			persistentBodies.add(b);
		}
		
		// LEFT SLOPES
		for (Vector2 pos : level.leftSlopes) {
			Body b = physics.createSlope(pos, false);
			persistentBodies.add(b);
		}
		
		// RIGHT SLOPES
		for (Vector2 pos : level.rightSlopes) {
			Body b = physics.createSlope(pos, true);
			persistentBodies.add(b);
		}

		// MINIONS
		for (MinionSpec minionSpec : level.minions) {
			Body b = physics.createMinion(minionSpec.position.cpy().add(0.5f, 0));
			Minion m = new Minion(minionSpec, b);
			minions.add(m);
			b.setUserData(m);
		}
		
		// PROPS
		for (PropSpec propSpec : level.props) {
			Body b = null;
			switch (propSpec.type) {
				case BALL:	b = physics.createBallProp(propSpec.position.cpy().add(0.5f, 0.4f)); break;
				case BLOCK: b = physics.createBallProp(propSpec.position.cpy().add(0.5f, 0)); break;
				case DOMINO: b = physics.createDominoProp(propSpec.position.cpy().add(0.5f, 0)); break;
			}
			persistentBodies.add(b);
			props.add(new Prop(propSpec, b));
		}
		
		// WELLS
		for (WellSpec wellSpec : level.wells) {
			Body b = physics.createWell(wellSpec.min, wellSpec.max);
			persistentBodies.add(b);
			Well w = new Well(wellSpec);
			wellsByID.put(wellSpec.id, w);
			b.setUserData(w);
		}
		
		// VATS
		Random r = new Random(1);
		for (VatSpec vatSpec : level.vats) {
			int numParticles = (int) ((vatSpec.max.x - vatSpec.min.x) * (vatSpec.max.y - vatSpec.min.y) * 15);
			for (int i = 0; i < numParticles; ++i) {
				float x = MathUtils.lerp(vatSpec.min.x, vatSpec.max.x, r.nextFloat());
				float y = MathUtils.lerp(vatSpec.min.y, vatSpec.max.y, r.nextFloat());
				fluid.spawnParticle(vatSpec.type, new Vector2(x, y), Vector2.Zero, true);
			}
		}
		
		// BEAMS
		for (BeamSpec beamSpec : level.beams) {
			Body body = physics.createBeam(beamSpec.min, beamSpec.max);
			persistentBodies.add(body);
			Beam beam = new Beam(beamSpec);
			beams.add(beam);
			body.setUserData(beam);
		}
		
		// PLATFORMS
		for (PlatformSpec platformSpec : level.platforms) {
			Body b = physics.createPlatform(platformSpec.position);
			persistentBodies.add(b);
			Platform p = new Platform(platformSpec, b);
			platforms.add(p);
			wireToWells(p, platformSpec);
			
			// implicitly create a 1x1 solid
			Body implicitSolid = physics.createSolidBlock(platformSpec.position, platformSpec.position.cpy().add(1, 1));
			persistentBodies.add(implicitSolid);
		}
		
		// TRAPDOORS
		for (TrapDoorSpec trapdoorSpec : level.trapdoors) {
			BodySet bodies = physics.createTrapDoor(trapdoorSpec.position, trapdoorSpec.width);
			persistentBodies.addAll(bodies.all);
			TrapDoor td = new TrapDoor(trapdoorSpec, bodies.main);
			trapdoors.add(td);
			wireToWells(td, trapdoorSpec);
		}
		
		// GEARS
		for (GearSpec gearSpec : level.gears) {
			BodySet bodies = physics.createGear(gearSpec.position, gearSpec.radius);
			persistentBodies.addAll(bodies.all);
			Gear g = new Gear(gearSpec, bodies.main);
			gears.add(g);
			wireToWells(g, gearSpec);
		}
		
		// SPACESHIPS
		for (SpaceshipSpec spaceshipSpec : level.spaceships) {
			Spaceship s = new Spaceship(spaceshipSpec);
			spaceships.add(s);
			wireToWells(s, spaceshipSpec);
		}
	}
	
	public Level getLevel() {
		return level;
	}
	
	public Rectangle getLevelSize() {
		return levelSize;
	}
	
	public Collection<Minion> getMinions() {
		return minions;
	}
	
	public Vector2 getSprayDirection() {
		return sprayDirection;
	}
	
	public Minion getActiveMinion() {
		return activeMinion;
	}
	
	public Collection<Droplet> getDroplets() {
		return fluid.getDroplets();
	}
	
	public Collection<Prop> getProps() {
		return props;
	}
	
	public Collection<Well> getWells() {
		return wellsByID.values();
	}
	
	public Collection<Beam> getBeams() {
		return beams;
	}
	
	public Collection<Platform> getPlatforms() {
		return platforms;
	}
	
	public Collection<TrapDoor> getTrapDoors() {
		return trapdoors;
	}
	
	public Collection<Gear> getGears() {
		return gears;
	}
	
	public Collection<Spaceship> getSpaceships() {
		return spaceships;
	}
	
	public Vector2 getAimPos() {
		return aimPos;
	}
	
	public void aim(float x, float y) {
		aimPos.set(x, y);
		
		if (!isSpraying) {
			// find the nearest minion within range
			Minion nearestMinion = null;
			float closestDistSquared = Float.POSITIVE_INFINITY;
			for (Minion m : minions) {
				if (m.isDead() || !m.isInteractive()) { continue; }
				float distanceToMinionSquared = Vector2.dst2(x, y, m.getPosition().x, m.getPosition().y);
				if (distanceToMinionSquared < closestDistSquared) {
					nearestMinion = m;
					closestDistSquared = distanceToMinionSquared;
				}
			}
			
			// if we found one then set it to be the active minion
			if (nearestMinion != null) {
				this.activeMinion = nearestMinion;
			}
		}
		
		if (activeMinion != null) {
			sprayDirection = new Vector2(x - activeMinion.getPosition().x, y - activeMinion.getPosition().y).nor();
		}
	}
	
	public void spray(boolean active) {
		this.isSpraying = active;
	}
	
	public void killAllMinions() {
		for (Minion m : minions) {
			onMinionDeath(m);
		}
	}
	
	void onCaptureDroplet(Droplet d, Well w) {
		d.beginCaptureDecay();
		if (w.getDropletAffinity() == d.getType()) {
			events.captureDroplet(d, w);
			
			boolean justActivated = w.notifyCapturedDroplets(1);
			if (justActivated) {
				for (Activatable a : w.getActivatables()) {
					a.activate(events);
				}
			}
		}
	}
	
	void onMinionDeath(Minion m) {
		Body b = m.getBody();
		if (m.notifyDeath()) {
			physics.destroy(b);
			Random r = new Random();
			for (int i = 0; i < 50; ++i) {
				float xRand = r.nextFloat()-0.5f;
				float yRand = r.nextFloat()-0.5f;
				float xVelRand = (r.nextFloat()-0.5f) * 2f;
				float yVelRand = (r.nextFloat()-0.5f) * 2f;
				fluid.spawnParticle(Droplet.Type.BLOOD, m.getPosition().cpy().add(xRand, yRand), new Vector2(xVelRand, yVelRand), false);
			}
			events.minionDeath(m);
			
			if (m == activeMinion) {
				activeMinion = null;
			}
		}
	}
	
	void onCaughtInBeam(Beam beam, Beamable item) {
		item.onEnterBeam(beam);
		events.beamActivated();
	}
	
	void onExitBeam(Beam beam, Beamable item) {
		item.onExitBeam();
	}
	
	public void onDullImpact(float xVel, float yVel, boolean isEntity) {
		if (isEntity) {
			if (yVel < xVel && yVel < -1f) {
				events.minionLanding();
			}
		}
		else {
			if (yVel < -3f || yVel > 3f || xVel < -3f || xVel > 3f) {
				events.propLanding();
			}
		}
	}
	
	public void onPropToPropImpact(float speedSquared) {
		if (speedSquared > 1) {
			events.propLanding();
		}
	}
	
	public void update(float dt) {
		contactListener.dispatchCollisionEvents();
		
		boolean isPerformingDialogYet = false;
		Iterator<Minion> minionIter = minions.iterator();
		while (minionIter.hasNext()) {
			Minion m = minionIter.next();
			m.update(dt);
			
			if (!isPerformingDialogYet) {
				isPerformingDialogYet = m.updateDialog(dt);
				if (m.justStartedTalking()) {
					events.minionSpeak();
				}
			}
			
			if (m.deathSceneComplete()) {
				minionIter.remove();
				if (m.isEssential()) {
					events.gameOver();
				}
			}
			
			// kill minions falling below the screen
			if (m.getPosition().y < levelSize.y) {
				onMinionDeath(m);
				m.skipDeathAnimation();
			}
		}
		
		if (isPerformingDialogYet) {
			activeMinion = null;
		}
		
		if (minions.isEmpty() && !level.minions.isEmpty()) {
			events.gameOver();
		}
		
		if (isSpraying && activeMinion != null) {
			fluid.spawnParticle(activeMinion.getDropletType(), activeMinion.getPosition().cpy().add(0, 0.2f).add(sprayDirection.cpy().scl(0.3f)), sprayDirection.cpy().scl(SPRAY_SPEED), false);
			events.sprayDroplet();
		}
		
		fluid.update(dt);
		
		for (Platform p : platforms) {
			p.update(dt);
		}
		
		int numGearsMoving = 0;
		for (Gear g : gears) {
			g.update(dt);
			if (g.hasSpunEnough()) {
				++numGearsMoving;
			}
		}
		
		int numSpaceshipsFlownAway = 0;
		for (Spaceship s : spaceships) {
			s.update(dt);
			if (s.hasFlownOff()) {
				++numSpaceshipsFlownAway;
			}
		}
		
		if (numSpaceshipsFlownAway == spaceships.size() && numGearsMoving == gears.size() && !levelComplete && !(spaceships.isEmpty() && gears.isEmpty())) {
			levelComplete = true;
			events.levelCompleted();
		}
		
		// kill props falling to infinity
		Iterator<Prop> propIter = props.iterator();
		while (propIter.hasNext()) {
			Prop p = propIter.next();
			if (p.getPosition().y < -1000) {
				persistentBodies.remove(p.getBody());
				physics.destroy(p.getBody());
				propIter.remove();
			}
		}
	}
	
	@Override
	public void dispose() {
		persistentBodies.forEach(physics::destroy);
		for (Minion m : minions) {
			if (m.getBody() != null) {
				physics.destroy(m.getBody());
			}
		}
		fluid.dispose();
	}
	
	private void wireToWells(Activatable a, ActivatableSpec spec) {
		for (String wellID : spec.wellActivatorIDs) {
			Well well = wellsByID.get(wellID);
			if (well == null) {
				System.err.println(spec.getClass().getSimpleName() + " at " + spec.position + " has lost connection to well " + wellID);
				continue;
			}
			well.registerActivatable(a);
		}
	}

}
