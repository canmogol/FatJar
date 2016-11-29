package fatjar;

import fatjar.implementations.date.CurrentDate;

public interface Date {

    static Date create() {
        return create(Type.LocalDate, Type.LocalDate.name());
    }

    static Date create(Type type, String url) {
        return CurrentDate.create(type, url);
    }

    java.util.Date getDate();

    enum Type {
        LocalDate, RemoteDate
    }

}
