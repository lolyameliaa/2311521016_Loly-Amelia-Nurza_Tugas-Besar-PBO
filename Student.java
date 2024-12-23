package src;

public class Student extends Person {
    private String studentId;

    public Student(String name, String email, String studentId) {
        super(name, email);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}