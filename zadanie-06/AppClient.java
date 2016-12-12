import pl.jrj.db.IDbManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Dominik Freicher
 * @version 1.0
 * Main class of project
 */
public class AppClient {

    private static final Logger LOG=Logger.getLogger(AppClient.class.getName());

    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "DbManager!pl.jrj.db.IDbManager";

    private static final String PERSISTENCE_NAME = "persistence120525";

    private static double result = 0;

    private static String carModel;

    private static String periodDate;

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
            manager.register(6, "120525");
        } catch (NamingException e) {
            LOG.info(e.getMessage());
        }
    }

    private static void fetchingData(String fileName) {
        Scanner scanner = null;
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
            carModel = scanner.nextLine();
            periodDate = scanner.nextLine();
            parseInsuranceDate();
        } catch (FileNotFoundException e) {
            LOG.info(e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static void calculateResult() {
        double customersCount = 0;
        double customersWithInsurance = 0;

        try {
            EntityManager em = Persistence
                    .createEntityManagerFactory(PERSISTENCE_NAME)
                    .createEntityManager();

            Query firstQuery = em.createQuery("SELECT count(c) " +
                    "FROM Customer c");
            customersCount = Double.parseDouble(firstQuery.getSingleResult()
                    .toString());

            Query secondQuery = em.createNativeQuery(
                    "select count(distinct tbinsurance.customerid) " +
                            "from tbinsurance " +
                            "join tbmodel on " +
                            "tbinsurance.modelid = tbmodel.id " +
                            "join tbcustomer on " +
                            "tbinsurance.customerId = tbcustomer.id " +
                            "where tbmodel.model = ? and " +
                            "datefrom <= ? and dateto >= ?")
                    .setParameter(1, carModel)
                    .setParameter(2, periodDate)
                    .setParameter(3, periodDate);

            customersWithInsurance = Double.parseDouble(secondQuery
                    .getSingleResult().toString());

            result = ((customersCount - customersWithInsurance) /
                    customersCount) * 100;
            printResult();
        } catch (java.lang.IllegalArgumentException e) {
            LOG.info(e.getMessage());
            printResult();
        } catch (javax.persistence.PersistenceException e) {
            LOG.info(e.getMessage());
            printResult();
        }
    }

    private static void printResult() {
        System.out.println("Wynik : "+String.format("%.1f", result) + "%");
    }

    private static void parseInsuranceDate() {
        try {
            Date dStamp = DateFormat.getDateInstance(
                    DateFormat.DEFAULT, Locale.getDefault()).parse(periodDate);
            periodDate = new Timestamp(dStamp.getTime()).toString();
        } catch (ParseException e) {
            LOG.info(e.getMessage());
        }
    }
}
