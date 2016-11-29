package fatjar.dto;


import java.io.Serializable;

public class Param<K, V> implements Serializable {

    private K key;

    private V value;

    private V valueSecondary;

    private ParamRelation relation = ParamRelation.EQ;

    public Param(K key, V value) {
        this(key, value, null, ParamRelation.EQ);
    }

    public Param(K key, V value, V valueSecondary) {
        this(key, value, valueSecondary, ParamRelation.EQ);
    }

    public Param(K key, V value, ParamRelation relation) {
        this(key, value, null, relation);
    }

    public Param(K key, V value, V valueSecondary, ParamRelation relation) {
        this.key = key;
        this.value = value;
        this.valueSecondary = valueSecondary;
        this.relation = relation;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V getValueSecondary() {
        return valueSecondary;
    }

    public ParamRelation getRelation() {
        return relation;
    }

    @Override
    public String toString() {
        return "Param{" +
                "key=" + key +
                ", value=" + value +
                ", valueSecondary=" + valueSecondary +
                ", relation='" + relation + '\'' +
                '}';
    }
}
