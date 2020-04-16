package games.reversi;

/**
 * A class representation for a 2D point in space.
 */
public class Vector2 {
    public float x;
    public float y;
    
    /**
     * The constructor. Initializes the X and Y position to 0.
     */
    public Vector2() {
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * The constructor. Initializes the X and Y position to the given value.
     * @param x The new X position.
     * @param y The new Y position.
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns a string containing the current X and Y position.
     * @return Returns the current state of the vector.
     */
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}
