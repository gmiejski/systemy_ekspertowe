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
    """
      |jezyk(ruby) :- is(simple),
      |               typed(dynamically),
      |               true(has, duckTyping),
      |               true(has, lambdas),
      |               true(has, typeInference),
      |               false(has, patternMatching),
      |               false(has, lazyEvaluation),
      |               false(has, actors),
      |               paradigm(object-oriented),
      |               paradigm(metaprogramming),
      |               (
      |                 paradigm(imperative);
      |                 paradigm(functional)
      |               ),
      |               interpreted.
      |
      |jezyk(c) :-      is(simple),
      |                 typed(statically),
      |                 false(has, patternMatching),
      |                 false(has, lambdas),
      |                 false(has, typeInference),
      |                 false(has, duckTyping),
      |                 false(has, lazyEvaluation),
      |                 paradigm(imperative),
      |                 paradigm(metaprogramming),
      |                 usable_from_ide(visualStudio),
      |                 run_directly.
      |
      |jezyk(scala) :- is(complicated),
      |                typed(statically),
      |                true(has, patternMatching),
      |                true(has, lambdas),
      |                true(has, typeInference),
      |                false(has, duckTyping),
      |                paradigm(object-oriented),
      |                paradigm(lazy),
      |                paradigm(concurrent),
      |                paradigm(metaprogramming),
      |                (
      |                   paradigm(imperative);
      |                   paradigm(functional)
      |                ),
      |                (
      |                   usable_from_ide(intellij);
      |                   usable_from_ide(eclipse)
      |                ),
      |                write_once_ruin_everywhere.
      |
      |jezyk(csharp) :- is(simple),
      |                 typed(statically),
      |                 false(has, patternMatching),
      |                 true(has, lambdas),
      |                 true(has, typeInference),
      |                 false(has, duckTyping),
      |                 false(has, actors),
      |                 false(has, lazyEvaluation),
      |                 false(has, macros),
      |                 paradigm(object-oriented),
      |                 paradigm(imperative),
      |                 usable_from_ide(visualStudio),
      |                 write_once_ruin_everywhere.
      |
      |jezyk(java) :-  is(simple),
      |                typed(statically),
      |                false(has, patternMatching),
      |                true(has, lambdas),
      |                false(has, typeInference),
      |                false(has, duckTyping),
      |                false(has, actors),
      |                false(has, lazyEvaluation),
      |                false(has, macros),
      |                paradigm(object-oriented),
      |                paradigm(imperative),
      |                (
      |                   usable_from_ide(intellij);
      |                   usable_from_ide(eclipse)
      |                ),
      |                write_once_ruin_everywhere.
      |
      |jezyk(haskell)  :-  is(complicated),
      |                    typed(statically),
      |                    true(has, patternMatching),
      |                    true(has, lambdas),
      |                    true(has, typeInference),
      |                    true(has, duckTyping),
      |                    false(has, actors),
      |                    false(has, macros),
      |                    paradigm(functional),
      |                    paradigm(lazy),
      |                    usable_from_ide(intellij),
      |                    run_directly.
      |
      |jezyk(python) :-    is(simple),
      |                    typed(dynamically),
      |                    false(has, patternMatching),
      |                    false(has, typeInference),
      |                    true(has, lambdas),
      |                    true(has, duckTyping),
      |                    false(has, actors),
      |                    false(has, lazyEvaluation),
      |                    false(has, macros),
      |                    paradigm(object-oriented),
      |                    (
      |                       paradigm(imperative);
      |                       paradigm(functional)
      |                    ),
      |                    usable_from_ide(intellij),
      |                    interpreted.
      |
      |jezyk(lisp) :-  is(complicated),
      |                typed(dynamically),
      |                true(has, lambdas),
      |                true(has, patternMatching),
      |                false(has, typeInheritance),
      |                false(has, lazyEvaluation),
      |                false(has, actors),
      |                paradigm(object-oriented),
      |                paradigm(metaprogramming),
      |                paradigm(functional),
      |                write_once_ruin_everywhere.
      |
      |jezyk(erlang) :- is(complicated),
      |                 typed(dynamically),
      |                 true(has, patternMatching),
      |                 true(has, lambdas),
      |                 true(has, typeInference),
      |                 true(has, duckTyping),
      |                 false(has, lazyEvaluation),
      |                 false(has, macros),
      |                 paradigm(functional),
      |                 paradigm(object-oriented),
      |                 paradigm(concurrent),
      |                 usable_from_ide(intellij),
      |                 write_once_ruin_everywhere.
      |
      |
      |
      |is(simple) :- true(has, simpleSyntax), true(has, clearDocumentation).
      |is(complicated) :-  false(has, simpleSyntax).
      |is(complicated) :-  false(has, clearDocumentation).
      |
      |typed(statically) :-  true(has, types_resolved_during_compilation).
      |typed(dynamically) :- false(has, types_resolved_during_compilation).
      |
      |usable_from_ide(X) :- true(mozna_uzywac_z, X).
      |
      |write_once_ruin_everywhere :- true(has, virtualMachine).
      |
      |interpreted :- true(has, interpreter).
      |
      |run_directly :- false(has, virtualMachine),
      |                false(has, interpreter).
      |
      |paradigm(object-oriented) :- true(has, typeInheritance),
      |                             true(has, encapsulation).
      |
      |paradigm(imperative) :- true(has, mutation),
      |                        true(has, sideEffects).
      |
      |paradigm(functional) :- false(has, sideEffects),
      |                        false(has, mutation).
      |
      |paradigm(concurrent) :- true(has, actors).
      |
      |paradigm(metaprogramming) :- true(has, macros).
      |
      |paradigm(lazy) :- true(has, lazyEvaluation).
      |
      |true(X,Y) :- (
      |                     yes(X, Y);
      |                     neutral(X, Y)
      |                  ), !.
      |
      |false(X,Y) :- (
      |                     no(X,Y);
      |                     neutral(X, Y)
      |                  ).
      |
      |
      | """.stripMargin

  val mainTheory = new Theory(mainTheorySource)
}
