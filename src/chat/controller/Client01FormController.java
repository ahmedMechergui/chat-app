package chat.controller;

import chat.StringUtils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

import static chat.FakerUtils.setFakeMessage;
import static chat.KeyUtils.isEnter;
import static chat.StringUtils.isImage;

public class Client01FormController {
    public static boolean isImageChoose = false;
    final int PORT = 50000;
    public ScrollPane msgContext;
    public TextField txtMessage;
    public AnchorPane context = new AnchorPane();
    public Label lblClient;
    public AnchorPane emoji;
    Socket socket;
    Socket imgSocket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message = "";
    int i = 10;
    String path = "";
    ObjectOutputStream oos;
    ObjectInputStream ois;
    File file;
    OutputStream imgOutputStream;
    InputStream imgInputStream;
    boolean isUsed = false;

    public void initialize() {
        Platform.setImplicitExit(false);
        msgContext.setContent(context);
        msgContext.vvalueProperty().bind(context.heightProperty());
        msgContext.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        msgContext.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        lblClient.setText(LoginForm01Controller.name);

        new Thread(() -> {
            try {
                socket = new Socket("localhost", PORT);
                while (true) {
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    message = dataInputStream.readUTF();
                    System.out.println(message);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (isImage(message)) {
                                BufferedImage sendImage = null;
                                try {
                                    sendImage = ImageIO.read(new File(message));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                i += 10;
                                Image img = SwingFXUtils.toFXImage(sendImage, null);
                                ImageView imageView = new ImageView(img);
                                imageView.setFitHeight(150);
                                imageView.setFitWidth(150);
                                imageView.setLayoutY(i);
                                context.getChildren().add(imageView);
                                i += 150;
                            } else if (message.startsWith(LoginForm01Controller.name)) {
                                message = message.replace(LoginForm01Controller.name, "You");
                                Label label = new Label(message);
                                label.setStyle(" -fx-font-family: Ubuntu; -fx-font-size: 20px; -fx-background-color: #85b6ff; -fx-text-fill: #5c5c5c");
                                label.setLayoutY(i);
                                context.getChildren().add(label);
                            } else {
                                Label label = new Label(message);
                                label.setStyle(" -fx-font-family: Ubuntu; -fx-font-size: 20px; -fx-background-color: #CDB4DB; -fx-text-fill: #5c5c5c");
                                label.setLayoutY(i);
                                context.getChildren().add(label);
                            }
                            i += 30;
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        txtMessage.setOnAction(event -> {
            try {
                sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void btnSendOnAction(MouseEvent actionEvent) throws IOException {
        sendMessage();
    }

    public void btnSendOnActionKeyPressed(KeyEvent keyEvent) {
        if (isEnter(keyEvent)) {
            try {
                sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() throws IOException {
        setFakeMessage(txtMessage);
        if (isImageChoose) {
            dataOutputStream.writeUTF(path.trim());
            dataOutputStream.flush();
        } else if (!StringUtils.isBlank(txtMessage.getText())) {
            dataOutputStream.writeUTF(lblClient.getText() + " : " + txtMessage.getText().trim());
            dataOutputStream.flush();
        }
        isImageChoose = false;
        txtMessage.clear();
    }

    public void btnImageChooserOnAction(MouseEvent actionEvent) throws IOException {
        // get the file selected
        FileChooser chooser = new FileChooser();
        Stage stage = new Stage();
        file = chooser.showOpenDialog(stage);

        if (file != null) {
            path = file.getPath();
            System.out.println("selected");
            System.out.println(file.getPath());
            isImageChoose = true;
            this.sendMessage();
        }
    }

    public void btnExitOnAction(MouseEvent actionEvent) throws IOException {
        if (socket != null) {
            dataOutputStream.writeUTF("exit".trim());
            dataOutputStream.flush();
            System.exit(0);
        }
        System.exit(0);
    }

    public void btnEmojiOnAction(MouseEvent mouseEvent) {
        if (isUsed) {
            emoji.getChildren().clear();
            isUsed = false;
            return;
        }
        isUsed = true;
        VBox dialogVbox = new VBox(20);
        ImageView smile = new ImageView(new Image("chat/assets/smile.png"));
        smile.setFitWidth(30);
        smile.setFitHeight(30);
        dialogVbox.getChildren().add(smile);
        ImageView heart = new ImageView(new Image("chat/assets/heart.png"));
        heart.setFitWidth(30);
        heart.setFitHeight(30);
        dialogVbox.getChildren().add(heart);
        ImageView sadFace = new ImageView(new Image("chat/assets/sad-face.png"));
        sadFace.setFitWidth(30);
        sadFace.setFitHeight(30);
        dialogVbox.getChildren().add(sadFace);
        smile.setOnMouseClicked(event -> {
            txtMessage.setText(txtMessage.getText() + "☺");
        });
        heart.setOnMouseClicked(event -> {
            txtMessage.setText(txtMessage.getText() + "♥");
        });
        sadFace.setOnMouseClicked(event -> {
            txtMessage.setText(txtMessage.getText() + "☹");
        });
        emoji.getChildren().add(dialogVbox);
    }
}
