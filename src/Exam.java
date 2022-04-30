import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Exam {
    private String type;//midterm, final, quiz etc.
    private LocalDate date;


    private final List<QuestionAndAnswer> QnA_List = new ArrayList<>();

    //Every exam have a list of questions and answers, questions and answers are bound together using QuestionAndAnswer class
    public void AddQuestion(String question, Answer answer) {
        QuestionAndAnswer QnA = new QuestionAndAnswer(question, answer);
        QnA_List.add(QnA);
    }

    public void SetDate(int[] dayMonthYear) {
        date = LocalDate.of(dayMonthYear[2], dayMonthYear[1], dayMonthYear[0]);
    }

    public void EditType(String type) {
        this.type = type;
    }
    
    public String GetType() {
        return type;
    }

    public String GetDate() {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        return "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month) + "/" + (year < 100 ? "20" + year : year);
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
