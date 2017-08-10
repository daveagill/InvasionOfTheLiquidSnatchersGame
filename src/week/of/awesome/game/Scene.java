package week.of.awesome.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;

import week.of.awesome.framework.PhysicsService;
import week.of.awesome.game.PhysicsFactory.BodySet;

public class Scene implements Disposable {
	private static final float MINION_AIM_RANGE = 3.5f;
	private static final float SPRAY_SPEED = 10f;
	
	private WorldEvents events;
	
	private Collection<Body> persistentBodies = new ArrayList<>();
	private PhysicsFactory physics;
	private ContactListenerImpl contactListener = new ContactListenerImpl(this);
	private FluidSystem fluid;
	
	private Collection<Vector2> solids;
	private Collection<Vector2> leftSlopes;
	private Collection<Vector2> rightSlopes;
	private Collection<Minion> minions = new ArrayList<>();
	private Collection<Prop> props = new ArrayList<>();
	private Map<String, Well> wellsByID = new HashMap<>();
	private Collection<Platform> platforms = new ArrayList<>();
	private Collection<TrapDoor> trapdoors = new ArrayList<>();
	private Collection<Gear> gears = new ArrayList<>();
	
	private boolean isSpraying = false;
	private Vector2 sprayDirection = new Vector2(1f, 1f);
	private Minion activeMinion;
	
	private boolean isGameOver = false;
	
	
	public Scene(WorldEvents events, Level level, PhysicsService physicsService) {
		this.events = events;
		this.physics = new PhysicsFactory(physicsService);
		this.fluid = new FluidSystem(physics);
		
		physicsService.setContactListener(contactListener);
		
		// SOLID BLOCKS
		this.solids = level.solids;
		for (Vector2 solidPos : level.solids) {
			Body b = physics.createSolidBlock(solidPos);
			persistentBodies.add(b);
		}
		
		// LEFT SLOPES
		this.leftSlopes = level.leftSlopes;
		for (Vector2 pos : level.leftSlopes) {
			Body b = physics.createSlope(pos, false);
			persistentBodies.add(b);
		}
		
		// RIGHT SLOPES
		this.rightSlopes = level.rightSlopes;
		for (Vector2 pos : level.rightSlopes) {
			Body b = physics.createSlope(pos, true);
			persistentBodies.add(b);
		}

		// MINIONS
		for (MinionSpec minionSpec : level.minions) {
			Body b = physics.createMinion(minionSpec.position.cpy().add(0.5f, 0));
			persistentBodies.add(b);
			Minion m = new Minion(minionSpec, b);
			minions.add(m);
			b.setUserData(m);
		}
		
		// PROPS
		for (PropSpec propSpec : level.props) {
			Body b = null;
			switch (propSpec.type) {
				case BALL:	b = physics.createBallProp(propSpec.position.cpy().add(0.5f, 0)); break;
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
		for (VatSpec vatSpec : level.vats) {
			int numParticles = (int) ((vatSpec.max.x - vatSpec.min.x) * (vatSpec.max.y - vatSpec.min.y) * 15);
			for (int i = 0; i < numParticles; ++i) {
				float x = MathUtils.lerp(vatSpec.min.x, vatSpec.max.x, (float) Math.random());
				float y = MathUtils.lerp(vatSpec.min.y, vatSpec.max.y, (float) Math.random());
				fluid.spawnParticle(vatSpec.type, new Vector2(x, y), Vector2.Zero, true);
			}
		}
		
		// PLATFORMS
		for (PlatformSpec platformSpec : level.platforms) {
			Body b = physics.createPlatform(platformSpec.position);
			persistentBodies.add(b);
			Platform p = new Platform(platformSpec, b);
			platforms.add(p);
			wireToWells(p, platformSpec);
		}
		
		// TRAPDOORS
		for (TrapDoorSpec trapdoorSpec : level.trapdoors) {
			BodySet bodies = physics.createTrapDoor(trapdoorSpec.position);
			persistentBodies.addAll(bodies.all);
			TrapDoor td = new TrapDoor(bodies.main);
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
	}
	
	public Collection<Minion> getMinions() {
		return minions;
	}
	
	public Vector2 getSprayDirection() {
		return sprayDirection;
	}
	
	public Vector2 getActiveSprayPosition() {
		return activeMinion == null ? null : activeMinion.getPosition();
	}
	
	public Collection<Droplet> getDroplets() {
		return fluid.getDroplets();
	}
	
	public Collection<Vector2> getSolids() {
		return solids;
	}
	public Collection<Vector2> getLeftSlopes() {
		return leftSlopes;
	}
	public Collection<Vector2> getRightSlopes() {
		return rightSlopes;
	}
	
	public Collection<Prop> getProps() {
		return props;
	}
	
	public Collection<Well> getWells() {
		return wellsByID.values();
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
	
	public void aim(float x, float y) {
		if (!isSpraying) {
			// find the nearest minion within range
			activeMinion = null;
			for (Minion m : minions) {
				if (Vector2.dst2(x, y, m.getPosition().x, m.getPosition().y) < MINION_AIM_RANGE*MINION_AIM_RANGE) {
					activeMinion = m;
				}
			}
		}
		
		if (activeMinion != null) {
			sprayDirection = new Vector2(x - activeMinion.getPosition().x, y - activeMinion.getPosition().y).nor();
		}
	}
	
	public void spray(boolean active) {
		this.isSpraying = active;
		if (!isSpraying) {
			activeMinion = null;
		}
	}
	
	void onCaptureDroplet(Droplet d, Well w) {
		d.beginCaptureDecay();
		if (w.getDropletAffinity() == d.getType()) {
			w.notifyCapturedDroplets(1);
			events.captureDroplet(d, w);
		}
	}
	
	void onPlayerDeath(Minion m) {
		if (m.notifyDeath()) {
			Random r = new Random();
			for (int i = 0; i < 70; ++i) {
				float xRand = r.nextFloat()-0.5f;
				float yRand = r.nextFloat()-0.5f;
				float xVelRand = (r.nextFloat()-0.5f) * 2f;
				float yVelRand = (r.nextFloat()-0.5f) * 2f;
				fluid.spawnParticle(Droplet.Type.BLOOD, m.getPosition().cpy().add(xRand, yRand), new Vector2(xVelRand, yVelRand), false);
			}
			events.minionDeath(m);
		}
	}
	
	public void update(float dt) {
		contactListener.dispatchCollisionEvents();
		
		for (Minion m : minions) {
			m.update(dt);
			
			if (!isGameOver && m.deathSceneComplete()) {
				isGameOver = true;
				events.gameOver();
			}
		}
		
		if (isSpraying && activeMinion != null) {
			fluid.spawnParticle(Droplet.Type.WATER, activeMinion.getPosition(), sprayDirection.cpy().scl(SPRAY_SPEED), false);
		}
				
		fluid.update(dt);
		
		for (Platform p : platforms) {
			p.update(dt);
		}
		
		for (Gear g : gears) {
			g.update(dt);
		}
	}
	
	@Override
	public void dispose() {
		persistentBodies.forEach(physics::destroy);
		fluid.dispose();
	}
	
	private void wireToWells(Activatable a, ActivatableSpec spec) {
		for (String wellID : spec.wellActivatorIDs) {
			Well well = wellsByID.get(wellID);
			if (well == null) { throw new RuntimeException(spec.getClass().getSimpleName() + " at " + spec.position + " has lost connection to well " + wellID); }
			well.registerActivatable(a);
		}
	}
}