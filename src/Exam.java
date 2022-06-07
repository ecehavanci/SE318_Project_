import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Exam {
    private int ID; //This is for text-database purposes, it is used while exam editing
    private String type;//midterm, final, quiz etc.
    private LocalDateTime endDateAndTime;
    private LocalDateTime startDateAndTime;

    private final ArrayList<StudentSheet> studentSheetList = new ArrayList<>();
    private int point;
    private final List<QuestionAndAnswer> QnA_List = new ArrayList<>();
    private int duration;

    public void SetID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public List<QuestionAndAnswer> getQnA_List() {
        return QnA_List;
    }

    public void SetType(String type) {
        this.type = type;
    }

    public ArrayList<StudentSheet> getStudentSheetList() {
        return studentSheetList;
    }

    public void SetPoint(int point) {
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    //Dates are set using an array filled with inputs taken from instructor
    public void SetEndDateAndTime(int[] dayMonthYear, int hour, int minute) {
        endDateAndTime = LocalDateTime.of(LocalDate.of(dayMonthYear[2], dayMonthYear[1], dayMonthYear[0]), LocalTime.of(hour, minute));
    }

    public void SetStartDateAndTime(int[] dayMonthYear, int hour, int minute) {
        startDateAndTime = LocalDateTime.of(LocalDate.of(dayMonthYear[2], dayMonthYear[1], dayMonthYear[0]), LocalTime.of(hour, minute));
    }

    public LocalDateTime getEndDateAndTime() {
        return endDateAndTime;
    }

    public LocalDateTime getStartDateAndTime() {
        return startDateAndTime;
    }

    //Exam type can be edited (for example can be set as midterm but then changed to a quiz)
    public void EditType(String type) {
        this.type = type;
    }

    //Returns exam type
    public String getType() {
        return type;
    }


    //get date with its day, month, year, day of week
    public String getFullDate() {
        return endDateAndTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
    }

    //get date with its day, month, year
    public String getLongDate() {
        return endDateAndTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public String getEndTime() {
        return endDateAndTime.getHour() + ":" + endDateAndTime.getMinute();
    }

    public String getStartTime() {
        return startDateAndTime.getHour() + ":" + startDateAndTime.getMinute();
    }

    public LocalDateTime getLocalDateTime() {
        return endDateAndTime;
    }

    public String getStoringDate() {
        return endDateAndTime.getDayOfMonth() + "." + endDateAndTime.getMonthValue() + "." + endDateAndTime.getYear() + "."
                + endDateAndTime.getHour() + "." + endDateAndTime.getMinute() + "." + startDateAndTime.getHour() + "." + startDateAndTime.getMinute() + "." + duration;
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
    public void Take(Student student, Course course) throws IOException {
        if (didStudentTake(student)) {
            TextColours.writeYellow("You already took the exam.");
        } else {
            if (isEarly()) {
                TextColours.writeYellow("Exam is not available yet.");
            } else {
                System.out.println("\n\n~" + type.toUpperCase(Locale.ROOT) + "~\n");
                Scanner scan = new Scanner(System.in);
                ArrayList<String> studentAnswers = new ArrayList<>();
                ArrayList<Integer> gradeList = new ArrayList<>();

                boolean thirtyMinMessage = true;
                boolean tenMinMessage = true;
                boolean fiveMinMessage = true;
                boolean oneMinMessage = true;


                for (QuestionAndAnswer QnA : QnA_List) {
                    QnA.printQuestion();

                    if (isPastDue()) {
                        TextColours.writeYellow("Exam is over.");
                        break;
                    }

                    System.out.print("Answer: ");
                    String studentAnswer = scan.nextLine();

                    if (QnA.isEvaluatedDirectly()) {
                        studentAnswer = studentAnswer.toUpperCase(Locale.ROOT);
                    }

                    if (QnA.getAnswer() instanceof MultipleChoiceAnswer) {
                        if (studentAnswer.toCharArray().length > 1) {
                            TextColours.writeYellow("Please enter the word symbol of the choice(A, B, C, etc.).");
                            return;
                        }

                        MultipleChoiceAnswer mca = (MultipleChoiceAnswer) QnA.getAnswer();

                        int lastChoiceInASCII = 65 + mca.getChoiceCount();
                        while ((int) studentAnswer.charAt(0) > (lastChoiceInASCII - 1) || (int) studentAnswer.charAt(0) < 65) {
                            TextColours.writeYellow("Invalid choice. Please enter a word between A and " + (char) (lastChoiceInASCII - 1));
                            System.out.print("Answer: ");
                            studentAnswer = scan.nextLine();
                        }
                    }

                    if (QnA.getAnswer() instanceof TrueFalseAnswer) {
                        if (studentAnswer.toCharArray().length > 1) {
                            TextColours.writeYellow("Please enter either T or F.");
                            return;
                        }

                        while (!(studentAnswer.equals("T") || studentAnswer.equals("F"))) {
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

                File file = new File(course.getName() + "_" + getID() + "_" + student.getSchoolID() + "_Sheet.txt");
                file.createNewFile();

                FileWriter sheetWriter = new FileWriter(file, true);

                for (int i = 0; i < sheet.getGradeList().size(); i++) {
                    sheetWriter.write(sheet.getAnswer(i) + "#" + sheet.getGradeList().get(i) + System.getProperty("line.separator"));
                }
                sheetWriter.close();

                File file2 = new File(course.getName() + "_" + getID() + "_StudentList.txt");
                FileWriter studentWriter = new FileWriter(file2, true);

                studentWriter.write(student.getSchoolID() + System.getProperty("line.separator"));

                studentWriter.close();

            }
        }
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
            } else {
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

    public StudentSheet FindStudentSheet(Student student) {
        for (StudentSheet ss : studentSheetList) {
            if (ss.getStudent().getSchoolID() == student.getSchoolID()) {
                return ss;
            }
        }
        return null;
    }

    public boolean isPastDue() {
        return LocalDateTime.now().isAfter(getLocalDateTime());
    }

    public boolean isEarly() {
        return LocalDateTime.now().isBefore(getStartDateAndTime());
    }

    public boolean didStudentTake(Student student) {
        for (StudentSheet sheet : studentSheetList) {
            if (student.getSchoolID() == sheet.getStudent().getSchoolID()) {
                return true;
            }
        }
        return false;
    }


}
