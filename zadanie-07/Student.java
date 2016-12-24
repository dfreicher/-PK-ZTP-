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
@Table(name = "Tbl_Students")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Id")
    private int id;
    private String firstName;
    private String lastName;

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
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
