package model

import model.geoobjects.{GeoObject, Point}
import signal.{Signal, Var}

object Plane {
  def find(selectedItem: String): Option[GeoObject] = objects().find(_.name == selectedItem)
  val showGrid = new Var(true)
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

  def pickAndAdvanceCharString(): String = getNextName(nextCharString, 'A', 'Z')

  def pickAndAdvanceLineCharString(): String = getNextName(nextLineString, 'a', 'z')

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

  private def getNextName(signal: Var[String], firstChar: Char, lastChar: Char): String = {
    val currentLineString = signal()
    if (currentLineString.last < lastChar) {
      signal.update(currentLineString.init + (currentLineString.last + 1).toChar)
    } else {
      signal.update(currentLineString.map(_ => firstChar) + firstChar)
    }
    if(objects().map(_.name).contains(currentLineString)) getNextName(signal, firstChar, lastChar) else currentLineString
  }
}
