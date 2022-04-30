public class QuestionAndAnswer {

    //Question is only a string, but answers are more complex (thus are classes) since we want to store for example all choices for a multiple
    //choice question and the right choice together
    private final String question;
    private final Answer answer;

    //A question is bound to its particular answer using this class
    public QuestionAndAnswer(String question, Answer answer) {
        this.question = question;
        this.answer = answer;
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
}
