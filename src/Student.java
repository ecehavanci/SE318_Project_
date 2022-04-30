import java.util.ArrayList;
import java.util.Objects;

public class Student extends User {
    public Student(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "student";
    }

    public void enrollLesson(Lesson lesson) {
        //When a student is enrolled a lesson, this lesson is added to the lessonList of that student who enrolled the lesson
        lessonList.add(lesson);
        System.out.println("Successfully enrolled");

    }

    public void unenrollLesson(Lesson lesson) {
        //When a student is unenrolled a lesson, this lesson is removed from the lessonList of that student who unenrolled from the lesson
        lessonList.remove(lesson);
        System.out.println("Successfully unenrolled");
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
}
