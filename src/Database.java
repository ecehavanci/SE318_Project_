import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database {
    List<User> userList = new ArrayList<>();
    List<Lesson> lessonList = new ArrayList<>();

    public void registerInstructor(String name, String surname, int schoolID, String password) {
        try {
            CheckUserExists(schoolID);
            User newUser = new Instructor(name, surname, schoolID, password);
            userList.add(newUser);
            System.out.println("Registration successful.");
        } catch(UserAlreadyExistsException UAEE){
            System.out.println("This school id already exists, please try again.");
        }
        catch (Exception e) {
            System.out.println("We cannot register you right now, please try again later.");
        }
    }

    public void registerStudent(String name, String surname, int schoolID, String password) {
        try {
            CheckUserExists(schoolID);
            User newUser = new Student(name, surname, schoolID, password);
            userList.add(newUser);
            System.out.println("Registration successful.");
        } catch(UserAlreadyExistsException UAEE){
            System.out.println("This school id already exists, please try again.");
        }catch (Exception e) {
            System.out.println("We cannot register you right now, please try again later.");
        }
    }

    //User logging in process does not matter if the person is an instructor or a student, since the userList stores both.
    public User logIn(int ID, String password) throws WrongIDException, WrongPasswordException {
        for (User user : userList) {
            if (user.getSchoolID() == ID) {
                if (user.getPassword().equals(password)) {
                    return user;
                } else {
                    throw new WrongPasswordException();
                }
            }
            else {
                if (userList.get(userList.size() - 1) == user) {
                    throw new WrongIDException();
                }
            }
        }
        return null;
    }

    public void addLesson(Lesson lesson) {
        lessonList.add(lesson);
    }

    //This function seeks a lesson with the given name in the lessonList of database.
    public Lesson FindLesson(String lessonName) throws LessonNotFoundException {
        for (Lesson lesson : lessonList) {
            if (Objects.equals(lesson.getName(), lessonName)) {
                return lesson;
            }
        }
        throw new LessonNotFoundException();
    }

    private void CheckUserExists(int ID) throws UserAlreadyExistsException {
        for (User user: userList) {
            if (user.getSchoolID()==ID) {
                throw new UserAlreadyExistsException();
            }
        }
    }

    public void showLessons() {
        //System.out.println("Lessons in database");
        for (Lesson lesson : lessonList) {
            System.out.println(lesson.getName());
        }
    }

}

class WrongIDException extends Exception {
    public WrongIDException() {
    }
}

class WrongPasswordException extends Exception {
    public WrongPasswordException() {
    }
}

class LessonNotFoundException extends Exception {
    public LessonNotFoundException() {
    }
}

class LessonAlreadyExistsException extends Exception {
    public LessonAlreadyExistsException() {
    }
}

class WrongChoiceException extends Exception{
    public WrongChoiceException(){
        System.out.println("");
    }
}

class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
    }
}