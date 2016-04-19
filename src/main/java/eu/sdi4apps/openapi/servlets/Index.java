/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.servlets;

import eu.sdi4apps.ftgeosearch.DatasetType;
import eu.sdi4apps.ftgeosearch.Indexer;
import eu.sdi4apps.ftgeosearch.IndexerQueue;
import eu.sdi4apps.openapi.utils.Logger;
import eu.sdi4apps.ftgeosearch.QueueItem;
import eu.sdi4apps.ftgeosearch.drivers.ShapefileDriver;
import eu.sdi4apps.openapi.config.Settings;
import eu.sdi4apps.openapi.types.DataResponse;
import eu.sdi4apps.openapi.utils.Cors;
import eu.sdi4apps.openapi.utils.HttpParam;
import java.io.IOException;
import java.io.PrintWriter;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author runarbe
 */
@WebServlet(urlPatterns = {"/index"})
public class Index extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response = Cors.EnableCors(response);
        DataResponse r = new DataResponse();
        r.parameters = request.getQueryString();
        try {
            String action = HttpParam.GetString(request, "action", "");

            switch (action) {
                case "ViewQueue":
                    int maxEntries = HttpParam.GetInt(request, "maxEntries", 10);
                    r.setData(IndexerQueue.top(maxEntries), true);
                    break;
                case "UnlockIndex":
                    Indexer.unlockIndex();
                    break;
                case "DropIndex":
                    Indexer.dropIndex();
                    break;
                case "EnqueueItemTest":
                    IndexerQueue.create();
                    ShapefileDriver driver2 = new ShapefileDriver(Settings.SHAPEDIR + "/ne_10m_populated_places_simple.shp");
                    List<String> titleFields2 = asList("name");
                    String titleFormat2 = "Title: %s";
                    List<String> descriptionFields2 = asList("featurecla", "adm0name", "sov0name");
                    String descriptionFormat2 = "Class: %s, Adm0: %s, (Sov: %s)";
                    List<String> additionalFields2 = asList("featurecla", "sov0name", "adm0name");
                    List<String> jsonDataFields2 = asList("featurecla", "adm0name");
                    QueueItem entry2 = QueueItem.create("Natural Earth Simple", "places", DatasetType.Shapefile, driver2, titleFields2, titleFormat2, descriptionFields2, descriptionFormat2, null, null, 4326);
                    entry2.additionalfields = additionalFields2;
                    entry2.jsondatafields = jsonDataFields2;
                    IndexerQueue.enqueue(entry2);

                    ShapefileDriver drv3 = new ShapefileDriver(Settings.SHAPEDIR + "/gn1000.shp");
                    List<String> titleFields3 = asList("field_2");
                    String titleFieldFormat3 = "%s";
                    List<String> descFields3 = asList("field_4", "field_8", "field_18");
                    String descFieldFormat3 = "Alternative forms: %s. Type of name %s in %s";
                    QueueItem entry3 = QueueItem.create("Geonames 1000", "names", DatasetType.Shapefile, drv3, titleFields3, titleFieldFormat3, descFields3, descFieldFormat3, null, null, 4326);
                    IndexerQueue.enqueue(entry3);
                    r.setData("Added two layers to index", true);
                    break;
                case "EnqueueShapefile":
                    Map<String, Boolean> reqFields = new HashMap<String, Boolean>();
                    reqFields.put("shapefileName", true);
                    reqFields.put("titleFields", true);
                    reqFields.put("titleFormat", true);
                    reqFields.put("descriptionFields", true);
                    reqFields.put("descriptionFormat", true);
                    reqFields.put("additionalFields", true);
                    reqFields.put("jsonDataFields", true);
                    Map<String, String> avFields = HttpParam.GetParameters(request, reqFields, r);
                    break;
                default:
                    r.setError("Unsupported action: " + action);
                    break;
            }

            r.setSuccess();
        } catch (Exception e) {
            r.setError("An error occurred: " + e.toString());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(r.asJson());
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
