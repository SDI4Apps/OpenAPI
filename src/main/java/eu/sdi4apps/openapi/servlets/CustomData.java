/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.sdi4apps.openapi.servlets;

import eu.sdi4apps.customdata.CustomDb;
import eu.sdi4apps.customdata.types.CustomApplication;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.types.DataResponse;
import static eu.sdi4apps.openapi.utils.Cors.EnableCors;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author runarbe
 */
@WebServlet(name = "customdata", urlPatterns = {"/customdata"})
public class CustomData extends HttpServlet {

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

        response = EnableCors(response);

        DataResponse jr = new DataResponse();

        jr.parameters = request.getQueryString();

        try {

            String action = request.getParameter("action");

            if (action == null) {
                action = "";
            }

            switch (action) {
                case "CreateApplication":
                    jr.mergeResponse(CustomDb.createApplication(request));
                    break;
                case "DeleteApplication":
                    jr.mergeResponse(CustomDb.deleteApplication(request));
                    break;
                case "ListApplications":
                    jr.mergeResponse(CustomDb.listApplications(request));
                    break;
                case "CreateType":
                    break;
                case "ListTypes":
                    break;
                case "AddObject":
                    jr.mergeResponse(CustomDb.addObject(request));
                    break;
                case "UpdateObject":
                    break;
                case "DeleteObject":
                    break;
                case "GetObject":
                    break;
                case "QueryObject":
                    break;
                default:
                    jr.unsupportedAction(action);
            }

        } catch (Exception e) {
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(jr.asJson());
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
