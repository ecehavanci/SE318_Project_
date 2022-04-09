import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("~Welcome to Exam Management System~");
        Database database = new Database();
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
                        case 1 -> database.registerInstructor(name, surname, ID, password);
                        case 2 -> database.registerStudent(name, surname, ID, password);
                    }
                }
                case 2 -> {
                    System.out.println("School ID: ");
                    int ID = scan.nextInt();
                    scan.nextLine();
                    System.out.println("Password: ");
                    String password = scan.nextLine();
                    try {
                        user = database.logIn(ID,password);
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
                break;
            }
        }

        System.out.println("Welcome " + user.getName());

    }


}
