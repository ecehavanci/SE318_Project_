import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Instructor extends User {
    public Instructor(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "instructor";
    }

    private boolean DoesCourseExists(String courseName) {
        for (Course course : courseList) {
            if (Objects.equals(course.getName(), courseName)) {
                return true;
            }
        }
        return false;
    }

    //This function adds and returns the course to instructor's local courseList so it can be added to the courseList in the database
    public Course AddAndReturnCourse(String courseName) throws CourseAlreadyExistsException, IOException {
        Database db = Database.getInstance();
        if (!DoesCourseExists(courseName)) {
            Course course = null;
            if (db.DoesCourseExists(courseName)) {
                try {
                    course = db.FindCourse(courseName);
                    course.AddInstructor(this);

                } catch (CourseNotFoundException e) {
                    System.out.println("Lesson not found");
                }
            } else {
                course = new Course(courseName, this);
            }
            courseList.add(course);

            String fileName = getSchoolID() + "_CourseList.txt";
            FileWriter fw = new FileWriter(fileName, true);
            String courseIndex = String.valueOf(db.ReturnOrCreateCourseIndex(course));
            fw.write(courseIndex);
            fw.close();

            return course;
        } else {
            throw new CourseAlreadyExistsException();
        }
    }


    //This function is for showing details of a course. It also asks if we want to change anything and if instructor wants to
    //change something SetCourseDetails function is called.
    public void ShowCourseDetails(String courseName) throws CourseNotFoundException {
        Course course = FindCourse(courseName);
        course.Print();

        //Instructor can choose to edit or not
        System.out.println("Would you like to change details of this course?");
        Scanner scan = new Scanner(System.in);
        String yesNoChoice = scan.nextLine().toUpperCase(Locale.ROOT);
        if (yesNoChoice.equals("YES")) {
            SetCourseDetails(course);
        }
    }

    //This function is for changing details (name, exams, exam dates, exam type, etc.) of the given course
    private void SetCourseDetails(Course course) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please choose what would you like to do: ");
        System.out.println("1) Edit the name of the course");
        System.out.println("2) Edit an exam");
        System.out.println("Press any other key to cancel");
        int detailChoice = scan.nextInt();
        scan.nextLine();
        switch (detailChoice) {
            case 1 -> {
                System.out.println("What is the new name of the course");
                course.setName(scan.nextLine());
            }
            case 2 -> {
                if (course.ExamCount() > 0) {
                    //Exam details are shown to the instructor to choose what to edit
                    course.ShowExamDetails();

                    //Instructor chooses which exam they want to edit from the exam list by identifying the index (Users will think
                    //first index is one, second index is 2 and so on. But actually first index is 0 and second index is 1, and it
                    //continues like that. So code manages it by subtracting 1 from desired index)
                    System.out.println("Which exam would you like to edit?");
                    int editedExamIndex = scan.nextInt();

                    if (editedExamIndex >= 1 || editedExamIndex <= course.ExamCount()) {
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
                                course.GetExam(editedExamIndex - 1).EditType(scan.nextLine());
                            }

                            case 2 -> {
                                System.out.println("What is the new date?");
                                //1 subtracted here to get the desired index
                                Main.SetDate(course.GetExam(editedExamIndex - 1));
                            }
                            default -> System.out.println("Cancelling...");
                        }
                    } else System.out.println("Please choose a valid number");
                } else {
                    System.out.println("This course has no exam yet, you can add an exam using \"Build up exam\" choice.");
                }

            }
            default -> System.out.println("Cancelling...");
        }
    }


    public void GradeUnevaluatedQuestions(String courseName, int examIndex) {
        List<QuestionAndAnswer> QnA_List = null;
        ArrayList<StudentSheet> sheets = null;
        Exam exam = null;

        try {
            Course course = FindCourse(courseName);
            if (examIndex - 1 <= course.ExamCount() - 1 && examIndex - 1 >= 0) {
                exam = course.GetExam(examIndex - 1);
                QnA_List = exam.getQnA_List();
                sheets = exam.getStudentSheetList();

            } else {
                System.out.println("There is no exam by the given index.");
            }
        } catch (CourseNotFoundException e) {
            System.out.println("Course not found");
        }


        if (QnA_List != null && sheets != null) {
            Scanner scan = new Scanner(System.in);

            if (!exam.AreAllSheetsApproved()) {

                for (int i = 0; i < sheets.size(); i++) {
                    if (sheets.get(i).isApproved()) continue;

                    String studentName = sheets.get(i).getStudent().getName() + " " + sheets.get(i).getStudent().getSurname();
                    System.out.println(studentName + "'s sheet:\n");

                    for (int j = 0; j < QnA_List.size(); j++) {
                        if (!QnA_List.get(j).isEvaluatedDirectly()) {
                            System.out.println("Unevaluated question: QUESTION " + (j + 1));
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

                    System.out.println(studentName + "'s sheet got " + sheets.get(i).getGrade());

                    System.out.println("Do you approve?");
                    System.out.println("1) Approve");
                    System.out.println("2) Not approve");

                    int approval = scan.nextInt();
                    scan.nextLine();

                    if (approval == 1) {
                        sheets.get(i).setApproved(true);
                    } else if (approval == 2) {
                        sheets.get(i).setApproved(false);
                    }

                }
                System.out.println("All exam sheets are revised.");
            } else System.out.println("All sheets are approved.");
        }
    }

    public void ShowExamStatistics(String courseName, int examIndex) {
        //TODO: Show exam statistics
        ArrayList<StudentSheet> sheets = null;
        List<QuestionAndAnswer> QnA_List = null;
        Exam exam = null;

        try {
            Course course = FindCourse(courseName);
            if (examIndex - 1 <= course.ExamCount() - 1 && examIndex - 1 >= 0) {
                exam = course.GetExam(examIndex - 1);
                sheets = exam.getStudentSheetList();

            } else {
                System.out.println("There is no exam by the given index.");
            }
        } catch (CourseNotFoundException e) {
            System.out.println("Course not found");
        }
        System.out.println("\n\nRESULTS OF " + courseName);

        if (sheets != null) {
            int totalPoints = 0;
            for (int i = 0; i < sheets.size(); i++) {
                    totalPoints += sheets.get(i).getGrade();

            }
            System.out.println("Average: " + totalPoints / sheets.size());
        }


    }
}
