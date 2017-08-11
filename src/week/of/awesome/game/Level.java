package week.of.awesome.game;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

public class Level {
	public Collection<MinionSpec> minions = new ArrayList<>();
	public Collection<Vector2> solids = new ArrayList<>();
	public Collection<Vector2> leftSlopes = new ArrayList<>();
	public Collection<Vector2> rightSlopes = new ArrayList<>();
	public Collection<PropSpec> props = new ArrayList<>();
	public Collection<WellSpec> wells = new ArrayList<>();
	public Collection<VatSpec> vats = new ArrayList<>();
	public Collection<BeamSpec> beams = new ArrayList<>();
	
	// activatables
	public Collection<PlatformSpec> platforms = new ArrayList<>();
	public Collection<TrapDoorSpec> trapdoors = new ArrayList<>();
	public Collection<GearSpec> gears = new ArrayList<>();
	public Collection<SpaceshipSpec> spaceships = new ArrayList<>();
	
	public void removeAt(int x, int y) {
		minions.removeIf(m -> m.position.x == x && m.position.y == y);
		solids.remove(new Vector2(x, y));
		leftSlopes.remove(new Vector2(x, y));
		rightSlopes.remove(new Vector2(x, y));
		props.removeIf(p -> p.position.x >= x && p.position.x <= x+1 && p.position.y >= y && p.position.y <= y+1);
		wells.removeIf(w -> w.min.x <= x && x < w.max.x && w.min.y <= y && y < w.max.y);
		vats.removeIf(v -> v.min.x <= x && x < v.max.x && v.min.y <= y && y < v.max.y);
		beams.removeIf(b -> b.min.x <= x && x < b.max.x && b.min.y <= y && y < b.max.y);
		platforms.removeIf(p -> p.position.x == x && p.position.y == y);
		trapdoors.removeIf(p -> p.position.x == x && p.position.y == y);
		gears.removeIf(g -> Vector2.dst2(x, y, g.position.x, g.position.y) < g.radius*g.radius);
		spaceships.removeIf(s -> s.position.x == x && s.position.y == y);
	}
	
	public WellSpec getWell(int x, int y) {
		for (WellSpec w : wells) {
			if (w.min.x <= x && x < w.max.x && w.min.y <= y && y < w.max.y) {
				return w;
			}
		}
		return null;
	}
	
	public ActivatableSpec getActivatable(int x, int y) {
		Collection<ActivatableSpec> activatables = new ArrayList<>();
		activatables.addAll(platforms);
		activatables.addAll(trapdoors);
		activatables.addAll(gears);
		activatables.addAll(spaceships);
		
		for (ActivatableSpec a : activatables) {
			if (a.position.x == x && a.position.y == y) {
				return a;
			}
		}
		return null;
	}
	
