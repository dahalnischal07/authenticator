import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.*;

public class AuthenticatorApp extends Application {

    private VBox accountsBox = new VBox(10);
    private Map<String, String> accounts = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        TextField accountNameField = new TextField();
        accountNameField.setPromptText("Account Name");

        TextField secretField = new TextField();
        secretField.setPromptText("Base32 Secret");

        Button addButton = new Button("Add Account");

        addButton.setOnAction(e -> {
            String name = accountNameField.getText();
            String secret = secretField.getText();

            if (!name.isEmpty() && !secret.isEmpty()) {
                accounts.put(name, secret);
                addAccountUI(name, secret);
                accountNameField.clear();
                secretField.clear();
            }
        });

        VBox inputBox = new VBox(10, accountNameField, secretField, addButton);
        inputBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(accountsBox);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(15, inputBox, scrollPane);
        root.setPadding(new Insets(10));

        stage.setTitle("Secure GUI Authenticator");
        stage.setScene(new Scene(root, 400, 500));
        stage.show();

        startUpdaterThread();
    }

    private void addAccountUI(String name, String secret) {
        Label nameLabel = new Label(name);
        Label otpLabel = new Label("------");
        Label timerLabel = new Label("");

        VBox box = new VBox(5, nameLabel, otpLabel, timerLabel);
        box.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        accountsBox.getChildren().add(box);

        box.setUserData(new AccountData(secret, otpLabel, timerLabel));
    }

    private void startUpdaterThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    long currentTime = System.currentTimeMillis() / 1000;
                    long remaining = 30 - (currentTime % 30);

                    Platform.runLater(() -> {
                        for (var node : accountsBox.getChildren()) {
                            VBox box = (VBox) node;
                            AccountData data = (AccountData) box.getUserData();

                            try {
                                String otp = generateTOTP(data.secret);
                                data.otpLabel.setText("OTP: " + otp);
                                data.timerLabel.setText("Expires in: " + remaining + "s");
                            } catch (Exception ignored) {}
                        }
                    });

                    Thread.sleep(1000);

                } catch (InterruptedException ignored) {}
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private static class AccountData {
        String secret;
        Label otpLabel;
        Label timerLabel;

        AccountData(String secret, Label otpLabel, Label timerLabel) {
            this.secret = secret;
            this.otpLabel = otpLabel;
            this.timerLabel = timerLabel;
        }
    }

    // ===== TOTP LOGIC =====

    public static String generateTOTP(String base32Secret) throws Exception {

        byte[] key = base32Decode(base32Secret);
        long timeStep = System.currentTimeMillis() / 1000 / 30;

        byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        mac.init(signKey);

        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;

        int binary =
                ((hash[offset] & 0x7F) << 24) |
                ((hash[offset + 1] & 0xFF) << 16) |
                ((hash[offset + 2] & 0xFF) << 8) |
                (hash[offset + 3] & 0xFF);

        int otp = binary % 1_000_000;

        return String.format("%06d", otp);
    }

    public static byte[] base32Decode(String base32) {
        base32 = base32.replace("=", "").toUpperCase();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        int buffer = 0;
        int bitsLeft = 0;
        byte[] result = new byte[base32.length() * 5 / 8];
        int index = 0;

        for (char c : base32.toCharArray()) {
            int val = chars.indexOf(c);
            if (val == -1) continue;

            buffer <<= 5;
            buffer |= val;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[index++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }

        return Arrays.copyOf(result, index);
    }
}