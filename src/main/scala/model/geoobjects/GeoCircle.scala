package model.geoobjects

case class GeoCircle(name: String, center: Point, radius: Double, id: String) extends GeoObject {

  override def update(newName: String, newX: Double, newY: Double): GeoObject = this.copy(name = newName, center = center.copy(x = newX, y = newY))
}
