import java.util.ArrayList;

public class Instructor extends User{
    public Instructor(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token="instructor";
    }

    private final ArrayList<Lesson> lessonList = new ArrayList<>();

    public void addLesson(String lessonName){
        lessonList.add(new Lesson(lessonName));
    }

    public void printLessons(){
        if (lessonList.size()!=0){
            System.out.println("Here are the lessons you are giving:");
            for (Lesson lesson : lessonList) {
                System.out.println(lesson.getName());
            }
        }
        else{
            System.out.println("You currently have no lessons.");
        }

    }
}
