
import java.util.*;

public class Main {
    public static void main(String[] args) throws LessonNotFoundException, WrongChoiceException {
        Scanner scan = new Scanner(System.in);
        System.out.println("~Welcome to Exam Management System~");
        Database database = new Database();

        while (true) {
            User user = ShowMainMenu(database);

            System.out.println("Welcome, " + user.getName() + ".");

            if (Objects.equals(user.getToken(), "student")) {

                //TODO: Show lessons
                //TODO: Add logging out when structure is formed

                Student student = (Student) user;
                while (true) {
                    boolean willLogOut = false;

                    System.out.println("Please choose what you would like to do");
                    System.out.println("1) Enroll/Unenroll in lessons");
                    System.out.println("2) List all enrolled lesson details");
                    System.out.println("3) See all exam informations");
                    System.out.println("4) Log out");

                    int insChoice = scan.nextInt();
                    scan.nextLine();

                    switch (insChoice) {
                        case 1 -> {
                            try{
                                enrollUnroll(database, student);
                            }
                            catch (WrongChoiceException WCE){
                                System.out.println("Unexpected output");
                            }

                        }
                        case 2 -> {
                            //list all enrolled lessons in std class
                            student.printLessons();

                        }
                        case 3 -> {
                            seeExamInfo(database, student);
                        }
                        case 4 -> {
                            System.out.println("Logging out...");
                            willLogOut = true;
                        }

                    }
                    if (willLogOut) {
                        break;
                    }
                }


            }
            else if (Objects.equals(user.getToken(), "instructor")) {
                Instructor instructor = (Instructor) user;
                while (true) {
                    boolean willLogOut = false;
                    //Instructors see their lessons when they first logged in
                    System.out.println();
                    instructor.printLessons();
                    //TODO: Add logging out when structure is formed

                    System.out.println();

                    //Instructors have 5 options as follows:
                    System.out.println("Please choose what you would like to do");
                    System.out.println("1) Add lesson");
                    System.out.println("2) See and set details of a lesson");
                    System.out.println("3) Build up an exam");
                    System.out.println("4) Grade or approve exams");
                    System.out.println("5) Log out");

                    //They choose what they want to do
                    int insChoice = scan.nextInt();
                    scan.nextLine();

                    switch (insChoice) {
                        case 1 -> {
                            System.out.print("Name of the lesson: ");
                            String lessonName = scan.nextLine();

                            //Instructor has a lesson list, he/she creates and ads a lesson to his/her lesson list with given name
                            Lesson lesson = instructor.addAndReturnLesson(lessonName, instructor);
                            database.addLesson(lesson);
                        }
                        case 2 -> {
                            System.out.print("Name of the lesson: ");
                            String lessonName = scan.nextLine();
                            try {
                                //This action shows all the lessons, instructor gives and then enables instructor to change their details
                                instructor.ShowLessonDetails(lessonName);
                            } catch (LessonNotFoundException LNFE) {
                                System.out.println("Lesson not found");
                            }

                        }
                        case 3 -> {
                            try {
                                //This action enables instructor to build an exam with desired questions in it
                                BuildUpExam(instructor);
                            } catch (LessonNotFoundException LNFE) {
                                System.out.println("Lesson not found");
                            }

                        }
                        case 4 -> {
                            //TODO: Grade or approve exams
                        }
                        case 5 -> {
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

    public static void BuildUpExam(Instructor inst) throws LessonNotFoundException {
        Scanner scan = new Scanner(System.in);
        //Lessons have exams so we get here a lesson name to add that exam to that lesson
        System.out.println("Please enter the name of the lesson that you want to add an exam to:");
        String lessonName = scan.nextLine();

        Lesson lesson = null;
        Exam exam = new Exam();

        lesson = inst.FindLesson(lessonName);
        boolean isDone = false;
        boolean isCancel = false;

        //We sequentially add questions (along with their answers if desired) to the exam
        while (true) {
            System.out.println("What kind of question would you like to add?");
            System.out.println("1) Classical type question (open ended)");
            System.out.println("2) Multiple choice question");
            System.out.println("3) True/False question");
            System.out.println("4) Done exam building");
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

                    //A classical question (and its answer if desired) is created since choice 1 indicates classical question
                    ClassicalAnswer ca = new ClassicalAnswer(answerText);
                    exam.AddQuestion(questionText, ca);


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
                    System.out.println("Which choice is right?");
                    char choiceCode = scan.nextLine().charAt(0);

                    for (int i = 0; i < choiceCount; i++) {
                        //Choices are added one by one with their status (if they are right or wrong answer)
                        mca.addChoice(new Choice(choiceList.get(i), (int) choiceCode == 65 + i));
                    }

                    //Question is added along with answer
                    exam.AddQuestion(questionText, mca);
                }
                case 3 -> {
                    System.out.println("Question: ");
                    //scan.nextLine();
                    String questionText = scan.nextLine();

                    System.out.println("Right Answer: ");
                    //scan.nextLine();
                    char answer = scan.nextLine().charAt(0);

                    TrueFalseAnswer tfa = new TrueFalseAnswer(answer);

                    exam.AddQuestion(questionText, tfa);


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

            try {
                lesson.AddExam(exam);
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

                    System.out.println("School ID: ");
                    int ID = scan.nextInt();
                    scan.nextLine();
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
                }
                case 2 -> {
                    if (db.userList.size()==0){
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
                    } catch (WrongEmailException WEex) {
                        System.out.println("Email Not Found");
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
                int[] dateParts = new int[3];
                for (int i = 0; i < 3; i++) {
                    dateParts[i] = Integer.parseInt(splitDate[i]);
                }
                exam.SetDate(dateParts);
                break;
            }
        }
    }

    public static void enrollUnroll(Database database, Student student) throws WrongChoiceException{
        Scanner scan = new Scanner(System.in);

        //Students can enroll and unenroll to a lesson using "enroll" or "unenroll" input
        System.out.println("Do you want to enroll or uneroll in lessons?");
        String enrollChoice = scan.nextLine();

        if (enrollChoice.equals("enroll")) {// enroll according to name in list
            System.out.println("You can enroll in these lessons");
            database.showLessons();//list all lessons in

            System.out.println("Please choose one of the lessons and write its name to enroll");
            String choosenLesson = scan.nextLine();
            try{
                Lesson foundLesson = database.FindLesson(choosenLesson);
                student.enrollLesson(foundLesson);
            }
            catch (LessonNotFoundException LNFE){
                System.out.println("Lesson not found");
            }
        } else if (enrollChoice.equals("unenroll")) { // unenroll according to name in list
            System.out.println("You can unenroll from your lessons");
            student.printLessons();
            String choosenLesson = scan.nextLine();
            try {
                Lesson foundLesson = student.FindLesson(choosenLesson);
                student.unenrollLesson(foundLesson);
            }
            catch (LessonNotFoundException LNFE){
                System.out.println("Lesson not found");
            }
        } else
            //If an input is given other than enroll or unenroll, system throws an error
            throw new WrongChoiceException();

    }

}


