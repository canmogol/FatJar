package fatjar.implementations.date;

import fatjar.Date;

import java.util.Calendar;

public class LocalDate implements Date {

    private String name;

    public LocalDate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public java.util.Date getDate() {
        return Calendar.getInstance().getTime();
    }

}
