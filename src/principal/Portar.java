package principal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

/*
 * Talvez feromonizar os pontos coloridos que possuirem mais popularidade/moda/repetição de cores entre as formigas,
 * pode por feromônio em todos os pontos, mas as cores são aquelas mais populares ou
 * por feromônio apenas naqueles que possuem cores iguais entre as formigas.
 * A primeira opção parece mais tentadora... talvez por feromônio apenas naqueles fixos...
 * sabendo que a formiga só segue o feromônio se ela tiver mais de uma opção para colorir.
*/

public class Portar {
	
	public static Grafo resGrafo (Grafo grafo, int it, int formigas) {
		Stack<Grafo> pilha = new Stack<Grafo>();
		grafo.atualizaLista(); //Para criar a lista
		while(true) {
			List<List<Integer>> lista = new ArrayList<List<Integer>>(grafo.getLista());
			lista.sort(Comparator.comparing(l -> l.get(2)));
			int id = 0;
			while((Integer) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(0) != 0) {//Enquanto for um vértice colorido
				id+=1;
				if(id == grafo.getMatriz().size() * grafo.getMatriz().size()) {
					grafo.setCompletude(id);
					return grafo;
				}
			}
			if(lista.get(id).get(2) == 0) {//Se não houverem possibilidades para o vértice descolorido com menores possibilidades (0) para o vertice descolorido então volta
				grafo = pilha.pop();//Pegue o último elemento da pilha
			} else {//Existem pelo menos uma possibilidade de coloração
				if(lista.get(id).get(2) > 1) {//Se existem mais
					//Começamos a usar as formigas
					return Formigas.colonizar(grafo, it, formigas);
				} else {//Só existe uma cor, escolha ela e pronto
					Integer cor = ((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).get(0);//Pega a primeira cor
					grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
					grafo.addToRestante(cor);
					attVizinhos(lista.get(id).get(0), lista.get(id).get(1),grafo.getMatriz().size(),grafo, cor);//Atualize as cores dos vizinhos e dele mesmo
				}
			}
		}
	}
	
	//n = dimensão do sudoku nxn, lin = número de instâncias de sudoku a serem lisdas do arquivo
	public static List<Grafo> criaGrafos(String pos, int n, int lin) { //Recebe String indicando posição/nome do arquivo e então lê e cria a array de matrizes nxn do arquivo
	
	   //Ler arquivo = Criar array de strings
	   List<String> linhas = new ArrayList<String>();
	   for(int i = 0; i < lin; i++) {
		   try {
			linhas.addAll(Files.readAllLines(Paths.get(pos)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	   }
	   
	   List<Integer> poss0 = new ArrayList<Integer>();
	   List<Grafo> vetMat = new ArrayList<Grafo>();
	   
	   //Cria todos os "lin" grafos
	   for (int i = 0; i < lin; i++) {
	   	Grafo vetCol = new Grafo(); //Vetor de colunas, que guarda em cada posição as linhas
	   	for (int x = 0; x < n; x++) {
	   		List<Vector<Object>> vetLin = new ArrayList<Vector<Object>>(); //Vetor de linhas, que em cada posição guarda vetores com cor e possibilidades
	   		for (int y = 0; y < n; y++) {
	   				Vector<Object> vetPos = new Vector<Object>(); //Vetor de cada posição, que guarda cor e possibilidades
	    			Integer cor = Integer.parseInt(linhas.get(i).split(",")[x+(y*n)]);
	    			vetPos.add(0, cor);
	    			if(cor == 0) {
	    				List<Integer> possN = new ArrayList<Integer>(); //para quando houver n possibilidades
	    				for(int aux = 1; aux <= n; aux++) {
	    					possN.add(aux);
	    				}
	    				vetPos.add(1, possN);
	    			} else {
	    				vetPos.add(1, poss0);
	    			}
	    			vetLin.add(vetPos);
	    		}
	   		vetCol.addToMatriz(vetLin);
	    	}
	    	vetMat.add(vetCol);
	   }
	   
	   //Atualiza todas as possibilidades dos "lin" grafos
	   for (int i = 0; i < lin; i++) {
		   	for (int x = 0; x < n; x++) {
		   		for (int y = 0; y < n; y++) {
		    		Integer cor = (Integer) vetMat.get(i).getMatriz().get(x).get(y).get(0);
		    		if(cor != 0) {
		    			vetMat.get(i).addToRestante(cor);
		    			attVizinhos(x, y, n, vetMat.get(i), cor);
		    		}
		    	}
		    }
		}
	  return vetMat;
	 }

	public static void imprimaSudoku(int n,Grafo grafo) {
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				System.out.print(grafo.getMatriz().get(x).get(y).get(0));
				if(y != (n-1)) {
					System.out.print("|");
				} else {
					System.out.println();
				}
			}
			for(int i = 0; i < (2*n); i++) {
				System.out.print("_");
			}
			System.out.println();
		}
	}
	
	public static void attVizinhos(int x, int y, int n, Grafo grafo, Integer cor) {
		((List<Integer>) grafo.getMatriz().get(x).get(y).get(1)).clear();
		
		//Linha e coluna
		for(int i = 0; i < grafo.getMatriz().size(); i++) {
			//Remove da lista de possibilidades se houver tal cor na lista
			((List<Integer>) grafo.getMatriz().get(i).get(y).get(1)).remove((Integer) cor);
			((List<Integer>) grafo.getMatriz().get(x).get(i).get(1)).remove((Integer) cor);
		}
		
		//Adiciona linhas e colunas daquela grade específica
		List<Integer> linhas = new ArrayList<Integer>();
		List<Integer> colunas = new ArrayList<Integer>();
		int aux = 0;
		int numl = 0;
		int numc = 0;
		while(aux < Math.sqrt(grafo.getMatriz().size())) {
			aux+=1;
			if(numl > -1) {
				linhas.add(x + numl);
				numl+=1;
			} else {
				linhas.add(x + numl);
				numl-=1;
			}
			
			if(numc > -1) {
				colunas.add(y + numc);
				numc+=1;
			} else {
				colunas.add(y + numc);
				numc-=1;
			}
			
			if((x + numl) % Math.sqrt(grafo.getMatriz().size()) == 0) {
				numl = -1;
			}
			
			if((y + numc) % Math.sqrt(grafo.getMatriz().size()) == 0) {
				numc = -1;
			}
		}
		
		//Grade
		for(Integer i : linhas) {
			for(Integer j : colunas) {
				((ArrayList<Integer>) grafo.getMatriz().get(i).get(j).get(1)).remove((Integer) cor);
			}
		}
		//Saiba que ele irá repetir a verificação nas linhas e colunas que pertencerem à grade
		
		grafo.atualizaLista();
	}
	
	public static void main(String args[]) {
		//Listas de grafos para serem usados nos testes
		List<Grafo> fac9 = criaGrafos("src\\arquivosDeTestes\\9x9facil.txt", 9, 5);
		List<Grafo> med9 = criaGrafos("src\\arquivosDeTestes\\9x9medio.txt", 9, 5);
		List<Grafo> dif9 = criaGrafos("src\\arquivosDeTestes\\9x9dificil.txt", 9, 5);
		List<Grafo> mui_dif9 = criaGrafos("src\\arquivosDeTestes\\9x9muito_dificil.txt", 9, 5);
		List<Grafo> fac16 = criaGrafos("src\\arquivosDeTestes\\16x16facil.txt", 16, 5);
		List<Grafo> med16 = criaGrafos("src\\arquivosDeTestes\\16x16medio.txt", 16, 5);
		List<Grafo> fac25 = criaGrafos("src\\arquivosDeTestes\\25x25facil.txt", 25, 1);
		long tf9 = 0, tm9 = 0, td9 = 0, tmd9 = 0, tf16 = 0, tm16 = 0, tf25=0;
		double cf9 = 0, cm9 = 0, cd9 = 0, cmd9 = 0, cf16 = 0, cm16 = 0, cf25=0;
		Scanner teclado = new Scanner(System.in);
		int it = 0, formigas = 0;
		System.out.println("Escolha o numero de interações:");
		it = teclado.nextInt();
		System.out.println("Escolha o número de formigas a serem instanciadas:");
		formigas = teclado.nextInt();
		System.out.println();
		List<Grafo> melhores = new ArrayList<Grafo>();
		tf9= System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			fac9.set(i, resGrafo(fac9.get(i), it, formigas));
			cf9 += fac9.get(i).getCompletude();
			if(melhores.size() == 0) {
				melhores.add(fac9.get(i));
			} else {
				if(melhores.get(0).getCompletude() < fac9.get(i).getCompletude()) {
					melhores.set(0, fac9.get(i));
				}
			}
		}
		tf9= System.currentTimeMillis() - tf9;
		tm9 = System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			med9.set(i, resGrafo(med9.get(i), it, formigas));
			cm9 += med9.get(i).getCompletude();
			if(melhores.size() == 1) {
				melhores.add(med9.get(i));
			} else {
				if(melhores.get(1).getCompletude() < med9.get(i).getCompletude()) {
					melhores.set(1, med9.get(i));
				}
			}
		}
		tm9= System.currentTimeMillis() - tm9;
		td9 = System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			dif9.set(i, resGrafo(dif9.get(i), it, formigas));
			cd9 += dif9.get(i).getCompletude();
			if(melhores.size() == 2) {
				melhores.add(dif9.get(i));
			} else {
				if(melhores.get(2).getCompletude() < dif9.get(i).getCompletude()) {
					melhores.set(2, dif9.get(i));
				}
			}
		}
		td9 = System.currentTimeMillis() - td9;
		tmd9 = System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			mui_dif9.set(i, resGrafo(mui_dif9.get(i), it, formigas));
			cmd9 += mui_dif9.get(i).getCompletude();
			if(melhores.size() == 3) {
				melhores.add(mui_dif9.get(i));
			} else {
				if(melhores.get(3).getCompletude() < mui_dif9.get(i).getCompletude()) {
					melhores.set(3, mui_dif9.get(i));
				}
			}
		}
		tmd9 = System.currentTimeMillis() - tmd9;
		tf16 = System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			fac16.set(i, resGrafo(fac16.get(i), it, formigas));
			cf16 += fac16.get(i).getCompletude();
			if(melhores.size() == 4) {
				melhores.add(fac16.get(i));
			} else {
				if(melhores.get(4).getCompletude() < fac16.get(i).getCompletude()) {
					melhores.set(4, fac16.get(i));
				}
			}
		}
		tf16 = System.currentTimeMillis() - tf16;
		tm16 = System.currentTimeMillis();
		for(int i = 0; i < 5; i++) {
			med16.set(i, resGrafo(med16.get(i), it, formigas));
			cm16 += med16.get(i).getCompletude();
			if(melhores.size() == 5) {
				melhores.add(med16.get(i));
			} else {
				if(melhores.get(5).getCompletude() < med16.get(i).getCompletude()) {
					melhores.set(5, med16.get(i));
				}
			}
		}
		tm16 = System.currentTimeMillis() - tm16;
		tf25 = System.currentTimeMillis();
		fac25.set(0, resGrafo(fac25.get(0), it, formigas));
		cf25 += fac25.get(0).getCompletude();
		melhores.add(fac25.get(0));
		tf25 = System.currentTimeMillis() - tf25;
		System.out.println("Tempos médios respectivos");
		System.out.println(((tf9)/5)+"ms");
		System.out.println(((tm9)/5)+"ms");
		System.out.println(((td9)/5)+"ms");
		System.out.println(((tmd9)/5)+"ms");
		System.out.println(((tf16)/5)+"ms");
		System.out.println(((tm16)/5)+"ms");
		System.out.println((tf25)+"ms");
		System.out.println();
		System.out.println("Completudes médias respectivas");
		System.out.println("Tempos médios respectivos");
		System.out.println(((cf9)/5)+"%");
		System.out.println(((cm9)/5)+"%");
		System.out.println(((cd9)/5)+"%");
		System.out.println(((cmd9)/5)+"%");
		System.out.println(((cf16)/5)+"%");
		System.out.println(((cm16)/5)+"%");
		System.out.println((cf25)+"%");
		System.out.println();
		System.out.println("Veja agora os melhores resultados de cada categoria testada:");
		System.out.println();
		imprimaSudoku(9, melhores.get(0));
		System.out.println();
		imprimaSudoku(9, melhores.get(1));
		System.out.println();
		imprimaSudoku(9, melhores.get(2));
		System.out.println();
		imprimaSudoku(9, melhores.get(3));
		System.out.println();
		imprimaSudoku(16, melhores.get(4));
		System.out.println();
		imprimaSudoku(16, melhores.get(5));
		System.out.println();
		imprimaSudoku(25, melhores.get(6));
		
	}
}
