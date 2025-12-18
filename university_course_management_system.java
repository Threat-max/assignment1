import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
abstract class Person {
    protected final UUID id;
    protected String firstName;
    protected String lastName;

    public Person(String firstName, String lastName) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() { return id; }
    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public String toString() {
        return String.format("%s (id=%s)", getFullName(), id.toString());
    }
}

class Student extends Person {
    private final String major;
    private final List<Enrollment> enrollments = new ArrayList<>();

    public Student(String firstName, String lastName, String major) {
        super(firstName, lastName);
        this.major = major;
    }

    public String getMajor() { return major; }
    public List<Enrollment> getEnrollments() { return Collections.unmodifiableList(enrollments); }
    void addEnrollment(Enrollment e){ enrollments.add(e); }
}

class Instructor extends Person {
    private final String department;

    public Instructor(String firstName, String lastName, String department) {
        super(firstName, lastName);
        this.department = department;
    }

    public String getDepartment() { return department; }
}

class Course {
    private final UUID id;
    private final String code;
    private final String title;
    private final int capacity;
    private Instructor instructor;
    private final List<Enrollment> enrollments = new ArrayList<>();

    public Course(String code, String title, int capacity) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCapacity() { return capacity; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor i) { instructor = i; }
    public List<Enrollment> getEnrollments(){ return Collections.unmodifiableList(enrollments); }
    boolean isFull(){ return enrollments.size() >= capacity; }
    void addEnrollment(Enrollment e){ enrollments.add(e); }
    void removeEnrollment(Enrollment e){ enrollments.remove(e); }

    public String toString(){
        return String.format("%s - %s (cap: %d, enrolled: %d) %s",
                code, title, capacity, enrollments.size(), (instructor==null?"":"Instructor: "+instructor.getFullName()));
    }
}

class Enrollment {
    public enum Status { ENROLLED, COMPLETED, DROPPED }

    private final UUID id;
    private final Student student;
    private final Course course;
    private Status status;
    private Double grade; // optional

    public Enrollment(Student student, Course course) {
        this.id = UUID.randomUUID();
        this.student = student;
        this.course = course;
        this.status = Status.ENROLLED;
    }

    public UUID getId(){ return id; }
    public Student getStudent(){ return student; }
    public Course getCourse(){ return course; }
    public Status getStatus(){ return status; }
    public void setStatus(Status s){ this.status = s; }
    public Double getGrade(){ return grade; }
    public void setGrade(Double g){ this.grade = g; }

    public String toString(){
        return String.format("Enrollment[id=%s, student=%s, course=%s, status=%s, grade=%s]",
                id.toString(), student.getFullName(), course.getCode(), status, (grade==null?"-":grade));
    }
}

class CourseManager {
    private final Map<UUID, Course> courses = new HashMap<>();

    public Course createCourse(String code, String title, int capacity){
        Course c = new Course(code, title, capacity);
        courses.put(c.getId(), c);
        return c;
    }

    public Optional<Course> getCourseById(UUID id){ return Optional.ofNullable(courses.get(id)); }
    public Optional<Course> getCourseByCode(String code){
        return courses.values().stream().filter(c->c.getCode().equalsIgnoreCase(code)).findFirst();
    }
    public List<Course> listCourses(){ return new ArrayList<>(courses.values()); }
    public boolean deleteCourse(UUID id){ return courses.remove(id) != null; }
}

class EnrollmentManager {
    private final Map<UUID, Enrollment> enrollments = new HashMap<>();

    public void enroll(Student s, @org.jetbrains.annotations.NotNull Course c) throws IllegalStateException{
        if(c.isFull()) throw new IllegalStateException("Course is full");
        Enrollment e = new Enrollment(s,c);
        enrollments.put(e.getId(), e);
        c.addEnrollment(e);
        s.addEnrollment(e);
    }

    public boolean drop(@NotNull Enrollment e){
        e.setStatus(Enrollment.Status.DROPPED);
        e.getCourse().removeEnrollment(e);
        return true;
    }

    public List<Enrollment> findByStudent(Student s){
        return enrollments.values().stream().filter(e->e.getStudent().equals(s)).collect(Collectors.toList());
    }
    public List<Enrollment> findByCourse(Course c){
        return enrollments.values().stream().filter(e->e.getCourse().equals(c)).collect(Collectors.toList());
    }
}
