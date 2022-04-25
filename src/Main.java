import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("~Welcome to Exam Management System~");
        Database database = new Database();

        while (true) {
            User user = ShowMainMenu(database);

            System.out.println("Welcome, " + user.getName() + ".");

            if (Objects.equals(user.getToken(), "student")) {
                System.out.println("Here are your lessons:");
                //TODO: Show lessons
                //TODO: Add logging out when structure is formed












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
                            instructor.addLesson(lessonName);
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
                                //This action enables instructor to
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

    public static void SetLessonDetails(){

    }

    public static void BuildUpExam(Instructor inst) throws LessonNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the name of the lesson that you want to add an exam to:");
        String lessonName = scan.nextLine();

        Lesson lesson = null;
        Exam exam = new Exam();

        lesson = inst.FindLesson(lessonName);
        boolean isDone = false;
        boolean isCancel = false;
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
                    System.out.println("Question: ");
                    String questionText = scan.nextLine();

                    System.out.println("Would you like to enter an answer to this classical question to help you grading? Yes/No ");
                    String answerChoice = scan.nextLine();
                    answerChoice=answerChoice.toUpperCase(Locale.ROOT);

                    String answerText = null;
                    if (answerChoice.equals("YES")){
                        System.out.println("Please enter the answer");
                        answerText = scan.nextLine();
                    }

                    ClassicalAnswer ca = new ClassicalAnswer(answerText);
                    exam.AddQuestion(questionText, ca);


                }
                case 2 -> {
                    System.out.println("Question: ");
                    String questionText = scan.nextLine();

                    System.out.println("How many choices are there?");
                    int choiceCount = scan.nextInt();
                    scan.nextLine();

                    MultipleChoiceAnswer mca = new MultipleChoiceAnswer();

                    List<String> choiceList = new ArrayList<>();
                    for (int i = 0; i < choiceCount; i++) {
                        System.out.println("Enter choice " + (char) (65 + i) + ":");
                        String choice = scan.nextLine();
                        choiceList.add(choice);
                    }

                    System.out.println("Which choice is right?");
                    char choiceCode = scan.nextLine().charAt(0);

                    for (int i = 0; i < choiceCount; i++) {
                        mca.choices.add(new Choice(choiceList.get(i), (int) choiceCode == 65 + i));
                    }

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
            System.out.print("Please enter a date: ");
            SetDate(exam);

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
            boolean isSignedIn = false;

            System.out.println("Please choose what you would like to do");
            System.out.println("1) Sign up");
            System.out.println("2) Sign in");
            System.out.println("3) Exit the system");

            Scanner scan = new Scanner(System.in);
            int signingChoice = scan.nextInt();

            switch (signingChoice) {
                case 1 -> {
                    System.out.println("What do you want to sign up as?");
                    System.out.println("1) Instructor");
                    System.out.println("2) Student");
                    System.out.println("3) Cancel registration");

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

    public static void SetDate(Exam exam){
        Scanner scan = new Scanner(System.in);
        while(true){
            String date = scan.nextLine();
            String[] splitDate = date.contains("/") ? date.split("/") : date.contains(".") ? date.split("\\.") : null;
            if (splitDate==null){
                System.out.println("Please enter a valid date");
            }
            else{
                int [] dateParts = new int[3];
                for (int i = 0; i < 3; i++) {
                    dateParts[i] = Integer.parseInt(splitDate[i]);
                }
                exam.SetDate(dateParts);
                break;
            }
        }
    }


}


