package customization;

public class ColorSet {
    public String primary = "";
    public String secondary = "";
    public String complementary = "";
    
    public String textPrimary = "";
    public String textSecondary = "";
    
    public ColorSet() { }
    public ColorSet(String primary, String secondary, String complementary, String textPrimary, String textSecondary) {
        this.primary = primary;
        this.secondary = secondary;
        this.complementary = complementary;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
    }
}
