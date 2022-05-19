import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            if (!DoesUserExistInStoringFiles(schoolID)) {
                FileWriter fw = new FileWriter(userListText, true);

                File CourseListText = new File(schoolID + "_CourseList.txt");
                System.out.println(CourseListText.createNewFile());

                fw.write("instructor," + schoolID + "," + password + "," + name + "," + surname + System.getProperty("line.separator"));
                fw.close();
            }
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

            if (!DoesUserExistInStoringFiles(schoolID)) {
                FileWriter fw = new FileWriter(userListText, true);

                File CourseListText = new File(schoolID + "_CourseList.txt");
                CourseListText.createNewFile();


                fw.write("student," + schoolID + "," + password + "," + name + "," + surname + System.getProperty("line.separator"));
                fw.close();
            }
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

                File examListText = new File(course.getName() + "_ExamsList.txt");
                examListText.createNewFile();

                //File instructorListText = new File(course.getName() + "_InstructorList.txt");
                //instructorListText.createNewFile();

                fw.write(course.getName() /*+ "," + instructorListText.getName() + "," + examListText.getName()*/ + System.getProperty("line.separator"));

                fw.close();
            }
            //Adding to system for this run
            courseList.add(course);
        } else System.out.println("This course already exist in database, you are added as instructor.");
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

    public void IMPORT() throws IOException {
        //We have a storing system using text files, import function loads the data in text files into corresponding places in system

        //First users are loaded into system with their data
        BufferedReader userReader = new BufferedReader(new FileReader("UserList.txt"));
        String data;
        while ((data = userReader.readLine()) != null) {
            String[] dataArray = data.split(",");
            System.out.println(Arrays.toString(dataArray));
            if (dataArray[0].equals("instructor")) {
                System.out.println(TextColours.blue + "Importing an instructor..." + TextColours.reset);

                int ID = Integer.parseInt(dataArray[1]);

                //Here an instructor is created with given data in UserList.txt line by line
                userList.add(new Instructor(dataArray[3], dataArray[4], ID, dataArray[2]));

                Instructor instructor = null;

                //There is no way user is not found since it is created just a second ago in the above, but this condition holds
                //only if system has no error. So it notifies us if something is wrong
                try {
                    instructor = (Instructor) FindUser(ID);
                } catch (NullPointerException NPE) {
                    TextColours.writeRed("User not found");
                }
                if (instructor != null) {
                    //dataArray[1] + "_CourseList" is the data which points to a text file that all names of courses instructor is giving is stored
                    String fileName = ID + "_CourseList.txt";
                    System.out.println("ID: " + ID);
                    File usersCourseList = new File(fileName);
                    BufferedReader courseReader = new BufferedReader(new FileReader(usersCourseList));
                    String courseLine;
                    while ((courseLine = courseReader.readLine()) != null) {
                        String[] dataArray2 = courseLine.split(",");
                        Course c = null;
                        try {
                            c = instructor.AddAndReturnCourse(dataArray2[0]);
                            if (!DoesCourseExists(c.getName())){
                                courseList.add(c);
                            }

                            BufferedReader examReader = new BufferedReader(new FileReader(c.getName() + "_ExamsList.txt"));
                            String examLine;
                            while ((examLine = examReader.readLine()) != null){
                                System.out.println(examLine);
                                String[] examInfo = examLine.split(",");
                                Exam exam = new Exam();
                                //examType,examDate,examPoint,courseName_SheetList.txt,courseName_QnA_List.txt
                                exam.SetType(examInfo[1]);
                                String [] dateInfo = examInfo[2].split("\\.");
                                int [] dateInfoAsInt = new int[dateInfo.length];
                                for (int i = 0; i <dateInfo.length ; i++) {
                                    dateInfoAsInt[i] = Integer.parseInt(dateInfo[i]);
                                }
                                int [] dateArray = new int[]{dateInfoAsInt[0],dateInfoAsInt[1],dateInfoAsInt[2]};
                                System.out.println(Arrays.toString(dateInfo));
                                System.out.println(Arrays.toString(dateInfoAsInt));

                                exam.SetDateAndTime(dateArray,dateInfoAsInt[3],dateInfoAsInt[4]);
                                exam.SetPoint(Integer.parseInt(examInfo[3]));
                                c.GetExamList().add(exam);
                            }

                            //c.AddInstructor(instructor);
                            /*if (!DoesCourseExistInStoringFiles(c.getName())) {
                                AddCourse(c, instructor);
                            }*/
                        } catch (CourseAlreadyExistsException e) {
                            //No problem even such an error is occurred, in this method we expect no output, no warning, etc.
                        }
                    }
                    courseReader.close();

                }

            } else if (dataArray[0].equals("student")) {
                System.out.println(data);

                int ID = Integer.parseInt(dataArray[2]);
                userList.add(new Student(dataArray[3], dataArray[4], ID, dataArray[2]));

                Student student = null;
                try {
                    student = (Student) FindUser(ID);
                } catch (NullPointerException NPE) {
                    System.out.println("User not found");
                }
                if (student != null) {
                    File usersCourseList = new File(dataArray[1] + "_CourseList.txt");
                    BufferedReader courseReader = new BufferedReader(new FileReader(usersCourseList));
                    String data2;
                    while ((data2 = courseReader.readLine()) != null) {
                        try {
                            student.courseList.add(FindCourse(data2));
                        } catch (CourseNotFoundException e) {
                            System.out.println("Course not found");
                        }
                    }
                    courseReader.close();
                }

            } else {
                System.out.println("Problem with database importing!!!!!");
                break;
            }
            System.out.println(TextColours.blue + "Now we are supposed to go next line and at the end we have an output..." + TextColours.reset);
        }
        System.out.println("\n\n\n");

        System.out.println(TextColours.blue + "SURPRISE!" + TextColours.reset);

        FileReader fr = new FileReader(userListText);
        BufferedReader br = new BufferedReader(fr);
        List<String> myfilevar = Files.readAllLines(Paths.get("UserList.txt"));
        System.out.println(myfilevar);
        String st;

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

    public boolean DoesCourseExistInStoringFiles(String courseName, String textName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(textName));
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
            if (ID == Integer.parseInt(dataArray[1])) {
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

class TextColours {
    public static final String blue = "\u001B[34m";
    public static final String red = "\u001B[31m";
    public static final String purple = "\u001B[35m";
    public static final String green = "\u001B[32m";
    public static final String yellow = "\u001B[33m";
    public static final String reset = "\u001B[0m";

    public static void writeBlue(String text){
        System.out.println(blue + text + reset);
    }

    public static void writeRed(String text){
        System.out.println(red + text + reset);
    }


}