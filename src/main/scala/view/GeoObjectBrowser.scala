package view

import model.Plane
import model.geoobjects.{GeoCircle, GeoLine, GeoObject, Point}
import signal.Signal

import scalafx.scene.control.{TreeItem, TreeView}
import scalafx.scene.layout.StackPane

class GeoObjectBrowser extends StackPane {
  private var prevPoints = List[GeoObject]()

  private var points = Set.empty[String]
  private var lines = Set.empty[String]
  private var shapes = Set.empty[String]


  val root = new TreeItem[String]("objects")
  val pointRoot = new TreeItem[String]("points")
  val lineRoot = new TreeItem("lines")
  val shapeRoot = new TreeItem("shapes")

  val pointNode = new TreeItem[String](pointRoot)
  val lineNode = new TreeItem[String](lineRoot)
  val shapeNode = new TreeItem[String](shapeRoot)

  private val tree = new TreeView(root)
  tree.selectionModel().selectedItemProperty().addListener((_, _, newValue) => {
    val selectedItem = newValue.getValue
    Plane.find(selectedItem) match {
      case Some(item) => Plane.selectedObject.update(Some(item))
      case _ => ()
    }
  })

  children.add(tree)
  root.children.add(pointNode)
  root.children.add(lineNode)
  root.children.add(shapeNode)

  Signal {
    val currentObjects = Plane.objects()
      .toList
      .sortBy(_.name)
    currentObjects
      .foreach {
        case p: Point => points = addToCorrectContext(points, p, pointNode)
        case l: GeoLine => lines = addToCorrectContext(lines, l, lineNode)
        case c: GeoCircle => shapes = addToCorrectContext(shapes, c, shapeNode)
        case _ => ()
      }
    deleteMissingObjects(prevPoints, currentObjects)
    prevPoints = currentObjects
  }

  private def addToCorrectContext(context: Set[String], objectToAdd: GeoObject, root: TreeItem[String]): Set[String] = {
    if (!context.contains(objectToAdd.name)) {
      root.children.add(new TreeItem(objectToAdd.name))
      context + objectToAdd.name
    } else context
  }

  private def deleteMissingObjects(prevPoints: List[GeoObject], currentObjects: List[GeoObject]): Unit = {
    val missingObjects = prevPoints.filterNot(currentObjects.contains)
    missingObjects.foreach {
      case p: Point => pointRoot.children.zipWithIndex.map { case (item, index) => if (p.name == item.getValue) pointRoot.children.remove(index) }
      case l: GeoLine => lineNode.children.zipWithIndex.map { case (item, index) => if (l.name == item.getValue) lineRoot.children.remove(index) }
      case s: GeoObject => shapeNode.children.zipWithIndex.map { case (item, index) => if (s.name == item.getValue) shapeRoot.children.remove(index) }
    }
  }

}
