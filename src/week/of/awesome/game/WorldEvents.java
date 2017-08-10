package week.of.awesome.game;

public interface WorldEvents {
	public void captureDroplet(Droplet droplet, Well well);
	public void minionDeath(Minion minion);
	public void gameOver();
}
