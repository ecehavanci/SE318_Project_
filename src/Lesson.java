import java.time.LocalDate;
import java.util.ArrayList;

public class Lesson {
    public Lesson(String name) {
        this.name = name;
    }

    private final String name;
    /*private*/ final ArrayList<Exam> examList = new ArrayList<>();

    public void addExam(Exam exam){
        examList.add(exam);
    }

    public String getName() {
        return name;
    }

    public void showExamDates() {
        //TODO:Show dates
        for (Exam e : examList){
            System.out.println(e.getDate());
        }
    }
}
