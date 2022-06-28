package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	
	private List<String> best;
	
	public Model(){
		dao = new EventsDao();
		
	}
	public void creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// aggiunta vertici
//		Graphs.addAllVertices(this.grafo, dao.getVertici(categoria, mese));
		
		// aggiunta archi
		for(Adiacenza a : dao.getArchi(categoria, mese)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
//		System.out.println("grafo creato!");
//		System.out.println("N. Vertici: "+this.grafo.vertexSet().size());
//		System.out.println("N. Archi: "+this.grafo.edgeSet().size());
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<String> getCategorie(){
		return this.dao.getCategorie();
	}
	
	public List<Adiacenza> getArchi(){
		List<Adiacenza> archi = new ArrayList<Adiacenza>();
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			archi.add(new Adiacenza(this.grafo.getEdgeSource(e),
						this.grafo.getEdgeTarget(e), 
						(int) this.grafo.getEdgeWeight(e)));
		}
		return archi;
	}
	
	public List<Adiacenza> getArchiMaggioriPesoMedio(){
		//scorro gli archi del grafo e calcolo il peso medio
		double pesoTot = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoTot += this.grafo.getEdgeWeight(e);
		}
		double avg = pesoTot/ this.grafo.edgeSet().size();
		System.out.println("Peso medio: "+avg);
		 //ri-scorro tutti gli archi, prendendo quelli > di avg
		List<Adiacenza> result = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>avg) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), (int) this.grafo.getEdgeWeight(e)));
			}
		}
		return result;
	}
	
	// ci dobbiamo trovare il cammino tra 2 vertici che tocca il maggior numero di vertici (non lo voglio diretto)
	// RICORSIONE
	public List<String> calcolaPercorso (String sorgente, String destinazione){
		
		best = new LinkedList<>();
		List<String> parziale = new LinkedList<>();
		parziale.add(sorgente);
		cerca(parziale, destinazione);
		return best;
		
	}
	
	private void cerca(List<String> parziale, String destinazione) {
		// condizione di terminazione: se l'ultimo elemento di parziale coincide con la destinazione, cioè sono arrivato
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			//è la migliore?
			if(parziale.size()> best.size()) {
				best = new LinkedList<>(parziale);
			}
			return;
		}
		
		//scorro i vicini dell'ultimo inserito e provo le varie "strade"
		//per ogni vicino lo aggiungo in parziale, lancio la ricorsione, faccio backtracking
		for(String v : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			
			if(!parziale.contains(v)) { // evito di creare dei cicli
				parziale.add(v);
				cerca(parziale, destinazione);
				parziale.remove(parziale.size()-1);
			}
		}
	}
}
