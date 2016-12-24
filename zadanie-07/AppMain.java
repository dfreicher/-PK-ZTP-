import pl.jrj.db.IDbManager;

import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.persistence.TypedQuery;

/**
 * @author Dominik Freicher
 * @version 1.0
 * Main class of project
 */
public class AppMain {

    private static final Logger LOG=Logger.getLogger(AppMain.class.getName());

    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "DbManager!pl.jrj.db.IDbManager";

    private static final String PERSISTENCE_NAME = "myPersistence";

    private static double result = 0;
    private static String courseName = "";
    private static String studentFirstName = "";
    private static String studentLastName = "";

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        registerWork();
        fetchingData(args[0]);
        calculateResult();
    }

    private static void registerWork() {
        try {
            InitialContext ctx = new InitialContext();
            IDbManager manager = (IDbManager) ctx.lookup(JNDI_NAME);
            manager.register(7, "120525");
        } catch (NamingException e) {
            LOG.info(e.getMessage());
        }
    }

    private static void fetchingData(String fileName) {
        Scanner scanner = null;
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
            courseName = scanner.nextLine();
            String secondLine = scanner.nextLine();
            String[] secondLineEl = secondLine.trim().split(" ");
            studentFirstName = secondLineEl[0];
            studentLastName = secondLineEl[secondLineEl.length - 1];
        } catch (FileNotFoundException e) {
            LOG.info(e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static void calculateResult() {
        try {
            EntityManager em = Persistence
                    .createEntityManagerFactory(PERSISTENCE_NAME)
                    .createEntityManager();

            Integer median = 0;
            Integer grade = getStudentGrade(em);
            List<Integer> courseStudentGrades = getCourseGrades(em);
            Collections.sort(courseStudentGrades);

            if ((courseStudentGrades.size() % 2) == 0) {
                median = (courseStudentGrades
                        .get(courseStudentGrades.size() / 2)
                        + courseStudentGrades
                        .get(courseStudentGrades.size() / 2 - 1)) / 2;
            } else {
                median = courseStudentGrades
                        .get(courseStudentGrades.size() / 2);
            }

            if ((grade >= 0) && (median > 0)
                    && ((grade > median) || grade < median)) {
                result = ((double) (grade - median) / median) * 100;
            }

            printResult();
        } catch (java.lang.IllegalArgumentException e) {
            LOG.info(e.getMessage());
            printResult();
        } catch (javax.persistence.PersistenceException e) {
            LOG.info(e.getMessage());
            printResult();
        }
    }

    private static Integer getStudentGrade(EntityManager em) {
        TypedQuery<Integer> q = em.createQuery("SELECT s.mark "
                        + "FROM StudentCourse s "
                        + "WHERE s.student.firstName = :studentFirstName "
                        + "AND s.student.lastName = :studentLastName "
                        + "AND s.course.courseName = :courseName",
                Integer.class);

        q.setParameter("studentFirstName", studentFirstName);
        q.setParameter("studentLastName", studentLastName);
        q.setParameter("courseName", courseName);

        List<Integer> grades = q.getResultList();
        if (grades != null && grades.size() > 0) {
            return grades.get(0);
        } else {
            return 0;
        }
    }

    private static List<Integer> getCourseGrades(EntityManager em) {
        TypedQuery<Integer> q = em.createQuery("SELECT s.mark "
                        + "FROM StudentCourse s "
                        + "WHERE s.course.courseName = :courseName",
                Integer.class);

        q.setParameter("courseName", courseName);

        return q.getResultList();
    }

    private static void printResult() {
        System.out.println("Wynik : " + String.format("%.0f", result) + "%");
    }

}
