package model


object DrawModes {
  val POINT = DrawMode("point")
  val FIRST_OF_STRAIT = DrawMode("firstPointOfStrait")
  val SECOND_OF_STRAIT = DrawMode("secondPointOfStrait")
  val CIRCLE_CENTER = DrawMode("circleCenter")
  val CIRCLE_RADIUS = DrawMode("circleRadius")
}

case class DrawMode(name: String)
