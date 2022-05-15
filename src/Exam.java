import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Exam {
    private String type;//midterm, final, quiz etc.
    private LocalDate date;//the date of the exam
    private final ArrayList<StudentSheet> studentSheetList = new ArrayList<>();
    private int point;

    private final List<QuestionAndAnswer> QnA_List = new ArrayList<>();

    public List<QuestionAndAnswer> getQnA_List() {
        return QnA_List;
    }

    public ArrayList<StudentSheet> getStudentSheetList() {
        return studentSheetList;
    }

    //Every exam have a list of questions and answers, questions and answers are bound together using QuestionAndAnswer class
    public void AddQuestion(String question, Answer answer, int point, boolean evaluatedDirectly) {
        QuestionAndAnswer QnA = new QuestionAndAnswer(question, answer, point, evaluatedDirectly);
        QnA_List.add(QnA);
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    //Dates are set using an array filled with inputs taken from instructor
    public void SetDate(int[] dayMonthYear) {
        date = LocalDate.of(dayMonthYear[2], dayMonthYear[1], dayMonthYear[0]);
    }

    //Exam type can be edited (for example can be set as midterm but then changed to a quiz)
    public void EditType(String type) {
        this.type = type;
    }

    //Returns exam type
    public String GetType() {
        return type;
    }


    //Get date with its day, month, year, day of week
    public String GetFullDate() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
    }

    //Get date with its day, month, year
    public String GetLongDate() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public void PrintQuestions() {
        for (QuestionAndAnswer QnA : QnA_List) {
            QnA.printQuestion();
        }
    }

    public void PrintQuestionsWithAnswers() {
        for (QuestionAndAnswer QnA : QnA_List) {
            QnA.printQuestionAndAnswer();
        }
    }

    //EXPERIMENTAL ADDITION
    public void take(Student student) {
        System.out.println("\n\n~" + type.toUpperCase(Locale.ROOT) + "~\n");
        Scanner scan = new Scanner(System.in);
        ArrayList<String> studentAnswers = new ArrayList<>();
        ArrayList<Integer> gradeList = new ArrayList<>();

        for (QuestionAndAnswer QnA : QnA_List) {
            QnA.printQuestion();

            System.out.print("Answer: ");
            String studentAnswer = scan.nextLine();
            studentAnswers.add(studentAnswer);

            System.out.println();
        }

        StudentSheet sheet = new StudentSheet(student, studentAnswers, gradeList);
        studentSheetList.add(sheet);
        System.out.println("Exam successfully submitted");
        ShowResult(sheet);


    }

    //This is for showing a particular student's results
    public void ShowResult(StudentSheet sheet) {
        System.out.println("\n\nRESULTS");
        CalculateResultAndPrintStatistics(sheet);
        System.out.println("Total grade: " + sheet.getGrade() + "/" + point);
        System.out.println();
    }

    //This is for calculating a particular student's sheet
    public void CalculateResultAndPrintStatistics(StudentSheet sheet) {
        System.out.println();

        //This variable is to determine how many points will be not evaluated directly
        int notEvaluatedPointsCount = 0;

        for (int i = 0; i < QnA_List.size(); i++) {
            System.out.println("Question:\n" + QnA_List.get(i).getQuestion());
            System.out.println();

            if (!QnA_List.get(i).isEvaluatedDirectly()) {
                System.out.println("Question " + (i + 1) + " won't be evaluated directly");
                notEvaluatedPointsCount += QnA_List.get(i).getPoint();
                System.out.println();
                sheet.getGradeList().add(-1);
                continue;
            }

            System.out.println("Right Answer:\n" + QnA_List.get(i).getAnswer().getRightAnswer());
            System.out.println();

            System.out.println("Your Answer:\n" + sheet.getAnswer(i));
            System.out.println();

            if (Objects.equals(sheet.getAnswer(i), QnA_List.get(i).getAnswer().getRightAnswer())) {
                sheet.addToGrade(QnA_List.get(i).getPoint());
                sheet.getGradeList().add(QnA_List.get(i).getPoint());
            }
            else{
                sheet.getGradeList().add(0);
            }

            System.out.println();
            System.out.println();
        }
        System.out.println(notEvaluatedPointsCount + " points will not be evaluated directly.");
    }

    public boolean AreAllSheetsApproved() {
        for (StudentSheet sheet : studentSheetList) {
            if (!sheet.isApproved()) {
                return false;
            }
        }
        return true;
    }


}