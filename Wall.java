import java.util.*;

public class Wall {

  private ArrayList<Tile> dividedTiles;

  public Wall(Tile tile) {
    dividedTiles = new ArrayList<Tile>();
    dividedTiles.add(tile);
  }

  public ArrayList<Tile> getDividedTiles() {
    return this.dividedTiles;
  }

  public void addDividedTile(Tile tile) {
    this.dividedTiles.add(tile);
  }

}
