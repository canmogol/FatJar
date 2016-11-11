package fatjar.dto;

public enum ParamRelation {

    EQ("equal"),
    LIKE("like"),
    NE("not equal"),
    BETWEEN("between"),
    GT("greater than"),
    GE("greater than or equal to"),
    LT("less than"),
    LE("less than or equal to");

    private String name;

    ParamRelation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal();
    }
}