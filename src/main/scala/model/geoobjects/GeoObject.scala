package model.geoobjects

trait GeoObject {
  def name: String
  def id: String
  def update(newName: String, newX: Double, newY: Double): GeoObject
}
