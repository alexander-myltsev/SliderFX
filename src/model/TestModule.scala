package model

object TestModule {
  def main(args: Array[String]) = {
    args foreach (x => println("arg: " + x))
  }
}