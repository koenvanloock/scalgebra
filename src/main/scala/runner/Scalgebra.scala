package runner

import model.Plane
import view.objectViewer.ObjectDetailPane
import view.{DrawControls, GeoCanvas, GeoObjectBrowser}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane


object Scalgebra extends JFXApp {
  private val canvas = new GeoCanvas(800,600)
  private val drawControls = new DrawControls()
  private val treeView = new GeoObjectBrowser()

  stage = new PrimaryStage {
    scene = new Scene {
      root = new BorderPane {
        padding = Insets(25)
        center = canvas
        right = drawControls
        left = new BorderPane {
          top = treeView
          bottom = new ObjectDetailPane(Plane.selectedObject)
        }
      }
    }
  }
}