package chat;

import com.github.javafaker.Faker;
import javafx.scene.control.TextField;

import static chat.StringUtils.isBlank;

public class FakerUtils {
    static Faker faker = new Faker();

    public static void setFakeMessage(TextField field){
        if (isBlank(field.getText())){
            field.setText(faker.leagueOfLegends().summonerSpell());
        }
    }

    public static String fakeName(){
        return faker.name().firstName();
    }
}
