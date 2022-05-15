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

    //User has a courseList to store all courses that student is taking
    protected final ArrayList<Course> courseList = new ArrayList<>();


    public void printCourses() {
        //Unless the courseList's size is 0, all courses in courseList is printed out
        if (courseList.size() != 0) {
            System.out.println("Those are your courses:");
            for (Course course : courseList) {
                System.out.println(course.getName());
            }
        } else {
            System.out.println("You currently have no courses.");
        }
    }

    //This function is used for searching a particular course using its name
    public Course FindCourse(String courseName) throws CourseNotFoundException {
        for (Course course : courseList) {
            if (Objects.equals(course.getName(), courseName)) {
                return course;
            }
        }
        throw new CourseNotFoundException();
    }


    //EXPERIMENTAL ADDITON
    public int ExamCountInCourse(String courseName){
        try {
            Course course = FindCourse(courseName);
            return course.ExamCount();
        }
        catch (CourseNotFoundException LNFE){
            System.out.println("Course not found");
        }
        return 0;
    }

    public int ExamCountTotal(){
        int count = 0;
        for (Course course : courseList) {
            count += course.ExamCount();
        }
        return count;
    }
}
