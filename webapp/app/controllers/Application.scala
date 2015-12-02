package controllers

import alice.tuprolog.{Prolog, SolveInfo, Theory}
import com.google.gson.Gson
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc._

class Application extends Controller {
  val prolog = new Prolog()
  val gson = new Gson

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def toPrologTheory(json: JsValue): Theory = {
    val strings: Array[String] = gson.fromJson(json.toString(), classOf[Array[String]])
    val theory = strings.mkString("\n")
    Logger.info("theory: " + theory)
    Logger.info(s"json=$json")
    new Theory(theory)
  }

  def choose = Action { request =>
    def buildResult(info: SolveInfo, acc: List[String]): Array[String] =
      if (!info.hasOpenAlternatives) {
        acc.toSet.toArray
      } else {
        if (info.isSuccess) {
          buildResult(prolog.solveNext(), info.getTerm("X").toString +: acc)
        } else {
          acc.toArray
        }
      }

    val maybeTheory: Option[Theory] = request.body.asJson.map(toPrologTheory)

    maybeTheory.map { t =>
      prolog.clearTheory()
      prolog.addTheory(Application.mainTheory)
      prolog.addTheory(t)

      val info: SolveInfo = prolog.solve("jezyk(X).")
      Logger.info("success: " + info.isSuccess)
      Logger.info("hasAlts: " + info.hasOpenAlternatives)
      Logger.info(info + "")

      val result = buildResult(info, Nil)
      Ok(gson.toJson(result))
    }.getOrElse(Ok)
  }
}

object Application {
  private[this] val mainTheorySource =
    """jezyk(scala) :- jest(skomplikowany),
      |                typowany(statycznie),
      |                pozytywne(ma, patternMatching),
      |                pozytywne(ma, lambdas),
      |                pozytywne(ma, typeInference),
      |                negatywne(ma, duckTyping),
      |                ma_paradygmat(obiektowy),
      |                (
      |                   ma_paradygmat(imperatywny);
      |                   ma_paradygmat(funkcyjny)
      |                ).
      |
      |jezyk(csharp) :- jest(prosty),
      |                 typowany(statycznie),
      |                 negatywne(ma, patternMatching),
      |                 pozytywne(ma, lambdas),
      |                 pozytywne(ma, typeInference),
      |                 negatywne(ma, duckTyping),
      |                 ma_paradygmat(obiektowy),
      |                 ma_paradygmat(imperatywny).
      |
      |jezyk(java) :-  jest(prosty),
      |                typowany(statycznie),
      |                negatywne(ma, patternMatching),
      |                pozytywne(ma, lambdas),
      |                negatywne(ma, typeInference),
      |                negatywne(ma, duckTyping),
      |                ma_paradygmat(obiektowy),
      |                ma_paradygmat(imperatywny).
      |
      |jezyk(haskell)  :-  jest(skomplikowany),
      |                    typowany(statycznie),
      |                    pozytywne(ma, patternMatching),
      |                    pozytywne(ma, lambdas),
      |                    pozytywne(ma, typeInference),
      |                    pozytywne(ma, duckTyping),
      |                    ma_paradygmat(funkcyjny).
      |
      |jezyk(python) :-    jest(prosty),
      |                    typowany(dynamicznie),
      |                    negatywne(ma, patternMatching),
      |                    pozytywne(ma, lambdas),
      |                    negatywne(ma, typeInference),
      |                    pozytywne(ma, duckTyping),
      |                    ma_paradygmat(obiektowy),
      |                    (
      |                       ma_paradygmat(imperatywny);
      |                       ma_paradygmat(funkcyjny)
      |                    ).
      |
      |jezyk(lisp) :-  jest(skomplikowany),
      |                typowany(dynamicznie),
      |                negatywne(ma, patternMatching),
      |                pozytywne(ma, lambdas),
      |                pozytywne(ma, patternMatching),
      |                negatywne(ma, typeInheritance),
      |                ma_paradygmat(funkcyjny).
      |
      |jezyk(erlang) :- jest(skomplikowany),
      |                 typowany(dynamicznie),
      |                 pozytywne(ma, patternMatching),
      |                 pozytywne(ma, lambdas),
      |                 pozytywne(ma, typeInference),
      |                 pozytywne(ma, duckTyping),
      |                 ma_paradygmat(funkcyjny),
      |                 ma_paradygmat(obiektowy).
      |
      |
      |jest(prosty) :- pozytywne(ma, simpleSyntax), pozytywne(ma, clearDocumentation).
      |jest(skomplikowany) :-  negatywne(ma, simpleSyntax).
      |jest(skomplikowany) :-  negatywne(ma, clearDocumentation).
      |
      |typowany(statycznie) :- pozytywne(ma, typy_rozwiazywane_podczas_kompilacji).
      |typowany(dynamicznie) :- negatywne(ma, typy_rozwiazywane_podczas_kompilacji).
      |
      |ma_framework_do(big_data) :- (
      |                                pozytywne(ma, interfejs_do_hadoopa);
      |                                pozytywne(ma, implementacje_sparka)
      |                             ).
      |
      |
      |ma_framework_do(tworzenie_serwisow) :- (
      |                                        ma_framework(play);
      |                                        ma_framework(spring);
      |                                        ma_framework(django);
      |                                        ma_framework(dotnet)
      |                                       ).
      |
      |ma_framework(X) :- pozytywne(ma_framework, X).
      |
      |podobny_do(X) :- pozytywne(podobny_do, X).
      |
      |ma_paradygmat(obiektowy) :- pozytywne(ma, typeInheritance),
      |                            pozytywne(ma, encapsulation).
      |
      |ma_paradygmat(imperatywny) :-   pozytywne(ma, mutation),
      |                                pozytywne(ma, sideEffects).
      |
      |ma_paradygmat(funkcyjny) :- negatywne(ma, sideEffects),
      |                            negatywne(ma, mutation).
      |
      |
      |
      |pozytywne(X,Y) :- (
      |                     tak(X, Y);
      |                     obojetne(X, Y)
      |                  ), !.
      |
      |negatywne(X,Y) :- (
      |                     nie(X,Y);
      |                     obojetne(X, Y)
      |                  ).
      |
      |
      | """.stripMargin

  val mainTheory = new Theory(mainTheorySource)
}
