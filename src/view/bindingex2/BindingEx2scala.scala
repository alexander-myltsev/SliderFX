package bindingex2

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.{TextField, Label, TextArea}
import scalafx.beans.property.{StringProperty, DoubleProperty}
import javafx.beans.property.SimpleStringProperty
import javafx.beans.binding.DoubleBinding

object BindingEx2scala extends JFXApp {
  stage = new Stage {
    title = "Fluent API"

    val taxRate: DoubleProperty = new javafx.beans.property.SimpleDoubleProperty(10)
    val price: DoubleProperty = new javafx.beans.property.SimpleDoubleProperty(20)
    val test: StringProperty = new javafx.beans.property.SimpleStringProperty("testing")


    def someFunStr(s: String) = "My string: " + s

    def someFunDouble(i: Double) = i - 50.0

    //def someFunDouble(i: DoubleBinding) = i - 50.0

    def lift(f: (Double) => Double) = {
      (x: DoubleBinding) => new DoubleBinding {
        super.bind(x)

        def computeValue(): Double = f(x.get)
      }
    }

    scene = new Scene {
      content = new VBox {
        val priceTF = new TextField {
          //prefWidth <== scene.width - 50
          //prefWidth <== (lift(someFunDouble))(scene.width)
          prefWidth <== (lift(someFunDouble))(scene.width)
        }

        val taxRateTF = new TextField {
        }

        content = List(
          new HBox {
            content = List(
              new Label {
                text = "Price: "
              },
              priceTF
            )
          },
          new HBox {
            content = List(
              new Label {
                text = "Tax rate: "
              },
              taxRateTF
            )
          },
          new Label {
            //text <== priceTF.text + 123
            //text <== scene.width + " <-- width"
            //text <== scene.width.asString() + " <-- width"
            //text <== "width: " + scene.width.asString()
          }
        )
      }
    }
  }
}