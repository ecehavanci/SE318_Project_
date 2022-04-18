public class QuestionAndAnswer {
    private final String question;
    private final Answer answer;

    public QuestionAndAnswer(String question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }

    public void printQuestion(){
        System.out.println(question);
    }

    public void printQuestionAndAnswer(){
        printQuestion();
        answer.print();
    }
}
