import java.util.ArrayList;

public class Instructor extends User{
    public Instructor(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token="instructor";
    }

    private ArrayList<Lesson> lessonList = new ArrayList<>();
}
