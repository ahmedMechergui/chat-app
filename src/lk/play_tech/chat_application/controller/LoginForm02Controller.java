package lk.play_tech.chat_application.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static lk.play_tech.chat_application.KeyUtils.isEnter;

public class LoginForm02Controller {
    public TextField txtName;
    public AnchorPane loginContext;
    public static String name;

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {
        login();
    }

    public void onKeyPressed(KeyEvent keyEvent) throws IOException {
        if (isEnter(keyEvent)){
            login();
        }
    }

    private void login() throws IOException {
        name = txtName.getText();
        loginContext.getChildren().clear();
        Stage stage = (Stage) loginContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/client-02-form.fxml"))));
    }
}