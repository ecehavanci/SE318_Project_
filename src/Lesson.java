import java.time.LocalDate;
import java.util.ArrayList;
public class Lesson {
    public Lesson(String name) {
        this.name = name;
    }

    private final String name;
    private final ArrayList <LocalDate> examDates = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void showExamDates(){
        //TODO:Show dates
    }
}
