import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Answer {
    protected boolean evaluatedDirectly;
    public abstract void print();
    public abstract void printResult();
}

class ClassicalAnswer extends Answer {
    private String text;

    ClassicalAnswer(String answerText) {
        text=answerText;
        evaluatedDirectly = false;
    }

    @Override
    public void print() {
        System.out.println();
    }

    @Override
    public void printResult() {
        System.out.println("No answer available for classical question");
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
        for (Choice c : choices) {
            System.out.println(c.text);
        }
    }

    public void printResult() {
        for (Choice c : choices) {
            if (c.isRight) System.out.println(c.text);
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
        System.out.println("  T  F");
    }

    @Override
    public void printResult() {
        System.out.println(answer);
    }
}
