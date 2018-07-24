package model.geoobjects

case class Point(x: Double, y: Double, name: String, id: String) extends GeoObject {
  override def update(newName: String, newX: Double, newY: Double): GeoObject = {
    this.copy(name = newName, x = newX, y = newY)
  }
}
