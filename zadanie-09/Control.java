import pl.jrj.db.IDbManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.util.logging.Logger;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
@Path("cnt")
public class Control {

    private static final Logger LOG=Logger.getLogger(Control.class.getName());

    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "DbManager!pl.jrj.db.IDbManager";

    private static final String EMPTY_RESPONSE = "";

    private int processCounter = 0;
    private int errorsCounter = 0;

    @Context
    private HttpServletRequest request;

    private Boolean isRunning = null;

    /**
     * Start process
     * @return empty response
     */
    @GET
    @Path("start")
    public String start() {
        initProcess();

        if (isRunning) {
            errorsCounter++;
        } else {
            registerWork();
            resetProcess();
        }

        saveProcess();

        return EMPTY_RESPONSE;
    }


    /**
     * Stop process
     * @return empty response
     */
    @GET
    @Path("stop")
    public String stop() {
        initProcess();

        if (isRunning) {
            isRunning = false;
        } else {
            errorsCounter++;
        }

        saveProcess();

        return EMPTY_RESPONSE;
    }


    /**
     * Return state of counter
     * @return counter
     */
    @GET
    @Path("counter")
    public String counter() {
        initProcess();
        return Integer.toString(processCounter);
    }


    /**
     * Return number of errors
     * @return number of errors
     */
    @GET
    @Path("errors")
    public String errors() {
        initProcess();
        return Integer.toString(errorsCounter);
    }

    /**
     * Increment counter by 1
     * @return empty response
     */
    @GET
    @Path("incr")
    public String increment() {
        initProcess();

        if (!isRunning) {
            errorsCounter++;
        } else {
            processCounter++;
        }

        saveProcess();

        return EMPTY_RESPONSE;
    }

    /**
     * Increment counter by number
     * @param number number to increment
     * @return empty response
     */
    @GET
    @Path("incr/{number}")
    public String increment(@PathParam("number") String number) {
        initProcess();

        if (!isRunning) {
            errorsCounter++;
        } else {
            processCounter += Integer.parseInt(number);
        }

        saveProcess();

        return EMPTY_RESPONSE;
    }

    /**
     * Decrement counter by 1
     * @return empty response
     */
    @GET
    @Path("decr")
    public String decrement() {
        initProcess();

        if (!isRunning) {
            errorsCounter++;
        } else {
            processCounter--;
        }

        saveProcess();

        return EMPTY_RESPONSE;
    }

    /**
     * Decrement counter by number
     * @param number number to decrement
     * @return empty response
     */
    @GET
    @Path("decr/{number}")
    public String decrement(@PathParam("number") String number) {
        initProcess();

        if (!isRunning) {
            errorsCounter++;
        } else {
            processCounter -= Integer.parseInt(number);
        }

        saveProcess();

        return EMPTY_RESPONSE;
    }

    private void registerWork() {
        try {
            InitialContext ctx = new InitialContext();
            IDbManager manager = (IDbManager) ctx.lookup(JNDI_NAME);
            manager.register(9, "120525");
        } catch (NamingException e) {
            LOG.info(e.getMessage());
        }
    }

    private void initProcess() {
        HttpSession session = request.getSession(true);
        isRunning = (Boolean) session.getAttribute("isRunning");
        if (isRunning == null) {
            isRunning = false;
            processCounter = 0;
            errorsCounter = 0;
        } else {
            processCounter = (Integer) session.getAttribute("processCounter");
            errorsCounter = (Integer) session.getAttribute("errorsCounter");
        }
    }

    private void saveProcess() {
        HttpSession session = request.getSession(true);
        session.setAttribute("processCounter", processCounter);
        session.setAttribute("errorsCounter", errorsCounter);
        session.setAttribute("isRunning", isRunning);
    }

    private void resetProcess() {
        processCounter = 0;
        errorsCounter = 0;
        isRunning = true;
        saveProcess();
    }

}
