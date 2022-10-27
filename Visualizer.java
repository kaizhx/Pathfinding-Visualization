import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Visualizer {
  private int amountOfRows = 0;
  private int amountOfColumns = 0;
  public static final int MIN_ROWS = 3;
  public static final int MAX_ROWS = 20;
  public static final int MIN_COLUMNS = 3;
  public static final int MAX_COLUMNS = 20;
  private static String gridString = "";
  private static Tile[][] tileMatrix = {};

  private boolean gridIsInitialized = false;
  private boolean sourceIsSet = false;
  private boolean destinationIsSet = false;
  private boolean mazeCreated = false;
  private boolean algorithmIsSelected = false;
  private boolean turnAnimationsOn = false;

  private int[] sourceLocation = new int[2];
  private int[] destinationLocation = new int[2];
  private String selectedAlgorithm = "";

  private static Scanner scanner = new Scanner(System.in);

  private void clearScreen() {
    System.out.println(new String(new char[100]).replace("\0", "\r\n"));
  }

  private void generateTiles() {

    tileMatrix = new Tile[amountOfRows][amountOfColumns];
    for (int i = 0; i < amountOfRows; i++) {
      for (int j = 0; j < amountOfColumns; j++) {
        tileMatrix[i][j] = new Tile(i, j);

        if (i == 0) {
          tileMatrix[i][j].setUpperWall(new Wall(tileMatrix[i][j]));
        }
        if (i == (amountOfRows - 1)) {
          tileMatrix[i][j].setLowerWall(new Wall(tileMatrix[i][j]));
        }
        if (j == 0) {
          tileMatrix[i][j].setLeftWall(new Wall(tileMatrix[i][j]));
        }
        if (j == (amountOfColumns - 1)) {
          tileMatrix[i][j].setRightWall(new Wall(tileMatrix[i][j]));
        }

        if (tileMatrix[i][j].getUpperWall() == null) {
          Wall newUpperWall = new Wall(tileMatrix[i][j]);
          tileMatrix[i][j].setUpperWall(newUpperWall);
          tileMatrix[i - 1][j].setLowerWall(newUpperWall);
          newUpperWall.addDividedTile(tileMatrix[i - 1][j]);
        }
        if (tileMatrix[i][j].getLeftWall() == null) {
          Wall newLeftWall = new Wall(tileMatrix[i][j]);
          tileMatrix[i][j].setLeftWall(newLeftWall);
          tileMatrix[i][j - 1].setRightWall(newLeftWall);
          newLeftWall.addDividedTile(tileMatrix[i][j - 1]);
        }
      }
    }
  }

  private void buildGridString() {

    String[][] visualizedMatrix = new String[amountOfRows * 2 + 1][amountOfColumns * 2 + 1];
    for (int i = 1; i < amountOfRows * 2 + 1; i += 2) {
      for (int j = 1; j < amountOfColumns * 2 + 1; j += 2) {
        if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getIsVisited()) {
          visualizedMatrix[i][j] = " X ";
        } else {
          visualizedMatrix[i][j] = "   ";
        }

        if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getValue().equals("S")) {
          visualizedMatrix[i][j] = " S ";
        } else if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getValue().equals("D")) {
          visualizedMatrix[i][j] = " D ";
        }

        if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getLeftWall() != null) {
          visualizedMatrix[i][j - 1] = "|";
        } else {
          visualizedMatrix[i][j - 1] = " ";
        }
        if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getRightWall() != null) {
          visualizedMatrix[i][j + 1] = "|";
        } else {
          visualizedMatrix[i][j + 1] = " ";
        }
        if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getUpperWall() != null) {
          visualizedMatrix[i - 1][j] = "---";
        } else {
          visualizedMatrix[i - 1][j] = "   ";
        }
        if (tileMatrix[(i - 1) / 2][(j - 1) / 2].getLowerWall() != null) {
          visualizedMatrix[i + 1][j] = "---";
        } else {
          visualizedMatrix[i + 1][j] = "   ";
        }
      }
    }

    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < amountOfRows * 2 + 1; i++) {
      for (int j = 0; j < amountOfColumns * 2 + 1; j++) {
        if (visualizedMatrix[i][j] == null) {
          stringBuilder.append("+");
        } else {
          stringBuilder.append(visualizedMatrix[i][j]);
        }
      }
      stringBuilder.append("\n");
    }
    stringBuilder.append("\n");

    gridString = stringBuilder.toString();
  }

  private void setAlgorithm() {
    clearScreen();
    String instruction = "Please select the algorithm that you want to use:\n\n1 : Dijkstra's algorithm\n\n2 : A* algorithm\n\n";
    while (true) {
      try {
        System.out.println(instruction);
        int number = scanner.nextInt();
        if (number == 1) {
          selectedAlgorithm = "Dijkstra";
          algorithmIsSelected = true;
          return;
        } else if (number == 2) {
          selectedAlgorithm = "A*";
          algorithmIsSelected = true;
          return;
        } else {
          clearScreen();
          System.out.print("Please enter '1' or '2'.\n\n");
        }
      } catch (InputMismatchException e) {
        clearScreen();
        System.out.println("Please enter a valid number.\n");
        scanner.next();
      }
    }
  }

  private Tile returnTileWithLowestFScore(ArrayList<Tile> openSet, Tile destination) {
    int lowestCost = Integer.MAX_VALUE;
    Tile tileWithLowestCost = null;
    for (int i = 0; i < openSet.size(); i++) {
      if (openSet.get(i).getDistanceFromStart() + getManhattanDistance(openSet.get(i), destination) < lowestCost) {
        lowestCost = openSet.get(i).getDistanceFromStart() + getManhattanDistance(openSet.get(i), destination);
        tileWithLowestCost = openSet.get(i);
      }
    }
    return tileWithLowestCost;
  }

  private Tile returnTileWithLowestGScore(ArrayList<Tile> openSet, Tile destination) {
    int lowestCost = Integer.MAX_VALUE;
    Tile tileWithLowestCost = null;
    for (int i = 0; i < openSet.size(); i++) {
      if (openSet.get(i).getDistanceFromStart() < lowestCost) {
        lowestCost = openSet.get(i).getDistanceFromStart();
        tileWithLowestCost = openSet.get(i);
      }
    }
    return tileWithLowestCost;
  }

  private void useDijkstra(Tile source, Tile destination) {
     source.setValue("S");
    destination.setValue("D");
    ArrayList<Tile> openSet = new ArrayList<Tile>();
    ArrayList<Tile> closedSet = new ArrayList<Tile>();
    openSet.add(source);

    while (!destination.getIsVisited()) {
      Tile currentTile = returnTileWithLowestGScore(openSet, destination);

      openSet.remove(currentTile);
      closedSet.add(currentTile);
      currentTile.setIsVisited(true);

      if (currentTile == destination) {
        break;
      }

      if (currentTile.getUpperWall() != null || currentTile.getRow() == 0
          || closedSet.contains(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()])) {
      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()])) {
            openSet.add(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()]);
          }
        }
      }

      if (currentTile.getLeftWall() != null || currentTile.getColumn() == 0
          || closedSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1])) {

      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1])) {
            openSet.add(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1]);
          }
        }
      }

      if (currentTile.getRightWall() != null || currentTile.getColumn() == (amountOfColumns - 1)
          || closedSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1])) {

      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1])) {
            openSet.add(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1]);
          }
        }
      }

      if (currentTile.getLowerWall() != null || currentTile.getRow() == (amountOfRows - 1)
          || closedSet.contains(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()])) {

      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()])) {
            openSet.add(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()]);
          }
        }
      }

      if (turnAnimationsOn) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {

        }
        clearScreen();
        buildGridString();
        System.out.println("Finding path using Dijkstra's algorithm...");
        System.out.println(gridString);
      }
    }

    for (int i = 0; i < amountOfRows; i++) {
      for (int j = 0; j < amountOfColumns; j++) {
        tileMatrix[i][j].setIsVisited(false);
      }
    }

    Tile currentTile = destination;

    while (currentTile != source) {
      currentTile.setIsVisited(true);
      currentTile = currentTile.getPrev();
      if (turnAnimationsOn) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {

        }
        clearScreen();
        buildGridString();
        System.out.println("Finding path using Dijkstra's algorithm...");
        System.out.println(gridString);
      }
    }
    source.setIsVisited(true);
    source.setValue("S");
    destination.setValue("D");
  }

  private void useAStar(Tile source, Tile destination) {
    source.setValue("S");
    destination.setValue("D");
    ArrayList<Tile> openSet = new ArrayList<Tile>();
    ArrayList<Tile> closedSet = new ArrayList<Tile>();
    openSet.add(source);

    while (!destination.getIsVisited()) {
      Tile currentTile = returnTileWithLowestFScore(openSet, destination);

      openSet.remove(currentTile);
      closedSet.add(currentTile);
      currentTile.setIsVisited(true);

      if (currentTile == destination) {
        break;
      }

      if (currentTile.getUpperWall() != null || currentTile.getRow() == 0
          || closedSet.contains(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()])) {
      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()])) {
            openSet.add(tileMatrix[currentTile.getRow() - 1][currentTile.getColumn()]);
          }
        }
      }

      if (currentTile.getLeftWall() != null || currentTile.getColumn() == 0
          || closedSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1])) {

      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1])) {
            openSet.add(tileMatrix[currentTile.getRow()][currentTile.getColumn() - 1]);
          }
        }
      }

      if (currentTile.getRightWall() != null || currentTile.getColumn() == (amountOfColumns - 1)
          || closedSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1])) {

      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1])) {
            openSet.add(tileMatrix[currentTile.getRow()][currentTile.getColumn() + 1]);
          }
        }
      }

      if (currentTile.getLowerWall() != null || currentTile.getRow() == (amountOfRows - 1)
          || closedSet.contains(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()])) {

      } else {
        if (!openSet.contains(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()])
            || currentTile.getDistanceFromStart() + 1 < tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()]
                .getDistanceFromStart()) {
          tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()]
              .setDistanceFromStart(currentTile.getDistanceFromStart() + 1);
          tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()].setPrev(currentTile);
          if (!openSet.contains(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()])) {
            openSet.add(tileMatrix[currentTile.getRow() + 1][currentTile.getColumn()]);
          }
        }
      }

      if (turnAnimationsOn) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {

        }
        clearScreen();
        buildGridString();
        System.out.println("Finding path using A* algorithm...");
        System.out.println(gridString);
      }
    }

    for (int i = 0; i < amountOfRows; i++) {
      for (int j = 0; j < amountOfColumns; j++) {
        tileMatrix[i][j].setIsVisited(false);
      }
    }

    Tile currentTile = destination;

    while (currentTile != source) {
      currentTile.setIsVisited(true);
      currentTile = currentTile.getPrev();
      if (turnAnimationsOn) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {

        }
        clearScreen();
        buildGridString();
        System.out.println("Finding path using A* algorithm...");
        System.out.println(gridString);
      }
    }
    source.setIsVisited(true);
    source.setValue("S");
    destination.setValue("D");
  }

  private int getManhattanDistance(Tile tile, Tile destination) {
    return (Math.abs(tile.getRow() - destination.getRow()) + Math.abs(tile.getColumn() - destination.getColumn()));
  }

  private Tile getUnvisitedNeighbour(Tile currentTile) {
    int currentTileRow = currentTile.getRow();
    int currentTileColumn = currentTile.getColumn();

    // // Case for cells in top left corner
    if (currentTileRow == 0 && currentTileColumn == 0) {
      if (tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        return currentTile;
      } else if (tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow + 1][currentTileColumn];
      } else if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow][currentTileColumn + 1];
      } else if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        int randomTile = (int) Math.floor(Math.random() * 2);
        if (randomTile == 0) {
          return tileMatrix[currentTileRow + 1][currentTileColumn];
        } else if (randomTile == 1) {
          return tileMatrix[currentTileRow][currentTileColumn + 1];
        }
      }
    } // Case for cells in top right corner
    else if (currentTileRow == 0 && currentTileColumn == amountOfColumns - 1) {
      if (tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        return currentTile;
      } else if (tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow + 1][currentTileColumn];
      } else if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow][currentTileColumn - 1];
      } else if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        int randomTile = (int) Math.floor(Math.random() * 2);
        if (randomTile == 0) {
          return tileMatrix[currentTileRow + 1][currentTileColumn];
        } else if (randomTile == 1) {
          return tileMatrix[currentTileRow][currentTileColumn - 1];
        }
      }
    } // Case for cells in bottom left corner
    else if (currentTileRow == (amountOfRows - 1) && currentTileColumn == 0) {
      if (tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        return currentTile;
      } else if (tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && !tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow - 1][currentTileColumn];
      } else if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow][currentTileColumn + 1];
      } else if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          && !tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        int randomTile = (int) Math.floor(Math.random() * 2);
        if (randomTile == 0) {
          return tileMatrix[currentTileRow - 1][currentTileColumn];
        } else if (randomTile == 1) {
          return tileMatrix[currentTileRow][currentTileColumn + 1];
        }
      }
    } // Case for cells in bottom right corner
    else if (currentTileRow == (amountOfRows - 1) && currentTileColumn == (amountOfColumns - 1)) {
      if (tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        return currentTile;
      } else if (tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && !tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow - 1][currentTileColumn];
      } else if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        return tileMatrix[currentTileRow][currentTileColumn - 1];
      } else if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          && !tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        int randomTile = (int) Math.floor(Math.random() * 2);
        if (randomTile == 0) {
          return tileMatrix[currentTileRow - 1][currentTileColumn];
        } else if (randomTile == 1) {
          return tileMatrix[currentTileRow][currentTileColumn - 1];
        }
      }
    } // Case for cells on left edge but not corner
    else if (currentTileRow != 0 && currentTileRow != (amountOfRows - 1) && currentTileColumn == 0) {

      int amountOfUnvisitedNeighbours = 0;

      if (!tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()
          || !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()
          || !tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()) {
        amountOfUnvisitedNeighbours++;
      }

      if (amountOfUnvisitedNeighbours == 0) {
        return currentTile;
      } else {
        int randomTile;
        while (true) {
          randomTile = (int) Math.floor(Math.random() * 3);
          if (randomTile == 0) {
            if (!tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow - 1][currentTileColumn];
            }
          } else if (randomTile == 1) {
            if (!tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow + 1][currentTileColumn];
            }
          } else if (randomTile == 2) {
            if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn + 1];
            }
          }
        }
      }

    } // Case for cells on right edge but not corner
    else if (currentTileRow != 0 && currentTileRow != (amountOfRows - 1)
        && currentTileColumn == (amountOfColumns - 1)) {

      int amountOfUnvisitedNeighbours = 0;

      if (!tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()
          || !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()
          || !tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()) {
        amountOfUnvisitedNeighbours++;
      }

      if (amountOfUnvisitedNeighbours == 0) {
        return currentTile;
      } else {
        int randomTile;
        while (true) {
          randomTile = (int) Math.floor(Math.random() * 3);
          if (randomTile == 0) {
            if (!tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow - 1][currentTileColumn];
            }
          } else if (randomTile == 1) {
            if (!tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow + 1][currentTileColumn];
            }
          } else if (randomTile == 2) {
            if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn - 1];
            }
          }
        }
      }
    } // Case for cells on upper edge but not corner
    else if (currentTileColumn != 0 && currentTileColumn != (amountOfColumns - 1) && currentTileRow == 0) {

      int amountOfUnvisitedNeighbours = 0;

      if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          || !tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          || !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        amountOfUnvisitedNeighbours++;
      }

      if (amountOfUnvisitedNeighbours == 0) {
        return currentTile;
      } else {
        int randomTile;
        while (true) {
          randomTile = (int) Math.floor(Math.random() * 3);
          if (randomTile == 0) {
            if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn - 1];
            }
          } else if (randomTile == 1) {
            if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn + 1];
            }
          } else if (randomTile == 2) {
            if (!tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow + 1][currentTileColumn];
            }
          }
        }
      }
    } // Case for cells on lower edge but not corner
    else if (currentTileColumn != 0 && currentTileColumn != (amountOfColumns - 1)
        && currentTileRow == (amountOfRows - 1)) {

      int amountOfUnvisitedNeighbours = 0;

      if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          || !tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          || !tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
        amountOfUnvisitedNeighbours++;
      }

      if (amountOfUnvisitedNeighbours == 0) {
        return currentTile;
      } else {
        int randomTile;
        while (true) {
          randomTile = (int) Math.floor(Math.random() * 3);
          if (randomTile == 0) {
            if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn - 1];
            }
          } else if (randomTile == 1) {
            if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn + 1];
            }
          } else if (randomTile == 2) {
            if (!tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow - 1][currentTileColumn];
            }
          }
        }
      }
    } // Case for cells that are not on any edge
    else if (currentTileRow != 0 && currentTileRow != (amountOfRows - 1) && currentTileColumn != 0
        && currentTileColumn != (amountOfColumns - 1)) {

      int amountOfUnvisitedNeighbours = 0;

      if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()
          || !tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()
          || !tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()
          || !tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
        amountOfUnvisitedNeighbours++;
      }

      if (amountOfUnvisitedNeighbours == 0) {
        return currentTile;
      } else {
        int randomTile;
        while (true) {
          randomTile = (int) Math.floor(Math.random() * 4);
          if (randomTile == 0) {
            if (!tileMatrix[currentTileRow][currentTileColumn - 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn - 1];
            }
          } else if (randomTile == 1) {
            if (!tileMatrix[currentTileRow][currentTileColumn + 1].getIsVisited()) {
              return tileMatrix[currentTileRow][currentTileColumn + 1];
            }
          } else if (randomTile == 2) {
            if (!tileMatrix[currentTileRow - 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow - 1][currentTileColumn];
            }
          } else if (randomTile == 3) {
            if (!tileMatrix[currentTileRow + 1][currentTileColumn].getIsVisited()) {
              return tileMatrix[currentTileRow + 1][currentTileColumn];
            }
          }
        }
      }
    }
    return currentTile;
  }

  private void useDFS() {
    int randomRow = (int) Math.floor(Math.random() * amountOfRows);
    int randomColumn = (int) Math.floor(Math.random() * amountOfColumns);
    Tile startTile = tileMatrix[randomRow][randomColumn];
    startTile.setIsVisited(true);
    Tile currentTile = startTile;
    Tile nextTile = getUnvisitedNeighbour(currentTile);

    System.out.println("Current Tile: " + currentTile.getRow() + ", " + currentTile.getColumn() + "\n");
    System.out.println("Next Tile: " + nextTile.getRow() + ", " + nextTile.getColumn() + "\n");

    while (true) {

      if (getUnvisitedNeighbour(currentTile) == currentTile) {
        while (getUnvisitedNeighbour(currentTile) == currentTile) {
          currentTile = currentTile.getPrev();
          if (currentTile == startTile) {
            for (int i = 0; i < amountOfRows; i++) {
              for (int j = 0; j < amountOfColumns; j++) {
                tileMatrix[i][j].setIsVisited(false);
              }
            }
            return;
          }
        }
      }
      nextTile = getUnvisitedNeighbour(currentTile);
      nextTile.setIsVisited(true);
      nextTile.setPrev(currentTile);

      if (currentTile.getRow() == nextTile.getRow()) {
        if (currentTile.getColumn() < nextTile.getColumn()) {
          currentTile.setRightWall(null);
          nextTile.setLeftWall(null);
        } else {
          currentTile.setLeftWall(null);
          nextTile.setRightWall(null);
        }
      } else {
        if (currentTile.getRow() < nextTile.getRow()) {
          currentTile.setLowerWall(null);
          nextTile.setUpperWall(null);
        } else {
          currentTile.setUpperWall(null);
          nextTile.setLowerWall(null);
        }
      }

      currentTile = nextTile;

      if (turnAnimationsOn) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {

        }
        clearScreen();
        buildGridString();
        System.out.println("Creating maze using DFS algorithm...");
        System.out.println(gridString);
      }
    }
  }

  private void useRandomizedPrim() {
    ArrayList<Wall> listOfWalls = new ArrayList<Wall>();
    int randomRow = (int) Math.floor(Math.random() * amountOfRows);
    int randomColumn = (int) Math.floor(Math.random() * amountOfColumns);
    Tile startTile = tileMatrix[randomRow][randomColumn];
    startTile.setIsVisited(true);

    if (startTile.getUpperWall().getDividedTiles().size() > 1) {
      listOfWalls.add(startTile.getUpperWall());
    }

    if (startTile.getLowerWall().getDividedTiles().size() > 1) {
      listOfWalls.add(startTile.getLowerWall());
    }

    if (startTile.getLeftWall().getDividedTiles().size() > 1) {
      listOfWalls.add(startTile.getLeftWall());
    }

    if (startTile.getRightWall().getDividedTiles().size() > 1) {
      listOfWalls.add(startTile.getRightWall());
    }

    int amountOfUnvisitedTiles = amountOfRows * amountOfColumns;

    while (amountOfUnvisitedTiles != 0) {
      amountOfUnvisitedTiles = 0;
      int randomListIndex = (int) Math.floor(Math.random() * listOfWalls.size());
      Wall randomWall = listOfWalls.get(randomListIndex);
      if (randomWall.getDividedTiles().get(0).getIsVisited() && !randomWall.getDividedTiles().get(1).getIsVisited()) {

        if (randomWall.getDividedTiles().get(0).getUpperWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setUpperWall(null);
        } else if (randomWall.getDividedTiles().get(0).getLowerWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setLowerWall(null);
        } else if (randomWall.getDividedTiles().get(0).getLeftWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setLeftWall(null);
        } else if (randomWall.getDividedTiles().get(0).getRightWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setRightWall(null);
        }

        if (randomWall.getDividedTiles().get(1).getUpperWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setUpperWall(null);
        } else if (randomWall.getDividedTiles().get(1).getLowerWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setLowerWall(null);
        } else if (randomWall.getDividedTiles().get(1).getLeftWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setLeftWall(null);
        } else if (randomWall.getDividedTiles().get(1).getRightWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setRightWall(null);
        }

        randomWall.getDividedTiles().get(1).setIsVisited(true);

        listOfWalls.remove(randomListIndex);

        if (randomWall.getDividedTiles().get(1).getUpperWall() != null
            && randomWall.getDividedTiles().get(1).getRow() != 0) {
          listOfWalls.add(randomWall.getDividedTiles().get(1).getUpperWall());
        }
        if (randomWall.getDividedTiles().get(1).getLowerWall() != null
            && randomWall.getDividedTiles().get(1).getRow() != (amountOfRows - 1)) {
          listOfWalls.add(randomWall.getDividedTiles().get(1).getLowerWall());
        }
        if (randomWall.getDividedTiles().get(1).getLeftWall() != null
            && randomWall.getDividedTiles().get(1).getColumn() != 0) {
          listOfWalls.add(randomWall.getDividedTiles().get(1).getLeftWall());
        }
        if (randomWall.getDividedTiles().get(1).getRightWall() != null
            && randomWall.getDividedTiles().get(1).getColumn() != (amountOfColumns - 1)) {
          listOfWalls.add(randomWall.getDividedTiles().get(1).getRightWall());
        }

      } else if (!randomWall.getDividedTiles().get(0).getIsVisited()
          && randomWall.getDividedTiles().get(1).getIsVisited()) {

        if (randomWall.getDividedTiles().get(0).getUpperWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setUpperWall(null);
        } else if (randomWall.getDividedTiles().get(0).getLowerWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setLowerWall(null);
        } else if (randomWall.getDividedTiles().get(0).getLeftWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setLeftWall(null);
        } else if (randomWall.getDividedTiles().get(0).getRightWall() == randomWall) {
          randomWall.getDividedTiles().get(0).setRightWall(null);
        }

        if (randomWall.getDividedTiles().get(1).getUpperWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setUpperWall(null);
        } else if (randomWall.getDividedTiles().get(1).getLowerWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setLowerWall(null);
        } else if (randomWall.getDividedTiles().get(1).getLeftWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setLeftWall(null);
        } else if (randomWall.getDividedTiles().get(1).getRightWall() == randomWall) {
          randomWall.getDividedTiles().get(1).setRightWall(null);
        }

        randomWall.getDividedTiles().get(0).setIsVisited(true);

        listOfWalls.remove(randomListIndex);

        if (randomWall.getDividedTiles().get(0).getUpperWall() != null
            && randomWall.getDividedTiles().get(0).getRow() != 0) {
          listOfWalls.add(randomWall.getDividedTiles().get(0).getUpperWall());
        }
        if (randomWall.getDividedTiles().get(0).getLowerWall() != null
            && randomWall.getDividedTiles().get(0).getRow() != (amountOfRows - 1)) {
          listOfWalls.add(randomWall.getDividedTiles().get(0).getLowerWall());
        }
        if (randomWall.getDividedTiles().get(0).getLeftWall() != null
            && randomWall.getDividedTiles().get(0).getColumn() != 0) {
          listOfWalls.add(randomWall.getDividedTiles().get(0).getLeftWall());
        }
        if (randomWall.getDividedTiles().get(0).getRightWall() != null
            && randomWall.getDividedTiles().get(0).getColumn() != (amountOfColumns - 1)) {
          listOfWalls.add(randomWall.getDividedTiles().get(0).getRightWall());
        }
      } else {
        listOfWalls.remove(randomWall);
      }

      for (int i = 0; i < tileMatrix.length; i++) {
        for (int j = 0; j < tileMatrix[i].length; j++) {
          if (!tileMatrix[i][j].getIsVisited()) {
            amountOfUnvisitedTiles++;
          }
        }
      }

      if (turnAnimationsOn) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {

        }
        clearScreen();
        buildGridString();
        System.out.println("Creating maze using Prim's algorithm...");
        System.out.println(gridString);
      }

    }
    for (int i = 0; i < amountOfRows; i++) {
      for (int j = 0; j < amountOfColumns; j++) {
        tileMatrix[i][j].setIsVisited(false);
      }
    }
  }

  private void createRandomMaze() {
    String mazeInstruction = "\n\nChoose a maze generation algorithm:\n\n1 : Depth First Search\n\n2 : Randomized Prim\n\n";
    while (true) {
      try {
        System.out.println(mazeInstruction);
        int choice = scanner.nextInt();
        if (choice == 1) {
          clearScreen();
          useDFS();
          buildGridString();
          mazeCreated = true;
          return;
        } else if (choice == 2) {
          clearScreen();
          useRandomizedPrim();
          buildGridString();
          mazeCreated = true;
          return;
        } else {
          clearScreen();
          System.out.println("\nPlease enter '1' or '2'.\n");
        }

      } catch (InputMismatchException e) {
        clearScreen();
        System.out.println("\nPlease enter a number.\n");
        scanner.next();
      }
    }
  }

  private void initialize() {
    String rowInstruction = "\n\nSet the amount of rows for the grid (Min: " + MIN_ROWS + ", Max: " + MAX_ROWS
        + "):\n\n";
    String columnInstruction = "\n\nSet the amount of columns for the grid (Min: " + MIN_COLUMNS + ", Max: "
        + MAX_COLUMNS + "):\n\n";
    boolean rowAmountIsSet = false;
    boolean columnAmountIsSet = false;
    while (true) {
      try {
        if (!rowAmountIsSet) {
          System.out.println(rowInstruction);
          int rowNumber = scanner.nextInt();
          if (rowNumber >= MIN_ROWS && rowNumber <= MAX_ROWS) {
            clearScreen();
            System.out.println("Amount of rows set to: " + rowNumber);
            amountOfRows = rowNumber;
            rowAmountIsSet = true;
          } else {
            clearScreen();
            System.out.println("\nPlease enter a row amount between " + MIN_ROWS + " and " + MAX_ROWS + ".\n");
          }
        }
        if (!columnAmountIsSet && rowAmountIsSet) {
          System.out.println(columnInstruction);
          int columnNumber = scanner.nextInt();
          if (columnNumber >= MIN_COLUMNS && columnNumber <= MAX_COLUMNS) {
            clearScreen();
            amountOfColumns = columnNumber;
            columnAmountIsSet = true;
          } else {
            clearScreen();
            System.out.println("\nPlease enter a column amount between " + MIN_COLUMNS + " and " + MAX_COLUMNS + ".\n");
          }
        }

        if (rowAmountIsSet && columnAmountIsSet) {
          generateTiles();
          buildGridString();
          sourceLocation[0] = -1;
          sourceLocation[1] = -1;
          destinationLocation[0] = -1;
          destinationLocation[1] = -1;
          sourceIsSet = false;
          destinationIsSet = false;
          algorithmIsSelected = false;
          mazeCreated = false;
          gridIsInitialized = true;
          return;
        }

      } catch (InputMismatchException e) {
        clearScreen();
        System.out.println("\nPlease enter a number.\n");
        scanner.next();
      }
    }
  }

  private void setPoint(String pointType) {
    clearScreen();
    String rowInstruction = "Set the row of your " + pointType + " point:\n\n";
    String columnInstruction = "Set the column of your " + pointType + " point:\n\n";
    int rowPosition = 0;
    int columnPosition = 0;
    boolean rowIsSet = false;
    boolean columnIsSet = false;
    while (!rowIsSet || !columnIsSet) {
      try {
        if (!rowIsSet) {
          System.out.println(rowInstruction);
          int rowChecker = scanner.nextInt();
          if (rowChecker >= 1 && rowChecker <= amountOfRows) {
            rowPosition = rowChecker - 1;
            rowIsSet = true;
          } else {
            System.out.println("\nPlease enter a row between " + 1 + " and " + amountOfRows + ".\n");
          }
        }
        if (rowIsSet && !columnIsSet) {
          System.out.println(columnInstruction);
          int columnChecker = scanner.nextInt();
          if (columnChecker >= 1 && columnChecker <= amountOfColumns) {
            columnPosition = columnChecker - 1;
            columnIsSet = true;
          } else {
            System.out.println("\nPlease enter a column between " + 1 + " and " + amountOfColumns + ".\n");
          }
        }
        if (rowIsSet && columnIsSet) {

          if (tileMatrix[rowPosition][columnPosition].getValue().equals(" ")) {
            if (pointType.equals("source") && !sourceIsSet) {
              sourceLocation[0] = rowPosition;
              sourceLocation[1] = columnPosition;
              sourceIsSet = true;
            } else if (pointType.equals("destination") && !destinationIsSet) {
              destinationLocation[0] = rowPosition;
              destinationLocation[1] = columnPosition;
              destinationIsSet = true;
            } else if (pointType.equals("source") && sourceIsSet) {
              tileMatrix[sourceLocation[0]][sourceLocation[1]].setValue(" ");
              sourceLocation[0] = rowPosition;
              sourceLocation[1] = columnPosition;
            } else if (pointType.equals("destination") && destinationIsSet) {
              tileMatrix[destinationLocation[0]][destinationLocation[1]].setValue(" ");
              destinationLocation[0] = rowPosition;
              destinationLocation[1] = columnPosition;
            }
          } else if (tileMatrix[rowPosition][columnPosition].getValue().equals("S")
              || tileMatrix[rowPosition][columnPosition].getValue().equals("D")) {
            if (pointType.equals("source") && tileMatrix[rowPosition][columnPosition].getValue().equals("D")
                && !sourceIsSet) {
              destinationLocation[0] = -1;
              destinationLocation[1] = -1;
              destinationIsSet = false;
              sourceLocation[0] = rowPosition;
              sourceLocation[1] = columnPosition;
              sourceIsSet = true;

            } else if (pointType.equals("source") && tileMatrix[rowPosition][columnPosition].getValue().equals("D")
                && sourceIsSet) {
              destinationLocation[0] = -1;
              destinationLocation[1] = -1;
              destinationIsSet = false;
              tileMatrix[sourceLocation[0]][sourceLocation[1]].setValue(" ");
              sourceLocation[0] = rowPosition;
              sourceLocation[1] = columnPosition;

            } else if (pointType.equals("destination") && tileMatrix[rowPosition][columnPosition].getValue().equals("S")
                && !destinationIsSet) {
              sourceLocation[0] = -1;
              sourceLocation[1] = -1;
              sourceIsSet = false;
              destinationLocation[0] = rowPosition;
              destinationLocation[1] = columnPosition;
              destinationIsSet = true;
            } else if (pointType.equals("destination") && tileMatrix[rowPosition][columnPosition].getValue().equals("S")
                && destinationIsSet) {
              sourceLocation[0] = -1;
              sourceLocation[1] = -1;
              sourceIsSet = false;
              tileMatrix[destinationLocation[0]][destinationLocation[1]].setValue(" ");
              destinationLocation[0] = rowPosition;
              destinationLocation[1] = columnPosition;
            }
          }
        }
      } catch (InputMismatchException e) {
        clearScreen();
        System.out.println("Please enter a number.\n");
        scanner.next();
      }
    }
    if (pointType.equals("source")) {
      tileMatrix[rowPosition][columnPosition].setValue("S");
    } else if (pointType.equals("destination")) {
      tileMatrix[rowPosition][columnPosition].setValue("D");
    }
    buildGridString();
  }

  private void switchToMainMenu() {
    String menuIntro = "Welcome to the main menu!\n\nFrom here you can create a random maze, set source/destination points and select a pathfinding algorithm of your choice.\n\n";
    String note = "Note: A grid must be initialized first before other options are available.\n\n";
    String options1 = "1 : Initialize a grid\n\n2 : Create random maze\n\n3 : Set new source point\n\n4 : Set new destination point\n\n5 : Choose pathfinding algorithm\n\n6 : Start pathfinding\n\n7 : Turn off animations\n\n8 : Exit program\n\n";
    String options2 = "1 : Initialize a grid\n\n2 : Create random maze\n\n3 : Set new source point\n\n4 : Set new destination point\n\n5 : Choose pathfinding algorithm\n\n6 : Start pathfinding\n\n7 : Turn on animations **EPILEPSY WARNING** POTENTIAL FLASHING/FLICKERING\n\n8 : Exit program\n\n";
    clearScreen();
    System.out.println(menuIntro);
    while (true) {
      try {
        if (!gridIsInitialized) {
          if (turnAnimationsOn == true) {
            System.out.println(note + options1);
          } else {
            System.out.println(note + options2);
          }
        } else {
          if (turnAnimationsOn == true) {
            System.out.println(options1);
          } else {
            System.out.println(options2);
          }
        }
        int number = scanner.nextInt();
        switch (number) {
          case 1:
            clearScreen();
            initialize();
            if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
            System.out.println(gridString + "\n");
            System.out.println("New grid initialized.\n");
            break;
          case 2:
            if (gridIsInitialized) {
              clearScreen();
              generateTiles();
              createRandomMaze();
              clearScreen();
              if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
              System.out.println(gridString + "\n");
              System.out.println("Random maze created.\n");
            } else {
              clearScreen();
              System.out.println("Grid must be initialized first.\n");
            }
            break;
          case 3:
            if (gridIsInitialized && mazeCreated) {
              clearScreen();
              setPoint("source");
              clearScreen();
              if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
              System.out.println(gridString + "\n");
              System.out.println("New source point set.\n");
            } else {
              if (!gridIsInitialized) {
                clearScreen();
                System.out.println("Grid must be initialized first.\n");
              } else if (!mazeCreated) {
                clearScreen();
                System.out.println(gridString + "\n");
                System.out.println("Maze must be created first.\n");
              }
            }
            break;
          case 4:
            if (gridIsInitialized && mazeCreated) {
              clearScreen();
              setPoint("destination");
              clearScreen();
              if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
              System.out.println(gridString + "\n");
              System.out.println("New destination point set.\n");
            } else {
              if (!gridIsInitialized) {
                clearScreen();
                System.out.println("Grid must be initialized first.\n");
              } else if (!mazeCreated) {
                clearScreen();
                System.out.println(gridString + "\n");
                System.out.println("Maze must be created first.\n");
              }
            }
            break;
          case 5:
            if (gridIsInitialized) {
              clearScreen();
              setAlgorithm();
              clearScreen();
              if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
              System.out.println(gridString + "\n");
              System.out.println("You chose algorithm: " + selectedAlgorithm + "\n");
            } else {
              clearScreen();
              System.out.println("Grid must be initialized first.\n");
            }
            break;
          case 6:
            if (gridIsInitialized && mazeCreated && sourceIsSet && destinationIsSet && algorithmIsSelected) {
              if (selectedAlgorithm.equals("Dijkstra")) {
                clearScreen();
                useDijkstra(tileMatrix[sourceLocation[0]][sourceLocation[1]],
                    tileMatrix[destinationLocation[0]][destinationLocation[1]]);
                buildGridString();
                clearScreen();
                if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
                System.out.println(gridString + "\n");
                System.out.println("Path found.\n");
                for (int i = 0; i < amountOfRows; i++) {
                  for (int j = 0; j < amountOfColumns; j++) {
                    tileMatrix[i][j].setIsVisited(false);
                    tileMatrix[i][j].setPrev(null);
                  }
                }
              } else if (selectedAlgorithm.equals("A*")) {
                clearScreen();
                useAStar(tileMatrix[sourceLocation[0]][sourceLocation[1]],
                    tileMatrix[destinationLocation[0]][destinationLocation[1]]);
                buildGridString();
                clearScreen();
                if (algorithmIsSelected) {
                System.out.println("Currently selected algorithm: " + selectedAlgorithm);
              }
                System.out.println(gridString + "\n");
                System.out.println("Path found.\n");
                for (int i = 0; i < amountOfRows; i++) {
                  for (int j = 0; j < amountOfColumns; j++) {
                    tileMatrix[i][j].setIsVisited(false);
                    tileMatrix[i][j].setPrev(null);
                  }
                }
              }
            } else {
              clearScreen();
              if (!gridIsInitialized) {
                System.out.println("Grid must be initialized first.\n");
              } else if (!mazeCreated) {
                System.out.println(gridString + "\n");
                System.out.println("Maze must be created first.\n");
              } else if (!sourceIsSet) {
                System.out.println(gridString + "\n");
                System.out.println("Source point must be set first.\n");
              } else if (!destinationIsSet) {
                System.out.println(gridString + "\n");
                System.out.println("Destination point must be set first.\n");
              } else if (!algorithmIsSelected) {
                System.out.println(gridString + "\n");
                System.out.println("An algorithm must be selected first.\n");
              }
            }
            break;
          case 7:
            if (!turnAnimationsOn) {
              turnAnimationsOn = true;
              clearScreen();
              if (gridIsInitialized) {
                System.out.println(gridString + "\n");
              }
              System.out.println("Animations turned on.\n");
            } else {
              turnAnimationsOn = false;
              clearScreen();
              if (gridIsInitialized) {
                System.out.println(gridString + "\n");
              }
              System.out.println("Animations turned off.\n");
            }
            break;
          case 8:
            System.exit(0);
          default:
            clearScreen();
            System.out.println("Please enter a number from '1' to '8'.\n");
        }

      } catch (InputMismatchException e) {
        clearScreen();
        System.out.println("Please enter a number from '1' to '7'.\n");
        scanner.next();
      }
    }
  }

  public void start() {
    String welcomeMessage = "\n\n\nWelcome to the Pathfinding Algorithm Visualizer (PAV)!\n\n";
    String description = "PAV is a program that was build to visualize some of the most well known pathfinding algorithms, Dijkstra's algorithm and the A* algorithm.\nUtilize the terminal to initialize a grid, create a randomized maze and set source/destination points to perform the algorithms on.";
    String howTo = "\n\nThroughout the program you will be entering a specific number depending on the command you want to perform.\n\nLet's get started!\n\n";
    String instruction = "1 : Start Program\n\n2 : Exit Program\n\n";
    clearScreen();
    System.out.println(welcomeMessage + description + howTo);
    while (true) {
      try {
        System.out.println(instruction);
        int number = scanner.nextInt();
        if (number == 1) {
          clearScreen();
          switchToMainMenu();
        } else if (number == 2) {
          System.exit(0);
        } else {
          clearScreen();
          System.out.println("\nPlease enter '1' to start or '2' to exit.\n");
        }

      } catch (InputMismatchException e) {
        clearScreen();
        System.out.println("\nPlease enter '1' to start or '2' to exit.\n");
        scanner.next();
      }
    }
  }
}