	public Rectangle calculateSize() {
		// just based on the solids will do
		float minX = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		
		for (Vector2 p : solids) {
			minX = Math.min(minX, p.x);
			maxX = Math.max(maxX, p.x);
			minY = Math.min(minY, p.y);
			maxY = Math.max(maxY, p.y);
		}
		
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	public static void save(Level level, String filename) {
		try (StringWriter writer = new StringWriter();
		     XmlWriter xmlWriter = new XmlWriter(writer)) {
			
			XmlWriter xml = xmlWriter.element("level").attribute("name", "test");
			
			for (MinionSpec minion : level.minions) {
				xml.element("minion")
					.attribute("x", minion.position.x).attribute("y", minion.position.y)
					.pop();
			}
			
			for (Vector2 pos : level.solids) {
				xml.element("solid").attribute("x", pos.x).attribute("y", pos.y).pop();
			}
			for (Vector2 pos : level.leftSlopes) {
				xml.element("leftSlope").attribute("x", pos.x).attribute("y", pos.y).pop();
			}
			for (Vector2 pos : level.rightSlopes) {
				xml.element("rightSlope").attribute("x", pos.x).attribute("y", pos.y).pop();
			}
			
			for (PropSpec prop : level.props) {
				xml.element("prop").attribute("x", prop.position.x).attribute("y", prop.position.y).attribute("type", prop.type).pop();
			}
			
			for (WellSpec well : level.wells) {
				xml.element("well")
					.attribute("id", well.id)
					.attribute("minX", well.min.x).attribute("minY", well.min.y)
					.attribute("maxX", well.max.x).attribute("maxY", well.max.y)
					.attribute("affinity", well.affinity)
					.pop();
			}
			
			for (VatSpec vat : level.vats) {
				xml.element("vat")
					.attribute("minX", vat.min.x).attribute("minY", vat.min.y)
					.attribute("maxX", vat.max.x).attribute("maxY", vat.max.y)
					.attribute("type", vat.type)
					.pop();
			}
			
			for (BeamSpec beam : level.beams) {
				xml.element("beam")
					.attribute("minX", beam.min.x).attribute("minY", beam.min.y)
					.attribute("maxX", beam.max.x).attribute("maxY", beam.max.y)
					.attribute("dir", beam.dir)
					.pop();
			}
			
			for (PlatformSpec platform : level.platforms) {
				writeActivatable(
						xml.element("platform").attribute("height", platform.height),
						platform).pop();
			}
			
			for (TrapDoorSpec trapdoor : level.trapdoors) {
				writeActivatable(
						xml.element("trapdoor"),
						trapdoor).pop();
			}
			
			for (GearSpec gear : level.gears) {
				writeActivatable(
						xml.element("gear").attribute("radius", gear.radius).attribute("rotatesRight", gear.rotatesRight),
						gear).pop();
			}
			
			for (SpaceshipSpec spaceship : level.spaceships) {
				writeActivatable(
						xml.element("spaceship"),
						spaceship).pop();
			}
			
			xml.close();

			Gdx.files.local("assets/" + filename).writeString(writer.toString(), false);
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	private static XmlWriter writeActivatable(XmlWriter xml, ActivatableSpec a) throws IOException {
		xml = xml.attribute("x", a.position.x).attribute("y", a.position.y);
		for (String wellID : a.wellActivatorIDs) {
			xml = xml.element("wellActivationID", wellID);
		}
		return xml;
	}
	
	public static Level load(String filename) {
		Element xml = null;
		try {
			xml = new XmlReader().parse(Gdx.files.local(filename));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		Level level = new Level();
		
		for (Element minionXml : xml.getChildrenByName("minion")) {
			MinionSpec minion = new MinionSpec();
			minion.position = new Vector2(minionXml.getFloatAttribute("x"), minionXml.getFloatAttribute("y"));
			level.minions.add(minion);
		}
		
		for (Element solid : xml.getChildrenByName("solid")) {
			level.solids.add( new Vector2(solid.getFloatAttribute("x"), solid.getFloatAttribute("y")) );
		}
		for (Element leftSlopeXml : xml.getChildrenByName("leftSlope")) {
			level.leftSlopes.add( new Vector2(leftSlopeXml.getFloatAttribute("x"), leftSlopeXml.getFloatAttribute("y")) );
		}
		for (Element rightSlopeXml : xml.getChildrenByName("rightSlope")) {
			level.rightSlopes.add( new Vector2(rightSlopeXml.getFloatAttribute("x"), rightSlopeXml.getFloatAttribute("y")) );
		}
		
		for (Element propXml : xml.getChildrenByName("prop")) {
			PropSpec prop = new PropSpec();
			prop.position = new Vector2(propXml.getFloatAttribute("x"), propXml.getFloatAttribute("y"));
			prop.type = PropSpec.Type.valueOf( propXml.getAttribute("type") );
			level.props.add(prop);
		}
		
		for (Element wellXml : xml.getChildrenByName("well")) {
			WellSpec well = new WellSpec();
			level.wells.add(well);
			
			well.id = wellXml.getAttribute("id");
			well.min = new Vector2(wellXml.getFloatAttribute("minX"), wellXml.getFloatAttribute("minY"));
			well.max = new Vector2(wellXml.getFloatAttribute("maxX"), wellXml.getFloatAttribute("maxY"));
			well.affinity = Droplet.Type.valueOf( wellXml.getAttribute("affinity") );
		}
		
		for (Element vatXml : xml.getChildrenByName("vat")) {
			VatSpec vat = new VatSpec();
			level.vats.add(vat);
			
			vat.min = new Vector2(vatXml.getFloatAttribute("minX"), vatXml.getFloatAttribute("minY"));
			vat.max = new Vector2(vatXml.getFloatAttribute("maxX"), vatXml.getFloatAttribute("maxY"));
			vat.type = Droplet.Type.valueOf( vatXml.getAttribute("type") );
		}
		
		for (Element beamXml : xml.getChildrenByName("beam")) {
			BeamSpec beam = new BeamSpec();
			level.beams.add(beam);
			
			beam.min = new Vector2(beamXml.getFloatAttribute("minX"), beamXml.getFloatAttribute("minY"));
			beam.max = new Vector2(beamXml.getFloatAttribute("maxX"), beamXml.getFloatAttribute("maxY"));
			beam.dir = BeamSpec.Direction.valueOf( beamXml.getAttribute("dir") );
		}
		
		for (Element platformXml : xml.getChildrenByName("platform")) {
			PlatformSpec platform = new PlatformSpec();
			level.platforms.add(platform);
			
			readActivable(platformXml, platform);
			platform.height = platformXml.getFloatAttribute("height");
		}
		
		for (Element trapdoorXml : xml.getChildrenByName("trapdoor")) {
			TrapDoorSpec trapdoor = new TrapDoorSpec();
			level.trapdoors.add(trapdoor);
			readActivable(trapdoorXml, trapdoor);
		}
		
		for (Element gearXml : xml.getChildrenByName("gear")) {
			GearSpec gear = new GearSpec();
			level.gears.add(gear);
			
			readActivable(gearXml, gear);
			gear.radius = gearXml.getFloatAttribute("radius");
			gear.rotatesRight = gearXml.getBooleanAttribute("rotatesRight");
		}
		
		for (Element spaceshipXml : xml.getChildrenByName("spaceship")) {
			SpaceshipSpec spaceship = new SpaceshipSpec();
			level.spaceships.add(spaceship);
			readActivable(spaceshipXml, spaceship);
		}
		
		return level;
	}
	
	private static void readActivable(Element xml, ActivatableSpec a) {
		a.position = new Vector2(xml.getFloatAttribute("x"), xml.getFloatAttribute("y"));
		for (Element activationIDXml : xml.getChildrenByName("wellActivationID")) {
			a.wellActivatorIDs.add(activationIDXml.getText());
		}
	}
}
