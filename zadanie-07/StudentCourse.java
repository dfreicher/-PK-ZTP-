import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author Dominik Freicher
 * @version 1.0
 * Entity class
 */
@Entity
@Table(name = "Tbl_Student_Course")
@IdClass(StudentCourseId.class)
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private int studentId;
    @Id
    private int courseId;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "studentId", referencedColumnName = "Id")
    private Student student;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "courseId", referencedColumnName = "Id")
    private Course course;

    private int mark;

    /**
     *
     * @return mark
     */
    public int getMark() {
        return mark;
    }

    /**
     *
     * @param mark
     */
    public void setMark(int mark) {
        this.mark = mark;
    }

    /**
     *
     * @return student
     */
    public Student getStudent() {
        return student;
    }

    /**
     *
     * @param student
     */
    public void setStudent(Student student) {
        this.student = student;
    }

    /**
     *
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    /**
     *
     * @param course
     */
    public void setCourse(Course course) {
        this.course = course;
    }

}

