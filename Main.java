import java.util.Arrays;

public class Main{
    public static void main(String[] args){
        CourseManager cm = new CourseManager();
        EnrollmentManager em = new EnrollmentManager();

        Instructor profIvan = new Instructor("Ivan", "Petrov", "Computer Science");
        Instructor profAnna = new Instructor("Anna", "Smirnova", "Mathematics");

        Course oop = cm.createCourse("OOP101", "Object-Oriented Programming", 3);
        Course alg = cm.createCourse("ALG201", "Algorithms", 2);

        oop.setInstructor(profIvan);
        alg.setInstructor(profAnna);

        Student s1 = new Student("Ainur", "K", "CS");
        Student s2 = new Student("Dana", "S", "CS");
        Student s3 = new Student("Erlan", "T", "Math");

        try{
            em.enroll(s1, oop);
            em.enroll(s2, oop);
            em.enroll(s3, oop);

            em.enroll(s1, alg);
            em.enroll(s2, alg);
        } catch(IllegalStateException ex){
            System.out.println("Enrollment failed: " + ex.getMessage());
        }

        System.out.println("Courses:");
        for(Course c : cm.listCourses()){
            System.out.println("  " + c);
            for(Enrollment e : c.getEnrollments()){
                System.out.println("     -> " + e.getStudent().getFullName() + " (" + e.getStatus() + ")");
            }
        }

        System.out.println("\nStudent schedules:");
        for(Student s : Arrays.asList(s1,s2,s3)){
            System.out.println("  " + s.getFullName() + " major=" + s.getMajor());
            for(Enrollment e : s.getEnrollments()){
                System.out.println("     - " + e.getCourse().getCode() + " : " + e.getStatus());
            }
        }

        Enrollment first = s1.getEnrollments().getFirst();
        first.setGrade(92.0);
        first.setStatus(Enrollment.Status.COMPLETED);
        Enrollment second = s2.getEnrollments().getFirst();
        second.setGrade(42.0);
        second.setStatus(Enrollment.Status.DROPPED);
        Enrollment third = s3.getEnrollments().getFirst();
        third.setGrade(69.0);

        System.out.println("\nAfter grading:");
        System.out.println(first);
        System.out.println(second);
        System.out.println(third);

    }
}
