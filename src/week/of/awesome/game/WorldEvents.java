package week.of.awesome.game;

public interface WorldEvents {
	public void sprayDroplet();
	public void captureDroplet(Droplet droplet, Well well);
	
	public void minionSpeak();
	public void minionDeath(Minion minion);
	
	public void minionLanding();
	public void propLanding();
	
	public void spaceshipActivated(boolean silent);
	public void gearActivated();
	public void platformActivated();
	public void trapdoorActivated();
	public void beamActivated();
	
	public void gameOver();
	public void levelCompleted();
}
