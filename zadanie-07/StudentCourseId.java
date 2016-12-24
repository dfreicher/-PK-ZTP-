
import java.io.Serializable;

/**
 * @author Dominik Freicher
 * @version 1.0
 * Entity class
 */
public class StudentCourseId implements Serializable{

    private static final long serialVersionUID = 1L;

    private int studentId;
    private int courseId;

    /**
     *
     * @return studentId
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     *
     * @param studentId
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     *
     * @return courseId
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     *
     * @param courseId
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     *
     * @param obj
     * @return equals obj value
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StudentCourseId that = (StudentCourseId) obj;

        return studentId == that.studentId && courseId == that.courseId;
    }

    /**
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


}