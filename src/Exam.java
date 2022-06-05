import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Exam {
    private int ID; //This is for text-database purposes, it is used while exam editing
    private String type;//midterm, final, quiz etc.
    private LocalDateTime dateAndTime;
    private final ArrayList<StudentSheet> studentSheetList = new ArrayList<>();
    private int point;
    private final List<QuestionAndAnswer> QnA_List = new ArrayList<>();

    public void SetID(int ID) {
        this.ID = ID;
    }

    public int GetID() {
        return ID;
    }

    public List<QuestionAndAnswer> GetQnA_List() {
        return QnA_List;
    }

    public void SetType(String type) {
        this.type = type;
    }

    public ArrayList<StudentSheet> GetStudentSheetList() {
        return studentSheetList;
    }

    public void SetPoint(int point) {
        this.point = point;
    }

    public int GetPoint() {
        return point;
    }

    //Dates are set using an array filled with inputs taken from instructor
    public void SetDateAndTime(int[] dayMonthYear, int hour, int minute) {
        dateAndTime = LocalDateTime.of(LocalDate.of(dayMonthYear[2], dayMonthYear[1], dayMonthYear[0]), LocalTime.of(hour,minute));
    }

    public String GetDateAndTime() {
        return dateAndTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG));
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
        return dateAndTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
    }

    //Get date with its day, month, year
    public String GetLongDate() {
        return dateAndTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public String GetTime() {
        return dateAndTime.getHour() + ":" +dateAndTime.getMinute();
    }

    public LocalDateTime GetLocalDateTime(){
        return dateAndTime;
    }

    public String GetStoringDate() {
        return dateAndTime.getDayOfMonth() + "." + dateAndTime.getMonthValue() + "." + dateAndTime.getYear() + "." + dateAndTime.getHour() + "." + dateAndTime.getMinute();
    }


    //Every exam have a list of questions and answers, questions and answers are bound together using QuestionAndAnswer class
    public void AddQuestion(String question, Answer answer, int point, boolean evaluatedDirectly) {
        QuestionAndAnswer QnA = new QuestionAndAnswer(question, answer, point, evaluatedDirectly);
        QnA_List.add(QnA);
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
    public void Take(Student student) {
        System.out.println("\n\n~" + type.toUpperCase(Locale.ROOT) + "~\n");
        Scanner scan = new Scanner(System.in);
        ArrayList<String> studentAnswers = new ArrayList<>();
        ArrayList<Integer> gradeList = new ArrayList<>();

        for (QuestionAndAnswer QnA : QnA_List) {
            QnA.printQuestion();

            System.out.print("Answer: ");
            String studentAnswer = scan.nextLine();

            if (QnA.isEvaluatedDirectly()){
                studentAnswer = studentAnswer.toUpperCase(Locale.ROOT);
            }

            if (QnA.getAnswer() instanceof MultipleChoiceAnswer){
                if (studentAnswer.toCharArray().length>1){
                    TextColours.writeYellow("Please enter the word symbol of the choice(A, B, C, etc.).");
                    return;
                }

                MultipleChoiceAnswer mca = (MultipleChoiceAnswer) QnA.getAnswer();

                int lastChoiceInASCII = 65 + mca.getChoiceCount();
                while((int) studentAnswer.charAt(0) > (lastChoiceInASCII-1) || (int) studentAnswer.charAt(0) < 65){
                    TextColours.writeYellow("Invalid choice. Please enter a word between A and " + (char)(lastChoiceInASCII-1));
                    System.out.print("Answer: ");
                    studentAnswer = scan.nextLine();
                }
            }

            if (QnA.getAnswer() instanceof TrueFalseAnswer){
                if (studentAnswer.toCharArray().length>1){
                    TextColours.writeYellow("Please enter either T or F.");
                    return;
                }

                while(!(studentAnswer.equals("T")||studentAnswer.equals("F"))){
                    TextColours.writeYellow("Please enter either T or F.");
                    System.out.print("Answer: ");
                    studentAnswer = scan.nextLine().toUpperCase(Locale.ROOT);
                }
            }

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

    public StudentSheet FindStudentSheet(Student student){
        for (StudentSheet ss : studentSheetList){
            if (ss.getStudent().getSchoolID()==student.getSchoolID()){
                return ss;
            }
        }
        return null;
    }

    public boolean isPastDue(){
        return LocalDateTime.now().isAfter(GetLocalDateTime());
    }



}
