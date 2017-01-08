import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import pl.jrj.db.IDbManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Dominik Freicher
 * @version 1.0
 */
@Path("/country")
public class Country {

    private static final Logger LOG=Logger.getLogger(Country.class.getName());

    private static final String JNDI_NAME = "java:global/ejb-project/" +
            "DbManager!pl.jrj.db.IDbManager";

    private static final String STAT_URL = "http://www.stat.gov.pl/broker/" +
            "access/prefile/downloadPreFile.jspa?id=1616";

    private static final String XML_EXT = ".xml";
    private static final String TAG_NAME = "name";
    private static final String TAG_NAZDOD = "NAZDOD";

    private File countryFile;

    private List<String> provinces;
    private List<String> counties;
    private List<String> municipalities;

    /**
     * Public constructor
     */
    public Country() {
        registerWork();
        getCountryFile();
    }

    private void registerWork() {
        try {
            InitialContext ctx = new InitialContext();
            IDbManager manager = (IDbManager) ctx.lookup(JNDI_NAME);
            manager.register(8, "120525");
        } catch (NamingException e) {
            LOG.info(e.getMessage());
        }
    }

    private void getCountryFile() {
        try {
            HttpURLConnection connection = getHttpURLConnection();
            ZipInputStream stream
                    = new ZipInputStream(connection.getInputStream());
            ZipEntry entry = stream.getNextEntry();
            findXMLFile(stream, entry);
            connection.disconnect();
            stream.close();
        } catch (MalformedURLException e) {
            LOG.info(e.getMessage());
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }
    }

    private HttpURLConnection getHttpURLConnection() throws IOException {
        URL statUrl = new URL(STAT_URL);
        return (HttpURLConnection)
                statUrl.openConnection();
    }

    private void findXMLFile(ZipInputStream stream,
                             ZipEntry entry) throws IOException {
        while (entry != null) {
            if (entry.getName().endsWith(XML_EXT)) {
                FileOutputStream outputStream = new FileOutputStream(
                        entry.getName());
                for (int c = stream.read(); c != -1; c = stream.read()) {
                    outputStream.write(c);
                }
                countryFile = new File(entry.getName());
                stream.closeEntry();
                outputStream.close();
            }
            entry = stream.getNextEntry();
        }
    }

    /**
     *
     * @return number of provinces
     */
    @GET
    public String getNumberOfProvinces() {
        int numberOfProvinces = 0;
        provinces = new ArrayList<String>();
        try {
            Document doc = getDocument();

            NodeList nodes = doc.getElementsByTagName("col");
            int length = nodes.getLength();
            for (int j = 0; j < length; j++) {
                Node node = nodes.item(j);
                Node previousNode = j > 1 ? nodes.item(j-1) : null;
                numberOfProvinces = getNumberOfProvinces(numberOfProvinces,
                        node, (Element) previousNode);
            }

        } catch (SAXException e) {
            LOG.info(e.getMessage());
        } catch (IOException e) {
            LOG.info(e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.info(e.getMessage());
        }

        return Integer.toString(numberOfProvinces);
    }

    private Document getDocument()
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(countryFile);
    }

