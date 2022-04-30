import java.util.ArrayList;
import java.util.Objects;

public class Student extends User {
    public Student(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "student";
    }

    private final ArrayList<Lesson> lessonList = new ArrayList<>();


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

    public void enrollLesson(Lesson lesson) {
        lessonList.add(lesson);

    }

    public void unenrollLesson(Lesson lesson) {
        lessonList.remove(lesson);
    }

    public void getExams() {
        System.out.println("Exams that you have: ");

        for (int i = 0; i < lessonList.size(); i++) {
            int exams = lessonList.get(i).ExamCount();

            if (exams == 0) {
                System.out.println("Don't have any exams");
            }

            for (int j = 0; j < exams; j++) {
                Lesson lesson = lessonList.get(i);
                lesson.ShowExamDetails();
            }
        }

    }

//}
