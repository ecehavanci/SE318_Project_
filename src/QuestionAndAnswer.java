public class QuestionAndAnswer {

    //Question is only a string, but answers are more complex (thus are classes) since we want to store for example all choices for a multiple
    //choice question and the right choice together
    private final String question;
    private final Answer answer;
    private final int point;
    private boolean evaluatedDirectly = false;

    //A question is bound to its particular answer using this class
    public QuestionAndAnswer(String question, Answer answer, int point, boolean evaluatedDirectly) {
        this.question = question;
        this.answer = answer;
        this.point=point;
        this.evaluatedDirectly=evaluatedDirectly;
    }

    //Questions are printed with their possible answers (nothing for classical type questions, with choices for multiple choice
    //questions, with true and false options for true/false questions)
    public void printQuestion() {
        System.out.println(question);
        answer.print();
        System.out.println();
    }

    //Questions are printed with their right answers (classical questions are printed with right answer if any answer is specified
    //otherwise with saying no answer is available, miltiple choice and true/false questions and are printed with the right choice)
    public void printQuestionAndAnswer() {
        printQuestion();
        answer.printResult();
        System.out.println();
    }

    public Answer getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public int getPoint() {
        return point;
    }

    public boolean isEvaluatedDirectly() {
        return evaluatedDirectly;
    }
}
