package games.reversi;

import games.Board;
import games.Vector2;

import java.util.ArrayList;

/**
 * This class makes the Reversi board and contains the backend functionality of it
 *
 * @author Evert de la Ferté
 * @version 1.0
 */
public class ReversiBoard extends Board {

    ReversiBoard(int mapSize){
        this.mapSize = mapSize;
        System.out.println(mapSize + " reversi");
        generateModelMap(mapSize);
    }

    @Override
    public Vector2[] getAvailablePositions() {
        ArrayList<Vector2> pos = new ArrayList<>();

        // Loop through the map/board
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                // Get the current tile id
                String tileId = modelMap.get(y).get(x);

                // If the tile is empty, check if the current player can place a valid tile here
                if (tileId.equals(emptyId)) {
//                    if (isValidTile(x, y))
//                        pos.add(new Vector2(x, y));
                    // TODO - sometimes it cannot find tiles on the first move of the game (fix this)
                    ArrayList<Vector2> p = getTilesToFlip(x, y);

                    if (p.size() > 0)
                        pos.add(new Vector2(x, y));
                }
            }
        }

        return pos.toArray(new Vector2[0]);
    }

    /**
     * Places the tiles that form the starting setup.
     */
    void placeStartingTiles() {
        ArrayList<Vector2> tilesToFlip = new ArrayList<>();

//        setTurn(false); //TODO for if we want a standard layout for the starting tiles, otherwise remove this.

        // Place the first 2 white/black tiles
        tilesToFlip.add(new Vector2(3, 4));
        tilesToFlip.add(new Vector2(4, 3));
        flipTiles(tilesToFlip, false);

        // Switch turns so we can place the other color
        switchTurn();

        // Place the second 2 black/white tiles
        tilesToFlip.set(0, new Vector2(3, 3));
        tilesToFlip.set(1, new Vector2(4, 4));
        flipTiles(tilesToFlip, false);

        // Switch turns again so we end up where we were
        switchTurn();

        // Add 2 points to both black and white
        addScoreToBlack(2);
        addScoreToWhite(2);
    }

    /**
     * Flip all tiles from the list to the color of the current active player. Also add score to the current active
     * player, and remove score from the waiting player.
     *
     * @param tiles Tiles that will be flipped.
     */
    private void flipTiles(ArrayList<Vector2> tiles) { flipTiles(tiles, true); }
    /**
     * Flip all tiles from the list to the color of the current active player. You have the option to add/remove
     * score or not.
     *
     * @param tiles Tiles that will be flipped.
     * @param addScore Should score be added while flipping the tiles.
     */
    private void flipTiles(ArrayList<Vector2> tiles, boolean addScore) {
        if (tiles.size() <= 0) return;

        // Flip all tiles in the list
        for (Vector2 tile : tiles) {
            modelMap.get((int)tile.y).set((int)tile.x, getPlayerId(isWhiteTurn()));

            // Skip adding score, if we don't need to
            if (!addScore) continue;

            // Add score to the correct player, and remove score from the other player
            if (isWhiteTurn()) {
                addScoreToWhite();
                subtractScoreFromBlack();
            }
            else {
                addScoreToBlack();
                subtractScoreFromWhite();
            }
        }
    }

    /**
     * Search for tiles that can be flipped from the given point. Returns a list of vectors which contain all tiles
     * that can be flipped.
     *
     * @param x X position to look from.
     * @param y Y position to look from.
     * @return Returns a list of vectors containing all tiles that can be flipped.
     */
    private ArrayList<Vector2> getTilesToFlip(int x, int y) {
        // Check if the clicked position is empty
        if (!modelMap.get(y).get(x).equals(emptyId)) return new ArrayList<>();

        // Check all directions for tiles that can be flipped
        ArrayList<Vector2> tilesUp       = getTilesInDirection(x, y,  0, -1);
        ArrayList<Vector2> tilesDown     = getTilesInDirection(x, y,  0,  1);
        ArrayList<Vector2> tilesLeft     = getTilesInDirection(x, y, -1,  0);
        ArrayList<Vector2> tilesRight    = getTilesInDirection(x, y,  1,  0);

        ArrayList<Vector2> tilesUpLeft   = getTilesInDirection(x, y, -1, -1);
        ArrayList<Vector2> tilesUpRight  = getTilesInDirection(x, y,  1, -1);
        ArrayList<Vector2> tilesDownLeft = getTilesInDirection(x, y, -1,  1);
        ArrayList<Vector2> tilesDownRight= getTilesInDirection(x, y,  1,  1);

        // Merge all directions to 1 list
        ArrayList<Vector2> r = new ArrayList<>();
        r.addAll(tilesUp);
        r.addAll(tilesDown);
        r.addAll(tilesLeft);
        r.addAll(tilesRight);
        r.addAll(tilesUpLeft);
        r.addAll(tilesUpRight);
        r.addAll(tilesDownLeft);
        r.addAll(tilesDownRight);

        return r;       // Return the merged list
    }

    /**
     * Gets a list of positions that can be flipped from a specific point to a given direction.
     *
     * @param x X start position.
     * @param y Y start position.
     * @param xDir X direction that will be searched.
     * @param yDir Y direction that will be searched.
     * @return Returns a list of positions.
     */
    private ArrayList<Vector2> getTilesInDirection(int x, int y, int xDir, int yDir) {
        String oppositePlayerId = getPlayerId(!whiteTurn);
        String playerId         = getPlayerId(whiteTurn);
        boolean valid = false;
        ArrayList<Vector2> tilesInDirection = new ArrayList<>();

        int newX = x + xDir;
        int newY = y + yDir;

        // Loop while the new position is still a valid position on the board
        int i = 1;
        while (isPositionInBoard(newX, newY)) {
            // Get the ID of the current tile
            String id = modelMap.get(newY).get(newX);

            // tile = opposite                  add to tiles
            if (id.equals(oppositePlayerId))    tilesInDirection.add(new Vector2(newX, newY));
                // tile = player                    valid and stop
            else if (id.equals(playerId))       { valid = true; break; }
            // tile = empty                     stop
            else                                break;

            // Set the new position to 1 tile further in the given direction
            i++;
            newX = x + xDir * i;
            newY = y + yDir * i;
        }

        // Only return the tiles if the player encloses the other players' tiles
        return valid ? tilesInDirection : new ArrayList<>();
    }


    /**
     * test if the current game is finished
     *
     * @return boolean indicating true if it is finished
     */
    public boolean isGameFinished() {
        // Loop through the map and find tiles that can still be flipped
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                ArrayList<Vector2> tiles = getTilesToFlip(x, y);

                // The game is finished when no tiles can be flipped, so return false if there are still tiles left
                if (tiles.size() > 0)
                    return false;
            }
        }

