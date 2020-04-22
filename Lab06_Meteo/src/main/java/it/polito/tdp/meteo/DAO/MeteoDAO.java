package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		return null;
	}

	/**
	 * Ottenere il valore medio di umidita' registrata nel mese per ogni localita' del db
	 * @param mese 
	 * @return mappa con Localita' e corrispettivo valore di umidita' medio
	 */
	public Map<String, Double> getUmiditaMediaPerMese(int mese) {
		
		String sql= "SELECT localita, AVG(umidita) AS umiMedia " + 
				"FROM situazione " + 
				"WHERE MONTH(DATA)= ? " + // notare che uso comandi specifici di SQL per estrarre solo il mese dalla data, same se volessi solo il giorno DAY(data) 
				"GROUP BY localita "; 
		
		Map<String, Double> risultato= new HashMap<>(); 
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1,mese);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				risultato.put(rs.getString("Localita"), rs.getDouble("umiMedia")); 
			}

			conn.close();
			return risultato;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}


}
