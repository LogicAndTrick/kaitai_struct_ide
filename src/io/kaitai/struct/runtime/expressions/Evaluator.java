package io.kaitai.struct.runtime.expressions;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.*;
import io.kaitai.struct.runtime.KEnumMember;
import io.kaitai.struct.runtime.results.*;
import scala.collection.JavaConversions;

import java.util.Collection;

/**
 * Dynamic expression evaluator.
 * This code would make more sense if it was written in Scala.
 * Java-Scala interop requires some funky class names, IntelliJ doesn't like it one bit.
 */
public class Evaluator {

    public static EvalResult evaluate(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        return new EvalResult(evaluateObject(expr, stream, context));
    }

    public static Value evaluateObject(Ast.expr expr, KaitaiStream stream, ContainerValue context) {

        if (expr instanceof Ast$expr$IntNum) {
            return intNum(expr, stream, context);
        } else if (expr instanceof Ast$expr$FloatNum) {
            return floatNum(expr, stream, context);
        } else if (expr instanceof Ast$expr$Str) {
            return str(expr, stream, context);
        } else if (expr instanceof Ast$expr$EnumByLabel) {
            return enumByLabel(expr, stream, context);
        } else if (expr instanceof Ast$expr$Name) {
            return name(expr, stream, context);
        } else if (expr instanceof Ast$expr$UnaryOp) {
            return unaryOp(expr, stream, context);
        } else if (expr instanceof Ast$expr$BinOp) {
            return binaryOp(expr, stream, context);
        } else if (expr instanceof Ast$expr$Compare) {
            return compare(expr, stream, context);
        } else if (expr instanceof Ast$expr$Attribute) {
            return attribute(expr, stream, context);
        } else if (expr instanceof Ast$expr$IfExp) {
            return ifExpr(expr, stream, context);
        } else if (expr instanceof Ast$expr$BoolOp) {
            return boolOp(expr, stream, context);
        } else if (expr instanceof Ast$expr$Subscript) {
            return subscript(expr, stream, context);
        }

        throw new UnsupportedOperationException("Unsupported expression type: " + expr.getClass().getName());
    }

    private static IntValue intNum(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Number n = ((Ast$expr$IntNum) expr).n();
        return new IntValue(context, n);
    }

    private static FloatValue floatNum(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Number n = ((Ast$expr$FloatNum) expr).n();
        return new FloatValue(context, n);
    }

    private static StringValue str(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        String s = ((Ast$expr$Str) expr).s();
        return new StringValue(context, s);
    }

    private static Boolean bool(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        throw new UnsupportedOperationException();
        //return ((Ast$expr$Bool) expr)
    }

    private static Value enumByLabel(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$EnumByLabel ebl = (Ast$expr$EnumByLabel) expr;
        return new EnumValue(context, ebl.enumName().name(), ebl.label().name());
    }

    private static Value name(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$Name name = (Ast$expr$Name) expr;
        String id = name.id().name();
        return context.getValue(id);
    }

    private static Value attribute(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$Attribute attr = (Ast$expr$Attribute) expr;
        Value value = evaluate(attr.value(), stream, context).getValue();
        String name = attr.attr().name();
        return value.getValue(name);
    }

    private static Value unaryOp(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$UnaryOp op = (Ast$expr$UnaryOp) expr;
        Object val = evaluateObject(op.operand(), stream, context).getValue();
        Ast.unaryop uop = op.op();

        if (uop instanceof Ast$unaryop$Invert$) {
            Number n = (Number) val;
            n = Caster.invert(n);
            return new IntValue(context, n);
        } else if (uop instanceof Ast$unaryop$Not$) {
            Boolean b = (Boolean) val;
            return new BoolValue(context, !b);
        } else if (uop instanceof Ast$unaryop$Minus$) {
            Number n = (Number) val;
            n = Caster.negate(n);
            return new IntValue(context, n);
        }
        throw new UnsupportedOperationException();
    }

    private static Value binaryOp(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$BinOp op = ((Ast$expr$BinOp) expr);
        Object left = evaluateObject(op.left(), stream, context).getValue();
        Object right = evaluateObject(op.right(), stream, context).getValue();
        Ast.operator bop = op.op();

        if (left instanceof Number && right instanceof Number) {
            Number l = (Number) left;
            Number r = (Number) right;
            if (bop instanceof Ast$operator$Add$) {
                return NumericValue.create(context, Caster.add(l, r));
            } else if (bop instanceof Ast$operator$Sub$) {
                return NumericValue.create(context, Caster.sub(l, r));
            } else if (bop instanceof Ast$operator$Mult$) {
                return NumericValue.create(context, Caster.mult(l, r));
            } else if (bop instanceof Ast$operator$Div$) {
                return NumericValue.create(context, Caster.div(l, r));
            } else if (bop instanceof Ast$operator$BitAnd$) {
                return NumericValue.create(context, Caster.and(l, r));
            } else if (bop instanceof Ast$operator$BitOr$) {
                return NumericValue.create(context, Caster.or(l, r));
            } else if (bop instanceof Ast$operator$BitXor$) {
                return NumericValue.create(context, Caster.xor(l, r));
            } else if (bop instanceof Ast$operator$LShift$) {
                return NumericValue.create(context, Caster.lshift(l, r));
            } else if (bop instanceof Ast$operator$RShift$) {
                return NumericValue.create(context, Caster.rshift(l, r));
            }
        } else if (left instanceof String && right instanceof String && bop instanceof Ast$operator$Add$) {
            return new StringValue(context, ((String) left) + right);
        }
        throw new UnsupportedOperationException();
    }

