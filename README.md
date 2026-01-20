# Pathfinding-Visualization

A pathfinding visualization program. Uses Dijkstra's or A* algorithm to find a path between two arbitrary points in a randomly generated maze created with Depth First Search or Randomized Prim's algorithm. Made by Kai Zhen.

## Overview

The program was build to visualize some of the most well known pathfinding algorithms, namely Dijkstra's algorithm and the A* algorithm. It utilizes the terminal to interact with the user and offers features like random maze generation using either Depth First Search algorithm or Randomized Prim's algorithm as well as selecting a source and destination cell to perform a pathfinding algorithm on.

** EPILEPSY WARNING ** 

In the main menu you will have the option to turn on animations for maze generation and pathfinding. The program is a lot more interactive with animations turned on, but the way it simulates the animations in the terminal may cause some flashing/flickering which may affect certain people.


## Functionality

The program begins by introducing the user to some of its basic features:

```
Welcome to the Pathfinding Algorithm Visualizer (PAV)!

PAV is a program that was build to visualize some of the most well known pathfinding algorithms, Dijkstra's algorithm and the A* algorithm.
Utilize the terminal to initialize a grid, create a randomized maze and set source/destination points to perform the algorithms on.

Throughout the program you will be entering a specific number depending on the command you want to perform.

Let's get started!

1 : Start Program

2 : Exit Program

```
The introductory screen leads to the main menu. You can navigate the menu by entering the corresponding number in the terminal:

```
Welcome to the main menu!

From here you can create a random maze, set source/destination points and select a pathfinding algorithm of your choice.

Note: A grid must be initialized first before other options are available.

1 : Initialize a grid

2 : Create random maze

3 : Set new source point

4 : Set new destination point

5 : Choose pathfinding algorithm

6 : Start pathfinding

7 : Turn on animations **EPILEPSY WARNING** POTENTIAL FLASHING LIGHTS

8 : Exit program

```
First, initialize a grid by entering a row and column number between 3 and 20:

```
New grid initialized.
+---+---+---+---+---+
|   |   |   |   |   |
+---+---+---+---+---+
|   |   |   |   |   |
+---+---+---+---+---+
|   |   |   |   |   |
+---+---+---+---+---+
|   |   |   |   |   |
+---+---+---+---+---+
|   |   |   |   |   |
+---+---+---+---+---+

```
Next, create a maze by choosing either Depth First Search or Randomized Prim's algorithm:

```
Random maze created.
+---+---+---+---+---+
|                   |
+---+   +   +---+---+
|       |   |       |
+   +---+   +   +   +
|       |   |   |   |
+   +   +---+   +   +
|   |       |   |   |
+   +---+   +   +---+
|       |           |
+---+---+---+---+---+

```
Select a source (S) and destination (D) :

```
+---+---+---+---+---+
| S                 |
+---+   +   +---+---+
|       |   |       |
+   +---+   +   +   +
|       |   |   |   |
+   +   +---+   +   +
|   |       |   |   |
+   +---+   +   +---+
|       |         D |
+---+---+---+---+---+

```
Choose a pathfinding algorithm (Dijkstra's or A*) to finish the setup and start the pathfinding:

```
Path found.
+---+---+---+---+---+
| S   X             |
+---+   +   +---+---+
| X   X |   |       |
+   +---+   +   +   +
| X   X |   |   |   |
+   +   +---+   +   +
|   | X   X |   |   |
+   +---+   +   +---+
|       | X   X   D |
+---+---+---+---+---+

```
(Note that differences between Dijkstra's algorithm and the A* algorithm won't be visible if you have animations turned off, as both will find an identical path if one exists.)

## How it works

The visible grid is implemented as a 2D array of tile objects together with walls that surround the tiles. A wall object does not occupy the array itself, it is "saved" as a property of the tile that is surrounds (OOP principle of composition). A tile has several other properties that each contribute to a specific problem (e.g. calculating the f score for the A* algorithm). 

This 2D array or rather tile matrix, gets manipulated depending on the algorithm that is being executed. If the algorithm is for example a maze generating one, wall properties of certain tiles would be changed / deleted. The grid string is a variable that gets updated each time the tile matrix is manipulated. Using the buildGridString function, it visualizes the current tile matrix by transforming it into a string so that it can be represented properly in the terminal.

### Depth First Search algorithm

The Depth First Search algorithm starts at a random tile and tries to explore a random path as far as possible until it encounters a dead end. It then uses backtracking to find a yet to be explored path and repeats this process until all tiles have been visited.

### Randomized Prim's algorithm

The Randomized Prim's algorithm starts off by choosing a random tile and adding the walls of the tile to a list. It repeats the following steps until all tiles have been visited:

* Choose a random wall from the list
  * If only one of tiles divided by the wall is visited, remove the wall property from both tiles and mark the unvisited tile as visited.
  * Add the neighbouring walls of the newly visited tile to the list
* Remove the randomly chosen wall from the list

### Dijkstra's algorithm

Dijkstra's algorithm uses an "open set" and a "closed set". The open set consists of tiles that have yet to be visited whereas the closed set contains the tiles that have already been visited. It stars by adding a random tile to the open set. At each iteration, it:

* chooses the tile with the lowest distance to the starting point from the open set as the current tile
* removes the current tile from the open set and adds it to the closed set
* checks if the current tile is equal to the destination tile:
  * if it is equal to the destination tile, then search is finished
  * else continue
* for each neighbour of the current tile:
  * if the neighbouring tile is in the closed set or the current and neighbouring tile are divided by a wall
    * skip to the next neighbour
  * else if the neighbouring tile is not in the open set or if the distance to the starting point from the neighbouring tile is greater than the sum of the distance to the starting point from the current tile and the distance from the current tile to the neighbouring tile
    * update the distance of the neighbouring tile to the newly found shortest distance
    * set the neighbouring tile's previous property to point to the current tile
    * add the neighbouring tile to the open set if it is not in the set

It repeats this process until the currently selected tile is equal to the destination tile.

### A* algorithm

The A* algorithm is identical to Dijkstra's algorithm, but instead of selecting the tile with the lowest distance to the starting point (also called g score, or g(n)) from the open set, it determines which one to choose by calculating the f score for each tile in the open set and selects the tile with the lowest f score. The f score is defined by

f(n) = g(n) + h(n)

Where f(n) is the f score, g(n) is the distance to the starting point from a given tile and h(n) is a heuristic that estimates the "true" distance from a given tile to the destination tile. In our implementation we will use the Manhattan distance defined as the sum of the absolute differences between the row/column position of two tiles.

Let a, b be two arbitrary tiles with row and column position noted as a.row for a's row position, a.column for a's column position, b.row for b's row position and b.column for b's column position. Define the Manhattan distance as

d(a, b) = abs(a.row - b.row) + abs(a.column - b.column)

In our implementation the Manhattan distance represents the actual "true" distance between two arbitrary tiles as the algorithm only allows horizontal or vertical movement.

The A* algorithm and Dijkstra's algorithm are equivalent for h(n) = 0.
