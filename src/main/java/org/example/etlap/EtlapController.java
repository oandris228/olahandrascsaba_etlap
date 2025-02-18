package org.example.etlap;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class EtlapController {

    @FXML
    private TableColumn nameCol;
    @FXML
    private TableColumn categoryCol;
    @FXML
    private Spinner<Integer> priceSpinner;
    @FXML
    private Spinner<Integer> percentageSpinner;
    @FXML
    private TableColumn priceCol;


    private EtlapService etlap;
    @FXML
    private TableView<Etel> etelek;
    @FXML
    private TextArea leirasTextArea;

    private ObservableList<Etel> selectedItem;

    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        selectedItem = etelek.getSelectionModel().getSelectedItems();
        percentageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 5));
        priceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 3000, 50));
        try {
            etlap = new EtlapService();
            listEtelek();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Couldn't connect to database");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
        selectedItem.addListener((ListChangeListener<Etel>) c -> {
            leirasTextArea.clear();
            leirasTextArea.setText(selectedItem.getFirst().getDescription());
        });

    }

    private void listEtelek() throws SQLException {
        etelek.getItems().clear();
        etelek.getItems().addAll(etlap.getAll());
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add_etel-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            AddEtelController controller = fxmlLoader.getController();
            stage.setTitle("Add new Phone");
            controller.setEtlapService(etlap);
            stage.setScene(scene);
            stage.setOnHidden(event -> {
                try {
                    listEtelek();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onDeleteButtonClick(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Megerősítés");
        alert.setHeaderText("Biztosan ki akarod törölni az ételt?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            etlap.delete(selectedItem.getFirst());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Siker");
            alert.setHeaderText(null);
            alert.setContentText("Sikeres törlés");
            alert.showAndWait();
            listEtelek();
        }
    }

    @FXML
    public void onPercentageIncreaseButtonClick(ActionEvent actionEvent) throws SQLException {

        //

        if (!selectedItem.isEmpty()) {
            selectedItem.getFirst().setPrice(selectedItem.getFirst().getPrice()+getExtraValue(selectedItem.getFirst().getPrice(), percentageSpinner.getValue()));
            System.out.printf(String.valueOf(selectedItem.getFirst().getPrice()) + " ");
            try {
                etlap.modifyPrice(selectedItem.getFirst());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            etelek.getItems().forEach(item -> {
                item.setPrice(item.getPrice()+getExtraValue(item.getPrice(), percentageSpinner.getValue()));
                System.out.printf(String.valueOf(item.getPrice()) + " ");
                try {
                    etlap.modifyPrice(item);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } );
        }
        listEtelek();
    }

    @FXML
    public void onPriceIncreaseButtonClick(ActionEvent actionEvent) throws SQLException {
        if (!selectedItem.isEmpty()) {
            selectedItem.getFirst().setPrice(selectedItem.getFirst().getPrice()+priceSpinner.getValue());
            etlap.modifyPrice(selectedItem.getFirst());
        } else {
            etelek.getItems().forEach(item -> {
                item.setPrice(item.getPrice()+priceSpinner.getValue());
                try {
                    etlap.modifyPrice(item);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } );
        }
        listEtelek();
    }

    public static int getExtraValue(int value, double percentage) {
        return (int) (value * (percentage / 100));
    }

}