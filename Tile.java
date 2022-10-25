public class Tile {
  private int row;
  private int column;
  private String value;
  private Tile prev;
  private boolean isVisited;

  private Wall leftWall;
  private Wall rightWall;
  private Wall upperWall;
  private Wall lowerWall;

  private int distanceFromStart;

  public Tile(int row, int column) {
    this.value = " ";
    this.row = row;
    this.column = column;
    this.prev = null;
    this.isVisited = false;

    this.leftWall = null;
    this.rightWall = null;
    this.upperWall = null;
    this.lowerWall = null;

    this.distanceFromStart = 0;
  }

  public int getRow() {
    return this.row;
  }

  public int getColumn() {
    return this.column;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public void setPrev(Tile tile) {
    this.prev = tile;
  }

  public Tile getPrev() {
    return this.prev;
  }

  public void setLeftWall(Wall wall) {
    this.leftWall = wall;
  }

  public Wall getLeftWall() {
    return this.leftWall;
  }

  public void setRightWall(Wall wall) {
    this.rightWall = wall;
  }

  public Wall getRightWall() {
    return this.rightWall;
  }

  public void setUpperWall(Wall wall) {
    this.upperWall = wall;
  }

  public Wall getUpperWall() {
    return this.upperWall;
  }

  public void setLowerWall(Wall wall) {
    this.lowerWall = wall;
  }

  public Wall getLowerWall() {
    return this.lowerWall;
  }

  public void setIsVisited(boolean value) {
    this.isVisited = value;
  }

  public boolean getIsVisited() {
    return this.isVisited;
  }

  public void setDistanceFromStart(int distance) {
    this.distanceFromStart = distance;
  }

  public int getDistanceFromStart() {
    return this.distanceFromStart;
  }

}
