
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        Database database = Database.getInstance();

        try {
            database.IMPORT();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot initiate system. Exitting...");
            System.exit(0);
        }

        System.out.println("~Welcome to Exam Management System~");
        //THIS METHOD HAS NOTHING TO DO WITH ACTUAL SYSTEM IMPLEMENTATION AND IS ONLY FOR DEBUGGING PURPOSES
        //IMPORT_DEFAULTS();


        while (true) {

            for (User user : database.userList) {
                System.out.println(user.getName());
            }
            User user = ShowMainMenu(database);

            System.out.println("Welcome, " + user.getName() + ".");

            if (Objects.equals(user.getToken(), "student")) {

                //TODO: Show courses
                //TODO: Add logging out when structure is formed

                Student student = (Student) user;
                while (true) {
                    boolean willLogOut = false;

                    System.out.println("Please choose what you would like to do");
                    System.out.println("1) Enroll/Unenroll in courses");
                    System.out.println("2) List all enrolled course details");
                    System.out.println("3) See all exam informations");
                    System.out.println("4) Log out");

                    //EXPERIMENTAL ADDITION
                    System.out.println("5) Take Exam");


                    int insChoice = scan.nextInt();
                    scan.nextLine();

                    switch (insChoice) {
                        case 1 -> {
                            try {
                                enrollUnroll(database, student);
                            } catch (WrongChoiceException WCE) {
                                System.out.println("Unexpected input");
                            }

                        }
                        case 2 -> {
                            //list all enrolled courses in std class
                            student.printCourses();

                        }
                        case 3 -> {
                            seeExamInfo(database, student);
                        }
                        case 4 -> {
                            System.out.println("Logging out...");
                            willLogOut = true;
                        }

                        //EXPERIMENTAL ADDITION
                        case 5 -> {
                            //System.out.println("Your upcoming exams:");
                            if (student.ExamCountTotal() > 0) {
                                student.getExams();

                                System.out.println("Which course's exam do you want to take?");
                                String courseName = scan.nextLine();
                                if (student.ExamCountInCourse(courseName) > 0) {
                                    Course course = null;
                                    try {
                                        course = database.FindCourse(courseName);
                                    } catch (CourseNotFoundException LNFE) {
                                        System.out.println("Course not found");
                                    }

                                    if (course != null) {
                                        System.out.println("Which exam do you want to take?");
                                        int examIndex = scan.nextInt();
                                        scan.nextLine();
                                        try {
                                            Exam exam = course.GetExam(examIndex - 1);
                                            exam.Take(student);
                                        } catch (IndexOutOfBoundsException IOOBE) {
                                            System.out.println("There is no exam in the given index");
                                        }
                                    }
                                }
                            } else {
                                System.out.println("You don't have any exams");
                            }
                        }
                    }
                    if (willLogOut) {
                        break;
                    }
                }


            } else if (Objects.equals(user.getToken(), "instructor")) {
                Instructor instructor = (Instructor) user;
                while (true) {
                    boolean willLogOut = false;
                    //Instructors see their courses when they first logged in
                    System.out.println();
                    instructor.printCourses();
                    //TODO: Add logging out when structure is formed

                    System.out.println();

                    //Instructors have 6 options as follows:
                    System.out.println("Please choose what you would like to do");
                    System.out.println("1) Add course");
                    System.out.println("2) See and set details of a course");
                    System.out.println("3) Build up an exam");
                    System.out.println("4) Grade or approve exams");
                    System.out.println("5) See the average score of the exam");
                    System.out.println("6) Log out");


                    //They choose what they want to do
                    int insChoice = scan.nextInt();
                    scan.nextLine();

                    switch (insChoice) {
                        case 1 -> {
                            System.out.print("Name of the course: ");
                            String courseName = scan.nextLine();

                            //Instructor has a course list, he/she creates and ads a course to his/her course list with given name
                            try {
                                Course course = instructor.AddAndReturnCourse(courseName);
                                database.AddCourse(course);
                            } catch (CourseAlreadyExistsException LAEE) {
                                System.out.println("Course already exists");
                            } /*catch (IOException e) {
                                System.out.println("Course cannot be added to system right now, please try again later");
                            }*/

                        }
                        case 2 -> {
                            System.out.print("Name of the course: ");
                            String courseName = scan.nextLine();
                            try {
                                //This action shows all the courses, instructor gives and then enables instructor to change their details
                                instructor.ShowCourseDetails(courseName);
                            } catch (CourseNotFoundException LNFE) {
                                System.out.println("Course not found");
                            }

                        }
                        case 3 -> {
                            try {
                                //This action enables instructor to build an exam with desired questions in it
                                BuildUpExam(instructor);
                            } catch (CourseNotFoundException LNFE) {
                                System.out.println("Course not found");
                            }

                        }
                        case 4 -> {
                            //TODO: Grade or approve exams
                            for (int i = 0; i < instructor.courseList.size(); i++) {
                                instructor.courseList.get(i).ShowExamDetails();
                            }

                            System.out.println("Which course's exam do you want to grade: ");
                            String courseName = scan.nextLine();

                            System.out.println("Which exam do you want to grade: ");
                            int examIndex = scan.nextInt();
                            scan.nextLine();

                            instructor.GradeUnevaluatedQuestions(courseName, examIndex);
                        }
                        case 5 -> {
                            System.out.println("Which course's would you like to see the average grade?");
                            String courseName = scan.nextLine();

                            System.out.println("Which exam do you want to see: ");
                            int examIndex = scan.nextInt();
                            scan.nextLine();

                            instructor.ShowExamStatistics(courseName, examIndex);
                        }
                        case 6 -> {
                            System.out.println("Logging out...");
                            willLogOut = true;
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                    if (willLogOut) {
                        break;
                    }
                }
            }
        }
    }

    private static void seeExamInfo(Database database, Student student) {
        Scanner scan = new Scanner(System.in);
        student.getExams();
    }

    public static void BuildUpExam(Instructor inst) throws CourseNotFoundException {
        Scanner scan = new Scanner(System.in);
        //Courses have exams so we get here a course name to add that exam to that course
        System.out.println("Please enter the name of the course that you want to add an exam to:");
        String courseName = scan.nextLine();

        Course course = null;
        Exam exam = new Exam();

        course = inst.FindCourse(courseName);
        boolean isDone = false;
        boolean isCancel = false;
        int examPointTotal = 0;

        //We sequentially add questions (along with their answers if desired) to the exam
        while (true) {
            System.out.println("What kind of question would you like to add?");
            System.out.println("1) Classical type question (open ended)");
            System.out.println("2) Multiple choice question");
            System.out.println("3) True/False question");
            System.out.println("4) Finish exam building");
            System.out.println("5) Cancel exam building");

            int examChoice = scan.nextInt();
            scan.nextLine();

            switch (examChoice) {
                case 1 -> {
                    //Question to be stored:
                    System.out.println("Question: ");
                    String questionText = scan.nextLine();

                    System.out.println("Would you like to enter an answer to this classical question to help you grading? Yes/No ");
                    String answerChoice = scan.nextLine();
                    answerChoice = answerChoice.toUpperCase(Locale.ROOT);

                    String answerText = null;
                    if (answerChoice.equals("YES")) {
                        //Answer to be stored along with the question:
                        System.out.println("Please enter the answer");
                        answerText = scan.nextLine();
                    }

                    System.out.println("How many points is this question?");
                    int point = scan.nextInt();
                    scan.nextLine();
                    examPointTotal += point;

                    //A classical question (and its answer if desired) is created since choice 1 indicates classical question
                    ClassicalAnswer ca = new ClassicalAnswer(answerText);
                    exam.AddQuestion(questionText, ca, point, false);


                }
                case 2 -> {
                    //Question to be stored:
                    System.out.println("Question: ");
                    String questionText = scan.nextLine();

                    //Instructor can specify how many choices a multiple choice question has
                    System.out.println("How many choices are there?");
                    int choiceCount = scan.nextInt();
                    scan.nextLine();

                    MultipleChoiceAnswer mca = new MultipleChoiceAnswer();

                    List<String> choiceList = new ArrayList<>();
                    //Instructor writes all choices one by one:
                    for (int i = 0; i < choiceCount; i++) {
                        System.out.println("Enter choice " + (char) (65 + i) + ":");
                        String choice = scan.nextLine();
                        choiceList.add(choice);
                    }

                    //Instructor chooses the right one among all choices:
                    char choiceCode;
                    while (true) {
                        System.out.println("Which choice is correct?");
                        choiceCode = scan.nextLine().charAt(0);

                        if ((int) choiceCode > (64 + choiceCount)) {
                            System.out.println("There is no choice " + choiceCode + ". Please enter again.");
                        } else {
                            break;
                        }
                    }

                    for (int i = 0; i < choiceCount; i++) {
                        //Choices are added one by one with their status (if they are right or wrong answer)
                        mca.addChoice(new Choice(choiceList.get(i), (int) choiceCode == 65 + i));
                    }
                    //this is for storing right answer as String to help grading
                    mca.setRightAnswer(Character.toString(choiceCode));

                    System.out.println("How many points is this question?");
                    int point = scan.nextInt();
                    scan.nextLine();
                    examPointTotal += point;

                    //Multiple choice question is added along with answer and the point of it
                    exam.AddQuestion(questionText, mca, point, true);
                }
                case 3 -> {
                    System.out.println("Question: ");
                    //scan.nextLine();
                    String questionText = scan.nextLine();

                    System.out.println("Right Answer: ");
                    //scan.nextLine();
                    char answer = scan.nextLine().charAt(0);

                    TrueFalseAnswer tfa = new TrueFalseAnswer(answer);

                    System.out.println("How many points is this question?");
                    int point = scan.nextInt();
                    scan.nextLine();
                    examPointTotal += point;

                    //True/false question is added along with answer and the point of it
                    exam.AddQuestion(questionText, tfa, point, true);
                }

                case 4 -> isDone = true;
                case 5 -> isCancel = true;
                default -> System.out.println("Please enter a valid choice");
            }
            if (isDone || isCancel) {
                break;
            }

        }
        if (isDone) {
            //When instructor is done with adding the exam questions, he/she has to specify the date and type of the exam
            System.out.print("Please enter a date: ");
            SetDate(exam);

            //The attribute could be final, quiz, midterm, etc.
            System.out.print("Please enter the type of the exam: ");
            String examType = scan.nextLine();
            exam.EditType(examType);

            exam.SetPoint(examPointTotal);
            try {
                course.AddExam(exam);
                System.out.println("Exam is successfully added");
            } catch (Exception e) {
                System.out.println("An error occurred while adding the exam, please try again later.");
            }
        }
    }

    public static User ShowMainMenu(Database db) {
        User user = null;
        while (true) {
            //This loop continues until a user signs in
            boolean isSignedIn = false;

            System.out.println("Please choose what you would like to do");
            System.out.println("1) Sign up");
            System.out.println("2) Sign in");
            System.out.println("3) Exit the system");

            Scanner scan = new Scanner(System.in);
            int signingChoice = scan.nextInt();

            switch (signingChoice) {
                case 1 -> {
                    //Both users (instructor and student) sign up with their school id, password, name and surname
                    //Both users sign in with their school id and password
                    System.out.println("What do you want to sign up as?");
                    System.out.println("1) Instructor");
                    System.out.println("2) Student");
                    System.out.println("3) Cancel registration");

                    //User chooses what he/she wants to sign up as:
                    int signingUpChoice = scan.nextInt();

                    if (signingUpChoice == 3) {
                        break;
                    }

                    if (signingUpChoice == 1 || signingUpChoice == 2) {
                        int ID;
                        while (true) {
                            System.out.println("School ID: ");
                            scan.nextLine();
                            try {
                                ID = scan.nextInt();
                                scan.nextLine();
                                break;
                            } catch (InputMismatchException IME) {
                                System.out.println("ID should be a number, please try again.");
                            }
                        }

                        System.out.println("Password: ");
                        String password = scan.nextLine();
                        System.out.println("Name: ");
                        String name = scan.nextLine();
                        System.out.println("Surname: ");
                        String surname = scan.nextLine();

                        switch (signingUpChoice) {
                            case 1 -> db.registerInstructor(name, surname, ID, password);
                            case 2 -> db.registerStudent(name, surname, ID, password);
                        }
                    } else System.out.println("Invalid choice.");


                }
                case 2 -> {
                    if (db.userList.size() == 0) {
                        System.out.println("No account created for Exam Management System yet. Please create an account for logging in.");
                        continue;
                    }
                    System.out.println("School ID: ");
                    int ID = scan.nextInt();
                    scan.nextLine();
                    System.out.println("Password: ");
                    String password = scan.nextLine();
                    try {
                        user = db.logIn(ID, password);
                        isSignedIn = true;
                    } catch (WrongIDException WIex) {
                        System.out.println("ID Not Found");
                    } catch (WrongPasswordException WPex) {
                        System.out.println("Wrong Password");
                    } catch (NullPointerException nullEx) {
                        System.out.println("Please try again");
                    }
                }
                case 3 -> {
                    System.out.println("Thank you for using Exam Management System. \nExiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
            if (isSignedIn) {
                return user;
            }
        }
    }

    public static void SetDate(Exam exam) {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String date = scan.nextLine();
            String[] splitDate = date.contains("/") ? date.split("/") : date.contains(".") ? date.split("\\.") : null;
            if (splitDate == null) {
                System.out.println("Please enter a valid date");
            } else {
                if (splitDate.length != 3) {
                    System.out.println("Please enter a valid date");
                    continue;
                }
                int[] dateParts = new int[3];
                for (int i = 0; i < 3; i++) {
                    dateParts[i] = Integer.parseInt(splitDate[i]);
                }
                int hour;
                int minute;
                while (true) {
                    System.out.print("Please enter the time of the exam (HH:MM): ");
                    String time = scan.nextLine();
                    String[] splitTime = time.split(":");

                    if (splitTime.length == 2) {
                        hour = Integer.parseInt(splitTime[0]);
                        minute = Integer.parseInt(splitTime[1]);
                    } else {
                        System.out.println("Please enter a valid time");
                        continue;
                    }

                    if (hour > 23 || hour < 0 || minute > 60 || minute < 0) {
                        System.out.println("Please enter a valid time");
                    } else break;
                    TextColours.writeBlue("We are stuck");
                }

                exam.SetDateAndTime(dateParts,hour, minute);
                break;
            }
        }
    }

    public static void enrollUnroll(Database database, Student student) throws WrongChoiceException {
        Scanner scan = new Scanner(System.in);

        //Students can enroll and unenroll to a course using "enroll" or "unenroll" input
        System.out.println("Do you want to enroll or unenroll in courses?");
        String enrollChoice = scan.nextLine();

        if (enrollChoice.equals("enroll")) {// enroll according to name in list
            System.out.println("You can enroll in these courses");
            database.showCourses();//list all courses in

            System.out.println("Please choose one of the courses and write its name to enroll");
            String choosenCourse = scan.nextLine();
            try {
                Course foundCourse = database.FindCourse(choosenCourse);
                student.enrollCourse(foundCourse);
            } catch (CourseNotFoundException LNFE) {
                System.out.println("Course not found");
            }
        } else if (enrollChoice.equals("unenroll")) { // unenroll according to name in list
            System.out.println("You can unenroll from your courses");
            student.printCourses();
            String choosenCourse = scan.nextLine();
            try {
                Course foundCourse = student.FindCourse(choosenCourse);
                student.unenrollCourse(foundCourse);
            } catch (CourseNotFoundException LNFE) {
                System.out.println("Course not found");
            }
        } else
            //If an input is given other than enroll or unenroll, system throws an error
            throw new WrongChoiceException();

    }


    //THIS METHOD IS FOR HELPING US DEBUG THE PROCESS: It creates default users, courses, exams etc. to see how changes affect.

    public static void IMPORT_DEFAULTS() {
        Database db = Database.getInstance();
        //Four default users: Instructor John and students Gerard, Alice and Cheryll
        db.registerInstructor("John", "Roseland", 1, "1");
        db.registerInstructor("William", "Wood", 2, "2");
        db.registerStudent("Gerard", "Greene", 3, "3");
        db.registerStudent("Alice", "King", 4, "4");
        db.registerStudent("Cheryll", "Basket", 5, "5");

        Instructor instructorJ = (Instructor) db.userList.get(0);
        Instructor instructorW = (Instructor) db.userList.get(1);
        Student studentG = (Student) db.userList.get(2);
        Student studentA = (Student) db.userList.get(3);
        Student studentC = (Student) db.userList.get(4);

        //John gives course BIO 101.
        try {
            db.AddCourse(instructorJ.AddAndReturnCourse("BIO 101"));
            db.AddCourse(instructorW.AddAndReturnCourse("BIO 101"));
        } catch (CourseAlreadyExistsException | IOException e) {
            e.printStackTrace();
        } /*catch (IOException e) {
            System.out.println("Course cannot be added to system right now, please try again later");
        }*/

        //Gerard, Alice and Cheryll takes course BIO 101.
        try {
            studentG.enrollCourse(db.FindCourse("BIO 101"));
            studentA.enrollCourse(db.FindCourse("BIO 101"));
            studentC.enrollCourse(db.FindCourse("BIO 101"));
        } catch (CourseNotFoundException e) {
            e.printStackTrace();
        }

        //BIO EXAM:
        Exam exam = new Exam();
        String classicalQuestion1 = "Write human classification in terms of Linnaean Taxonomy.";
        Answer classicalAnswer1 = new ClassicalAnswer("Animalia -> Cordata -> Mammalia -> Piramates -> Homminidae -> Homo -> Homo Sapiens");
        classicalAnswer1.setRightAnswer("Animalia -> Cordata -> Mammalia -> Piramates -> Homminidae -> Homo -> Homo Sapiens");
        exam.AddQuestion(classicalQuestion1, classicalAnswer1, 30, false);

        String trueFalseQuestion1 = "Mushrooms are classified as plants.";
        Answer trueFalseAnswer1 = new TrueFalseAnswer('F');
        exam.AddQuestion(trueFalseQuestion1, trueFalseAnswer1, 5, true);

        String trueFalseQuestion2 = "Mammals are known for laying eggs.";
        Answer trueFalseAnswer2 = new TrueFalseAnswer('F');
        exam.AddQuestion(trueFalseQuestion2, trueFalseAnswer2, 5, true);

        String trueFalseQuestion3 = "Humans and dinosaurs never lived in the same era.";
        Answer trueFalseAnswer3 = new TrueFalseAnswer('T');
        exam.AddQuestion(trueFalseQuestion3, trueFalseAnswer3, 5, true);

        String multipleChoiceQuestion1 = "Which human species did homo sapiens lived together with?";
        MultipleChoiceAnswer multipleChoiceAnswer1 = new MultipleChoiceAnswer();
        multipleChoiceAnswer1.addChoice(new Choice("Homo habilis", false));
        multipleChoiceAnswer1.addChoice(new Choice("Homo rufoldensis", false));
        multipleChoiceAnswer1.addChoice(new Choice("Homo neanderthalensis", true));
        multipleChoiceAnswer1.addChoice(new Choice("Homo rhodesiensis", false));
        multipleChoiceAnswer1.setRightAnswer("C");

        exam.AddQuestion(multipleChoiceQuestion1, multipleChoiceAnswer1, 10, true);

        String classicalQuestion2 = "Summarize Lamarckian Eveolution in 2-3 sentences and give an example.";
        Answer classicalAnswer2 = new ClassicalAnswer("");
        exam.AddQuestion(classicalQuestion2, classicalAnswer2, 45, false);

        exam.SetPoint(100);
        exam.EditType("Midterm");
        exam.SetDateAndTime(new int[]{6, 6, 2022},10,50);

        try {
            db.FindCourse("BIO 101").AddExam(exam);
        } catch (CourseNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}


