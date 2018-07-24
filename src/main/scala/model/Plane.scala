package model

import model.geoobjects.{GeoObject, Point}
import signal.Var

object Plane {
  def find(selectedItem: String): Option[GeoObject] = objects().find(_.name == selectedItem)

  val selectedObject = new Var[Option[GeoObject]](None)
  val objects = new Var(Set[GeoObject]())
  val lastPoint = new Var(Point(0, 0, "", ""))
  val drawMode = new Var(DrawModes.POINT)

  private val nextCharString = new Var("A")
  private val nextLineString = new Var("a")

  def +=(geoObject: GeoObject): Unit = {
    val currentObjects = objects()
    objects.update(currentObjects + geoObject)

    geoObject match {
      case p: Point => lastPoint.update(p)
      case _ => ()
    }

  }

  def -=(geoObject: GeoObject): Unit = {
    val currentObjects = objects()
    objects.update(currentObjects.filterNot(_ == geoObject))
  }

  def pickAndAdvanceCharString(): String = {
    val currentCharString = nextCharString()
    if (currentCharString.last < 'Z') {
      nextCharString.update(currentCharString.init + (currentCharString.last + 1).toChar)
    } else {
      nextCharString.update(currentCharString.map(_ => 'A') + 'A')
    }
    currentCharString
  }

  def pickAndAdvanceLineCharString(): String = {
    val currentCharString = nextLineString()
    if (currentCharString.last < 'z') {
      nextLineString.update(currentCharString.init + (currentCharString.last + 1).toChar)
    } else {
      nextLineString.update(currentCharString.map(_ => 'a') + 'a')
    }
    currentCharString
  }

  def updateActive(name: String, x: Double, y: Double): Unit = {
    selectedObject() match {
      case Some(selected: GeoObject) =>
        val obj = objects()
        val geoObject = selected.update(name, x, y)
        selectedObject.update(Some(geoObject))
        objects.update(obj.filterNot(_ == selected) + geoObject)
      case _ => ()
    }
  }
}
