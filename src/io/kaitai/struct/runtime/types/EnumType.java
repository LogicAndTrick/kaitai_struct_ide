package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.EnumValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;
import io.kaitai.struct.runtime.KEnumMember;

import java.io.IOException;

/**
 * An enum type with a base type (which should probably be an integer)
 */
public class EnumType implements IType {

    private IType baseType;
    private String enumName;

    public EnumType(String enumName, IType baseType) {
        this.enumName = enumName;
        this.baseType = baseType;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        long result = ((Number) baseType.read(object, stream, read, parent).getValue()).longValue();
        KEnumMember rv = object.findEnum(enumName).getEnumValue(result);
        return new EnumValue(parent, rv.getEnumName(), rv.getValue());
    }

    @Override
    public String getName() {
        return baseType.getName();
    }

    @Override
    public String getDisplayName() {
        return enumName + " (" + baseType.getDisplayName() + ")";
    }
}
