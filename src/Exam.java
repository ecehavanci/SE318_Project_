import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class Exam {
    private String type;//midterm, final, quiz etc.
    private LocalDate date;//the date of the exam


    private final List<QuestionAndAnswer> QnA_List = new ArrayList<>();

    //Every exam have a list of questions and answers, questions and answers are bound together using QuestionAndAnswer class
    public void AddQuestion(String question, Answer answer) {
        QuestionAndAnswer QnA = new QuestionAndAnswer(question, answer);
        QnA_List.add(QnA);
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
}
