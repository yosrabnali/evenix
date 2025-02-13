package services.EventsServices;


import Entity.Events.Event;
import Util.MyDB;
import services.IService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {
    private Connection con;

    public ServiceEvent() {
        con = MyDB.getInstance().getConnection();
    }

    @Override
    public void ajouter(Event event) throws SQLException {
        String req = "INSERT INTO evenement (idadmin, date, titre, description, NBplaces, prix, etat, type, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, event.getIdEvent());
        pre.setDate(2, new Date(event.getDate().getTime()));
        pre.setString(3, event.getTitre());
        pre.setString(4, event.getDescription());
        pre.setInt(5, event.getNBplaces());
        pre.setBigDecimal(6, event.getPrix());
        pre.setString(7, event.getEtat());
        pre.setString(8, event.getType());
        pre.setString(9, event.getImage());
        pre.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM evenement WHERE idevent = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, id);
        pre.executeUpdate();
    }

    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE evenement SET idadmin = ?, date = ?, titre = ?, description = ?, NBplaces = ?, prix = ?, etat = ?, type = ?, image = ? WHERE idevent = ?";
        PreparedStatement pre = con.prepareStatement(req);
        pre.setInt(1, event.getIdEvent());
        pre.setDate(2, new Date(event.getDate().getTime()));
        pre.setString(3, event.getTitre());
        pre.setString(4, event.getDescription());
        pre.setInt(5, event.getNBplaces());
        pre.setBigDecimal(6, event.getPrix());
        pre.setString(7, event.getEtat());
        pre.setString(8, event.getType());
        pre.setString(9, event.getImage());
        pre.setInt(10, event.getIdEvent());
        pre.executeUpdate();
    }

   // @Override
  //  public List<Event> afficher() throws SQLException {
    //    List<Event> events = new ArrayList<>();
      //  String req = "SELECT * FROM evenement";
        //Statement st = con.createStatement();
        //ResultSet rs = st.executeQuery(req);

        //while (rs.next()) {
          //  Event event = new Event(rs.getInt("idevent"),
                 //   rs.getInt("idadmin"),
                   // rs.getDate("date"),
                    //rs.getString("titre"),
                    //rs.getString("description"),
                    //rs.getInt("NBplaces"),
                    //rs.getBigDecimal("prix"),
                    //rs.getString("etat"),
                    //rs.getString("type"),
                    //rs.getString("image")
            //);
           // events.add(event);
       // }
       // return events;
    //}
}