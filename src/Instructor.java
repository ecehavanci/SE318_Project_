import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Instructor extends User {
    public Instructor(String name, String surname, int schoolID, String password) {
        super(name, surname, schoolID, password);
        token = "instructor";
    }

    private boolean DoesCourseExists(String courseName) {
        for (Course course : courseList) {
            if (Objects.equals(course.getName(), courseName)) {
                return true;
            }
        }
        return false;
    }

    //This function adds and returns the course to instructor's local courseList so it can be added to the courseList in the database
    public Course AddAndReturnCourse(String courseName) throws CourseAlreadyExistsException, IOException {
        System.out.println(TextColours.blue + "Adding and returning course" + TextColours.reset);
        Database db = Database.getInstance();
        if (!DoesCourseExists(courseName)) {
            Course course = null;
            if (db.DoesCourseExists(courseName)) {
                try {
                    course = db.FindCourse(courseName);
                    course.AddInstructor(this);

                } catch (CourseNotFoundException e) {
                    System.out.println(TextColours.yellow + "Course not found" + TextColours.reset);
                }
            } else {
                course = new Course(courseName, this);
            }
            if (course != null) {
                if (!db.DoesCourseExistInStoringFiles(courseName, getSchoolID() + "_CourseList.txt")) {
                    String fileName = getSchoolID() + "_CourseList.txt";
                    FileWriter fw = new FileWriter(fileName, true);
                    fw.write(course.getName() + System.getProperty("line.separator"));
                    fw.close();

                    /*String fileName2 = courseName + "_InstructorList.txt";
                    FileWriter fw2 = new FileWriter(fileName2, true);
                    System.out.println(getSchoolID());
                    fw2.write(getSchoolID() + System.getProperty("line.separator"));
                    fw2.close();*/
                }

                courseList.add(course);
            }

            return course;
        } else {
            throw new CourseAlreadyExistsException();
        }
    }


    //This function is for showing details of a course. It also asks if we want to change anything and if instructor wants to
    //change something SetCourseDetails function is called.
    public void ShowCourseDetails(String courseName) throws CourseNotFoundException {
        Course course = FindCourse(courseName);
        course.Print();

        //Instructor can choose to edit or not
        System.out.println("Would you like to change details of this course?");
        Scanner scan = new Scanner(System.in);
        String yesNoChoice = scan.nextLine().toUpperCase(Locale.ROOT);
        if (yesNoChoice.equals("YES")) {
            try {
                SetCourseDetails(course);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Course FindCourse(String courseName) throws CourseNotFoundException {
        for (Course c : courseList) {
            if (Objects.equals(c.getName(), courseName)) {
                return c;
            }
        }
        throw new CourseNotFoundException();
    }

    //This function is for changing details (name, exams, exam dates, exam type, etc.) of the given course
    private void SetCourseDetails(Course course) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please choose what would you like to do: ");
        System.out.println("1) Edit the name of the course");
        System.out.println("2) Edit an exam");
        System.out.println("Press any other key to cancel");
        int detailChoice = scan.nextInt();
        scan.nextLine();
        switch (detailChoice) {
            case 1 -> {
                //TextColours.writeRed("1");
                System.out.println("What is the new name of the course");

                String courseName = scan.nextLine();
                String oldCourseName=course.getName();


                //CHANGING THE COURSE NAME FOR THE LIST THAT STORES ALL COURSES IN DATABASE: CourseList.txt
                BufferedReader reader = new BufferedReader(new FileReader("CourseList.txt"));
                ArrayList<String> allLines = new ArrayList<>();
                String line = "";
                while((line=reader.readLine())!=null){
                    allLines.add(line);
                }
                reader.close();

                System.out.println("HERE ARE LINES: " + allLines);

                for (int i = 0 ; i< allLines.size(); i++){
                    if (allLines.get(i).equals(course.getName())){
                        TextColours.writeBlue("FOUND: " + course.getName() + "!");
                        TextColours.writeBlue("Replacing " + course.getName() + " with " + courseName + " in temporary storing area...");
                        allLines.set(i, courseName);
                    }
                }

                FileWriter courseWriter = new FileWriter("CourseList.txt");
                for (String replacingLine : allLines){
                    courseWriter.write(replacingLine + System.getProperty("line.separator"));
                }
                courseWriter.close();

                course.setName(courseName);
                //CHANGING THE COURSE NAME IN INSTRUCTORS DATABASE RECORDS: schoolID_CourseList.txt
                FileWriter localCourseWriter = new FileWriter(getSchoolID()+"_CourseList.txt");
                for (Course replacingLine : courseList){
                    localCourseWriter.write(replacingLine.getName() + System.getProperty("line.separator"));
                }
                localCourseWriter.close();

                File newCourseFile = new File(courseName + "_ExamsList.txt");

                if (newCourseFile.createNewFile()){
                    TextColours.writeBlue("New course file successfully created: " + newCourseFile);
                    FileWriter newCourseWriter = new FileWriter(newCourseFile, true);
                    //newCourseWriter.write(courseName);
                    //newCourseWriter.close();

                    File oldCourseFile = new File(oldCourseName + "_ExamsList.txt");

                    BufferedReader examReader = new BufferedReader(new FileReader(oldCourseFile));
                    ArrayList<String> exams = new ArrayList<>();
                    String exam = "";
                    while((exam=examReader.readLine())!=null){
                        exams.add(exam);
                    }
                    System.out.println("HERE ARE EXAMS: " + exams);
                    examReader.close();

                    for (String e : exams){
                        newCourseWriter.write(e);
                    }
                    newCourseWriter.close();
                }
                else{
                    TextColours.writeBlue("Course file creating failed for file: " + newCourseFile.getName());
                }





                //Files.delete(Paths.get(oldCourseFile.getPath()));

                /*if (oldCourseFile.delete()){
                    TextColours.writeBlue("Old course file successfully deleted.");
                }
                else{
                    TextColours.writeBlue("Course file deleting failed for file: " + oldCourseFile.getName());
                }*/





                //deleting old courseName_ExamList and creating new one


                /*File dFile = new File(courseName + "_ExamsList.txt");

                TextColours.writeRed("2");

                if (dFile.createNewFile()) {
                    System.out.println("File created with name: " + dFile);
                } else {
                    System.out.println("Failed creating file: " + dFile);
                }

                File sFile = new File(course.getName() + "_ExamsList.txt");
                FileReader fin = new FileReader(sFile);
                FileWriter fout = new FileWriter(dFile, true);
                int c;
                while ((c = fin.read()) != -1) {
                    fout.write(c);
                }
                TextColours.writeRed("3");

                if (sFile.delete()) {
                    System.out.println("File deleted with name: " + sFile);
                } else {
                    System.out.println("Failed deleting file: " + sFile);
                }

                TextColours.writeRed("4");

                fin.close();
                fout.close();

                //Reading and storing kine by line
                File mainCourseFile = new File("CourseList.txt");
                FileReader mainCourseFileReader = new FileReader(mainCourseFile);
                TextColours.writeRed("5");

                BufferedReader courseReader = new BufferedReader(mainCourseFileReader);
                ArrayList<String[]> allData = new ArrayList<>();
                String data;
                while ((data = courseReader.readLine()) != null) {
                    String[] dataArray = data.split(",");
                    //System.out.println(Arrays.toString(dataArray));
                    allData.add(dataArray);
                }

                for (int i = 0; i < allData.size(); i++) {
                    System.out.println(Arrays.toString(allData.get(i)));
                }
                TextColours.writeRed("6");


                File tempFile = new File("TEMP.txt");
                tempFile.createNewFile();
                FileWriter courseWriter = new FileWriter(tempFile, true);

                for (String[] line : allData) {
                    courseWriter.write(line[0] + "," + line[1]);
                }
                TextColours.writeRed("7");

                mainCourseFile.delete();
                tempFile.renameTo(new File("CourseList.txt"));

                courseReader.close();
                mainCourseFileReader.close();
                TextColours.writeRed("8");*/

            }
            case 2 -> {
                if (course.ExamCount() > 0) {
                    //Exam details are shown to the instructor to choose what to edit
                    course.ShowExamDetails();

                    //Instructor chooses which exam they want to edit from the exam list by identifying the index (Users will think
                    //first index is one, second index is 2 and so on. But actually first index is 0 and second index is 1, and it
                    //continues like that. So code manages it by subtracting 1 from desired index)
                    System.out.println("Which exam would you like to edit?");
                    int editedExamIndex = scan.nextInt();

                    if (editedExamIndex >= 1 || editedExamIndex <= course.ExamCount()) {
                        System.out.println("What would you like to edit?");
                        System.out.println("1) Exam type");
                        System.out.println("2) Exam date");
                        System.out.println("Press any other key to cancel");

                        int editChoice = scan.nextInt();
                        scan.nextLine();

                        switch (editChoice) {
                            case 1 -> {
                                System.out.println("What is the new type?");
                                //1 subtracted here to get the desired index
                                course.GetExam(editedExamIndex - 1).EditType(scan.nextLine());
                            }

                            case 2 -> {
                                System.out.println("What is the new date?");
                                //1 subtracted here to get the desired index
                                Main.SetDate(course.GetExam(editedExamIndex - 1));
                            }
                            default -> System.out.println("Cancelling...");
                        }
                    } else System.out.println("Please choose a valid number");
                } else {
                    System.out.println("This course has no exam yet, you can add an exam using \"Build up exam\" choice.");
                }

            }
            default -> System.out.println("Cancelling...");
        }
    }


    public void GradeUnevaluatedQuestions(String courseName, int examIndex) {
        List<QuestionAndAnswer> QnA_List = null;
        ArrayList<StudentSheet> sheets = null;
        Exam exam = null;

        try {
            Course course = FindCourse(courseName);
            if (examIndex - 1 <= course.ExamCount() - 1 && examIndex - 1 >= 0) {
                exam = course.GetExam(examIndex - 1);
                QnA_List = exam.GetQnA_List();
                sheets = exam.GetStudentSheetList();

            } else {
                System.out.println("There is no exam by the given index.");
            }
        } catch (CourseNotFoundException e) {
            System.out.println(TextColours.yellow + "Course not found" + TextColours.reset);

        }


        if (QnA_List != null && sheets != null) {
            Scanner scan = new Scanner(System.in);

            if (!exam.AreAllSheetsApproved()) {

                for (int i = 0; i < sheets.size(); i++) {
                    if (sheets.get(i).isApproved()) continue;

                    String studentName = sheets.get(i).getStudent().getName() + " " + sheets.get(i).getStudent().getSurname();
                    System.out.println(studentName + "'s sheet:\n");

                    for (int j = 0; j < QnA_List.size(); j++) {
                        if (!QnA_List.get(j).isEvaluatedDirectly()) {
                            System.out.println("Unevaluated question: QUESTION " + (j + 1));
                            System.out.println("Question:\n" + QnA_List.get(j).getQuestion());
                            System.out.println();

                            System.out.println("Right Answer:\n" + QnA_List.get(j).getAnswer().getRightAnswer());
                            System.out.println();

                            System.out.println("Student Answer:\n" + sheets.get(i).getAnswer(j));
                            System.out.println();

                            System.out.println("Grade the answer out of " + QnA_List.get(j).getPoint());
                            int point = scan.nextInt();
                            scan.nextLine();

                            sheets.get(i).addToGrade(point);

                            for (int k = 0; k < sheets.get(i).getGradeList().size(); k++) {
                                if (sheets.get(i).getGradeList().get(k) == -1) {
                                    sheets.get(i).getGradeList().set(k, point);
                                }
                            }

                            System.out.println();
                            System.out.println();
                        }
                    }

                    System.out.println(studentName + "'s sheet got " + sheets.get(i).getGrade());

                    System.out.println("Do you approve?");
                    System.out.println("1) Approve");
                    System.out.println("2) Not approve");

                    int approval = scan.nextInt();
                    scan.nextLine();

                    if (approval == 1) {
                        sheets.get(i).setApproved(true);
                    } else if (approval == 2) {
                        sheets.get(i).setApproved(false);
                    }

                }
                System.out.println("All exam sheets are revised.");
            } else System.out.println("All sheets are approved.");
        }
    }

    public void ShowExamStatistics(String courseName, int examIndex) {
        //TODO: Show exam statistics
        ArrayList<StudentSheet> sheets = null;
        List<QuestionAndAnswer> QnA_List = null;
        Exam exam = null;

        try {
            Course course = FindCourse(courseName);
            if (examIndex - 1 <= course.ExamCount() - 1 && examIndex - 1 >= 0) {
                exam = course.GetExam(examIndex - 1);
                sheets = exam.GetStudentSheetList();

            } else {
                System.out.println("There is no exam by the given index.");
            }
        } catch (CourseNotFoundException e) {
            System.out.println(TextColours.yellow + "Lesson not found" + TextColours.reset);
        }
        System.out.println("\n\nRESULTS OF " + courseName);

        if (sheets != null) {
            int totalPoints = 0;
            int[] grades = new int[sheets.get(0).getGradeList().size()];
            for (int i = 0; i < sheets.size(); i++) {
                totalPoints += sheets.get(i).getGrade();
                for (int j = 0; j < sheets.get(i).getGradeList().size(); j++) {
                    grades[j] += sheets.get(i).getGradeList().get(j);
                }
            }
            System.out.println("Average: " + totalPoints / sheets.size());

            for (int i = 0; i < grades.length; i++) {
                System.out.println(i + "th question average: " + grades[i] / sheets.size());
            }
        }
    }
}
