package model.geoobjects

case class GeoLine(point1: Point, point2: Point, pointsOnLine: Set[Point], name: String, id: String) extends GeoObject {
  override def update(newName: String, newX: Double, newY: Double): GeoObject = this.copy(name = newName)
}
