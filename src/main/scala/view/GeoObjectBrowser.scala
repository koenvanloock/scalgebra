package view

import model.Plane
import model.geoobjects.{GeoLine, GeoObject, Point}
import signal.{NoSignal, Signal, Var}

import scalafx.scene.control.{TreeItem, TreeView}
import scalafx.scene.layout.StackPane

class GeoObjectBrowser extends StackPane {

  private var points = Set.empty[String]
  private var lines = Set.empty[String]


  val root = new TreeItem[String]("objects")
  val pointRoot = new TreeItem[String]("points")
  val lineRoot = new TreeItem("lines")

  val pointNode = new TreeItem[String](pointRoot)
  val lineNode = new TreeItem[String](lineRoot)

  private val tree = new TreeView(root)
  tree.selectionModel().selectedItemProperty().addListener((obs, old, newValue) => {
    val selectedItem = newValue.getValue
    Plane.find(selectedItem) match {
      case Some(item) => Plane.selectedObject.update(Some(item))
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

  private def addToCorrectContext(context: Set[String], objectToAdd: GeoObject, root: TreeItem[String]): Set[String] = {
    if (!context.contains(objectToAdd.name)) {
      root.children.add(new TreeItem(objectToAdd.name))
      context + objectToAdd.name
    } else context
  }

}
