package games.reversi;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ReversiController {
    static class ButtonHandler implements EventHandler<ActionEvent>
    {
        private int xPos;
        private int yPos;
        private ReversiModel model;
        
        public ButtonHandler(int xPos, int yPos, ReversiModel model) {
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
