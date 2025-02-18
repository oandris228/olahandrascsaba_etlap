package org.example.etlap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AddEtelController {
    @FXML
    private Button AddFormButton;
    @FXML
    private MenuButton categoryFormMenuButton;
    @FXML
    private Spinner<Integer> priceFormSpinner;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private MenuItem menuItem1;
    @FXML
    private MenuItem menuItem3;
    @FXML
    private MenuItem menuItem2;

    private EtlapService etlap;

    public void initialize() {
        menuItem1.setOnAction(e -> categoryFormMenuButton.setText(menuItem1.getText()));
        menuItem2.setOnAction(e -> categoryFormMenuButton.setText(menuItem2.getText()));
        menuItem3.setOnAction(e -> categoryFormMenuButton.setText(menuItem3.getText()));
        priceFormSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 10000, 1000));
    }

    @FXML
    public void onAddFormButtonClick(ActionEvent actionEvent) {
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();
        String category = categoryFormMenuButton.getText();
        int price = priceFormSpinner.getValue();

        Etel etel = new Etel(null, name, category, price, description);

        try {
            etlap.create(etel);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Új étel felvétele");
            alert.setHeaderText(null);
            alert.setContentText("Étel hozzáadva az étlaphoz!");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();

        }
    }

    public void setEtlapService(EtlapService etlap) {
        this.etlap = etlap;
    }
}
