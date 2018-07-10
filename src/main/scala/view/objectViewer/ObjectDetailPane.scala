package view.objectViewer

import model.Plane
import model.geoobjects.{GeoLine, GeoObject, Point}
import signal.Signal

import scalafx.scene.layout.VBox


class ObjectDetailPane(selectedObject: Signal[GeoObject]) extends VBox {


  private val nameField = new LabeledTextInput("Naam")
  children.add(nameField)

  private val widthField = new LabeledTextInput("Width")
  children.add(widthField)

  private val heightField = new LabeledTextInput("Height")
  children.add(heightField)


  Signal {
    nameField.setText(selectedObject().name)

    selectedObject() match {
      case p: Point =>
        widthField.setText(p.x.toString)
        heightField.setText(p.y.toString)
      case _ => ()
    }
  }

  Signal {
    selectedObject() match {
      case p: Point =>
        Plane.updateObject(p.copy(
          name = nameField.text(),
          x = widthField.text().toDouble,
          y = heightField.text().toDouble))
      case l: GeoLine =>
        Plane.updateObject(
          l.copy(name = nameField.text())
        )
    }
  }

}
