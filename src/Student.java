public class Student extends User{
    public Student(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token="student";
    }
}