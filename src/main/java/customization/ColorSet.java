package customization;

public class ColorSet {
    public String name = "";
    
    public String primary = "";
    public String secondary = "";
    public String complementary = "";
    
    public String textPrimary = "";
    public String textSecondary = "";
    
    public ColorSet() { }
    public ColorSet(String name, String primary, String secondary, String complementary, String textPrimary, String textSecondary) {
        this.name = name;
        this.primary = primary;
        this.secondary = secondary;
        this.complementary = complementary;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
    }
}
