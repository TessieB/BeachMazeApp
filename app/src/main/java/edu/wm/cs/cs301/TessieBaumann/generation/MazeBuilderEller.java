package edu.wm.cs.cs301.TessieBaumann.generation;

import java.util.*;

public class MazeBuilderEller extends MazeBuilder implements Runnable {

    public MazeBuilderEller() {
        super();
        System.out.println("MazeBuilderPrim uses Eller's algorithm to generate maze.");
    }

    /**
     * This method runs through each cell of the matrix and creates a new set for each cell that
     * is not in a set. Once every cell has been put into a set, the method
     * randomly combines adjacent cells if they belong to different sets
     * by randomly tearing down left and right walls. Afterwards, it randomly chooses at least one cell from each
     * set that is in the row being iterated through and removes each of those cells' bottom wallboards, subsequently
     * adding the cells beneath them to their set. Once that has been accomplished, it goes to the next row and creates
     * a new set for each cell that is not already in a set. Then it randomly joins adjacent cells if they belong to
     * different sets again, and chooses at least one cell from each set in the row to remove the bottom
     * wallboards for. This process is repeated until the last row. At this point, the method
     * connects all the disjointed sets so that it is possible to reach the exit point of the maze from any spot
     * inside the maze. In the case of rooms, the method randomly tears down the borders of the room when it
     * needs to so it can continue to work in the room.
     */
    protected void generatePathways() {

        int[] cells = new int[width*height];
        boolean lastCellOfSet = false;
        boolean lastRowDisjointedSet = false;
        int timeThrough = 0;


        while(timeThrough < height-1) {
            Map<Integer,int[]> setTracker = new HashMap<Integer,int[]>();
            for(int i = width*timeThrough; i < width*(timeThrough+1); i++) {
                lastRowDisjointedSet = false;
                if(cells[i] == 0) {
                    cells[i] = i+1;
                }
                if(i != width -1) {
                    randomlyJoinAdjacentCells(cells, i, i+1, lastRowDisjointedSet, width);
                }
                if(setTracker.containsKey(cells[i])) {
                    setTracker.get(cells[i])[0]++;
                }
                else {
                    int[] temp = new int[] {1, 0, 0};
                    setTracker.put(cells[i], temp);
                }
            }
            for(int i = width*timeThrough; i < width*(timeThrough+1); i++) {
                if(setTracker.get(cells[i])[0] > setTracker.get(cells[i])[1] && setTracker.get(cells[i])[2] == 0) {
                    lastCellOfSet = true;
                }
                randomlyJoinCellsBeneathRow(cells, i, width, lastCellOfSet, setTracker);
                lastCellOfSet = false;
                setTracker.get(cells[i])[1]++;
            }
            timeThrough++;
        }

        if(timeThrough == height-1) {
            for(int i = width*timeThrough; i < width*(timeThrough+1); i++) {
                lastRowDisjointedSet = false;
                if(cells[i] == 0) {
                    cells[i] = i+1;
                }
                if(i+1 < width*(timeThrough+1)) {
                    if(cells[i] != cells[i+1]) {
                        lastRowDisjointedSet = true;
                        randomlyJoinAdjacentCells(cells, i, i+1, lastRowDisjointedSet, width);
                    }
                }
            }
        }
    }

    /**
     * This method randomly joins cells that are adjacent to each other in a row
     * and are part of different sets by removing the the wall separating the cells and
     * adding one of the cells into the set of the other cell.
     */
    private void randomlyJoinAdjacentCells(int[] cells, int cell1, int cell2, boolean lastRow, int width) {
        int randomCell = random.nextIntWithinInterval(0, 1);
        if(lastRow) {
            randomCell = 1;
        }

        if(cells[cell1] != cells[cell2]) {
            if(randomCell == 1) {
                Wallboard randomWall = new Wallboard(cell1%width, cell1/width, CardinalDirection.East);
                if(floorplan.canTearDown(randomWall)) {
                    floorplan.deleteWallboard(randomWall);
                }
                else if(floorplan.isInRoom(cell2%width, cell2/width) && floorplan.isPartOfBorder(randomWall)) {
                    floorplan.deleteWallboard(randomWall);
                }
                else if(floorplan.isInRoom(cell1%width, cell1/width) && floorplan.isPartOfBorder(randomWall)) {
                    floorplan.deleteWallboard(randomWall);
                }
                for(int i = 0; i < cells.length; i++) {
                    if(cells[i] == cells[cell2]) {
                        cells[i] = cells[cell1];
                    }
                }
            }
        }
    }

    /**
     * This method randomly joins cells that are beneath the current row by removing the bottom wallboard
     * of at least one cell per set and adding the cell that had its top wallboard removed to the set
     * of the cell above it.
     */
    private void randomlyJoinCellsBeneathRow(int[] cells, int cell1, int width, boolean lastCellOfSet, Map<Integer,int[]> setTracker) {
        int randomCell = random.nextIntWithinInterval(0, 1);
        if(lastCellOfSet) {
            randomCell = 1;
        }
        if(randomCell == 1) {
            Wallboard randomWall = new Wallboard(cell1%width, cell1/width, CardinalDirection.South);
            if(floorplan.canTearDown(randomWall)) {
                floorplan.deleteWallboard(randomWall);
            }
            else if(floorplan.isInRoom((cell1+width)%width, (cell1+width)/width) && floorplan.isPartOfBorder(randomWall)) {
                floorplan.deleteWallboard(randomWall);
            }
            else if(floorplan.isInRoom((cell1)%width, (cell1)/width) && floorplan.isPartOfBorder(randomWall)) {
                floorplan.deleteWallboard(randomWall);
            }
            cells[cell1 + width] = cells[cell1];
            setTracker.get(cells[cell1])[2]++;
        }
    }
}

