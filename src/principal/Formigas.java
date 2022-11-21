package principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Formigas {

	public static Grafo colonizar(Grafo graf, int it, int formigas) {
		Grafo melhor = graf.copyMatriz();
		int mNum = 0;
		for(int i = 0; i < it; i++) {//Bora rodar as "it" vezes para tentar encontrar a solução
			Grafo results[] = new Grafo[formigas];
			Integer alc[] = new Integer[formigas];
			for (int f = 0; f < formigas; f++) {
				List<Object> res = formigar(graf.copyMatriz(), it, i);
				results[f] = (Grafo) res.get(0);
				alc[f] = (Integer) res.get(1);
			}
			//Pegar o melhor grafo
			for(int num = 0; num < formigas; num++) {
				if(alc[num] > mNum) {//O grafo atual possuir mais células preenchidas que o melhor atual
					mNum = alc[num];
					melhor = results[num];
				}
			}
			if(mNum == graf.getMatriz().size() * graf.getMatriz().size()) {//Se todos os vértices estão coloridos
				return melhor; //Retorna a solução
			} else {//Se não
				//Atualize os feromônios
				for (int x = 0; x < graf.getMatriz().size(); x++) {
					for (int y = 0; y < graf.getMatriz().size(); y++) {//Itera sobre a matriz célula por célula para descobrir quais cores foram usadas
						List<Integer> escolhas = new ArrayList<Integer>();
						for (int aux = 0; aux < formigas; aux++) {//Itera sobre cada célula de cada resultado obtido das formigas
							Integer cor = (Integer) results[aux].getMatriz().get(x).get(y).get(0);//Pega a cor naquela célula
							if(cor != 0) {//Se a célula foi colorida
								escolhas.add(cor);//Adiciona as escolhas
								escolhas.add(alc[aux]);
							}
						}
						if(escolhas.size() != 0) {
							if(escolhas.size() == 2) {
								graf.addFeromonio(x+(y*graf.getMatriz().size()), escolhas.get(0));
							} else {
								int popular = 0;
								int indice = 0;
								for(int aux = 0; aux < escolhas.size() - 2; aux+=2) {
									Integer total = escolhas.get(aux + 1);
									for (int aux2 = 2; aux2 < escolhas.size(); aux2+=2) {
										if(escolhas.get(aux) == escolhas.get(aux2)) {//Se a cor for igual
											total = total + escolhas.get(aux2 + 1);
										}
									}
									if(total > popular) {
										popular = total;
										indice = aux;
									}
								}
								graf.addFeromonio(x+(y*graf.getMatriz().size()), escolhas.get(indice));
							}
						} else {//Essa célula não foi colorida por nenhum, por tanto, adicione zero feromônio para evaporar outros possíveis feromônios nas 10 iterações
							graf.addFeromonio(x+(y*graf.getMatriz().size()), 0);
						}
					}
				}
			}
		} 
		return melhor;
	}
	
	public static List<Object> formigar(Grafo graf, int it, int vez) {
		List<Object> res = new ArrayList<Object>();
		while(true) {
			List<List<Integer>> lista = new ArrayList<List<Integer>>(graf.getLista());
			lista.sort(Comparator.comparing(l -> l.get(2)));
			int id = 0;
			while((Integer) graf.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(0) != 0) {//Enquanto for um vértice colorido
				id+=1;
				if(id == graf.getMatriz().size() * graf.getMatriz().size()) {
					graf.setCompletude(id);
					res.add(graf);
					res.add(id);
					return res;
				}
			}
			if(lista.get(id).get(2) == 0) {//Se não houverem possibilidades para o vértice descolorido com menores possibilidades (0) para o vertice descolorido então retorna esse que é o melhor
				graf.setCompletude(id);
				res.add(graf);
				res.add(id);
				return res;
			} else {//Existem pelo menos uma possibilidade de coloração
				if(lista.get(id).get(2) > 1) {//Se existem mais, bora sortear e decidir o que a formiga vai fazer
					Random rand = new Random();
					rand.setSeed(System.currentTimeMillis());//Sortear de forma mais aleatória pegando o tempo do sistema
					boolean dsatur = (rand.nextFloat() < (0.75 - (vez/(2*it)))); //Saber se o número que for sorteado é menor do que a chance atual de gerar a chance do DSatur que diminui com as iterações
					if(dsatur) {
						int cor = 0;
						for(Integer pCor : ((List<Integer>) graf.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1))) {
							if(cor == 0) {//Se for a cor 0
								cor = pCor; //Só atualiza
							} else if(graf.getRestanteCor(pCor) < graf.getRestanteCor(cor)) {//Se não for e a nova cor for mais usada que 
								cor = pCor; //Atualize pois essa cor pode aumentar a quantidade de células coloridas
							}
						}
						((List<Integer>) graf.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).remove((Integer) cor); //Remova essa cor das possibilidades
						graf.atualizaLista();
						graf.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
						Portar.attVizinhos(lista.get(id).get(0), lista.get(id).get(1),graf.getMatriz().size(),graf, cor);//Atualize as cores dos vizinhos, desde que dele mesmo já foi removido
					} else {
						//Selecionar algum outro local
						int local = (rand.nextInt((graf.getMatriz().get(0).size() * graf.getMatriz().get(0).size()) - id - 1)) + id;
						//Tentar colorir com um feromônio
						List<Integer> possiveis = new ArrayList<Integer>();
						for(int i = 0; i < graf.getMatriz().get(lista.get(local).get(0)).get(lista.get(local).get(1)).size(); i++) {
							for(int j = 0; j < graf.getFeromonios().get(local).size(); j++) {
								if(((List<Integer>) graf.getMatriz().get(lista.get(local).get(0)).get(lista.get(local).get(1)).get(1)).get(i) == graf.getFeromonios().get(local).get(j)) {//Se essa cor específica estiver nos feromônios
									possiveis.add( graf.getFeromonios().get(local).get(j));
								}
							}
						}
						if(possiveis.size() != 0) {//Existe pelo menos uma cor do feromônio que dá pra usar
							//Sortei e pegue a cor(dado que quanto mais feromônio da mesma cor, maiores as chances dela ser escolhida)
							Integer cor = possiveis.get(rand.nextInt(possiveis.size() - 1));
							graf.getMatriz().get(lista.get(local).get(0)).get(lista.get(local).get(1)).set(0, cor);//Colore com a cor pega
							graf.addToRestante(cor);
							Portar.attVizinhos(lista.get(local).get(0), lista.get(local).get(1),graf.getMatriz().size(),graf, cor);//Atualize as cores dos vizinhos e dele mesmo
						} else {
							//Escolha a cor que for menos usada
							int cor = 0;
							for(Integer pCor : ((List<Integer>) graf.getMatriz().get(lista.get(local).get(0)).get(lista.get(local).get(1)).get(1))) {
								if(cor == 0) {//Se for a cor 0
									cor = pCor; //Só atualiza
								} else if(graf.getRestanteCor(pCor) < graf.getRestanteCor(cor)) {//Se não for e a nova cor for mais usada que 
									cor = pCor; //Atualize pois essa cor pode aumentar a quantidade de células coloridas
								}
							}
							((List<Integer>) graf.getMatriz().get(lista.get(local).get(0)).get(lista.get(local).get(1)).get(1)).remove((Integer) cor); //Remova essa cor das possibilidades
							graf.atualizaLista();
							graf.getMatriz().get(lista.get(local).get(0)).get(lista.get(local).get(1)).set(0, cor);//Colore com a cor pega
							Portar.attVizinhos(lista.get(local).get(0), lista.get(local).get(1),graf.getMatriz().size(),graf, cor);//Atualize as cores dos vizinhos, desde que dele mesmo já foi removido
						}
					}
				} else {//Só existe uma cor, escolha ela e pronto
					Integer cor = ((List<Integer>) graf.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).get(0);//Pega a primeira cor
					graf.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
					graf.addToRestante(cor);
					Portar.attVizinhos(lista.get(id).get(0), lista.get(id).get(1),graf.getMatriz().size(),graf, cor);//Atualize as cores dos vizinhos e dele mesmo
				}
			}
		}
	}
	
	
}