    private int getNumberOfProvinces(int numberOfProvinces, Node node,
                                     Element previousNode) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element el = (Element) node;
            if (el.getAttribute(TAG_NAME).equals(TAG_NAZDOD)) {
                if (el.getTextContent().equals("województwo")) {
                    numberOfProvinces++;
                    Element previousEl = previousNode;
                    provinces.add(previousEl.getTextContent());
                }
            }
        }
        return numberOfProvinces;
    }

    /**
     *
     * @return list of provinces
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("/L")
    public String getListOfProvinces() {
        getNumberOfProvinces();
        return new Gson().toJson(provinces);
    }

    /**
     *
     * @return number of county
     */
    @GET
    @Path("{province : \\d{2}}")
    public String getNumberOfCounty(@PathParam("province") String province) {
        int numberOfCounty = 0;
        counties = new ArrayList<String>();
        try {

            Document doc = getDocument();
            NodeList nodes = doc.getElementsByTagName("row");

            boolean isProv;
            boolean isCounty;

            for (int j = 0; j < nodes.getLength(); j++) {
                isProv = false;
                isCounty = false;
                Element row = (Element) nodes.item(j);
                NodeList childs = row.getChildNodes();

                for (int k = 0; k < childs.getLength(); k++) {
                    Node childNode = childs.item(k);

                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element child = (Element) childNode;
                        if (child.getAttribute(TAG_NAME) != null) {

                            if (child.getAttribute(TAG_NAME)
                                    .equals("WOJ")) {

                                if (child.getTextContent().equals(province)) {
                                    isProv = true;
                                }

                            } else if (child.getAttribute(TAG_NAME)
                                    .equals(TAG_NAZDOD)) {
                                if (child.getTextContent().equals("powiat")
                                        || child.getTextContent().equals("miasto na prawach powiatu")) {
                                    isCounty = true;
                                }
                            }
                            if (isProv && isCounty) {
                                numberOfCounty++;
                                isProv = false;
                                isCounty = false;
                                Node previousNode = childs.item(k-2);
                                counties.add(previousNode.getFirstChild().getTextContent());
                            }

                        }

                    }

                }

            }
        } catch (SAXException e) {
            LOG.info(e.getMessage());
        } catch (IOException e) {
            LOG.info(e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.info(e.getMessage());
        }

        return Integer.toString(numberOfCounty);
    }

    /**
     *
     * @return list of counties
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{province : \\d{2}}/L")
    public String getListOfCounty(@PathParam("province") String province) {
        getNumberOfCounty(province);
        return new Gson().toJson(counties);
    }

    /**
     *
     * @return number of municipalities
     */
    @GET
    @Path("{province : \\d{2}}/{district : \\d{2}}")
    public String numberOfMunicipalitiesFirst(@PathParam("province") String province,
                                              @PathParam("district") String district) {
        int numberOfMunicipalities = 0;
        municipalities = new ArrayList<String>();
        try {
            Document doc = getDocument();

            NodeList nodes = doc.getElementsByTagName("row");

            boolean isProv;
            boolean isDist;
            boolean isMunicipality;

            for (int j = 0; j < nodes.getLength(); j++) {

                isProv = false;
                isDist = false;
                isMunicipality = false;

                Element row = (Element) nodes.item(j);
                NodeList childs = row.getChildNodes();

                for (int k = 0; k < childs.getLength(); k++) {
                    Node child = childs.item(k);

                    if (child.getNodeType() == Node.ELEMENT_NODE) {

                        Element el = (Element) child;

                        if (el.getAttribute(TAG_NAME) != null) {

                            if (el.getAttribute(TAG_NAME).equals("WOJ")) {
                                if (el.getTextContent().equals(province)) {
                                    isProv = true;
                                }
                            } else if (el.getAttribute(TAG_NAME).equals("POW")) {
                                if (el.getTextContent().equals(district)) {
                                    isDist = true;
                                }
                            } else if (el.getAttribute(TAG_NAME).equals(TAG_NAZDOD)) {
                                if (el.getTextContent().startsWith("gmina")) {
                                    isMunicipality = true;
                                }
                            }
                            if (isProv && isDist && isMunicipality) {
                                numberOfMunicipalities++;
                                isProv = false;
                                isDist = false;
                                isMunicipality = false;
                                Node previousNode = childs.item(k-2);
                                municipalities.add(previousNode.getFirstChild().getTextContent());
                            }

                        }
                    }
                }

            }
        } catch (SAXException e) {
            LOG.info(e.getMessage());
        } catch (IOException e) {
            LOG.info(e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.info(e.getMessage());
        }

        return Integer.toString(numberOfMunicipalities);
    }

    /**
     *
     * @return list of municipalities
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{province : \\d{2}}/{district : \\d{2}}/L")
    public String listOfMunicipalitiesFirst(@PathParam("province") String province,
                                            @PathParam("district") String district) {
        numberOfMunicipalitiesFirst(province, district);
        return new Gson().toJson(municipalities);
    }

    /**
     *
     * @return number of municipalities
     */
    @GET
    @Path("{province : \\d{2}}{district : \\d{2}}")
    public String numberOfMunicipalitiesSecond(@PathParam("province") String province,
                                               @PathParam("district") String district) {
        return numberOfMunicipalitiesFirst(province, district);
    }

    /**
     *
     * @return list of municipalities
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{province : \\d{2}}{district : \\d{2}}/L")
    public String listOfMunicipalitiesSecond(@PathParam("province") String province,
                                             @PathParam("district") String district) {
        return listOfMunicipalitiesFirst(province, district);
    }
}
