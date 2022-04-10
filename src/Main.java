import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("~Welcome to Exam Management System~");
        Database database = new Database();

        while (true ){
            User user = showMainMenu(database);

            System.out.println("Welcome, " + user.getName() + ".");

            if (Objects.equals(user.getToken(), "student")){
                System.out.println("Here are your lessons:");
                //TODO: Show lessons
                //TODO: Add logging out when structure is formed
            }

            else if (Objects.equals(user.getToken(), "instructor")){
                Instructor instructor = (Instructor) user;
                while (true){
                    boolean willLogOut=false;
                    //TODO: Show lessons
                    instructor.printLessons();
                    //TODO: Add logging out when structure is formed

                    System.out.println();

                    System.out.println("Please choose what you would like to do");
                    System.out.println("1) Add lesson");
                    System.out.println("2) See and set details of a lesson");
                    System.out.println("3) Build up an exam");
                    System.out.println("4) Grade or approve exams");
                    System.out.println("5) Log out");

                    int insChoice=scan.nextInt();

                    switch (insChoice){
                        case 1 ->{
                            System.out.println("Name of the lesson: ");
                            String lessonName=scan.nextLine();
                            instructor.addLesson(lessonName);
                        }
                        case 2 ->{
                            //TODO: See and set details of a lesson
                        }
                        case 3 ->{
                            //TODO: Build up an exam
                        }
                        case 4 ->{
                            //TODO: Grade or approve exams
                        }
                        case 5 ->{
                            System.out.println("Logging out...");
                            willLogOut=true;
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                    if (willLogOut){
                        break;
                    }
                }

            }
        }


    }

    public static User showMainMenu(Database db){
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
                    System.out.println("What do you want to log in as?");
                    System.out.println("1) Instructor");
                    System.out.println("2) Student");
                    System.out.println("3) Cancel registration");

                    int signingUpChoice = scan.nextInt();

                    if (signingUpChoice==3){
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
                        user = db.logIn(ID,password);
                        isSignedIn=true;
                    }
                    catch (WrongEmailException WEex){
                        System.out.println("Email Not Found");
                    }
                    catch (WrongPasswordException WPex){
                        System.out.println("Wrong Password");
                    }
                    catch (NullPointerException nullEx){
                        System.out.println("Please try again");
                    }
                }
                case 3 -> {
                    System.out.println("Thank you for using Exam Management System. \nExiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
            if (isSignedIn){
                return user;
            }
        }
    }



}
