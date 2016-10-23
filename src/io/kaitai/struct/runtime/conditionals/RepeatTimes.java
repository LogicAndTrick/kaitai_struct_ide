package io.kaitai.struct.runtime.conditionals;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.Value;

/**
 * Repeat a certain number of times, given by an expression.
 */
public class RepeatTimes implements IRepeat {

    private int count;
    private Ast.expr expr;

    public RepeatTimes(int count) {
        this.count = count;
    }

    public RepeatTimes(Ast.expr expr) {
        this.expr = expr;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    public boolean shouldStop(int iterationNum, KaitaiStream stream, Value lastValue, ContainerValue context) {
        long c = count;
        if (expr != null) {
            c = Evaluator.evaluate(expr, stream, context).intValue();
        }
        return iterationNum >= c;
    }
}
