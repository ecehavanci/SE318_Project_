import java.util.*;

public abstract class Answer {
    protected boolean evaluatedDirectly;

    public abstract void print();

    public abstract void printResult();
}

class ClassicalAnswer extends Answer {
    private String text;

    ClassicalAnswer(String answerText) {
        text = answerText;
        evaluatedDirectly = false;
    }

    @Override
    public void print() {
        //Print Nothing
    }

    @Override
    public void printResult() {
        System.out.println(Objects.requireNonNullElse(text, "No answer available for question"));
    }
}

class MultipleChoiceAnswer extends Answer {
    List<Choice> choices = new ArrayList<>();

    public void addChoice(String choice, boolean isRightChoice) {
        choices.add(new Choice(choice, isRightChoice));
        evaluatedDirectly = true;
    }

    @Override
    public void print() {
        for (int i = 0; i < choices.size(); i++) {
            System.out.println((char) 65 + i + ") " + choices.get(i).text);
        }
    }

    public void printResult() {
        for (Choice c : choices) {
            if (c.isRight) System.out.println("Right Choice: " + c.text);
        }
    }
}

class Choice {
    boolean isRight;
    String text;

    public Choice(String text, boolean isRight) {
        this.isRight = isRight;
        this.text = text;
    }
}


class TrueFalseAnswer extends Answer {
    char answer;

    public TrueFalseAnswer(char answer) {
        this.answer = answer;
        evaluatedDirectly = true;
    }

    @Override
    public void print() {
        System.out.print("True (T)\nFalse (F)");
    }

    @Override
    public void printResult() {
        System.out.println(answer);
    }
}
