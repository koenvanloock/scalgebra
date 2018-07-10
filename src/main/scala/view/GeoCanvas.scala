package view

import java.util.UUID

import model.{DrawMode, DrawModes, Plane}
import model.geoobjects.{GeoLine, GeoObject, Point}
import signal.Signal

import scalafx.Includes._
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

class GeoCanvas(width: Int, height: Int) extends Canvas(width, height) {
  private var shouldPreviewLine = false

  Signal(repaint(Plane.objects()))

  onMouseClicked = (event: MouseEvent) => drawPoint(event.x, event.y)
  onMouseMoved = (event: MouseEvent) => previewLine(event.x, event.y)



  private def paintObject(o:GeoObject)(implicit graphicsContext: GraphicsContext): Unit = {
    o match {
      case p: Point => paintPoint(p)
      case l: GeoLine => paintLine(l)
    }
  }

  private def repaint(geoObjects: Set[GeoObject]): Unit =  {
    graphicsContext2D.clearRect(0,0, width, height)
    geoObjects.foreach(x => paintObject(x)(graphicsContext2D))

  }

  private def drawPoint(x: Double, y: Double) = {

    val currentPoint = Point(x, y, Plane.pickAndAdvanceCharString(), UUID.randomUUID().toString)
    Plane.drawMode() match {
      case firstPointOnStrait: DrawMode if firstPointOnStrait == DrawModes.FIRST_OF_STRAIT  =>
        shouldPreviewLine = true
        Plane.drawMode.update(DrawModes.SECOND_OF_STRAIT)
      case firstPointOnStrait: DrawMode if firstPointOnStrait == DrawModes.SECOND_OF_STRAIT =>
        Plane += GeoLine(Plane.lastPoint(), currentPoint, Set(Plane.lastPoint(), currentPoint),Plane.pickAndAdvanceLineCharString(), UUID.randomUUID().toString)
        shouldPreviewLine = false
        Plane.drawMode.update(DrawModes.FIRST_OF_STRAIT)
      case _ => ()
    }

    Plane += currentPoint
  }


  private def paintLine(line: GeoLine)(implicit gc: GraphicsContext): Unit = {
    gc.setStroke(Color.Black)
    gc.setLineWidth(1)
    val slope = (line.point2.y - line.point1.y) / (line.point2.x - line.point1.x)
    val lastShownVal = slope * gc.canvas.getWidth - line.point1.x * slope + line.point1.y
    val firstShownVal = line.point1.y - slope * line.point1.x

    gc.fillText(line.name, 5, firstShownVal - 5)
    gc.strokeLine(0, firstShownVal, gc.canvas.getWidth, lastShownVal)
  }

  private def paintPoint(point: Point)(implicit gc: GraphicsContext) = {
    gc.setFill(Color.Blue)
    gc.setLineWidth(2)
    gc.fillOval(point.x - 5, point.y - 5, 10, 10)
    gc.fillText(point.name, point.x + 10, point.y)
  }

  private def previewLine(x: Double, y: Double): Unit = {
    repaint(Plane.objects())

    if(Plane.drawMode() == DrawModes.SECOND_OF_STRAIT) {
      graphicsContext2D.setStroke(Color.Black)
      graphicsContext2D.setLineWidth(2)
      val slope = (y - Plane.lastPoint().y) / (x - Plane.lastPoint().x)
      val lastShownVal = slope * graphicsContext2D.canvas.getWidth - x * slope + y
      graphicsContext2D.strokeLine(0, y - slope * x, graphicsContext2D.canvas.getWidth, lastShownVal)
    }
  }
}
