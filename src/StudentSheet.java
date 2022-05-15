import java.util.ArrayList;


//EXPERIMENTAL ADDITION


public class StudentSheet {
    private final Student student;
    private final ArrayList<String> answersList;
    private final ArrayList<Integer> gradeList;
    private int grade;

    public ArrayList<Integer> getGradeList() {
        return gradeList;
    }

    //Students will be able to see their grade only when their grade is approved
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

    public StudentSheet(Student student, ArrayList<String> answersList, ArrayList<Integer> gradeList) {
        this.student = student;
        this.answersList = answersList;
        this.gradeList = gradeList;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }

    public ArrayList<String> getAnswersList() {
        return answersList;
    }
}
