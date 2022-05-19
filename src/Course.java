import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Course {
    private ArrayList<Instructor> instructors = new ArrayList<>();
    private String name;
    private final ArrayList<Exam> examList = new ArrayList<>();

    //A lesson has a name and instructors
    public Course(String name, Instructor instructor) {
        this.name = name;
        instructors.add(instructor);
    }

    public Course(String name) {
        this.name = name;
    }

    public Instructor GetFirstInstructor() {
        return instructors.get(0);
    }


    public void AddExam(Exam exam) throws IOException {
        exam.SetID(examList.size());
        TextColours.writeBlue("adding exam");
        FileWriter examWriter = new FileWriter(name + "_ExamsList.txt", true);
        examWriter.write(exam.GetID() + "," + exam.GetType() + "," + exam.GetStoringDate() + "," + exam.GetPoint() + System.getProperty("line.separator"));
        examList.add(exam);
        examWriter.close();
    }


    public Exam GetExam(int index) {
        return examList.get(index);
    }

    public ArrayList<Exam> GetExamList() {
        return examList;
    }

    public int ExamCount() {
        return examList.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void AddInstructor(Instructor instructor) {
        instructors.add(instructor);
        /*Database db = Database.getInstance();
        try {
            int userIndex = db.ReturnUserIndex(instructor);
            if (userIndex == -1){
                System.out.println("Instructor not registered");
            }
            else{
                FileWriter fr = new FileWriter(name + "_InstructorList.txt", true);
                fr.write(String.valueOf(instructor.getSchoolID()));
                fr.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void PrintInstructors() {
        int counter = 0;
        for (Instructor instructor : instructors) {
            counter++;
            System.out.print(instructor.getName() + " " + instructor.getSurname());
            if (counter < instructors.size()) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }

    //Exam details consists of the lesson exam belongs to, exam's name, type and date
    public void ShowExamDetails() {
        for (int i = 0; i < examList.size(); i++) {
            ShowExamDetailsWithIndex(i);
        }
    }

    public void ShowExamDetailsWithIndex(int i) {
        Exam e = examList.get(i);
        System.out.println("Lesson name: " + name);
        System.out.println("EXAM " + (i + 1));
        System.out.println("Exam type: " + e.GetType());
        System.out.println("Date: " + e.GetLongDate());
        System.out.println("Time: " + e.GetTime());

        System.out.println();
    }



    //This function is for printing Lesson with info of it (its name, its instrutor's name details -exam list-)
    public void Print() {
        System.out.println("INFO FOR LESSON " + name.toUpperCase(Locale.ROOT));
        System.out.print("INSTRUCTORS: ");
        PrintInstructors();
        System.out.println("\n~EXAM DETAILS~");
        ShowExamDetails();
    }


}
