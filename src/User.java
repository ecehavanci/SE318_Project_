import java.util.ArrayList;
import java.util.Objects;

public abstract class User {
    public User(String name, String surname, int schoolID, String password) {
        this.name = name;
        this.surname = surname;
        this.schoolID = schoolID;
        this.password = password;
    }

    protected String token;
    private final String name;
    private final String surname;
    private final int schoolID;
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public String getPassword() {
        return password;
    }

    public String getSurname() {
        return surname;
    }

    //User has a lessonList to store all lessons that student is taking
    protected final ArrayList<Lesson> lessonList = new ArrayList<>();


    public void printLessons() {
        //Unless the lessonList's size is 0, all lessons in lessonList is printed out
        if (lessonList.size() != 0) {
            System.out.println("Here are the lessons you are taking:");
            for (Lesson lesson : lessonList) {
                System.out.println(lesson.getName());
            }
        } else {
            System.out.println("You currently have no lessons.");
        }
    }

    //This function is used for searching a particular lesson using its name
    public Lesson FindLesson(String lessonName) throws LessonNotFoundException {
        for (Lesson lesson : lessonList) {
            if (Objects.equals(lesson.getName(), lessonName)) {
                return lesson;
            }
        }
        throw new LessonNotFoundException();
    }
}
