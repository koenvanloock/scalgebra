package view

import javafx.scene.control.Hyperlink

import model.{DrawModes, Plane}

import scalafx.scene.layout.VBox

class DrawControls extends VBox {

  private val pointLink = new Hyperlink("Point")
  pointLink.setOnMouseClicked(event => Plane.drawMode.update(DrawModes.POINT))

  private val lineLink = new Hyperlink("Line")
  lineLink.setOnMouseClicked(event => Plane.drawMode.update(DrawModes.FIRST_OF_STRAIT))

  children.add(pointLink)
  children.add(lineLink)

}
