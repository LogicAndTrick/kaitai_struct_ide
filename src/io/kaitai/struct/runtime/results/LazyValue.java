package io.kaitai.struct.runtime.results;

import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.KProperty;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.types.IType;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A lazily-evaluated value. Could be anything.
 */
public class LazyValue extends Value {

    protected Value value;
    protected Ast.expr valueExpr;
    protected KProperty property;
    protected boolean loaded;
    protected ReadResult read;

    public LazyValue(ContainerValue owner, KProperty property, Ast.expr valueExpr) {
        this.owner = owner;
        this.property = property;
        this.valueExpr = valueExpr;
        this.loaded = false;
    }

    public LazyValue(ContainerValue owner, KProperty property, ReadResult read) {
        this.owner = owner;
        this.property = property;
        this.read = read;
        this.loaded = false;
    }

    public Value evaluate() {
        if (loaded) return value;
        if (valueExpr != null) {
            value = Evaluator.evaluate(valueExpr, null, owner).getValue();
            valueExpr = null;
        } else {
            try {
                value = property.read(owner.getStream(), read, owner, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.setStartIndex(value.getStartIndex());
            this.setEndIndex(value.getEndIndex());

            read = null;
        }

        loaded = true;

        return value;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public IType getType() {
        return evaluate().getType();
    }

    @Override
    public Object getValue() {
        return evaluate().getValue();
    }

    @Override
    public Value getValue(String name) {
        return evaluate().getValue(name);
    }

    @Override
    public Value getValueAtIndex(Value index) {
        return evaluate().getValueAtIndex(index);
    }

    @Override
    public ArrayList<Value> getValues() {
        return evaluate().getValues();
    }

    @Override
    public boolean isPrimitive() {
        return evaluate().isPrimitive();
    }

    @Override
    public boolean isContainer() {
        return evaluate().isContainer();
    }

    @Override
    public boolean isArray() {
        return evaluate().isArray();
    }

    @Override
    public boolean isStream() {
        return evaluate().isStream();
    }
}
