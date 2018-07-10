package model.geoobjects

case class GeoLine(point1: Point, point2: Point, pointsOnLine: Set[Point], name: String, id: String) extends GeoObject
