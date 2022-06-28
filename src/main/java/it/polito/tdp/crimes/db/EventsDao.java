package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getCategorie() {
		String sql = "select DISTINCT offense_category_id "
				+ "from events";
				
		List<String> categorie = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				categorie.add(res.getString("offense_category_id"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return categorie;
	}
	
	public List<String> getVertici(String categoria, int mese){
		String sql = "SELECT distinct offense_type_id "
				+ "FROM EVENTS "
				+ "WHERE offense_category_id = ? "
				+ "AND MONTH(reported_date)= ?" ;
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, categoria);
			st.setInt(2, mese);
			
			List<String> vertici = new ArrayList<>() ;
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
					vertici.add(res.getString("offense_type_id"));
			}
			conn.close();
			return vertici;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Adiacenza> getArchi(String categoria, int mese){
		String sql = "SELECT e.offense_type_id as v1, ev.offense_type_id as v2, COUNT(DISTINCT e.neighborhood_id) AS peso "
				+ "FROM EVENTS e, EVENTS ev "
				+ "WHERE e.offense_type_id > ev.offense_type_id AND  "
				+ "e.offense_category_id = ? AND e.offense_category_id=ev.offense_category_id AND "
				+ "MONTH(e.reported_date) = ? AND MONTH(e.reported_date)= MONTH(ev.reported_date) AND "
				+ "e.neighborhood_id = ev.neighborhood_id "
				+ "GROUP BY e.offense_type_id, ev.offense_type_id" ;
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, categoria);
			st.setInt(2, mese);
			
			List<Adiacenza> archi = new ArrayList<>() ;
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
					archi.add(new Adiacenza(res.getString("v1"), res.getString("v2"), res.getInt("peso")));
			}
			conn.close();
			return archi;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
}
