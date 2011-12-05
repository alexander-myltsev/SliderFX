/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bindingex2;

import javafx.application.Application;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author ericjbruno
 */
public class BindingEx2 extends Application {

    DoubleProperty taxRate = new SimpleDoubleProperty();
    DoubleProperty price = new SimpleDoubleProperty();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fluent API");
        Group root = new Group();
        Scene scene = new Scene(root, 300, 250);

        // Use binding to automatically calculate total price
        NumberBinding tax = taxRate.multiply(price);
        NumberBinding total = price.add(tax);
        /*
        NumberBinding total = new DoubleBinding() {
            {
                super.bind(taxRate, price);
            }

            @Override
            protected double computeValue() {
                return taxRate.get() * price.get();
            }
        };
        */
        StringBinding totalStr = taxRate.multiply(price).asString("Total: %.2f");


        // Price
        Label priceLbl = new Label("Price: ");
        TextField priceInput = new TextField();
        HBox priceBox = new HBox();
        priceBox.getChildren().addAll(priceLbl, priceInput);

        // Tax rate
        Label taxLbl = new Label("Tax rate: ");
        TextField taxInput = new TextField();
        HBox taxBox = new HBox();
        taxBox.getChildren().addAll(taxLbl, taxInput);

        // Total 
        final Label totalLbl = new Label();

        VBox vb = new VBox();
        vb.getChildren().addAll(priceBox, taxBox, totalLbl);

        // Add binding to update variables based on input
        priceInput.textProperty().addListener(
                new ChangeListener() {
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        Double val = new Double((String) newValue);
                        price.setValue(val);
                    }
                });

        taxInput.textProperty().addListener(
                new ChangeListener() {
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        try {
                            Double val = new Double((String) newValue);
                            taxRate.setValue(val);
                        } catch (Exception e) {
                        }
                    }
                });

        //total.addListener(
        totalStr.addListener(
                new ChangeListener() {
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        totalLbl.setText((String)newValue);
                    }
                });

        root.getChildren().add(vb);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
