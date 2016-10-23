package io.kaitai.struct.runtime;

import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.exprlang.DataType;
import io.kaitai.struct.format.*;
import io.kaitai.struct.format.RepeatUntil;
import io.kaitai.struct.runtime.conditionals.*;
import io.kaitai.struct.runtime.types.*;
import scala.collection.JavaConversions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts KSC metadata into useful objects
 */
public class Converter {

    /**
     * Convert an identifier into a string
     * @param id The identifier
     * @return The string result
     */
    public static String convert(Identifier id) {
        if (id instanceof NamedIdentifier) {
            return ((NamedIdentifier) id).name();
        } else if (id instanceof RawIdentifier) {
            return convert(((RawIdentifier) id).innerId());
        } else if (id instanceof IoStorageIdentifier) {
            return "io";
        } else if (id instanceof InstanceIdentifier) {
            return ((InstanceIdentifier) id).name();
        } else if (id instanceof SpecialIdentifier) {
            return ((SpecialIdentifier) id).name();
        }
        throw new UnsupportedOperationException("Identifier type " + id.getClass().getName() + " is not supported");
    }

    /**
     * Convert a BaseType into an IType
     * @param type The type to convert
     * @return The converted IType
     */
    public static IType convert(DataType.BaseType type) {
        if (type instanceof DataType.Int1Type) {
            return new IntType(1, ((DataType.Int1Type) type).signed(), false);
        } else if (type instanceof DataType.IntMultiType) {
            DataType.IntMultiType mt = ((DataType.IntMultiType) type);
            return new IntType(mt.width().width(), mt.signed(), mt.endian() instanceof DataType.BigEndian$);
        } else if (type instanceof DataType.FloatMultiType) {
            DataType.FloatMultiType mt = ((DataType.FloatMultiType) type);
            return new FloatType(mt.width().width(), mt.endian() instanceof DataType.BigEndian$);
        } else if (type instanceof DataType.FixedBytesType) {
            DataType.FixedBytesType fbt = ((DataType.FixedBytesType) type);
            return new FixedBytesType(fbt.contents());
        } else if (type instanceof DataType.UserTypeInstream) {
            DataType.UserTypeInstream utis = (DataType.UserTypeInstream) type;
            Collection<String> names = JavaConversions.asJavaCollection(utis._name());
            return new UserType(new ArrayList<>(names));
        } else if (type instanceof DataType.StrByteLimitType) {
            DataType.StrByteLimitType st = (DataType.StrByteLimitType) type;
            return new StringType(st.encoding(), st.s());
        } else if (type instanceof DataType.StrZType) {
            DataType.StrZType st = (DataType.StrZType) type;
            return new StringType(st.encoding(), st.terminator(), st.include(), st.consume());
        } else if (type instanceof DataType.BytesLimitType) {
            DataType.BytesLimitType blt = (DataType.BytesLimitType) type;
            return new ByteArray(blt.s());
        } else if (type instanceof DataType.UserTypeByteLimit) {
            DataType.UserTypeByteLimit ubl = (DataType.UserTypeByteLimit) type;
            Collection<String> names = JavaConversions.asJavaCollection(ubl._name());
            // ubl.process()
            return new SizedUserType(new ArrayList<>(names), ubl.size(), false);
        } else if (type instanceof DataType.EnumType) {
            DataType.EnumType enumType = (DataType.EnumType) type;
            String name = enumType.name();
            IType baseType = convert(enumType.basedOn());
            return new EnumType(name, baseType);
        } else if (type instanceof DataType.UserTypeEos) {
            DataType.UserTypeEos ute = (DataType.UserTypeEos) type;
            Collection<String> names = JavaConversions.asJavaCollection(ute._name());
            // ubl.process()
            return new SizedUserType(new ArrayList<>(names), null, true);
        } else if (type instanceof DataType.StrEosType) {
            DataType.StrEosType se = (DataType.StrEosType) type;
            return new StringType(se.encoding());
        } else if (type instanceof DataType.BytesEosType) {
            DataType.BytesEosType be = (DataType.BytesEosType) type;
            // be.process
            return new ByteArray();
        } else if (type instanceof DataType.SwitchType) {
            DataType.SwitchType st = (DataType.SwitchType) type;
            Ast.expr on = st.on();
            Map<Ast.expr, IType> types = new HashMap<>();
            for (Map.Entry<Ast.expr, DataType.BaseType> entry : JavaConversions.mapAsJavaMap(st.cases()).entrySet()) {
                types.put(entry.getKey(), convert(entry.getValue()));
            }
            return new SwitchType(on, types);
        }
        throw new UnsupportedOperationException("Data type " + type.getClass().getName() + " is not supported");
    }

    /**
     * Convert a ConditionalSpec into an IfTest
     * @param cond The ConditionalSpec to convert
     * @return The converted IfTest
     */
    public static IfTest convert(ConditionalSpec cond) {
        if (cond.ifExpr().isEmpty()) return IfTest.INSTANCE;
        else return new IfTest(cond.ifExpr().get());
    }

    /**
     * Convert a RepeatSpec into an IRepeat
     * @param spec The RepeatSpec to convert
     * @return The converted IRepeat
     */
    public static IRepeat convert(RepeatSpec spec) {
        if (spec == null || spec instanceof NoRepeat$) {
            return io.kaitai.struct.runtime.conditionals.NoRepeat.INSTANCE;
        } else if (spec instanceof RepeatExpr) {
            RepeatExpr re = (RepeatExpr) spec;
            return new RepeatTimes(re.expr());
        } else if (spec instanceof RepeatUntil) {
            RepeatUntil ru = (RepeatUntil) spec;
            return new io.kaitai.struct.runtime.conditionals.RepeatUntil(ru.expr());
        } else if (spec instanceof RepeatEos$) {
            return io.kaitai.struct.runtime.conditionals.RepeatEos.INSTANCE;
        }
        throw new UnsupportedOperationException("Repeat type " + spec.getClass().getName() + " is not supported");
    }
}
