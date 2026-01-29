package legacy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Legacy OOP assignment code (kept as-is, so your "old assignments" are present).
 * This file is NOT used by Spring Boot API, it's just included in the project.
 */
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
    public String getRole() { return "Person"; }

    @Override
    public String toString() {
        return String.format("%s (%s, id=%s)", getFullName(), getRole(), id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    @Override
    public String getRole() { return "Student"; }

    public List<Enrollment> getEnrollments() {
        return Collections.unmodifiableList(enrollments);
    }

    public List<Enrollment> getEnrollmentsByStatus(Enrollment.Status status) {
        return enrollments.stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    void addEnrollment(Enrollment e) { enrollments.add(e); }
}

class Instructor extends Person {
    private final String department;

    public Instructor(String firstName, String lastName, String department) {
        super(firstName, lastName);
        this.department = department;
    }

    public String getDepartment() { return department; }

    @Override
    public String getRole() { return "Instructor"; }
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

    public void setInstructor(Instructor instructor) { this.instructor = instructor; }

    public List<Enrollment> getEnrollments() {
        return Collections.unmodifiableList(enrollments);
    }

    boolean isFull() { return enrollments.size() >= capacity; }

    void addEnrollment(Enrollment e) { enrollments.add(e); }
    void removeEnrollment(Enrollment e) { enrollments.remove(e); }

    @Override
    public String toString() {
        return String.format(
                "%s - %s (cap: %d, enrolled: %d, instructor: %s)",
                code, title, capacity, enrollments.size(),
                instructor == null ? "-" : instructor.getFullName()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id.equals(course.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

class Enrollment {
    public enum Status { ENROLLED, COMPLETED, DROPPED }

    private final UUID id;
    private final Student student;
    private final Course course;
    private Status status;
    private Double grade;

    public Enrollment(Student student, Course course) {
        this.id = UUID.randomUUID();
        this.student = student;
        this.course = course;
        this.status = Status.ENROLLED;
    }

    public UUID getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }

    @Override
    public String toString() {
        return String.format(
                "Enrollment[id=%s, student=%s, course=%s, status=%s, grade=%s]",
                id, student.getFullName(), course.getCode(), status,
                grade == null ? "-" : grade
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

class CourseManager {
    private final Map<UUID, Course> courses = new HashMap<>();

    public Course createCourse(String code, String title, int capacity) {
        Course c = new Course(code, title, capacity);
        courses.put(c.getId(), c);
        return c;
    }

    public Optional<Course> getCourseById(UUID id) {
        return Optional.ofNullable(courses.get(id));
    }

    public Optional<Course> getCourseByCode(String code) {
        return courses.values().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    public List<Course> listCourses() {
        return new ArrayList<>(courses.values());
    }

    public List<Course> sortByTitle() {
        return courses.values().stream()
                .sorted(Comparator.comparing(Course::getTitle))
                .collect(Collectors.toList());
    }

    public boolean deleteCourse(UUID id) { return courses.remove(id) != null; }
}

class EnrollmentManager {
    private final Map<UUID, Enrollment> enrollments = new HashMap<>();

    public void enroll(Student s, Course c) {
        if (c.isFull()) throw new IllegalStateException("Course is full");

        boolean alreadyEnrolled = enrollments.values().stream()
                .anyMatch(e -> e.getStudent().equals(s) && e.getCourse().equals(c));

        if (alreadyEnrolled) throw new IllegalStateException("Student already enrolled");

        Enrollment e = new Enrollment(s, c);
        enrollments.put(e.getId(), e);
        c.addEnrollment(e);
        s.addEnrollment(e);
    }

    public boolean drop(Enrollment e) {
        e.setStatus(Enrollment.Status.DROPPED);
        e.getCourse().removeEnrollment(e);
        enrollments.remove(e.getId());
        return true;
    }

    public List<Enrollment> findByStudent(Student s) {
        return enrollments.values().stream()
                .filter(e -> e.getStudent().equals(s))
                .collect(Collectors.toList());
    }

    public List<Enrollment> findByCourse(Course c) {
        return enrollments.values().stream()
                .filter(e -> e.getCourse().equals(c))
                .collect(Collectors.toList());
    }

    public List<Enrollment> findByStatus(Enrollment.Status status) {
        return enrollments.values().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Enrollment> sortByGradeDesc() {
        return enrollments.values().stream()
                .filter(e -> e.getGrade() != null)
                .sorted(Comparator.comparing(Enrollment::getGrade).reversed())
                .collect(Collectors.toList());
    }
}
