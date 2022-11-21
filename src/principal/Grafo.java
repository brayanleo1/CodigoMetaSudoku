package principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Grafo {

	private List<List<Vector<Object>>> matriz;
	private List<List<Integer>> lista;
	private List<Integer> restantes;
	private List<List<Integer>> feromonios;
	private double completude;
	
	public Grafo() {
		matriz = new ArrayList<List<Vector<Object>>>();
		lista = new ArrayList<List<Integer>>();
		restantes = new ArrayList<Integer>();
		feromonios = new ArrayList<List<Integer>>();
	}
	
	public void setMatriz(List<List<Vector<Object>>> matriz) {
		this.matriz = matriz;
	}
	
	public List<List<Vector<Object>>> getMatriz(){
		return matriz;
	}
	
	public void addToMatriz(List<Vector<Object>> linha) {
		matriz.add(linha);
		restantes.add(0);
		for(int i = 0; i < linha.size(); i++) {
			List<Integer> fer = new ArrayList<Integer>();
			for(int j = 0; j < 10; j++) {
				fer.add(0);
			}
			feromonios.add(fer);
		}
	}
	
	public List<List<Integer>> getLista() {
		return lista;
	}
	
	public void atualizaLista() {
		if(lista.isEmpty()) {//Se lista ta vazia, cria ela
			for(int i = 0; i < matriz.size(); i++) {
				for(int j = 0; j < matriz.size(); j++) {
					List<Integer> lugar = new ArrayList<Integer>();
					lugar.add(i); //Linha
					lugar.add(j); //Coluna
					lugar.add((((List<Integer>) matriz.get(i).get(j).get(1)).size()));
					lista.add(lugar);
				}
			}
		} else {
			for(int i = 0; i < matriz.size(); i++) {
				for(int j = 0; j < matriz.size(); j++) {
					lista.get((i*matriz.size())+j).set(2, ((((List<Integer>) matriz.get(i).get(j).get(1)).size())));
				}
			}
		}	
	}
	
	public void addToRestante(Integer cor) {
		Integer val = restantes.get(cor - 1) + 1;
		restantes.set(cor - 1, val);
	}
	
	public Integer getRestanteCor(Integer cor) {
		return restantes.get(cor - 1);
	}
	
	public void setLista(List<List<Integer>> lista) {
		this.lista = lista;
	}
	
	public void setRestantes(List<Integer> restantes) {
		this.restantes = restantes;
	}
	
	public void setFeromonios(List<List<Integer>> feromonios) {
		this.feromonios = feromonios;
	}
	
	public void addFeromonio(int pos, Integer cor) {
		List<Integer> fers = feromonios.get(pos);//Pega os feromônios naquela posição, levando em consideração que as posições (0 a matriz.size * matriz.size) que referência a matriz
		fers.add(cor);//Adiciona nova cor nos feromônios
		fers.remove(10);//Remove a última cor dos feromônios para limitar a 10 cores, evaporando os feromônios que ficarem mais que 10 iterações
	}
	
	public List<List<Integer>> getFeromonios() {
		return feromonios;
	}
	
	public Grafo copyMatriz(){
		Grafo nG = new Grafo();
		List<List<Vector<Object>>> copia = new ArrayList<List<Vector<Object>>>();
		for(int i = 0; i < matriz.size(); i++) {
			List<Vector<Object>> cl = new ArrayList<Vector<Object>>();
			for(int j = 0; j < matriz.size(); j++) {
				Vector<Object> co = new Vector<Object>();
				co.add(Integer.parseInt((String) matriz.get(i).get(j).get(0).toString()));
				List<Integer> co2 = new ArrayList<Integer>();
				for(Integer it : ((List<Integer>) matriz.get(i).get(j).get(1))) {
					co2.add(Integer.parseInt(it.toString()));
				}
				co.add(co2);
				cl.add(co);
			}
			copia.add(cl);
		}
		nG.setMatriz(copia);
		
		List<List<Integer>> listaCop = new ArrayList<List<Integer>>();
		for(int i =0; i<lista.size(); i++) {
			List<Integer> tuplaCop = new ArrayList<Integer>();
			for(int j =0; j<3; j++) {
				tuplaCop.add(Integer.parseInt(lista.get(i).get(j).toString()));
			}
			listaCop.add(tuplaCop);
		}

		nG.setLista(listaCop);
		
		List<Integer> restCop = new ArrayList<Integer>();
		for(int i =0; i<restantes.size(); i++) {
			restCop.add(Integer.parseInt(restantes.get(i).toString()));
		}
		
		nG.setRestantes(restCop);
		
		nG.setFeromonios(feromonios);
		
		return nG;
	}
	
	public void setCompletude(double totCol) {
		completude = 100 * (totCol/(matriz.size()*matriz.size()));
	}
	
	public double getCompletude() {
		return completude;
	}
}
