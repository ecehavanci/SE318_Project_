import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Instructor extends User {
    public Instructor(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "instructor";
    }

    //This function adds and returns the lesson to instructor's local lessonList so it can be added to the lessonList in the database
    public Lesson addAndReturnLesson(String lessonName, Instructor instructor) {
        Lesson newLesson = new Lesson(lessonName, instructor);
        lessonList.add(newLesson);
        return newLesson;
    }


    //This function is for showing details of a lesson. It also asks if we want to change anything and if instructor wants to
    //change something SetLessonDetails function is called.
    public void ShowLessonDetails(String lessonName) throws LessonNotFoundException {
        Lesson lesson = FindLesson(lessonName);
        lesson.Print();

        //Instructor can choose to edit or not
        System.out.println("Would you like to change details of this lesson?");
        Scanner scan = new Scanner(System.in);
        String yesNoChoice = scan.nextLine().toUpperCase(Locale.ROOT);
        if (yesNoChoice.equals("YES")) {
            SetLessonDetails(lesson);
        }
    }

    //This function is for changing details (name, exams, exam dates, exam type, etc.) of the given lesson
    private void SetLessonDetails(Lesson lesson) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please choose what would you like to do: ");
        System.out.println("1) Edit the name of the lesson");
        System.out.println("2) Edit an exam");
        System.out.println("Press any other key to cancel");
        int detailChoice = scan.nextInt();
        scan.nextLine();
        switch (detailChoice) {
            case 1 -> {
                System.out.println("What is the new name of the lesson");
                lesson.setName(scan.nextLine());
            }
            case 2 -> {
                //Exam details are shown to the instructor to choose what to edit
                lesson.ShowExamDetails();

                //Instructor chooses which exam they want to edit from the exam list by identifying the index (Users will think
                //first index is one, second index is 2 and so on. But actually first index is 0 and second index is 1, and it
                //continues like that. So code manages it by subtracting 1 from desired index)
                System.out.println("Which exam would you like to edit?");
                int editedExamIndex = scan.nextInt();

                if (editedExamIndex >= 1 || editedExamIndex <= lesson.ExamCount()) {
                    System.out.println("What would you like to edit?");
                    System.out.println("1) Exam type");
                    System.out.println("2) Exam date");
                    System.out.println("Press any other key to cancel");

                    int editChoice = scan.nextInt();
                    scan.nextLine();

                    switch (editChoice) {
                        case 1 -> {
                            System.out.println("What is the new type?");
                            //1 subtracted here to get the desired index
                            lesson.GetExam(editedExamIndex - 1).EditType(scan.nextLine());
                        }

                        case 2 -> {
                            System.out.println("What is the new date?");
                            //1 subtracted here to get the desired index
                            Main.SetDate(lesson.GetExam(editedExamIndex - 1));
                        }
                        default -> System.out.println("Cancelling...");
                    }
                } else System.out.println("Please choose a valid number");
            }
            default -> System.out.println("Cancelling...");
        }
    }
}
