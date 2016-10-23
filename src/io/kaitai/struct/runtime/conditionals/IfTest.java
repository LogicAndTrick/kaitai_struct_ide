package io.kaitai.struct.runtime.conditionals;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.ContainerValue;

/**
 * Implements an if statement.
 */
public class IfTest {
    public Ast.expr test;

    public final static IfTest INSTANCE  = new IfTest();

    public IfTest() {
        this.test = null;
    }

    public IfTest(Ast.expr test) {
        this.test = test;
    }

    /**
     * Returns the result of the if test expression.
     * @param stream The kaitai stream object
     * @param context The owner of the current context
     * @return true if the test succeeds
     */
    public boolean test(KaitaiStream stream, ContainerValue context) {
        return this.test == null || Evaluator.evaluate(test, stream, context).boolValue();
    }
}
