package org.jetbrains.plugins.scala.lang.parser.parsing.expressions

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder

/**
 * @author Alexander Podkhalyuzin
 *  Date: 13.02.2008
 */

/*
 * SelfInvocation ::= 'this' ArgumentExprs {ArgumentExprs}
 */
object SelfInvocation extends SelfInvocation {
  override protected def argumentExprs = ArgumentExprs
}

trait SelfInvocation {
  protected def argumentExprs: ArgumentExprs

  def parse(builder: ScalaPsiBuilder): Boolean = {
    val selfMarker = builder.mark
    builder.getTokenType match {
      case ScalaTokenTypes.kTHIS =>
        builder.advanceLexer() //Ate this
      case _ =>
        //error moved to ScalaAnnotator to differentiate with compiled files
        selfMarker.drop()
        return true
    }
    if (!argumentExprs.parse(builder)) {
      selfMarker.done(ScalaElementTypes.SELF_INVOCATION)
      return true
    }
    while (!builder.newlineBeforeCurrentToken && argumentExprs.parse(builder)) {}
    selfMarker.done(ScalaElementTypes.SELF_INVOCATION)
    true
  }
}