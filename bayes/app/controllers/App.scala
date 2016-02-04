package controllers

import smile.Network

object App {
  def main(args: Array[String]) {
    val path = "/home/marek/Dropbox/Studia/s9/Eksperty/medycyna_pracy_2.0.xdsl"
    val network = new Network()
    network.readFile(path)
    val symptoms = network.getAllNodeIds
      .filter(_.startsWith("Leczenie"))
      .map(network.getNode)

    network.updateBeliefs()

    def getSymptoms = {
      symptoms
        .map(id => (network.getNodeName(id), network.getNodeValue(id)(1)))
        .sortBy(_._2)
        .reverse
    }

    println(getSymptoms.mkString("\n"))
  }

}
