import java.util.ArrayList;


//EXPERIMENTAL ADDITION


public class StudentSheet {
    private final Student student;
    private final ArrayList<String> answersList;
    private int grade;
    private boolean approved = false;

    public String getAnswer(int index) {
        return answersList.get(index);
    }

    public Student getStudent() {
        return student;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void addToGrade(int point) {
        grade += point;
    }

    public int getGrade() {
        return grade;
    }

    public StudentSheet(Student student, ArrayList<String> answersList) {
        this.student = student;
        this.answersList = answersList;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public ArrayList<String> getAnswersList() {
        return answersList;
    }
}
