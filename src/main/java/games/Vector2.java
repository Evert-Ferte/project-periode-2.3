package games;

public class Vector2 {
    public float x;
    public float y;
    
    public Vector2() {
        this.x = 0;
        this.y = 0;
    }
    
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}
