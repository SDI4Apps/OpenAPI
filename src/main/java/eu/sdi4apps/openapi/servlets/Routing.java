package eu.sdi4apps.openapi.servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.sdi4apps.openapi.exceptions.UnsupportedAction;
import eu.sdi4apps.pgrouting.types.NodeResponse;
import eu.sdi4apps.pgrouting.types.RoutingOperations;
import eu.sdi4apps.openapi.types.Response;
import eu.sdi4apps.pgrouting.types.RouteResponse;
import eu.sdi4apps.openapi.utils.Cors;
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
@WebServlet(name = "Routing", urlPatterns = {"/Routing"})
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
        hResp.setContentType("application/json;charset=UTF-8");
        hResp = Cors.EnableCors(hResp);

        PrintWriter out = hResp.getWriter();

        try {

            String action = hReq.getParameter("action");

            if (action.equals(RoutingOperations.GetNearestNode)) {
                double lat = Double.parseDouble(hReq.getParameter("lat"));
                double lon = Double.parseDouble(hReq.getParameter("lon"));
                int radius = Integer.parseInt(hReq.getParameter("radius"));
                NodeResponse mResponse = NodeResponse.GetByLonLat(lon, lat, radius);
                out.println(mResponse.asJson());
            } else if (action.equals(RoutingOperations.GetShortestPath)) {
                int from = Integer.parseInt(hReq.getParameter("from"));
                int to = Integer.parseInt(hReq.getParameter("to"));
                RouteResponse mResponse = RouteResponse.GetDijkstra(from, to);
                out.println(mResponse.asJson());
            } else {
                throw new UnsupportedAction(action);
            }

        } catch (Exception e) {
            Response mResponse = Response.Error(e.toString());
            out.println(mResponse.asJson());
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
