package view

import java.util.UUID

import model.geoobjects.{GeoCircle, GeoLine, GeoObject, Point}
import model.{DrawModes, Plane}
import signal.Signal

import scalafx.Includes._
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

class GeoCanvas(width: Int, height: Int) extends Canvas(width, height) {
  private var shouldPreviewLine = false

  Signal(repaint(Plane.objects()))

  onMouseClicked = (event: MouseEvent) => drawPoint(event.x, event.y)
  onMouseMoved = (event: MouseEvent) => preview(event.x, event.y)


  private def paintObject(o: GeoObject)(implicit graphicsContext: GraphicsContext): Unit = {
    o match {
      case p: Point => paintPoint(p)
      case l: GeoLine => paintLine(l)
      case c: GeoCircle => paintCircle(c)
    }
  }

  def drawGrid() = {
    graphicsContext2D.stroke = Color.LightGray
    graphicsContext2D.setLineWidth(1)
    (0 to width).by(50).foreach(x => drawDashedLine(x, 0, x, height))
    (0 to height).by(50).foreach(y => drawDashedLine(0, y, width, y))
  }

  private def repaint(geoObjects: Set[GeoObject]): Unit = {
    graphicsContext2D.fill = Color.White
    graphicsContext2D.fillRect(0, 0, width, height)
    drawGrid()
    geoObjects.foreach(x => paintObject(x)(graphicsContext2D))
  }

  def drawCircleFromLastTo(x: Double, y: Double) = {
    graphicsContext2D.setStroke(Color.Black)
    graphicsContext2D.setLineWidth(2)
    val radius = Math.sqrt(Math.pow(y - Plane.lastPoint().y, 2) + Math.pow(x - Plane.lastPoint().x, 2))
    Plane += GeoCircle(Plane.pickAndAdvanceLineCharString(), Plane.lastPoint(), radius, UUID.randomUUID().toString)
    drawCircle(Plane.lastPoint().x, Plane.lastPoint().y, radius)
    Plane.drawMode.update(DrawModes.CIRCLE_CENTER)
  }

  private def drawPoint(x: Double, y: Double) = {

    val currentPoint = Point(x, y, Plane.pickAndAdvanceCharString(), UUID.randomUUID().toString)
    Plane.drawMode() match {
      case DrawModes.FIRST_OF_STRAIT =>
        shouldPreviewLine = true
        Plane.drawMode.update(DrawModes.SECOND_OF_STRAIT)
      case DrawModes.SECOND_OF_STRAIT =>
        Plane += GeoLine(Plane.lastPoint(), currentPoint, Set(Plane.lastPoint(), currentPoint), Plane.pickAndAdvanceLineCharString(), UUID.randomUUID().toString)
        shouldPreviewLine = false
        Plane.drawMode.update(DrawModes.FIRST_OF_STRAIT)
      case DrawModes.CIRCLE_CENTER => Plane.drawMode.update(DrawModes.CIRCLE_RADIUS)
      case DrawModes.CIRCLE_RADIUS => drawCircleFromLastTo(x, y)
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

  private def preview(x: Double, y: Double): Unit = {
    repaint(Plane.objects())

    Plane.drawMode() match {
      case DrawModes.SECOND_OF_STRAIT => previewLine(x, y)
      case DrawModes.CIRCLE_RADIUS => previewCircle(x, y)
      case _ => ()
    }
  }

  private def drawDashedLine(x1: Int, y1: Int, x2: Int, y2: Int): Unit = {
    graphicsContext2D.setLineDashes(25d, 10d)
    graphicsContext2D.strokeLine(x1, y1, x2, y2)
    graphicsContext2D.setLineDashes()
  }

  private def previewLine(x: Double, y: Double) = {
    graphicsContext2D.setStroke(Color.Black)
    graphicsContext2D.setLineWidth(2)
    val slope = (y - Plane.lastPoint().y) / (x - Plane.lastPoint().x)
    val lastShownVal = slope * graphicsContext2D.canvas.getWidth - x * slope + y
    graphicsContext2D.strokeLine(0, y - slope * x, graphicsContext2D.canvas.getWidth, lastShownVal)
  }

  private def previewCircle(x: Double, y: Double) = {
    graphicsContext2D.setStroke(Color.Black)
    graphicsContext2D.setLineWidth(2)
    val radius = Math.sqrt(Math.pow(y - Plane.lastPoint().y, 2) + Math.pow(x - Plane.lastPoint().x, 2))

    drawCircle(Plane.lastPoint().x, Plane.lastPoint().y, radius)
  }

  private def paintCircle(circle: GeoCircle) = {
    graphicsContext2D.setStroke(Color.Black)
    graphicsContext2D.setLineWidth(2)
    graphicsContext2D.strokeArc(circle.center.x - circle.radius, circle.center.y - circle.radius, circle.radius * 2, circle.radius * 2, 0, 360, ArcType.Open)
  }

  private def drawCircle(x: Double, y: Double, radius: Double) = {
    graphicsContext2D.setStroke(Color.Black)
    graphicsContext2D.setLineWidth(2)
    graphicsContext2D.strokeArc(x - radius, y - radius, radius * 2, radius * 2, 0, 360, ArcType.Open)
  }
}

