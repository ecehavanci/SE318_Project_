import java.util.*;

public class Instructor extends User {
    public Instructor(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "instructor";
    }

    private boolean DoesLessonExists(String lessonName) {
        for (Lesson lesson : lessonList) {
            if (Objects.equals(lesson.getName(), lessonName)) {
                return true;
            }
        }
        return false;
    }

    //This function adds and returns the lesson to instructor's local lessonList so it can be added to the lessonList in the database
    public Lesson addAndReturnLesson(String lessonName) throws LessonAlreadyExistsException {
        if (!DoesLessonExists(lessonName)) {
            Lesson newLesson = new Lesson(lessonName, this);
            lessonList.add(newLesson);
            return newLesson;
        } else {
            throw new LessonAlreadyExistsException();
        }

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


    public void GradeUnevaluatedQuestions(String lessonName, int examIndex) {
        List<QuestionAndAnswer> QnA_List = null;
        ArrayList<StudentSheet> sheets = null;
        try {
            Lesson lesson = FindLesson(lessonName);
            if (examIndex-1<=lesson.ExamCount()-1 &&examIndex-1>=0){
                QnA_List = lesson.GetExam(examIndex-1).getQnA_List();
                sheets = lesson.GetExam(examIndex-1).getStudentSheetList();
            }
            else{
                System.out.println("There is no exam by the given index.");
            }
        } catch (LessonNotFoundException e) {
            System.out.println("Lesson not found");
        }
        if (QnA_List != null && sheets != null) {
            Scanner scan = new Scanner(System.in);

            for (int i = 0; i < sheets.size(); i++) {
                System.out.println("Student " + (i + 1));

                for (int j = 0; j < QnA_List.size(); j++) {
                    System.out.println("Unevaluated question " + (j + 1));

                    if (!QnA_List.get(i).getAnswer().evaluatedDirectly) {
                        System.out.println("Question:\n" + QnA_List.get(j).getQuestion());
                        System.out.println();

                        System.out.println("Right Answer:\n" + QnA_List.get(j).getAnswer().getRightAnswer());
                        System.out.println();

                        System.out.println("Student Answer:\n" + sheets.get(i).getAnswer(j));
                        System.out.println();

                        System.out.println("Grade the answer out of " + QnA_List.get(j).getPoint());
                        int point = scan.nextInt();
                        scan.nextLine();

                        sheets.get(i).addToGrade(point);

                        System.out.println();
                        System.out.println();
                    }
                }
                System.out.println("This sheet got " + sheets.get(i).getGrade());

                System.out.println("Do you approve?");
                System.out.println("1) Approve");
                System.out.println("2) Not approve");

                int approval = scan.nextInt();
                scan.nextLine();

                if (approval==1){
                    sheets.get(i).setApproved(true);
                }
                else if (approval==2){
                    sheets.get(i).setApproved(false);
                }
            }
        }
    }
}
