import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Objects;

public class Student extends User {
    public Student(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "student";
    }

    public void enrollCourse(Course course) {
        //When a student is enrolled a course, this course is added to the courseList of that student who enrolled the course
        courseList.add(course);

        Database db = Database.getInstance();
        //System.out.println(TextColours.blue + getSchoolID() + TextColours.reset);
        String fileName = getSchoolID() + "_CourseList.txt";

        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.write(course.getName() + System.getProperty("line.separator"));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully enrolled");

    }

    public void unenrollCourse(Course course) {
        //When a student is unenrolled a course, this course is removed from the courseList of that student who unenrolled from the course
        courseList.remove(course);
        System.out.println("Successfully unenrolled");
    }

    public void getExams() {
        System.out.println("Exams that you have: ");

        for (int i = 0; i < courseList.size(); i++) {
            int exams = courseList.get(i).ExamCount();

            if (exams == 0) {
                System.out.println("Don't have any exams");
            }

            for (int j = 0; j < exams; j++) {
                Course course = courseList.get(i);
                course.ShowExamDetails();
            }
        }
    }

    public void getUpcomingExams() {
        System.out.println("Upcoming exams: ");

        for (int i = 0; i < courseList.size(); i++) {
            int exams = courseList.get(i).ExamCount();

            if (exams == 0) {
                System.out.println("Don't have any exams");
            }
            LocalDateTime localDate = LocalDateTime.now();
            for (int j = 0; j < exams; j++) {
                Exam exam = courseList.get(i).GetExam(j);
                if (localDate.isBefore(exam.GetLocalDateTime())) {
                    Course course = courseList.get(i);
                    course.ShowExamDetailsWithIndex(j);
                }
            }
        }
    }

    public void getPastDueExams() {
        System.out.println("Past due exams: ");

        for (int i = 0; i < courseList.size(); i++) {
            int exams = courseList.get(i).ExamCount();

            if (exams == 0) {
                System.out.println("Don't have any exams");
            }
            LocalDateTime localDate = LocalDateTime.now();
            for (int j = 0; j < exams; j++) {
                Exam exam = courseList.get(i).GetExam(j);
                if (localDate.isAfter(exam.GetLocalDateTime())) {
                    Course course = courseList.get(i);
                    course.ShowExamDetailsWithIndex(j);
                }
            }
        }
    }
}
