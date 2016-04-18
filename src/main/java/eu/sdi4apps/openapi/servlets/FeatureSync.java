/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.servlets;

import eu.sdi4apps.featuresync.CheckIn;
import eu.sdi4apps.featuresync.CheckOut;
import eu.sdi4apps.featuresync.types.CheckOutResponse;
import eu.sdi4apps.featuresync.types.FeatureSyncOperations;
import eu.sdi4apps.openapi.exceptions.GeneralException;
import eu.sdi4apps.openapi.exceptions.UnsupportedAction;
import eu.sdi4apps.openapi.types.BBox;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.utils.Cors;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author runarbe
 */
@WebServlet(name = "FeatureSync", urlPatterns = {"/FeatureSync"})
public class FeatureSync extends HttpServlet {

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
            throws ServletException, IOException {

        response = Cors.EnableCors(response);

        try (PrintWriter out = response.getWriter()) {
            Response r = new Response();
            String action = request.getParameter("action");
            r.action = action;

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String key = params.nextElement();
                String value = request.getParameter(key);
                r.parameters += String.format("%s=%s&", key, value);
            }

            try {
                switch (action) {
                    case FeatureSyncOperations.CheckOut:
                        String layerId = request.getParameter("layerId");
                        String idField = request.getParameter("idField");

                        BBox bbox = null;

                        String bboxStr = request.getParameter("bbox");
                        if (bboxStr == null) {
                            double longitude = Double.parseDouble(request.getParameter("longitude"));
                            double latitude = Double.parseDouble(request.getParameter("latitude"));
                            double buffer = Double.parseDouble(request.getParameter("buffer"));
                            bbox = BBox.createFromPointMeters(longitude, latitude, buffer);
                            r.messages.add(bbox.jsArray());
                        } else {
                            bbox = BBox.parseJsonBBox(bboxStr);
                        }

                        if (bbox == null) {
                            throw new Exception("Could not parse bbox from string: " + bboxStr);
                        }

                        r = CheckOut.Do(layerId, idField, bbox).mergeResponse(r);
                        //r = r1;
                        break;
                    case FeatureSyncOperations.CheckIn:
                        r = CheckIn.Do().mergeResponse(r);
                        break;
                    default:
                        throw new UnsupportedAction(action);
                }
            } catch (Exception e) {
                r.setError("Error during handling of request: " + e.toString());
            }
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
