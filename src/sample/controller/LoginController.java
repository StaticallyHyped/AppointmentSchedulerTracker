package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Main;
import sample.model.MySQLConnector;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    Button loginConfirm;
    @FXML
    public TextField userNameField;
    @FXML
    public TextField passwordBox;
    @FXML
    protected Label username;
    @FXML
    protected Label password;
    Locale locale = Locale.getDefault();
    String lang = locale.getDisplayLanguage();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (lang.equals("es")) {
            username.setText("Nombre de usuario");
            password.setText("contraseña");
        }
    }

    sample.controller.CustomersListController test = new sample.controller.CustomersListController();

    public void loginSubmit(ActionEvent event) throws IOException {

        if (userNameField.getText().equals("test") && passwordBox.getText().equals("test")) {
            try {
                MySQLConnector.makeConnection();

                try (FileWriter fw = new FileWriter("logfile.txt", true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    String log = String.valueOf(LocalDateTime.now());
                    out.println(log);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("your login button is not working");
                e.printStackTrace();
            }

            Stage stage = (Stage) loginConfirm.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/customersList.fxml"));
            AnchorPane page = loader.load();
            Scene scene = new Scene(page);

            try {
                test.setClientData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.setScene(scene);
            stage.show();
        } else {
            if (lang.equals("es")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Tu nombre de usuario o contraseña es incorrecta.");
                alert.setTitle("Error al iniciar sesión");
                alert.setHeaderText("Credenciales no válidas");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your password or login are incorrect");
                alert.showAndWait();
            }
        }

    }
}
