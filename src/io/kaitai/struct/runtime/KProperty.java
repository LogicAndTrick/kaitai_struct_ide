package io.kaitai.struct.runtime;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.format.AttrSpec;
import io.kaitai.struct.format.InstanceSpec;
import io.kaitai.struct.format.ParseInstanceSpec;
import io.kaitai.struct.format.ValueInstanceSpec;
import io.kaitai.struct.runtime.conditionals.IRepeat;
import io.kaitai.struct.runtime.conditionals.IfTest;
import io.kaitai.struct.runtime.conditionals.NoRepeat;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.*;
import io.kaitai.struct.runtime.types.CalcType;
import io.kaitai.struct.runtime.types.IType;

import java.io.IOException;

/**
 * A kaitai property.
 */
public class KProperty {

    private String name;
    private IType type;
    private IRepeat repeat;
    private IfTest ifTest;
    private KObject parent;
    private boolean isInstance;
    private Ast.expr valueExpression;
    private Ast.expr pos;
    private Ast.expr io;

    public KProperty(AttrSpec spec, KObject parent) {
        this.parent = parent;
        this.name = Converter.convert(spec.id());
        this.type = Converter.convert(spec.dataType());
        this.repeat = Converter.convert(spec.cond().repeat());
        this.ifTest = Converter.convert(spec.cond());
        this.isInstance = false;
    }

    public KProperty(String name, InstanceSpec spec, KObject parent) {
        this.parent = parent;
        this.name = name;
        this.isInstance = true;
        if (spec instanceof ValueInstanceSpec) {
            ValueInstanceSpec vis = ((ValueInstanceSpec) spec);
            this.valueExpression = vis.value();
            if (vis.dataType().nonEmpty()) {
                this.type = Converter.convert(vis.dataType().get());
            } else {
                this.type = CalcType.INSTANCE;
            }
            this.repeat = NoRepeat.INSTANCE;
            this.ifTest = IfTest.INSTANCE;
        } else if (spec instanceof ParseInstanceSpec) {
            ParseInstanceSpec pis = ((ParseInstanceSpec) spec);
            this.type = Converter.convert(pis.dataType());
            this.repeat = Converter.convert(pis.cond().repeat());
            this.ifTest = Converter.convert(pis.cond());
            if (pis.pos().isDefined()) this.pos = pis.pos().get();
            if (pis.io().isDefined()) this.io = pis.io().get();
        } else {
            throw new UnsupportedOperationException("Instance type " + spec.getClass().getName() + " is not supported.");
        }
    }

    public String getName() {
        return name;
    }

    public IType getType() {
        return type;
    }

    public boolean isArray() {
        return this.repeat.isArray();
    }

    public Value read(KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        return read(stream, read, parent, false);
    }

    /**
     * Read this property value from the stream.
     * @param stream The stream to read from
     * @param read The result to read into
     * @param parent The owner of the current context
     * @param forceEvaluation True to force evaluation of lazy values
     * @return The value that is read
     * @throws IOException If the read fails
     */
    public Value read(KaitaiStream stream, ReadResult read, ContainerValue parent, boolean forceEvaluation) throws IOException {
        long start = stream.pos();
        KaitaiStream originalStream = stream;

        // expression instance - evaluate on request
        if (this.valueExpression != null) {
            return new LazyValue(parent, this, this.valueExpression);
        }

        // read instance - evaluate on request
        if (this.isInstance && !forceEvaluation) {
            return new LazyValue(parent, this, read);
        }

        // instance with io - update before seeking
        if (this.isInstance && this.io != null) {
            stream = Evaluator.evaluate(this.io, stream, parent).streamValue();
        }

        // instance with position - seek now
        if (this.isInstance && this.pos != null) {
            long p = Evaluator.evaluate(this.pos, stream, parent).longValue();
            stream.seek(p);
        }

        long seek = stream.pos();

        // if there's an if test, check if it passes
        if (!ifTest.test(stream, parent)) {
            Value rv = new NullValue(parent);
            rv.setProperty(this);
            rv.setStartIndex(seek);
            rv.setEndIndex(seek);
            return rv;
        }

        // if we have a repeating block, then we need an array
        ArrayValue av = null;
        if (repeat.isArray()) {
            av = new ArrayValue(parent, this);
        }

        Value lastValue = null;

        // create a substream if required
        KaitaiStream io = type.createStream(stream, parent);

        // loop until done, reading values
        int i;
        for (i = 0; !repeat.shouldStop(i, io, lastValue, parent); i++) {

            long loopStart = stream.pos();

            // read the value, set some data
            lastValue = type.read(this.parent, io, read, parent);
            lastValue.setProperty(this);
            lastValue.setStartIndex(loopStart);
            lastValue.setEndIndex(stream.pos());

            // add this result to the array if repeating
            if (av != null) {
                av.add(lastValue);
                read.add(lastValue);
            }

            // re-create the substream for the next loop
            // seeking the child also seeks the parent, so we're okay to do this
            io = type.createStream(stream, parent);
        }

        // set the array properties if repeating
        if (av != null) {
            av.setStartIndex(seek);
            av.setEndIndex(stream.pos());
            lastValue = av;
        }

        // if an instance with an io, restore the original stream before seeking
        if (this.isInstance && this.io != null) {
            stream = originalStream;
        }

        // if an instance with a position, return to the original position
        if (this.isInstance && this.pos != null) {
            stream.seek(start);
        }

        // return null if no value was read
        if (lastValue == null) {
            Value rv = new NullValue(parent);
            rv.setProperty(this);
            rv.setStartIndex(seek);
            rv.setEndIndex(seek);
            return rv;
        }

        return lastValue;
    }
}
