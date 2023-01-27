package chat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static chat.FakerUtils.fakeName;
import static chat.KeyUtils.isEnter;

public class LoginForm01Controller {
    public TextField txtName;
    public AnchorPane loginContext;
    public static String name;

    public void initialize(){
        txtName.setText(fakeName());
    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {
        login();
    }

    private void login() throws IOException {
        name = txtName.getText();
        loginContext.getChildren().clear();
        Stage stage = (Stage) loginContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/client-01-form.fxml"))));
    }

    public void onKeyPressed(KeyEvent keyEvent) throws IOException {
        if (isEnter(keyEvent)){
            login();
        }
    }

}