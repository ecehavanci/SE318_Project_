import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Exam {
    private LocalDate date;
    private final List<QuestionAndAnswer> QnA_List = new ArrayList<>();

    public void addQuestion(String question, Answer answer) {
        QuestionAndAnswer QnA = new QuestionAndAnswer(question, answer);
        QnA_List.add(QnA);
    }

    public void setDate(int [] dayMonthYear) {
        date = LocalDate.of(dayMonthYear[2],dayMonthYear[1],dayMonthYear[0]);
    }

    public String getDate() {
        return date.toString();
    }

    public void printQuestions(){
        for (QuestionAndAnswer QnA : QnA_List){
            QnA.printQuestion();
        }
    }

    public void printQuestionsWithAnswers(){
        for (QuestionAndAnswer QnA : QnA_List){
            QnA.printQuestionAndAnswer();
        }
    }


}
