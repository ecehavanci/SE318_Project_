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

            //Every user is stored in UserList.txt, if it is already stored system does not try to store again
            if (!DoesUserExistInStoringFiles(schoolID)) {
                FileWriter fw = new FileWriter(userListText, true);

                //Every user has their course list, the course list text file is created here
                File CourseListText = new File(schoolID + "_CourseList.txt");
                CourseListText.createNewFile();

                //Instructors are stored in the following manner: instructor,ID,password,name,surname
                fw.write("instructor," + schoolID + "," + password + "," + name + "," + surname + System.getProperty("line.separator"));
                fw.close();
            }
            User newUser = new Instructor(name, surname, schoolID, password);
            userList.add(newUser);
            TextColours.writeBlue("Registration successful.");


        } catch (UserAlreadyExistsException UAEE) {
            System.out.println("This school id already exists, please try again.");
        } catch (IOException e) {
            System.out.println("We cannot register you right now, please try again later.");
        }
    }

    public void registerStudent(String name, String surname, int schoolID, String password) {
        try {
            CheckUserExists(schoolID);

            //Every user is stored in UserList.txt, if it is already stored system does not try to store again
            if (!DoesUserExistInStoringFiles(schoolID)) {
                FileWriter fw = new FileWriter(userListText, true);

                //Every user has their course list, the course list text file is created here
                File CourseListText = new File(schoolID + "_CourseList.txt");
                CourseListText.createNewFile();

                //Students are stored in the following manner: student,ID,password,name,surname
                fw.write("student," + schoolID + "," + password + "," + name + "," + surname + System.getProperty("line.separator"));
                fw.close();
            }
            User newUser = new Student(name, surname, schoolID, password);
            userList.add(newUser);
            TextColours.writeBlue("Registration successful.");


        } catch (UserAlreadyExistsException UAEE) {
            TextColours.writeYellow("This school id already exists, please try again.");
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
                //Adding to text file CourseList.txt (we store all courses here) so it stays permanently
                FileWriter fw = new FileWriter("CourseList.txt", true);

                //Every course have their examList that is created using their names: courseName_ExamList.txt (because no
                //two exams can have the same name)
                File examListText = new File(course.getName() + "_ExamsList.txt");
                examListText.createNewFile();

                fw.write(course.getName() + System.getProperty("line.separator"));

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
        File userListFile = new File("UserList.txt");
        BufferedReader userReader = new BufferedReader(new FileReader("UserList.txt"));
        String data;
        while ((data = userReader.readLine()) != null) {
            String[] dataArray = data.split(",");

            if (dataArray[0].equals("instructor")) {
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
                    File usersCourseList = new File(fileName);
                    BufferedReader courseReader = new BufferedReader(new FileReader(usersCourseList));

                    //All course names are read from a particular user's courseList and added to their courses in the system
                    String courseLine;
                    while ((courseLine = courseReader.readLine()) != null) {
                        String[] dataArray2 = courseLine.split(",");
                        Course c = null;
                        try {
                            c = instructor.AddAndReturnCourse(dataArray2[0]);
                            if (!DoesCourseExists(c.getName())) {
                                courseList.add(c);
                            }

                            //All exam names are read from a particular course's examList and added to their exams in the system

                            BufferedReader examReader = new BufferedReader(new FileReader(c.getName() + "_ExamsList.txt"));
                            String examLine;
                            while ((examLine = examReader.readLine()) != null) {
                                String[] examInfo = examLine.split(",");

                                Exam exam = new Exam();

                                exam.SetType(examInfo[1]);
                                String[] dateInfo = examInfo[2].split("\\.");
                                int[] dateInfoAsInt = new int[dateInfo.length];
                                for (int i = 0; i < dateInfo.length; i++) {
                                    dateInfoAsInt[i] = Integer.parseInt(dateInfo[i]);
                                }
                                int[] dateArray = new int[]{dateInfoAsInt[0], dateInfoAsInt[1], dateInfoAsInt[2]};

                                exam.SetEndDateAndTime(dateArray, dateInfoAsInt[3], dateInfoAsInt[4]);
                                exam.SetStartDateAndTime(dateArray, dateInfoAsInt[5], dateInfoAsInt[6]);
                                exam.setDuration(dateInfoAsInt[7]);

                                exam.SetPoint(Integer.parseInt(examInfo[3]));
                                boolean willAddExam = true;
                                for (Exam ex : c.getExamList()) {
                                    if (exam.getLocalDateTime().isEqual(ex.getLocalDateTime())) {
                                        willAddExam = false;
                                    }
                                }
                                if (willAddExam) {
                                    //Since database importing should not print out anything I use this method instead of course.AddExam()
                                    c.AddExam(exam);
                                }

                                //Loading questions and answers to system
                                BufferedReader questionReader = new BufferedReader(new FileReader(c.getName() + "_" + examInfo[0] + "_QnA_List.txt"));
                                String questionLine;
                                while ((questionLine = questionReader.readLine()) != null) {
                                    String[] questionInfo = questionLine.split("@");

                                    if (questionInfo[0].equals("t")) {//If it is a text based question
                                        TextBasedAnswer ta = new TextBasedAnswer(questionInfo[2]);
                                        exam.AddQuestion(questionInfo[1], ta, Integer.parseInt(questionInfo[3]), false);
                                    } else if (questionInfo[0].equals("m")) {//If it is a multiple choice question
                                        MultipleChoiceAnswer ma = new MultipleChoiceAnswer();

                                        String[] choiceArr = questionInfo[2].split("\\?");
                                        for (int i = 0; i < choiceArr.length; i++) {
                                            ma.addChoice(new Choice(choiceArr[i], 65 + i == (int) questionInfo[3].charAt(0)));
                                        }
                                        ma.setCorrectAnswer(questionInfo[3]);
                                        exam.AddQuestion(questionInfo[1], ma, Integer.parseInt(questionInfo[4]), true);
                                    } else if (questionInfo[0].equals("f")) {//If it is a true/false question
                                        TrueFalseAnswer fa = new TrueFalseAnswer(questionInfo[2].charAt(0));
                                        exam.AddQuestion(questionInfo[1], fa, Integer.parseInt(questionInfo[3]), true);
                                    }
                                }


                            }

                        } catch (CourseAlreadyExistsException e) {
                            //No problem even such an error is occurred, in this method we expect no output, no warning, etc.
                        } catch (ExamCannotBeAddedException e) {
                            e.printStackTrace();
                        }
                    }
                    courseReader.close();

                }

            } else if (dataArray[0].equals("student")) {
                int ID = Integer.parseInt(dataArray[1]);

                //Here a student is created with given data in UserList.txt line by line
                userList.add(new Student(dataArray[3], dataArray[4], ID, dataArray[2]));

                Student student = null;
                try {
                    student = (Student) FindUser(ID);
                } catch (NullPointerException NPE) {
                    System.out.println("User not found");
                }
                if (student != null) {
                    //dataArray[1] + "_CourseList" is the data which points to a text file that all names of courses student is taking is stored
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
                TextColours.writeRed("Problem with database importing!!!!!");
                break;
            }
        }

        //Loading students' sheets to system
        for (Course c : courseList){
            for (Exam e : c.getExamList()){
                BufferedReader examStudentListReader = new BufferedReader(new FileReader(c.getName() + "_" + e.getID() + "_StudentList.txt"));
                String studentLine;
                while ((studentLine = examStudentListReader.readLine()) != null) {
                    String[] studentInfo = studentLine.split(",");

                    BufferedReader sheetReader = new BufferedReader(new FileReader(c.getName() + "_" + e.getID() + "_" + studentInfo[0] + "_Sheet.txt"));
                    String sheetLine;

                    ArrayList <String> answers = new ArrayList<>();
                    ArrayList <Integer> points = new ArrayList<>();

                    int totalPoint=0;
                    while ((sheetLine = sheetReader.readLine()) != null) {
                        String[] sheetInfo = sheetLine.split("#");
                        answers.add(sheetInfo[0]);
                        points.add(Integer.parseInt(sheetInfo[1]));
                        totalPoint+=Integer.parseInt(sheetInfo[1])==-1?0:Integer.parseInt(sheetInfo[1]);
                    }
                    StudentSheet sheet = new StudentSheet((Student)FindUser(Integer.parseInt(studentInfo[0])),answers,points);
                    sheet.setApproved(studentInfo[1].equals("a"));
                    e.AddStudentSheet(sheet);
                    sheet.setGrade(totalPoint);
                }
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

    public boolean DoesCourseExistInStoringFiles(String courseName, String textName) throws IOException {
        //This function seeks course in CourseList.txt and evaluates true or false according to if file is found or not
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
        //This function seeks user in UserList.txt and evaluates true or false according to if file is found or not
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

   /* public int ReturnOrCreateCourseIndex(Course course) {
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
    }*/

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

class ExamCannotBeAddedException extends Exception {
    public ExamCannotBeAddedException() {

    }
}

class TextColours {
    public static final String blue = "\u001B[34m";
    public static final String red = "\u001B[31m";
    public static final String purple = "\u001B[35m";
    public static final String green = "\u001B[32m";
    public static final String yellow = "\u001B[33m";
    public static final String reset = "\u001B[0m";

    public static void writeBlue(String text) {
        System.out.println(blue + text + reset);
    }

    public static void writeRed(String text) {
        System.out.println(red + text + reset);

    }

    public static void writePurple(String text) {
        System.out.println(purple + text + reset);
    }

    public static void writeGreen(String text) {
        System.out.println(green + text + reset);
    }

    public static void writeYellow(String text) {
        System.out.println(yellow + text + reset);
    }
}