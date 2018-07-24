package view.objectViewer

import signal.Var

import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.HBox
import scalafx.Includes._

class LabeledTextInput(label: String) extends HBox{

  val text = Var("")


  val textInput = new TextField()

  textInput.onKeyTyped = _ => {
      text.update(textInput.text.value)
  }

  children.add(new Label(label))
  children.add(textInput)

  def setText(value: String) = {
    textInput.text = value
    text.update(value)
  }
}
