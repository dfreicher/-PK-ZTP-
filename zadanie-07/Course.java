import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Dominik Freicher
 * @version 1.0
 * Entity class
 */
@Entity
@Table(name = "Tbl_Courses")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Id")
    private int id;
    private String courseName;

    @OneToMany
    private List<StudentCourse> studentCourses = new ArrayList<StudentCourse>();

    /**
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return courseName
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     *
     * @param courseName
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     *
     * @return studentCourses
     */
    public List<StudentCourse> getStudentCourses() {
        return studentCourses;
    }

    /**
     *
     * @param studentCourses
     */
    public void setStudentCourses(List<StudentCourse> studentCourses) {
        this.studentCourses = studentCourses;
    }

}
