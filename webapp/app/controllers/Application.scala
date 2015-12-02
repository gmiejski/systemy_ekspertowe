package controllers

import java.io.InputStream

import alice.tuprolog.{Prolog, SolveInfo, Theory}
import com.google.gson.Gson
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.{Logger, Play}

import scala.io.Source

class Application extends Controller {
  val prolog = new Prolog()
  val gson = new Gson

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def toPrologTheory(json: JsValue): Theory = {
    val strings: Array[String] = gson.fromJson(json.toString(), classOf[Array[String]])
    val theory = strings.map(s => if (s.endsWith(".")) s else s + ".").mkString("\n")
    Logger.info("theory: " + theory)
    Logger.info(s"json=$json")
    new Theory(theory)
  }

  def choose = Action { request =>
    def buildResult(info: SolveInfo, acc: List[String]): Array[String] =
      if (!info.hasOpenAlternatives) {
        acc.toSet.toList.sorted.toArray
      } else {
        if (info.isSuccess) {
          buildResult(prolog.solveNext(), info.getTerm("X").toString +: acc)
        } else {
          acc.sorted.toArray
        }
      }

    val maybeTheory: Option[Theory] = request.body.asJson.map(toPrologTheory)

    maybeTheory.map { t =>
      prolog.clearTheory()
      val mainTheory = Application.mainTheory
        .get
      prolog.addTheory(mainTheory)
      prolog.addTheory(t)

      val info: SolveInfo = prolog.solve("language(X).")
      Logger.info("success: " + info.isSuccess)
      Logger.info("hasAlts: " + info.hasOpenAlternatives)
      Logger.info(info + "")

      val result = buildResult(info, Nil)
      Ok(gson.toJson(result))
    }.getOrElse(Ok)
  }
}

object Application {
  val path = "/public/theory.txt"
  val stream: Option[InputStream] = Play.resourceAsStream(path)
  val text = stream.map(s => Source.fromInputStream(s).mkString)

  val mainTheory = text.map(new Theory(_))
}
