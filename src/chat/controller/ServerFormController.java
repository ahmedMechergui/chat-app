package chat.controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import chat.model.Client;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

import static chat.StringUtils.isImage;

public class ServerFormController {
    public ScrollPane msgContext;
    public TextField txtMessage;

    final int PORT = 64000;
    final static int PORT1 = 50000;
    final int PORT2 = 60000;
    final int PORT3 = 65000;
    public AnchorPane emoji;
    Socket socket;
    DataInputStream dataInputStream0;
    DataOutputStream dataOutputStream0;
    ImageInputStream imgInputStream;
    ImageOutputStream imgOutputStream;
    String message = "";
    int i = 0;
    public AnchorPane context = new AnchorPane();
    Client serverClient;
    Client client;
    Client client2;
    Client client3;
    boolean isUsed = false;
    boolean isImageChoose = false;
    String path = "";
//    https://www.codegrepper.com/code-examples/java/java+send+an+image+over+a+socket

    public void initialize() {
        Platform.setImplicitExit(false);
        msgContext.setContent(context);
        msgContext.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        msgContext.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        msgContext.vvalueProperty().bind(context.heightProperty());

        new Thread(() -> {
            while (true) {
                serverClient = new Client(PORT);
                try {
                    serverClient.setName("You ");
                    serverClient.acceptConnection();
                    serverClient.setInputAndOutput();
                    processTextMessage(serverClient, serverClient.getDataInputStream());
                    client = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    client = new Client(PORT1);
                    client.setName("Client 01");
                    client.acceptConnection();
                    client.setInputAndOutput();
                    processTextMessage(client, client.getDataInputStream());
                } catch (IOException ignored) {
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                client2 = new Client(PORT2);
                try {
                    client2.setName("Client 02");
                    client2.acceptConnection();
                    client2.setInputAndOutput();
                    processTextMessage(client2, client2.getDataInputStream());
                } catch (IOException ignored) {
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                client3 = new Client(PORT3);
                try {
                    client3.setName("Client 03");
                    client3.acceptConnection();
                    client3.setInputAndOutput();
                    processTextMessage(client3, client3.getDataInputStream());
                } catch (IOException ignored) {
                }
            }
        }).start();

        new Thread(() -> {
            try {
                socket = new Socket("localhost", PORT);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Label label = new Label("Server Started...");
                        label.setStyle("-fx-font-family: Ubuntu; -fx-font-size: 20px;");
                        label.setLayoutY(i);
                        context.getChildren().add(label);
                        i += 30;
                    }
                });
                while (true) {
                    dataOutputStream0 = new DataOutputStream(socket.getOutputStream());
                    dataInputStream0 = new DataInputStream(socket.getInputStream());
                    message = dataInputStream0.readUTF();
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
                                Image img = SwingFXUtils.toFXImage(sendImage, null);
                                ImageView imageView = new ImageView(img);
                                imageView.setFitHeight(150);
                                imageView.setFitWidth(150);
                                imageView.setLayoutY(i);
                                context.getChildren().add(imageView);
                                i += 150;
                            }else if (message.startsWith("Admin")) {
                                message = message.replace("Admin", "You");
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
    }

    public void btnSendOnAction(MouseEvent actionEvent) throws IOException {
        if (isImageChoose){
            dataOutputStream0.writeUTF(path.trim());
            dataOutputStream0.flush();
            isImageChoose = false;
        }else {
            dataOutputStream0.writeUTF("Admin : " + txtMessage.getText().trim());
            dataOutputStream0.flush();
        }
        txtMessage.clear();
    }

    public void btnImageChooserOnAction(MouseEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        Stage stage = new Stage();
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            path = file.getPath();
            System.out.println("selected");
            System.out.println(file.getPath());
            isImageChoose = true;
        }
    }

    public void btnExitOnAction(MouseEvent actionEvent) throws IOException {
        message = "Server Offline";
        sendTextMessage(message);
        System.exit(0);
    }

    public void processTextMessage(Client client, DataInputStream dataInputStream) throws IOException {
        if (dataOutputStream0 != null) {
            dataOutputStream0.writeUTF("👋\t\t\t" + client.getName() + "  Joined\t\t\t👋".trim());
            dataOutputStream0.flush();
        }

        while (true) {
            message = dataInputStream.readUTF();
            System.out.println(message);
            if (message.equals("exit")) {
                client.setAccept(null);
                client.setImgSocket(null);
                dataOutputStream0.writeUTF("👋\t\t\t" + client.getName() + " left\t\t\t👋".trim());
                dataOutputStream0.flush();
                client.setServerSocket(null);
                return;
            }
            sendTextMessage(message);
        }
    }

    private void sendTextMessage(String message) throws IOException {
        if (serverClient.getAccept() != null) {
            serverClient.getDataOutputStream().writeUTF(message.trim());
            serverClient.getDataOutputStream().flush();
        }
        if (client.getAccept() != null) {
            client.getDataOutputStream().writeUTF(message.trim());
            client.getDataOutputStream().flush();
        }
        if (client2.getAccept() != null) {
            client2.getDataOutputStream().writeUTF(message.trim());
            client2.getDataOutputStream().flush();
        }
        if (client3.getAccept() != null) {
            client3.getDataOutputStream().writeUTF(message.trim());
            client3.getDataOutputStream().flush();
        }
    }

//    private void processImage(InputStream inputStream) throws IOException {
//        System.out.println("come");
////        BufferedImage sendImage = ImageIO.read(new File(path));
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byte[] sendSize = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//        sendImgMessage(sendSize, byteArrayOutputStream);
//        System.out.println("Flushed: " + System.currentTimeMillis());
//        System.out.println("Closing: " + System.currentTimeMillis());
////        sendTextMessage(path);
//        sendImgMessage(sendSize,byteArrayOutputStream);
//    }

//    private void sendImgMessage(byte[] sendSize, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
//        if (serverClient.getImgSocket() != null) {
//            serverClient.getImgOutputStream().write(sendSize);
//            serverClient.getImgOutputStream().write(byteArrayOutputStream.toByteArray());
//            serverClient.getImgOutputStream().flush();
//        }
//        if (client.getImgSocket() != null) {
//            client.getImgOutputStream().write(sendSize);
//            client.getImgOutputStream().write(byteArrayOutputStream.toByteArray());
//            client.getImgOutputStream().flush();
//            System.out.println("done");
//        }
//        if (client2.getImgSocket() != null) {
//            client2.getImgOutputStream().write(sendSize);
//            client2.getImgOutputStream().write(byteArrayOutputStream.toByteArray());
//            client2.getImgOutputStream().flush();
//        }
//        if (client3.getImgSocket() != null) {
//            client3.getImgOutputStream().write(sendSize);
//            client3.getImgOutputStream().write(byteArrayOutputStream.toByteArray());
//            client3.getImgOutputStream().flush();
//        }
//    }

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
