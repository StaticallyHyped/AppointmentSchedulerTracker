package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Main;
import sample.model.Appointment;
import sample.model.Data;
import sample.model.MySQLConnector;

import java.net.URL;
import java.util.ResourceBundle;

public class ScheduleViewerController implements Initializable {

    CustomersListController custList = new CustomersListController();
    @FXML
    public TableView<Appointment> userSchedTableView;
    @FXML
    private TableColumn<Appointment, String> userAptTypeColumn;
    @FXML
    private TableColumn<Appointment, String> userAptDateColumn;
    @FXML
    private TableColumn<Appointment, String> userAptTimeColumn;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Button exitCustListButton;
    @FXML
    private Label userName;
    @FXML
    private Button backToCustInfo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.userAptTypeColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentTypeProperty();
        });
        this.userAptDateColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentDateProperty();
        });
        this.userAptTimeColumn.setCellValueFactory((cellData) -> {
            return (cellData.getValue()).appointmentTimeProperty();
        });
        try {
            userSchedTableView.setItems(Data.getConsulAppointmentData());
            userName.setText(Data.selectedUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void exit() throws Exception {
        Stage stage = (Stage) exitCustListButton.getScene().getWindow();
        MySQLConnector.conn.close();
        stage.close();
    }

    public void backToCustinfo() throws Exception {
        Stage stage = (Stage) backToCustInfo.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/customersList.fxml"));
        AnchorPane page = loader.load();
        //ScheduleViewerController controllerTest = loader.getController();
        Scene scene = new Scene(page);
        userSchedTableView.getItems().clear();
        userSchedTableView.refresh();
        stage.setScene(scene);
        stage.show();
    }

}
