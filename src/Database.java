import java.util.ArrayList;
import java.util.List;

public class Database {
    List<User> userList = new ArrayList<>();

    public void registerInstructor(String name, String surname, int schoolID, String password){
        try {
            User newUser = new Instructor(name,surname,schoolID,password);
            userList.add(newUser);
            System.out.println("Registration successful.");
        }
        catch (Exception e){
            System.out.println("We cannot register you right now, please try again later.");
        }
    }

    public void registerStudent(String name, String surname, int schoolID, String password){
        try {
            User newUser = new Student(name,surname,schoolID,password);
            userList.add(newUser);
            System.out.println("Registration successful.");
        }
        catch (Exception e){
            System.out.println("We cannot register you right now, please try again later.");
        }
    }

    public User logIn(int ID, String password) throws WrongEmailException, WrongPasswordException {
       for (User user : userList){
           if (user.getSchoolID()==ID){
               if (user.getPassword().equals(password)){
                   return user;
               }
               else{
                   throw new WrongPasswordException();
               }
           }
           else{
               if (userList.get(userList.size()-1)==user){
                   throw new WrongEmailException();
               }
           }
       }
       return null;
    }
}

class WrongEmailException extends Exception{
    public WrongEmailException() {}
}

class WrongPasswordException extends Exception{
    public WrongPasswordException() {}
}
