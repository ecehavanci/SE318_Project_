import java.util.ArrayList;

public class Student extends User {
    public Student(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "student";
    }

    private final ArrayList<Lesson> lessonList = new ArrayList<>();

    public void addToStdLessonList(Lesson lesson) {
        lessonList.add(lesson);
    }

    public void printLessons() {
        if (lessonList.size() != 0) {
            System.out.println("Here are the lessons you are taking:");
            for (Lesson lesson : lessonList) {
                System.out.println(lesson.getName());
            }
        } else {
            System.out.println("You currently have no lessons.");
        }
    }

}

}
