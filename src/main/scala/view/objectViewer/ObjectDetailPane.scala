package view.objectViewer

import model.Plane
import model.geoobjects.{GeoLine, GeoObject, Point}
import signal.Signal

import scala.util.Try
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.Includes._


class ObjectDetailPane(selectedObject: Signal[Option[GeoObject]]) extends VBox {


  private val nameField = new LabeledTextInput("Naam")
  children.add(nameField)

  private val widthField = new LabeledTextInput("Width")
  children.add(widthField)

  private val heightField = new LabeledTextInput("Height")
  children.add(heightField)

  private val button = new Button("save")
  children.add(button)


  Signal {
    selectedObject() match {
      case Some(p: Point) =>
        nameField.setText(p.name)
        widthField.setText(p.x.toString)
        heightField.setText(p.y.toString)
      case Some(l: GeoLine) =>
        nameField.setText(l.name)
      case _ =>

    }
  }

 button.onMouseClicked = (event) => {
   Plane.updateActive(
     nameField.text(),
     Try(widthField.text().toDouble).toOption.getOrElse(0),
     Try(heightField.text().toDouble).toOption.getOrElse(0)
   )
 }

}
