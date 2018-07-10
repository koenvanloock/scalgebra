package view

import model.Plane
import model.geoobjects.{GeoLine, GeoObject, Point}
import signal.{NoSignal, Signal, Var}

import scalafx.scene.control.{TreeItem, TreeView}
import scalafx.scene.layout.StackPane

class GeoObjectBrowser extends StackPane {
  val selectedObject = new Var[GeoObject](Point(0,0,"O", "O"))

  private var objectsMap = Map[String, String]()
  private var points = Set.empty[Point]
  private var lines = Set.empty[GeoLine]


  val root = new TreeItem[String]("objects")
  val pointRoot = new TreeItem[String]("points")
  val lineRoot = new TreeItem("lines")

  val pointNode = new TreeItem[String](pointRoot)
  val lineNode = new TreeItem[String](lineRoot)

  private val tree = new TreeView(root)
  tree.selectionModel().selectedItemProperty().addListener((obs, old, newValue) => {
    val selectedItem = newValue.getValue
    Plane.find(selectedItem) match {
      case Some(item) => selectedObject.update(item)
      case _ => ()
    }
  })

  children.add(tree)
  root.children.add(pointNode)
  root.children.add(lineNode)



  Signal {
    Plane.objects()
      .toList
      .sortBy(_.name)
      .foreach {
        case p: Point => points = addToCorrectContext(points, p, pointNode)
        case l: GeoLine => lines = addToCorrectContext(lines, l, lineNode)
      }
  }

  private def addToCorrectContext[T <: GeoObject](context: Set[T], objectToAdd: T, root: TreeItem[String]): Set[T] = {
    if (!context.contains(objectToAdd)) {
      root.children.add(new TreeItem(objectToAdd.name))
      objectsMap += (objectToAdd.name -> objectToAdd.id)
      context + objectToAdd
    } else context
  }

}
