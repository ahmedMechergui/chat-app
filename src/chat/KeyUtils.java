package chat;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class KeyUtils {

    public static boolean isEnter(KeyEvent keyEvent){
        return keyEvent.getCode() == KeyCode.ENTER;
    }
}
