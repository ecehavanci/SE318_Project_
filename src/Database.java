import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Database {
    List<User> userList = new ArrayList<>();
    List<Course> courseList = new ArrayList<>();
    File userListText = new File("UserList.txt");
    File CourseListText = new File("CourseList.txt");

    private Database() {
    }

    private static Database database = null;

    public static Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public void registerInstructor(String name, String surname, int schoolID, String password) {
        try {
            CheckUserExists(schoolID);

            FileWriter fw = new FileWriter(userListText, true);

            File CourseListText = new File(schoolID + "_CourseList.txt");
            CourseListText.createNewFile();

            fw.write("instructor," + schoolID + "," + password + "," + name + "," + surname + "," + CourseListText.getName() + "\n");
            fw.close();

            User newUser = new Instructor(name, surname, schoolID, password);
            userList.add(newUser);
            System.out.println("Registration successful.");

        } catch (UserAlreadyExistsException UAEE) {
            System.out.println("This school id already exists, please try again.");
        } catch (IOException e) {
            System.out.println("We cannot register you right now, please try again later.");
        }
    }

    public void registerStudent(String name, String surname, int schoolID, String password) {
        try {
            CheckUserExists(schoolID);
            FileWriter fw = new FileWriter(userListText, true);

            File CourseListText = new File(schoolID + "_CourseList.txt");
            CourseListText.createNewFile();

            fw.write("student," + schoolID + "," + password + "," + name + "," + surname + "," + CourseListText.getName() + "\n");
            fw.close();

            CheckUserExists(schoolID);
            User newUser = new Student(name, surname, schoolID, password);
            userList.add(newUser);
            System.out.println("Registration successful.");
        } catch (UserAlreadyExistsException UAEE) {
            System.out.println("This school id already exists, please try again.");
        } catch (IOException e) {
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
            } else {
                if (userList.get(userList.size() - 1) == user) {
                    throw new WrongIDException();
                }
            }
        }
        return null;
    }

    public void AddCourse(Course course) throws IOException {
        System.out.println("Adding course...");
        if (!DoesCourseExists(course.getName())) {
            if (!DoesCourseExistInStoringFiles(course.getName())) {
                //Adding to text files, so it stays permanently
                FileWriter fw = new FileWriter("CourseList.txt", true);

                File examListText = new File(course.getName() + "_examsList.txt");
                examListText.createNewFile();

                File instructorListText = new File(course.getName() + "_instructorList.txt");
                instructorListText.createNewFile();

                fw.write(course.getName() + "," + instructorListText.getName() + "," + examListText.getName() + "\n");


                FileWriter fw2 = new FileWriter(instructorListText, true);
                fw2.write(ReturnUserIndex(course.getFirstInstructor()));

                fw.close();
                fw2.close();
            }
            //Adding to system for this run
            courseList.add(course);
        } else System.out.println("failed");
    }

    //This function seeks a course with the given name in the CourseList of database.
    public Course FindCourse(String courseName) throws CourseNotFoundException {
        for (Course course : courseList) {
            if (Objects.equals(course.getName(), courseName)) {
                return course;
            }
        }
        throw new CourseNotFoundException();
    }

    public boolean DoesCourseExists(String CourseName) {
        for (Course Course : courseList) {
            if (Objects.equals(Course.getName(), CourseName)) {
                return true;
            }
        }
        return false;
    }

    private void CheckUserExists(int ID) throws UserAlreadyExistsException {
        for (User user : userList) {
            if (user.getSchoolID() == ID) {
                throw new UserAlreadyExistsException();
            }
        }
    }

    public void showCourses() {
        //System.out.println("Courses in database");
        for (Course Course : courseList) {
            System.out.println(Course.getName());
        }
    }

    public void IMPORT() throws IOException, CourseAlreadyExistsException {
        BufferedReader br = new BufferedReader(new FileReader(userListText));
        String st;
        while ((st = br.readLine()) != null) {
            String[] dataArray = st.split(",");
            System.out.println(Arrays.toString(dataArray));
            if (dataArray[0].equals("instructor")) {
                int ID = Integer.parseInt(dataArray[2]);
                userList.add(new Instructor(dataArray[3], dataArray[4], ID, dataArray[2]));

                Instructor instructor = null;
                try {
                    instructor = (Instructor) FindUser(ID);
                } catch (NullPointerException NPE) {
                    System.out.println("User not found");
                }
                if (instructor != null) {
                    File usersCourseList = new File(dataArray[5]);
                    BufferedReader br2 = new BufferedReader(new FileReader(usersCourseList));
                    String data;
                    while ((data = br2.readLine()) != null) {
                        String[] dataArray2 = data.split(",");
                        Course c = instructor.AddAndReturnCourse(dataArray2[0]);
                        courseList.add(c);
                        c.AddInstructor(instructor);

                        if (DoesCourseExistInStoringFiles(c.getName())) {
                            AddCourse(c);
                        }
                    }
                    br2.close();

                }

            } else if (dataArray[0].equals("student")) {
                int ID = Integer.parseInt(dataArray[2]);
                userList.add(new Student(dataArray[3], dataArray[4], ID, dataArray[2]));

                Student student = null;
                try {
                    student = (Student) FindUser(ID);
                } catch (NullPointerException NPE) {
                    System.out.println("User not found");
                }
                if (student != null) {
                    File usersCourseList = new File(dataArray[5]);

                    BufferedReader br2 = new BufferedReader(new FileReader(usersCourseList));
                    String data;
                    while ((data = br2.readLine()) != null) {
                        String[] dataArray2 = data.split(",");
                        student.courseList.add(new Course(dataArray2[0], (Instructor) FindUser(Integer.parseInt(dataArray2[1]))));
                    }
                    br2.close();
                }
            } else {
                System.out.println("Problem with database importing!!!!!");
                break;
            }


        }
    }


    private User FindUser(int ID) {
        for (User user : userList) {
            if (user.getSchoolID() == ID) {
                return user;
            }
        }
        return null;
    }

    public boolean DoesCourseExistInStoringFiles(String courseName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("CourseList.txt"));
        String st;
        while ((st = br.readLine()) != null) {
            String[] dataArray = st.split(",");
            if (dataArray[0].equals(courseName)) {
                return true;
            }
        }
        return false;
    }

    public boolean DoesUserExistInStoringFiles(int ID) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("UserList.txt"));
        String st;
        while ((st = br.readLine()) != null) {
            String[] dataArray = st.split(",");
            if (ID == Integer.parseInt(dataArray[0])) {
                return true;
            }
        }
        return false;
    }

    public int ReturnUserIndex(User user) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getSchoolID() == user.getSchoolID()) {
                return i;
            }
        }
        return -1;
    }

    public int ReturnOrCreateCourseIndex(Course course) {
        int index = -1;
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getName().equals(course.getName())) {
                return i;
            }
            index++;
        }

        //If course index is not found, this means it is going to be created almost immediately this function is called.
        //So here index + 1 is referred to its position when it will be created.
        return (index + 1);
    }

    public int ReturnCourseIndex(Course course) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getName().equals(course.getName())) {
                return i;
            }
        }
        return -1;
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

class CourseNotFoundException extends Exception {
    public CourseNotFoundException() {
    }
}

class CourseAlreadyExistsException extends Exception {
    public CourseAlreadyExistsException() {
    }
}

class WrongChoiceException extends Exception {
    public WrongChoiceException() {
        System.out.println("");
    }
}

class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
    }
}