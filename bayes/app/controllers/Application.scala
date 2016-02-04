package controllers

import java.io.InputStream

import alice.tuprolog.Theory
import com.google.gson.Gson
import play.api.Play
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.io.Source

class Application extends Controller {
  val gson = new Gson
  val path = "/home/marek/Dropbox/Studia/s9/Eksperty/medycyna_pracy_2.0.xdsl"


  val net = new Net(path)

  def applyTreatments(json: JsValue): Unit = {
    val appliedTreatments: Array[String] = gson.fromJson(json.toString(), classOf[Array[String]])

      net.setEvidence(appliedTreatments)
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def choose = Action { request =>
    net.clearAllEvidence()
    request.body.asJson.foreach(applyTreatments)

    net.updateBeliefs()

    Ok(gson.toJson(net.getCuredSymptoms))
  }

  object Application {
    val path = "/public/theory.txt"
    val stream: Option[InputStream] = Play.resourceAsStream(path)
    val text = stream.map(s => Source.fromInputStream(s).mkString)

    val mainTheory = text.map(new Theory(_))
  }

}
