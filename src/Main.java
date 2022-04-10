import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("~Welcome to Exam Management System~");
        Database database = new Database();
        User user = showMainMenu(database);


        System.out.println("Welcome " + user.getName());

        if (Objects.equals(user.getToken(), "student")){
            System.out.println("Here are your lessons:");
            //TODO: Show lessons
            //TODO: Add logging out when structure is formed
        }

        else if (Objects.equals(user.getToken(), "instructor")){
            System.out.println("Here are the lessons you are giving:");
            //TODO: Show lessons
            //TODO: Add logging out when structure is formed
        }

    }

    public static User showMainMenu(Database db){
        User user = null;
        while (true) {
            boolean isSignedIn = false;
            System.out.println("Please choose what you would like to do");
            System.out.println("1) Sign up");
            System.out.println("2) Sign in");

            Scanner scan = new Scanner(System.in);
            int signingChoice = scan.nextInt();

            switch (signingChoice) {
                case 1 -> {
                    System.out.println("What do you want to log in as?");
                    System.out.println("1) Instructor");
                    System.out.println("2) Student");
                    int signingUpChoice = scan.nextInt();
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
            }
            if (isSignedIn){
                return user;
            }
        }
    }



}
