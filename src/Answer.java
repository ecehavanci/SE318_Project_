import java.util.*;

public abstract class Answer {
    protected boolean evaluatedDirectly;

    //print() function prints the part of the answer which should be shown with a question (For example all the A-B-C choices,
    //True/False choices) -> This will be used while a student taking an exam
    public abstract void print();

    //printResult function prints the right part of the answer (For example in a multiple choice question with A-B-C choces, if
    //B is the right choice, B will be printed out) -> This will be used while a student seeing results of an exam and while
    //an instructor is grading it
    public abstract void printResult();
}

class ClassicalAnswer extends Answer {
    private String text;

    //A classical answer can be blank or a text might be given
    ClassicalAnswer(String answerText) {
        text = answerText;
        evaluatedDirectly = false;
    }

    //A classical answer has no part in it that can be shown with the question so here print() funcyion prints nothing
    @Override
    public void print() {
        //Print Nothing
    }

    //A classical answer might have an answer (to help instructor grading and help students see if they wrote something close
    //to as answer to a classical question when seeing their exam statistics) or not (instructor might want to grade it without
    //a guideline)
    @Override
    public void printResult() {
        System.out.println(Objects.requireNonNullElse(text, "No answer available for question"));
    }
}

class MultipleChoiceAnswer extends Answer {
    //MultipleChoiceAnswer stores a choice list that represents the choices (A, B, C,...)
    private final List<Choice> choices = new ArrayList<>();

    //We can add a choice without exposing list
    public void addChoice(Choice choice) {
        choices.add(choice);
        evaluatedDirectly = true;
    }

    //All choices of the question will be printed (with the question)
    @Override
    public void print() {
        for (int i = 0; i < choices.size(); i++) {
            System.out.println((char) 65 + i + ") " + choices.get(i).text);
        }
    }

    //Only right choice will be printed
    public void printResult() {
        for (Choice c : choices) {
            if (c.isRight) System.out.println("Right Choice: " + c.text);
        }
    }
}

class Choice {
    boolean isRight;
    String text;

    //Every choice is bound to their status (if they are the right choice or not)
    public Choice(String text, boolean isRight) {
        this.isRight = isRight;
        this.text = text;
    }
}

class TrueFalseAnswer extends Answer {
    char answer;

    //This is the answer of a question type which can have either true of false as right answer, here the right answer is set
    //(If true is right answer "T" is stored, if false is right answer "F" is stored. In the later releases we will ensure
    //that only T or F can be given as input to here.
    public TrueFalseAnswer(char answer) {
        this.answer = answer;
        evaluatedDirectly = true;
    }

    //True/false questions only have 2 choices which is true and false, so this is printed (along with the question)
    @Override
    public void print() {
        System.out.print("True (T)\nFalse (F)");
    }

    //This answer will be either T or F meaning true or false.
    @Override
    public void printResult() {
        System.out.println(answer);
    }
}
