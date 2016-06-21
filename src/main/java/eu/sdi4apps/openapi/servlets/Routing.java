package eu.sdi4apps.openapi.servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.sdi4apps.routing.types.NodeResponse;
import eu.sdi4apps.routing.types.RoutingOperations;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.openapi.utils.Cors;
import eu.sdi4apps.routing.RouteCalculator;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author runarbe
 */
@WebServlet(name = "routing", urlPatterns = {"/routing"})
public class Routing extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param hReq servlet request
     * @param hResp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest hReq, HttpServletResponse hResp)
            throws ServletException, IOException, SQLException, ClassNotFoundException {

        hResp = Cors.EnableCors(hResp);

        PrintWriter out = hResp.getWriter();

        Response r = Response.Error(null);

        try {

            String action = hReq.getParameter("action");

            switch (action) {
                case RoutingOperations.NEARESTNODE:
                    double lat = Double.parseDouble(hReq.getParameter("lat"));
                    double lon = Double.parseDouble(hReq.getParameter("lon"));
                    int radius = Integer.parseInt(hReq.getParameter("radius"));
                    r = RouteCalculator.GetNodeByLonLat(lon, lat, radius);
                    break;
                case RoutingOperations.SHORTESTPATH:
                    int from = Integer.parseInt(hReq.getParameter("from"));
                    int to = Integer.parseInt(hReq.getParameter("to"));
                    r = RouteCalculator.GetShortestRoute(from, to);
                    break;
                case RoutingOperations.OPTIMALROUTE:
                    r = RouteCalculator.GetOptimalRoute(hReq);
                    break;
                case RoutingOperations.REACHABLEAREA:
                    r = RouteCalculator.GetReachableArea(hReq);
                    break;
                default:
                    r.unsupportedAction(action);
                    break;
            }
        } catch (Exception e) {
            r.addMessage(e.toString());
        } finally {
            out.println(r.asJson());
            out.close();
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
        try {
            processRequest(request, response);

        } catch (SQLException ex) {
            Logger.getLogger(Routing.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Routing.class
                    .getName()).log(Level.SEVERE, null, ex);
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

        } catch (SQLException ex) {
            Logger.getLogger(Routing.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Routing.class
                    .getName()).log(Level.SEVERE, null, ex);
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
