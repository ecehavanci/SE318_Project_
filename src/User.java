public abstract class User {
    public User(String name, String surname, int schoolID, String password) {
        this.name = name;
        this.surname = surname;
        this.schoolID = schoolID;
        this.password = password;
    }

    protected String token;
    private final String name;
    private final String surname;
    private final int schoolID;
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }


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
