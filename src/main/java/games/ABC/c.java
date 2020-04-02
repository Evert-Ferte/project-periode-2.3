package games.ABC;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class c {
    static class ButtonHandler implements EventHandler<ActionEvent>
    {
        private int xPos;
        private int yPos;
        private m model;
        
        public ButtonHandler(int xPos, int yPos, m model) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.model = model;
        }
        
        @Override
        public void handle(ActionEvent event) {
            model.clickPosition(xPos, yPos);
        }
    }
}
