package week.of.awesome.game;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

public class Level {
	public List<Object> undoHistory = new ArrayList<>();
	
	public String music;
	public Collection<DecalSpec> decals = new ArrayList<>();
	public Collection<BgTextSpec> bgTexts = new ArrayList<>();
	public Collection<MinionSpec> minions = new ArrayList<>();
	public Collection<SolidSpec> solids = new ArrayList<>();
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
	
	public void undo() {
		Object lastItem = undoHistory.remove(undoHistory.size()-1);
		
		decals.remove(lastItem);
		bgTexts.remove(lastItem);
		minions.remove(lastItem);
		solids.remove(lastItem);
		leftSlopes.remove(lastItem);
		rightSlopes.remove(lastItem);
		props.remove(lastItem);
		wells.remove(lastItem);
		vats.remove(lastItem);
		beams.remove(lastItem);
		platforms.remove(lastItem);
		trapdoors.remove(lastItem);
		gears.remove(lastItem);
		spaceships.remove(lastItem);
	}
	
	public void removeAt(int x, int y) {
		decals.removeIf(d -> d.position.x == x && d.position.y == y);
		bgTexts.removeIf(t -> t.position.x == x && t.position.y == y);
		minions.removeIf(m -> m.position.x == x && m.position.y == y);
		solids.removeIf(s -> s.min.x <= x && x < s.max.x && s.min.y <= y && y < s.max.y);
		leftSlopes.remove(new Vector2(x, y));
		rightSlopes.remove(new Vector2(x, y));
		props.removeIf(p -> p.position.x >= x && p.position.x <= x+1 && p.position.y >= y && p.position.y <= y+1);
		vats.removeIf(v -> v.min.x <= x && x < v.max.x && v.min.y <= y && y < v.max.y);
		beams.removeIf(b -> b.min.x <= x && x < b.max.x && b.min.y <= y && y < b.max.y);
		platforms.removeIf(p -> p.position.x == x && p.position.y == y);
		trapdoors.removeIf(p -> p.position.x == x && p.position.y == y);
		gears.removeIf(g -> Vector2.dst2(x, y, g.position.x, g.position.y) < g.radius*g.radius);
		spaceships.removeIf(s -> s.position.x == x && s.position.y == y);
		
		// wells need special casing
		Iterator<WellSpec> wellIter = wells.iterator();
		while (wellIter.hasNext()) {
			WellSpec w = wellIter.next();
			if (w.min.x <= x && x < w.max.x && w.min.y <= y && y < w.max.y) {
				wellIter.remove();
				for (ActivatableSpec a : getAllActivatables()) {
					a.wellActivatorIDs.remove(w.id);
				}
			}
		}
	}
	
	public void shiftX(int fromX, int dx) {
		decals.stream().filter(t -> t.position.x > fromX).forEach(t -> { t.position.x += dx; });
		bgTexts.stream().filter(t -> t.position.x > fromX).forEach(t -> { t.position.x += dx; });
		minions.stream().filter(t -> t.position.x > fromX).forEach(m -> { m.position.x += dx; });
		solids.stream().filter(s -> s.min.x > fromX).forEach(v -> { v.min.x += dx; v.max.x += dx; });
		leftSlopes.stream().filter(t -> t.x > fromX).forEach(s -> {s.x += dx; });
		rightSlopes.stream().filter(t -> t.x > fromX).forEach(s -> {s.x += dx; });
		props.stream().filter(t -> t.position.x > fromX).forEach(p -> { p.position.x += dx; });
		wells.stream().filter(t -> t.min.x > fromX).forEach(w -> { w.min.x += dx; w.max.x += dx; });
		vats.stream().filter(t -> t.min.x > fromX).forEach(v -> { v.min.x += dx; v.max.x += dx; });
		beams.stream().filter(t -> t.min.x > fromX).forEach(b -> { b.min.x += dx; b.max.x += dx; });
		platforms.stream().filter(t -> t.position.x > fromX).forEach(p -> { p.position.x += dx; });
		trapdoors.stream().filter(t -> t.position.x > fromX).forEach(p -> { p.position.x += dx; });
		gears.stream().filter(t -> t.position.x > fromX).forEach(g -> { g.position.x += dx; });
		spaceships.stream().filter(t -> t.position.x > fromX).forEach(s -> { s.position.x += dx; });
	}
	
	public void shiftY(int fromY, int dy) {
		decals.stream().filter(t -> t.position.y > fromY).forEach(t -> { t.position.y += dy; });
		bgTexts.stream().filter(t -> t.position.y > fromY).forEach(t -> { t.position.y += dy; });
		minions.stream().filter(t -> t.position.y > fromY).forEach(m -> { m.position.y += dy; });
		solids.stream().filter(t -> t.min.y > fromY).forEach(v -> { v.min.y += dy; v.max.y += dy; });
		leftSlopes.stream().filter(t -> t.y > fromY).forEach(s -> {s.y += dy; });
		rightSlopes.stream().filter(t -> t.y > fromY).forEach(s -> {s.y += dy; });
		props.stream().filter(t -> t.position.y > fromY).forEach(p -> { p.position.y += dy; });
		wells.stream().filter(t -> t.min.y > fromY).forEach(w -> { w.min.y += dy; w.max.y += dy; });
		vats.stream().filter(t -> t.min.y > fromY).forEach(v -> { v.min.y += dy; v.max.y += dy; });
		beams.stream().filter(t -> t.min.y > fromY).forEach(b -> { b.min.y += dy; b.max.y += dy; });
		platforms.stream().filter(t -> t.position.y > fromY).forEach(p -> { p.position.y += dy; });
		trapdoors.stream().filter(t -> t.position.y > fromY).forEach(p -> { p.position.y += dy; });
		gears.stream().filter(t -> t.position.y > fromY).forEach(g -> { g.position.y += dy; });
		spaceships.stream().filter(t -> t.position.y > fromY).forEach(s -> { s.position.y += dy; });
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
		for (ActivatableSpec a : getAllActivatables()) {
			if (a.position.x-1 <= x && a.position.x >= x && a.position.y-1 <= y && a.position.y >= y) {
				return a;
			}
		}
		return null;
	}
	
