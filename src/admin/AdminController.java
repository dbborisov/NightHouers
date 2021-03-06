package admin;

import com.sun.istack.internal.Nullable;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private javafx.scene.control.TextField whatIncident;
    @FXML
    private javafx.scene.control.TextField description;
    @FXML
    private javafx.scene.control.TextField whoCalled;
    @FXML
    private javafx.scene.control.TextField onShift;
    @FXML
    javafx.scene.control.TextField status;
    @FXML
    private DatePicker dayTime;

    @FXML
    private TableView<UserData> EventTable;

    @FXML
    private TableColumn<UserData, String> what_Incidentcolumn;


    @FXML
    private TableColumn<UserData, String> descriptioncolumn;

    @FXML
    private TableColumn<UserData, String> whoCalledColumn;

    @FXML
    private TableColumn<UserData, String> whoIsOnShiftcolumn;

    @FXML
    private TableColumn<UserData, String> dayTimecolumn;
    @FXML
    private TableColumn<UserData, String> statuscolumn1;

    private dbConnection dc;
    private ObservableList<UserData> data;

    private String sqlSelect = "SELECT  * FROM event_task";


    public void initialize(URL url, ResourceBundle rb) {
        this.dc = new dbConnection();

        EventTable.setEditable(true);
        what_Incidentcolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptioncolumn.setCellFactory(TextFieldTableCell.forTableColumn());
        whoCalledColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        whoIsOnShiftcolumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        statuscolumn1.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @FXML
    private void loadUserData(ActionEvent event) {
        String sql = "CREATE TABLE IF NOT EXISTS event_task (\n" +
                "    id            INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
                "    WHAT_INCIDENT VARCHAR (255),\n" +
                "    DESCRIPTION   TEXT,\n" +
                "    WHO_CALLED    VARCHAR,\n" +
                "    ON_SHIFT      VARCHAR,\n" +
                "    DAY_TIME      VARCHAR,\n" +
                "    STATUS      VARCHAR\n" +
                ");";


        try {

            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            Statement statement = conn.createStatement();
            statement.execute(sql);
            ResultSet rs = conn.createStatement().executeQuery(sqlSelect);

            while (rs.next()) {
                this.data.add(new UserData(rs.getInt("id"), rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)));

            }

        } catch (SQLException e) {
            System.err.println("Error in" + e);
        }
        this.what_Incidentcolumn.setCellValueFactory(new PropertyValueFactory<>("whatIncident"));
        this.descriptioncolumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("description"));
        this.whoCalledColumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("whoCalled"));
        this.whoIsOnShiftcolumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("onShift"));
        this.dayTimecolumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("dayTime"));
//        this.statuscolumn1.setCellValueFactory(new PropertyValueFactory<UserData, String>("status"));

        EventTable.setEditable(true);

        this.EventTable.setItems(null);
        this.EventTable.setItems(this.data);
    }

    @FXML
    private void addEventTask(ActionEvent event) {
        String sqlInsert = "INSERT INTO event_task(WHAT_INCIDENT,DESCRIPTION,WHO_CALLED,ON_SHIFT,DAY_TIME) VALUES(?,?,?,?,?)";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);
            stmt.setString(1, this.whatIncident.getText());
            stmt.setString(2, this.description.getText());
            stmt.setString(3, this.whoCalled.getText());
            stmt.setString(4, this.onShift.getText());
//            stmt.setString(6, this.status.getText());
            if (!this.dayTime.getEditor().getText().equals("")) {
                stmt.setString(5, this.dayTime.getEditor().getText());
            } else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                java.util.Date date = new Date();
                System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
                stmt.setString(5, dateFormat.format(date));
            }


            stmt.execute();
            conn.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        clearFields(event);
    }

    @FXML
    private void clearFields(ActionEvent event) {
        this.whatIncident.setText("");
        this.description.setText("");
        this.whoCalled.setText("");
        this.onShift.setText("");
        this.dayTime.setValue(null);
    }

    public void onEditWhatIncident(TableColumn.CellEditEvent<UserData, String> userDataStringCellEditEvent) {


        UserData data = EventTable.getSelectionModel().getSelectedItem();
        data.setWhatIncident(userDataStringCellEditEvent.getNewValue());
//        data.setDescription(userDataStringCellEditEvent.getNewValue());
//        data.setWhoCalled(userDataStringCellEditEvent.getNewValue());
//        data.setOnShift(userDataStringCellEditEvent.getNewValue());

        String sqlUpdate = "UPDATE event_task SET WHAT_INCIDENT = ?  where id = ?";

        //UPDATE event_task SET WHAT_INCIDENT = ?,DESCRIPTION = ?,WHO_CALLED = ?,ON_SHIFT = ?,DAY_TIME = ?  where id = ?
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
            stmt.setString(1, data.getWhatIncident());
//           stmt.setString(2, data.getDescription());
//           stmt.setString(3, data.getWhoCalled());
//           stmt.setString(4, data.getOnShift());
//           stmt.setString(5, data.getDayTime());
            stmt.setInt(2, data.getId());

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onEditDescription(TableColumn.CellEditEvent<UserData, String> userDataStringCellEditEvent) {
        UserData data = EventTable.getSelectionModel().getSelectedItem();

        data.setDescription(userDataStringCellEditEvent.getNewValue());

        String sqlUpdate = "UPDATE event_task SET DESCRIPTION = ?  where id = ?";

        //UPDATE event_task SET WHAT_INCIDENT = ?,DESCRIPTION = ?,WHO_CALLED = ?,ON_SHIFT = ?,DAY_TIME = ?  where id = ?
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
            stmt.setString(1, data.getDescription());
            stmt.setInt(2, data.getId());

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onEditWhoCalled(TableColumn.CellEditEvent<UserData, String> userDataStringCellEditEvent) {
        UserData data = EventTable.getSelectionModel().getSelectedItem();
        data.setWhoCalled(userDataStringCellEditEvent.getNewValue());

        String sqlUpdate = "UPDATE event_task SET WHO_CALLED = ?  where id = ?";

        //UPDATE event_task SET WHAT_INCIDENT = ?,DESCRIPTION = ?,WHO_CALLED = ?,ON_SHIFT = ?,DAY_TIME = ?  where id = ?
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
            stmt.setString(1, data.getWhoCalled());

            stmt.setInt(2, data.getId());

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onEditOnShift(TableColumn.CellEditEvent<UserData, String> userDataStringCellEditEvent) {
        UserData data = EventTable.getSelectionModel().getSelectedItem();
        data.setOnShift(userDataStringCellEditEvent.getNewValue());

        String sqlUpdate = "UPDATE event_task SET ON_SHIFT = ?  where id = ?";

        //UPDATE event_task SET WHAT_INCIDENT = ?,DESCRIPTION = ?,WHO_CALLED = ?,ON_SHIFT = ?,DAY_TIME = ?  where id = ?
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlUpdate);
            stmt.setString(1, data.getOnShift());
            stmt.setInt(2, data.getId());

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