//        return scoreWhite + scoreBlack == mapSize * mapSize;
        return true;
    }

    /**
     * check if a specific move is valid and perform it
     *
     * @param x The x axis of the move
     * @param y The y axis of the move
     * @return True if the move was successful
     */
    public boolean move(int x, int y){
        // Get and check for possible tiles that can be flipped at the clicked position
        ArrayList<Vector2> tilesToFlip = getTilesToFlip(x, y);
        boolean validClick = tilesToFlip.size() > 0;

        // Return if the click was not on a valid position
        if (!validClick) return false;

        // Flip all tiles that need to be flipped
        flipTiles(tilesToFlip);

        // Also flip the clicked tile
        ArrayList<Vector2> clickedPosition = new ArrayList<>();
        clickedPosition.add(new Vector2(x, y));
        flipTiles(clickedPosition, false);
        if (isWhiteTurn())  addScoreToWhite();
        else                addScoreToBlack();
        return true;
    }

    /**
     * clone the board
     *
     * @return a new clone of the current board
     * @throws CloneNotSupportedException
     */
    @Override
    public ReversiBoard clone() throws CloneNotSupportedException {
        return (ReversiBoard) super.clone();
    }

    /**
     * @return a string representation of the board
     */
    @Override
    public String toString() {
        return "ReversiBoard{" +
                "scoreWhite=" + scoreWhite +
                ", scoreBlack=" + scoreBlack +
                ", whiteTurn=" + whiteTurn +
                ", playerTurn=" + playerTurn +
                ", modelMap=" + modelMap +
                '}';
    }

}
