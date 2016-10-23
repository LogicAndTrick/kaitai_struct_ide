package io.kaitai.struct.runtime.conditionals;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.LazyValue;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * Repeat until a certain condition is met.
 */
public class RepeatUntil implements IRepeat {

    private Ast.expr until;
    private long lastPosition;

    public RepeatUntil(Ast.expr until) {
        this.until = until;
    }

    public boolean isArray() {
        return true;
    }

    @Override
    public boolean shouldStop(int iterationNum, KaitaiStream stream, Value lastValue, ContainerValue context) {
        if (iterationNum <= 0) return false;
        try {

            long last = stream.pos();
            if (last == lastPosition) throw new IllegalStateException("Repeat-until loop did not advance the stream position, infinite loop detected.");
            lastPosition = last;

            Value it = new WrapperValue(context, lastValue);
            context.add(it);
            boolean result = Evaluator.evaluate(this.until, stream, context).boolValue();
            context.remove(it);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Wrapper value so we can expose `_` as the last evaluated value in the expression context.
     */
    private class WrapperValue extends LazyValue {

        public WrapperValue(ContainerValue owner, Value value) {
            super(owner, null, (Ast.expr) null);
            this.value = value;
            this.loaded = true;
        }

        @Override
        public Value evaluate() {
            return super.evaluate();
        }

        @Override
        public boolean isLoaded() {
            return super.isLoaded();
        }

        @Override
        public String getName() {
            return "_";
        }
    }
}
