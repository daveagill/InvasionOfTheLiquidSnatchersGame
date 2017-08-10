package week.of.awesome.game;

public class TileMap {
	private Tile[] tiles;
	private int width;
	private int height;
	
	public TileMap(int width, int height) {
		tiles = new Tile[width * height];
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	public void set(int x, int y, Tile t) {
		tiles[y * width + x] = t;
	}
	
	public Tile get(int x, int y) {
		return tiles[y * width + x];
	}
}
