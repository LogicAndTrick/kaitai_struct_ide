package io.kaitai.struct.runtime;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.format.ClassSpec;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;
import scala.collection.JavaConversions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * A kaitai sequence. Contains child types, properties, instances, and enums.
 */
public class KObject {
    private String name;
    private KObject parent;
    private ArrayList<KObject> types;
    private ArrayList<KProperty> properties;
    private ArrayList<KProperty> instances;
    private ArrayList<KEnum> enums;


    public final static KObject EMPTY = new KObject();

    protected KObject() {
        this.name = "";
        this.parent = null;
        this.types = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.instances = new ArrayList<>();
        this.enums = new ArrayList<>();
    }

    public KObject(ClassSpec spec, KObject parent, String name) {
        this.name = name;
        this.parent = parent;
        this.types = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.instances = new ArrayList<>();
        this.enums = new ArrayList<>();

        JavaConversions.asJavaCollection(spec.seq()).forEach(x -> {
            KProperty prop = new KProperty(x, this);
            this.properties.add(prop);
        });

        JavaConversions.asJavaCollection(spec.types()).forEach(x -> {
            KObject obj = new KObject(x._2(), this, x._1());
            this.types.add(obj);
        });

        JavaConversions.asJavaCollection(spec.instances()).forEach(x -> {
            String instName = Converter.convert(x._1());
            KProperty inst = new KProperty(instName, x._2(), this);
            this.instances.add(inst);
        });

        JavaConversions.asJavaCollection(spec.enums()).forEach(x -> {
            KEnum en = new KEnum(x._1());
            for (Map.Entry<Object, String> entry : JavaConversions.mapAsJavaMap(x._2()).entrySet()) {
                en.add(((Number) entry.getKey()).longValue(), entry.getValue());
            }
            this.enums.add(en);
        });
    }

    public String getName() {
        return name;
    }

    public ContainerValue read(KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        ContainerValue val = new ContainerValue(parent, stream, this);
        //read.add(val);

        // Read instances first (these are lazy evaluated)
        for (KProperty prop : instances) {
            Value rv = prop.read(stream, read, val);
            val.add(rv);
            read.add(rv);
        }

        // Read actual data
        val.setStartIndex(stream.pos());
        for (KProperty prop : properties) {
            Value rv = prop.read(stream, read, val);
            val.add(rv);
            read.add(rv);
            //if (read.getEndIndex() < stream.pos()) break;
        }
        val.setEndIndex(stream.pos());

        return val;
    }

    public ArrayList<KObject> getTypes() {
        return types;
    }

    public ArrayList<KProperty> getProperties() {
        return properties;
    }

    public KObject getRoot() {
        KObject ko = this;
        while (ko.parent != null) {
            ko = ko.parent;
        }
        return ko;
    }

    public KObject findType(String name) {
        return getRoot().findTypeRecursive(name);
    }

    private KObject findTypeRecursive(String name) {
        for (KObject ko : types) {
            if (name.equals(ko.name)) return ko;
            KObject recurse = ko.findTypeRecursive(name);
            if (recurse != null) return recurse;
        }
        return null;
    }

    public KEnum findEnum(String name) {
        return getRoot().findEnumRecursive(name);
    }

    private KEnum findEnumRecursive(String name) {
        for (KEnum ke : enums) {
            if (name.equals(ke.getName())) return ke;
        }
        for (KObject ko : types) {
            KEnum recurse = ko.findEnumRecursive(name);
            if (recurse != null) return recurse;
        }
        return null;
    }
}