    private static BoolValue compare(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$Compare op = ((Ast$expr$Compare) expr);
        Object left = evaluateObject(op.left(), stream, context).getValue();
        Object right = evaluateObject(op.right(), stream, context).getValue();
        Ast.cmpop cop = op.ops();
        if (left instanceof Number && right instanceof Number) {
            Number l = (Number) left;
            Number r = (Number) right;
            if (cop instanceof Ast$cmpop$Eq$) {
                return new BoolValue(context, Caster.compare(l, r) == 0);
            } else if (cop instanceof Ast$cmpop$NotEq$) {
                return new BoolValue(context, Caster.compare(l, r) != 0);
            } else if (cop instanceof Ast$cmpop$Lt$) {
                return new BoolValue(context, Caster.compare(l, r) < 0);
            } else if (cop instanceof Ast$cmpop$LtE$) {
                return new BoolValue(context, Caster.compare(l, r) <= 0);
            } else if (cop instanceof Ast$cmpop$Gt$) {
                return new BoolValue(context, Caster.compare(l, r) > 0);
            } else if (cop instanceof Ast$cmpop$GtE$) {
                return new BoolValue(context, Caster.compare(l, r) >= 0);
            }
        } else if (left instanceof String && right instanceof String) {
            String l = (String) left;
            String r = (String) right;
            if (cop instanceof Ast$cmpop$Eq$) {
                return new BoolValue(context, l.compareTo(r) == 0);
            } else if (cop instanceof Ast$cmpop$NotEq$) {
                return new BoolValue(context, l.compareTo(r) != 0);
            } else if (cop instanceof Ast$cmpop$Lt$) {
                return new BoolValue(context, l.compareTo(r) < 0);
            } else if (cop instanceof Ast$cmpop$LtE$) {
                return new BoolValue(context, l.compareTo(r) <= 0);
            } else if (cop instanceof Ast$cmpop$Gt$) {
                return new BoolValue(context, l.compareTo(r) > 0);
            } else if (cop instanceof Ast$cmpop$GtE$) {
                return new BoolValue(context, l.compareTo(r) >= 0);
            }
        } else if (left instanceof KEnumMember && right instanceof KEnumMember) {
            if (cop instanceof Ast$cmpop$Eq$) {
                return new BoolValue(context, left.toString().compareTo(right.toString()) == 0);
            }
        }
        throw new UnsupportedOperationException();
    }

    private static Value ifExpr(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$IfExp ifExp = (Ast$expr$IfExp) expr;
        Ast.expr condition = ifExp.condition();
        Ast.expr ifTrue = ifExp.ifTrue();
        Ast.expr ifFalse = ifExp.ifFalse();
        boolean result = evaluate(condition, stream, context).boolValue();
        return evaluateObject(result ? ifTrue : ifFalse, stream, context);
    }

    private static BoolValue boolOp(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$BoolOp boolOp = (Ast$expr$BoolOp) expr;
        Ast.boolop bop = boolOp.op();
        Collection<Ast.expr> values = JavaConversions.asJavaCollection(boolOp.values());
        if (bop instanceof Ast$boolop$And$) {
            for (Ast.expr e : values) {
                if (!evaluate(e, stream, context).boolValue()) return new BoolValue(context, false);
            }
            return new BoolValue(context, true);
        } else if (bop instanceof Ast$boolop$Or$) {
            for (Ast.expr e : values) {
                if (evaluate(e, stream, context).boolValue()) return new BoolValue(context, true);
            }
            return new BoolValue(context, false);
        }
        throw new UnsupportedOperationException();
    }

    private static Value subscript(Ast.expr expr, KaitaiStream stream, ContainerValue context) {
        Ast$expr$Subscript sub = (Ast$expr$Subscript) expr;
        Ast.expr value = sub.value();
        Ast.expr idx = sub.idx();
        Value obj = evaluateObject(value, stream, context);
        Value i = evaluateObject(idx, stream, context);
        return obj.getValueAtIndex(i);
    }
}
