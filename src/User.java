public abstract class User{
    public User(String name, String surname, int schoolID, String password) {
        this.name = name;
        this.surname = surname;
        this.schoolID = schoolID;
        this.password = password;
    }

    protected String token;
    private String name;
    private String surname;
    private int schoolID;
    private String password;

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public String getPassword() {
        return password;
    }

    public String getSurname() {
        return surname;
    }
}
