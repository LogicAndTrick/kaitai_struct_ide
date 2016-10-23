package io.kaitai.struct.runtime.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The result of a parsed binary file
 */
public class ReadResult {

    private ArrayList<Value> values;
    private HashMap<Long, ArrayList<Value>> index;
    private long last;

    public ReadResult() {
        this.values = new ArrayList<>();
        this.index = new HashMap<>();
        this.last = 0;
    }

    /**
     * Assemble an index for fast index-based lookups on the result.
     * This is very primitive but it does the job.
     */
    public void index() {
        index = new HashMap<>();
        last = 0;
        for (Value rv : values) {
            long id = rv.getStartIndex() / 1000;
            if (!index.containsKey(id)) {
                index.put(id, new ArrayList<>());
            }
            index.get(id).add(rv);
            last = Math.max(id, last);
        }
        for (ArrayList<Value> list : index.values()) {
            list.sort((a, b) -> Long.compare(a.getStartIndex(), b.getStartIndex()));
        }
    }

    public void add(Value value) {
        this.values.add(value);
    }

    public ArrayList<Value> getNextValues(long currentOffset) {
        ArrayList<Value> vals = new ArrayList<>();
        long startIndex = currentOffset / 1000;
        long foundIndex = -1;
        for (Long i = startIndex; i <= last; i++) {
            if (index.containsKey(i)) {
                for (Value rv: index.get(i)) {
                    if (rv.getStartIndex() < currentOffset) continue;
                    if (foundIndex < 0) foundIndex = rv.getStartIndex();
                    if (rv.getStartIndex() > foundIndex) break;
                    vals.add(rv);
                }
            }
            if (foundIndex >= 0) break;
        }

        vals.sort((o1, o2) -> Integer.compare(sortValue(o1), sortValue(o2)));
        return vals;
    }

    private int sortValue(Value rv) {
        if (rv.isArray()) return 1;
        if (rv.isContainer()) return 2;
        return 3;
    }

    public Value getNextPrimitiveValue(long currentOffset) {
        long startIndex = currentOffset / 1000;
        for (Long i = startIndex; i < last; i++) {
            if (index.containsKey(i)) {
                for (Value rv: index.get(i)) {
                    if (!rv.isPrimitive()) continue;
                    if (rv.getStartIndex() < currentOffset) continue;
                    return rv;
                }
            }
        }
        return null;
    }

    public void evaluateLazyValues() {
        while (true) {
            List<LazyValue> lazy = values.stream()
                    .filter(LazyValue.class::isInstance)
                    .map(LazyValue.class::cast)
                    .filter(x -> !x.isLoaded())
                    .collect(Collectors.toList());

            if (lazy.isEmpty()) {
                break;
            }

            lazy.forEach(LazyValue::evaluate);
        }
    }
}
