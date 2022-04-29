import java.util.ArrayList;
import java.util.Locale;

public class Lesson {
    private Instructor instructor;
    private String name;
    public Lesson(String name, Instructor instructor) {
        this.name = name;
        this.instructor=instructor;
    }

    private final ArrayList<Exam> examList = new ArrayList<>();

    public void AddExam(Exam exam){
        examList.add(exam);
    }

    public Exam GetExam(int index){
        return  examList.get(index);
    }

    public int ExamCount(){
        return examList.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void ShowExamDetails() {
        for (int i = 0; i<examList.size(); i++){
            Exam e = examList.get(i);
            System.out.println("EXAM " + (i+1));
            System.out.println("Exam type: " + e.GetType());
            System.out.println("Date: " + e.GetDate());
            System.out.println();
        }
    }


    public void Print(){
        System.out.println("INFO FOR LESSON " + name.toUpperCase(Locale.ROOT));
        System.out.println("Instructor: " + instructor.getName());
        ShowExamDetails();
    }



}
