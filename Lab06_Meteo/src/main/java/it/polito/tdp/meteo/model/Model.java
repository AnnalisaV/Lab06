package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO dao; 
	private List<Citta> tutteLeCitta; 
	List<Citta> soluzioneMigliore; 

	public Model() {
		this.dao= new MeteoDAO(); 
		this.tutteLeCitta= new ArrayList<>(this.dao.getCitta()); 

	}

	// of course you can change the String output with what you think works best
	public Map<String, Double> getUmiditaMedia(int mese) {
		Map<String, Double> umidita= new HashMap<>(); 
		return this.dao.getUmiditaMediaPerMese(mese);
	}
	
	// of course you can change the String output with what you think works best
	/**
	 * Sequenza migliore di citta da visitare in un certo emse per minimizzare il costo
	 * @param mese
	 * @return
	 */
	public List<Citta> trovaSequenza(int mese) {
		
		this.soluzioneMigliore=null; // così ad ogni nuova ricorsion e' 'pulita'
		List<Citta> parziale= new ArrayList<>(); // soluzione parziale da comporre durante la ricorsione
		
		// per ogni localita del db popolo la sua lista dei rilevamenti per quel mese specifico
		for (Citta c : tutteLeCitta) {
			c.setRilevamenti(this.dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		
		ricorsione(parziale, 0); 
		return soluzioneMigliore; 
		
		
		
	}

	/**
	 * Algoritmo ricorsivo
	 * @param parziale
	 * @param livello giorno del mese (max 15)
	 */
	private void ricorsione(List<Citta> parziale, int livello) {
		
		//caso terminale 
		if (livello== this.NUMERO_GIORNI_TOTALI) { // livello== 15
			// ma quando e' accettabile come soluzione?
			// quando ho minimizzato il costo oppure e' la primissima soluzione trovata
			if (this.soluzioneMigliore == null || calcolaCosto(parziale)<calcolaCosto(this.soluzioneMigliore)) {
				this.soluzioneMigliore= new ArrayList<Citta>(parziale); 
			}
		}
		
		//caso generale 
		for (Citta c : tutteLeCitta) {
			if (valida(c, parziale)) {
				// proseguo nella ricorsione
				parziale.add(c); 
				ricorsione(parziale, livello+1); 
				parziale.remove(parziale.size()-1); // backtracking
			}
		}
		
		
	}

	/**
	 * Criterio per inserire una citta nella sequenza 
	 * (max presente sei volte e per minimo tre giorni consecutivi)
	 * @param c {@code Citta} da verificare
	 * @return true se si puo' aggiungere alla lista, false altrimenti
	 */
	private boolean valida(Citta c, List<Citta> parziale) {
		
		int count=0; // contatore di quante volte quella citta sta gia' li dentro
		
		for (Citta citta : parziale) {
			if(citta.getNome().equals(c.getNome())) {
				count++; 
			}
		}
			if (count>=this.NUMERO_GIORNI_CITTA_MAX) return false; // non posso aggiungerla >=6
			
			// la prima citta puo' essere qualunque 
			if (parziale.size()==0) return true; 
			
			// se e' il primo o il secondo giorno deve essere la stessa citta
			if (parziale.size()==1 || parziale.size()==2) {
				if (parziale.get(parziale.size()-1).equals(c))
					return true; 
				else return false; 
			}
			
			// ora posso rimanere per i successivi ai primi tre giorni
			if (parziale.get(parziale.size()-1).equals(c))
				return true; 
			
			// se voglio cambiare devo essere stata come minimo tre giorni ferma
			// il giorno precedente dev'essere uguale al suo precedente ovvero 3=2
			// e 2=1
			if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
            && parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
				return true;
			
			//altri casi 
		return false;
	}
	

	/**
	 * Calcolare il costo degli interventi sulla sequenza di Citta da visitare
	 * @param parziale
	 * @return costo totale (umidita del giorno +100*spostamenti)
	 */
	private Double calcolaCosto(List<Citta> parziale) {
		Double costo=0.0; 
		
		// prendo il valore dell'umidita di ogni giorno
		/* anche con for(Citta c: parziale) */
		for (int i=1; i<=15; i++) {
			//prendo la Citta in posizione i-1 (partendo da 1 il conto ma l'array parte da 0), 
			//mi faccio dare la lista di Rilevamenti e prendo
			// quello che corrisponde a quel giorno così ho la sua umidita
			double umidita=parziale.get(i-1).getRilevamenti().get(i-1).getUmidita();
			costo+=umidita;
			}
		
		// quante volte mi sono spostato di citta?
		for (int i=1; i<=15; i++ ) { // non potrei farlo con foreach perche' ho bisogno degli indici
			// se quella Citta non e' la stessa del 'giorno' prima
			// mi sono spostato ed aggiungo 100
			if (!parziale.get(i).equals(parziale.get(i-1))) {
				costo+=100; 
			}
		}
		
		
		return costo;
	}
	

}
