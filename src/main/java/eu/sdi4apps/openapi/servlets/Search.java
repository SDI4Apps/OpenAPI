package eu.sdi4apps.openapi.servlets;

import eu.sdi4apps.ftgeosearch.SearchResult;
import eu.sdi4apps.ftgeosearch.Searcher;
import eu.sdi4apps.openapi.types.DataResponse;
import eu.sdi4apps.openapi.utils.Cors;
import eu.sdi4apps.openapi.utils.HttpParam;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author runarbe
 */
@WebServlet(urlPatterns = {"/search"})
public class Search extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {

        response = Cors.EnableCors(response);

        DataResponse r = new DataResponse();

        r.parameters = request.getQueryString();

        PrintWriter out = response.getWriter();

        try {

            Map<String, Boolean> m = new LinkedHashMap<>();
            m.put("q", true);
            m.put("filter", false);
            m.put("maxresults", false);
            m.put("extent", false);

            Map<String, String> params = HttpParam.GetParameters(request, m, r);
            if (r.status == "error") {
                out.println(r.asJson());
                return;
            }

            Integer maxResults;
            if (params.get("maxresults") != null) {
                maxResults = NumberUtils.toInt(params.get("maxresults"));
            } else {
                maxResults = null;
            }

            List<Object> sres = Searcher.Search((String) params.get("q"),
                    maxResults,
                    params.get("filter"),
                    params.get("extent"));

            r.setData(sres, true);
            r.count = sres.size();

        } catch (Exception e) {
            r.addMessage("An error occurred: " + e.toString());
        }
        out.println(r.asJson());
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }
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
