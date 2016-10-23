package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.*;
import io.kaitai.struct.runtime.Converter;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A type that could be one of many depending on the result of an expression.
 */
public class SwitchType implements IType {
    private Ast.expr on;
    private Map<Ast.expr, IType> cases;
    private IType elseType;

    public SwitchType(Ast.expr on, Map<Ast.expr, IType> cases) {
        this.on = on;
        this.cases = new HashMap<>();
        for (Map.Entry<Ast.expr, IType> ex : cases.entrySet()) {
            Ast.expr key = ex.getKey();
            if (key instanceof Ast$expr$Name && ((Ast$expr$Name) key).id().name().equals("_")) {
                this.elseType = ex.getValue();
            } else {
                this.cases.put(ex.getKey(), ex.getValue());
            }
        }
    }

    protected IType determineType(KaitaiStream stream, ContainerValue context) {
        for (Map.Entry<Ast.expr, IType> ex : cases.entrySet()) {
            Ast.expr expr = new Ast$expr$Compare(on, new Ast$cmpop$Eq$(), ex.getKey());
            boolean eq = Evaluator.evaluate(expr, stream, context).boolValue();
            if (eq) return ex.getValue();
        }
        if (elseType != null) {
            return elseType;
        }

        throw new java.lang.UnsupportedOperationException("No valid type found for expression: " + on);
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        return determineType(stream, parent).read(object, stream, read, parent);
    }

    @Override
    public String getName() {
        return "switch";
    }

    @Override
    public String getDisplayName() {
        return "dynamic type";
    }

    @Override
    public KaitaiStream createStream(KaitaiStream stream, ContainerValue context) throws IOException {
        return determineType(stream, context).createStream(stream, context);
    }
}
