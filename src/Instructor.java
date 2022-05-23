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
        Database db = Database.getInstance();
        if (!DoesCourseExists(courseName)) {
            Course course = null;
            if (db.DoesCourseExists(courseName)) {
                try {
                    course = db.FindCourse(courseName);
                    course.AddInstructor(this);

                } catch (CourseNotFoundException e) {
                    TextColours.writeYellow("Course not found.");
                }
            } else {
                course = new Course(courseName, this);
            }
            if (course != null) {
                //If an exam does not already exist in an in its relative storing file (instructorID_Courselist.txt), then
                //it will be added to there.
                if (!db.DoesCourseExistInStoringFiles(courseName, getSchoolID() + "_CourseList.txt")) {
                    String fileName = getSchoolID() + "_CourseList.txt";
                    FileWriter fw = new FileWriter(fileName, true);
                    fw.write(course.getName() + System.getProperty("line.separator"));
                    fw.close();
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

        int detailChoice;
        while (true) {
            System.out.println("Please choose what would you like to do: ");
            System.out.println("1) Edit the name of the course");
            System.out.println("2) Edit an exam");
            System.out.println("Press any other key to cancel");
            try {
                detailChoice = scan.nextInt();
                break;
            } catch (InputMismatchException IME) {
                TextColours.writeYellow("You should enter an integer, please try again");
            }
            scan.nextLine();
        }
        scan.nextLine();

        switch (detailChoice) {
            case 1 -> {
                System.out.println("What is the new name of the course");

                String courseName = scan.nextLine();
                String oldCourseName = course.getName();

                //Changing the name of the course in its relative text file (instructorsID_CourseList.txt) requires 4 steps:
                //STEP 1: Changing its name in the CourseList.txt (the text file which stores all courses)
                BufferedReader reader = new BufferedReader(new FileReader("CourseList.txt"));
                ArrayList<String> allLines = new ArrayList<>();
                String line = "";
                //First all lines (all course names) is stored in an ArrayList
                while ((line = reader.readLine()) != null) {
                    allLines.add(line);
                }
                reader.close();

                //System.out.println("HERE ARE LINES: " + allLines);

                //Then, in the arraylist the name course (that is wanted to be changed) is changed
                for (int i = 0; i < allLines.size(); i++) {
                    if (allLines.get(i).equals(course.getName())) {
                        allLines.set(i, courseName);
                    }
                }

                //Since we have now a list with right course names, all the course names are read from here and written
                //into CourseList.txt
                FileWriter courseWriter = new FileWriter("CourseList.txt");
                for (String replacingLine : allLines) {
                    courseWriter.write(replacingLine + System.getProperty("line.separator"));
                }
                courseWriter.close();

                //STEP 4: Change the name of the course in student files (studentID_CourseList)
                Database db = Database.getInstance();
                for (User user : db.userList) {
                    if (user instanceof Student) {
                        Student student = (Student) user;
                        if (student.isTakingCourse(oldCourseName)) {
                            TextColours.writeRed("Taking course");
                            File studentCourseFile = new File(student.getSchoolID() + "_CourseList.txt");

                            //All courses are read from the student's course file
                            BufferedReader examReader = new BufferedReader(new FileReader(studentCourseFile));
                            ArrayList<String> exams = new ArrayList<>();
                            String studentCourse = "";
                            while ((studentCourse = examReader.readLine()) != null) {
                                exams.add(studentCourse);
                            }
                            examReader.close();
                            FileWriter studentCourseWriter = new FileWriter(studentCourseFile, true);

                            for (String e : exams) {
                                studentCourseWriter.write(e);
                            }
                            studentCourseWriter.close();

                        }
                    }
                }

                //Name is set here for further steps
                course.setName(courseName);

                //STEP 2: Course name is changed in instructor's course list text (instructorID_CourseList.txt)
                FileWriter localCourseWriter = new FileWriter(getSchoolID() + "_CourseList.txt");
                for (Course replacingLine : courseList) {
                    localCourseWriter.write(replacingLine.getName() + System.getProperty("line.separator"));
                }
                localCourseWriter.close();

                //STEP 3: Course's exam List text file is changed
                //Every course has en exam list text file relative to their names (name_ExamList.txt)
                //A new text file is created for storing exams
                File newCourseFile = new File(courseName + "_ExamsList.txt");

                if (newCourseFile.createNewFile()) {
                    FileWriter newCourseWriter = new FileWriter(newCourseFile, true);

                    File oldCourseFile = new File(oldCourseName + "_ExamsList.txt");

                    //All exams are read from old file and written into the new one
                    BufferedReader examReader = new BufferedReader(new FileReader(oldCourseFile));
                    ArrayList<String> exams = new ArrayList<>();
                    String exam = "";
                    while ((exam = examReader.readLine()) != null) {
                        exams.add(exam);
                    }
                    System.out.println("HERE ARE EXAMS: " + exams);
                    examReader.close();

                    for (String e : exams) {
                        newCourseWriter.write(e);
                    }
                    newCourseWriter.close();
                } else {
                    TextColours.writeRed("Course file creating failed for file: " + newCourseFile.getName());
                }


            }
            case 2 -> {
                if (course.ExamCount() > 0) {
                    //Exam details are shown to the instructor to choose what to edit
                    course.ShowExamDetails();

                    //Instructor chooses which exam they want to edit from the exam list by identifying the index (Users will think
                    //first index is one, second index is 2 and so on. But actually first index is 0 and second index is 1, and it
                    //continues like that. So code manages it by subtracting 1 from desired index)

                    int editedExamIndex;
                    while (true) {
                        System.out.println("Which exam would you like to edit?");
                        try {
                            editedExamIndex = scan.nextInt();
                            if (editedExamIndex > course.ExamCount()) {
                                TextColours.writeYellow("There is no exam at the given index, please try again.");
                                continue;
                            }
                            break;
                        } catch (InputMismatchException IME) {
                            TextColours.writeYellow("Exam index should be an integer, please try again");
                        }
                        scan.nextLine();
                    }
                    scan.nextLine();

                    if (editedExamIndex >= 1 || editedExamIndex <= course.ExamCount()) {
                        int editChoice;
                        while (true) {
                            System.out.println("What would you like to edit?");
                            System.out.println("1) Exam type");
                            System.out.println("2) Exam date");
                            System.out.println("Press any other key to cancel");

                            try {
                                editChoice = scan.nextInt();
                                break;
                            } catch (InputMismatchException IME) {
                                TextColours.writeYellow("You should enter an integer, please try again");
                            }
                            scan.nextLine();
                        }
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
            TextColours.writeYellow("Course not found.");

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

                            int point;
                            while (true) {
                                System.out.println("Grade the answer out of " + QnA_List.get(j).getPoint());

                                try {
                                    point = scan.nextInt();
                                    break;
                                } catch (InputMismatchException IME) {
                                    TextColours.writeYellow("Grade should be an integer, please try again");
                                }
                                scan.nextLine();
                            }
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


                    int approval;
                    while (true) {
                        System.out.println("Do you approve?");
                        System.out.println("1) Approve");
                        System.out.println("2) Not approve");

                        try {
                            approval = scan.nextInt();
                            if (!(approval == 1 || approval == 2)) {
                                throw new WrongChoiceException();
                            }
                            break;
                        } catch (InputMismatchException | WrongChoiceException IME) {
                            TextColours.writeYellow("Please enter 1 or 2");
                        }
                        scan.nextLine();
                    }
                    scan.nextLine();
                    scan.nextLine();

                    if (approval == 1) {
                        sheets.get(i).setApproved(true);
                    } else if (approval == 2) {
                        sheets.get(i).setApproved(false);
                    }

                }
                TextColours.writeBlue("All exam sheets are revised.");
            } else TextColours.writeBlue("All sheets are approved.");
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
                TextColours.writeYellow("There is no exam by the given index.");
            }
        } catch (CourseNotFoundException e) {
            TextColours.writeYellow("Course not found.");
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