	private Collection<ActivatableSpec> getAllActivatables() {
		Collection<ActivatableSpec> activatables = new ArrayList<>();
		activatables.addAll(platforms);
		activatables.addAll(trapdoors);
		activatables.addAll(gears);
		activatables.addAll(spaceships);
		return activatables;
	}
	
	public Rectangle calculateSize() {
		// just based on the solids will do
		float minX = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		
		for (SolidSpec p : solids) {
			minX = Math.min(minX, p.min.x);
			maxX = Math.max(maxX, p.max.x);
			minY = Math.min(minY, p.min.y);
			maxY = Math.max(maxY, p.max.y);
		}
		
		return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}
	
	public static void save(Level level, String filename) {
		// cleanup wiring to wells
		Collection<String> allWellIds = level.wells.stream().map(w -> w.id).collect(Collectors.toList());
		for (ActivatableSpec a : level.getAllActivatables()) {
			a.wellActivatorIDs.retainAll(allWellIds);
		}
		
		
		try (StringWriter writer = new StringWriter();
		     XmlWriter xmlWriter = new XmlWriter(writer)) {
			
			XmlWriter xml = xmlWriter.element("level").attribute("music", level.music);
			
			for (DecalSpec decal : level.decals) {
				xml.element("decal")
					.attribute("x", decal.position.x).attribute("y", decal.position.y)
					.attribute("texture", decal.texture)
					.pop();
			}
			
			for (BgTextSpec bgText : level.bgTexts) {
				xml.element("bgText")
					.attribute("x", bgText.position.x).attribute("y", bgText.position.y)
					.attribute("text", bgText.text)
					.pop();
			}
			
			for (MinionSpec minion : level.minions) {
				XmlWriter minionWriter = xml.element("minion")
					.attribute("x", minion.position.x).attribute("y", minion.position.y)
					.attribute("essential", minion.essential);
				if (minion.dialog != null) {
					minionWriter.attribute("dialog", minion.dialog);
				}
				if (minion.fluidType != null) {
					minionWriter.attribute("type", minion.fluidType);
				}
				minionWriter.pop();
			}
			
			for (SolidSpec solid : level.solids) {
				xml.element("solid")
					.attribute("minX", solid.min.x).attribute("minY", solid.min.y)
					.attribute("maxX", solid.max.x).attribute("maxY", solid.max.y)
					.pop();
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
					.attribute("percentFull", well.percentFull)
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
						xml.element("trapdoor").attribute("width", trapdoor.width),
						trapdoor).pop();
			}
			
			for (GearSpec gear : level.gears) {
				writeActivatable(
						xml.element("gear").attribute("radius", gear.radius).attribute("rotatesRight", gear.rotatesRight),
						gear).pop();
			}
			
			for (SpaceshipSpec spaceship : level.spaceships) {
				writeActivatable(
						xml.element("spaceship").attribute("silent", spaceship.silent),
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
			xml = new XmlReader().parse(Gdx.files.internal(filename));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		Level level = new Level();
		
		level.music = xml.getAttribute("music");
		
		for (Element decalXml : xml.getChildrenByName("decal")) {
			DecalSpec decal = new DecalSpec();
			decal.position = new Vector2(decalXml.getFloatAttribute("x"), decalXml.getFloatAttribute("y"));
			decal.texture = decalXml.getAttribute("texture");
			level.decals.add(decal);
		}
		
		for (Element bgTextXml : xml.getChildrenByName("bgText")) {
			BgTextSpec bgText = new BgTextSpec();
			bgText.position = new Vector2(bgTextXml.getFloatAttribute("x"), bgTextXml.getFloatAttribute("y"));
			bgText.text = bgTextXml.getAttribute("text");
			level.bgTexts.add(bgText);
		}
		
		for (Element minionXml : xml.getChildrenByName("minion")) {
			MinionSpec minion = new MinionSpec();
			minion.position = new Vector2(minionXml.getFloatAttribute("x"), minionXml.getFloatAttribute("y"));
			minion.dialog = minionXml.getAttribute("dialog", null);
			String fluidType = minionXml.getAttribute("type", null);
			minion.fluidType = fluidType == null ? null : Droplet.Type.valueOf(fluidType);
			minion.essential = minionXml.getBooleanAttribute("essential");
			level.minions.add(minion);
		}
		
		for (Element solidXml : xml.getChildrenByName("solid")) {
			SolidSpec solid = new SolidSpec();
			level.solids.add(solid);
			
			try {
				solid.min = new Vector2(solidXml.getFloatAttribute("minX"), solidXml.getFloatAttribute("minY"));
				solid.max = new Vector2(solidXml.getFloatAttribute("maxX"), solidXml.getFloatAttribute("maxY"));
			} catch (Exception e) {
				solid.min = new Vector2(solidXml.getFloatAttribute("x"), solidXml.getFloatAttribute("y"));
				solid.max = solid.min.cpy().add(1, 1);
			}
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
			well.percentFull = wellXml.getFloatAttribute("percentFull");
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
			trapdoor.width = trapdoorXml.getFloatAttribute("width");
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
			spaceship.silent = spaceshipXml.getBooleanAttribute("silent");
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
