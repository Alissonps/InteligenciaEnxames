package cludipso;

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.*;

public class CLUDIPSO {

	public int numero_Particulas;
	public int numero_Iteracoes;
	public double[][] particulas;
	public double[][] pBest;
	public double[][][] velocidade;
	public double[] gBest;
	public double inercia;
	public double[] fitness;
	public double[] p_fitness; // Essa varavel vai armazenar o melhor fitness de cada particula.
	public double bestFitness;
	double pm_Max = 0.9;
	double pm_Min = 0.4;
	public double n1 = 2.8;
	public double n2 = 1.3;
	public int numero_Dimensoes = 2;

	public CLUDIPSO(int iteracoes, int n_particulas, double inercia) {
		this.inercia = inercia;

		this.numero_Iteracoes = iteracoes;

		this.numero_Particulas = n_particulas;

		this.particulas = new double[numero_Particulas][numero_Dimensoes];
		this.velocidade = new double[iteracoes][numero_Particulas][numero_Dimensoes];

		this.gBest = new double[numero_Dimensoes];
		this.fitness = new double[numero_Particulas];
		this.p_fitness = new double[numero_Particulas];
		this.pBest = new double[numero_Particulas][numero_Dimensoes];

	}

	public void CriarParticula() {

		for (int i = 0; i < particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				particulas[i][j] = Math.random();

			}
		}

		Fitness();
		this.gBest = particulas[0].clone();

		for (int i = 0; i < particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {
				this.pBest[i][j] = particulas[i][j];
				this.p_fitness[i] = this.fitness[i];
			}
		}

		this.bestFitness = this.fitness[0];

		for (int i = 0; i < particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {
				this.velocidade[0][i][j] = 0;
			}
		}

	}

	public void Fitness() {

		for (int i = 0; i < this.particulas.length; i++) {

			this.fitness[i] = Sphere_Fuction(particulas[i]);

		}

	}

	public void Velocidade(int iteracao) {
		
		double r = Math.random(); //variável aleatoria para comparação da velocidade

		//--------------------------------------------- Calcular Velocidade -------------------------------------------
		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				this.velocidade[iteracao][i][j] = (this.inercia * this.velocidade[iteracao - 1][i][j])
						+ (this.n1 * Math.random() * (this.gBest[j] - this.particulas[i][j]))
						+ (this.n2 * Math.random() * (this.pBest[i][j] - this.particulas[i][j]));
				
			}
		}
		
		//normalizando a velocidade
		normalizacao(this.velocidade[iteracao]); 
		//--------------------------------------------------------------------------------------------------------------
		
		//--------------------------------------------- Atualizar Particulas -------------------------------------------
		
		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				//comparacao com a variavel r que é um número aleatorio entre 0 e 1
				if(this.velocidade[iteracao][i][j] >= r)
				{
					
					//se a particula for igual ao seu pbest, utiliza-se o fator de mutação
					if(this.particulas[i][j] == this.pBest[i][j])
					{
						
						Random num_random = new Random(); 
						
						//mudar o valor de duas dimensões aleatoriamente 
						for (int k = 0; k < 2; k++) 
						{
							
							int num = num_random.nextInt(this.numero_Dimensoes);
							this.particulas[i][num] = Mutacao(iteracao);
							
						}
					
					//se a dimensão da velocidade for maior que r então usa-se a equação 1 para receber o valor do pbest
					} else 
					{
						this.particulas[i][j] = this.pBest[i][j];
					}		
					
				}
				
			}
		}
		
		//--------------------------------------------------------------------------------------------------------------
		
		
	}
	
	public double Mutacao(int iteracao){
		double pm = 0;
		
		pm = this.pm_Max - ((this.pm_Max - this.pm_Min) / this.numero_Iteracoes) * iteracao;
		
		return pm;
	}
	
	public double min(double[][] serie) {
		double menor = serie[0][0];

		for (int i = 0; i < serie.length; i++) {
			for (int j = 0; j < serie[0].length; j++) {

				if (serie[i][j] < menor) {
					menor = serie[i][j];
				}
			}
		}

		return menor;

	}

	public double max(double[][] serie) {
		double maior = serie[0][0];

		for (int i = 0; i < serie.length; i++) {
			for (int j = 0; j < serie[0].length; j++) {
				if (serie[i][j] > maior) {
					maior = serie[i][j];
				}
			}
		}
		return maior;

	}

	public double[][] normalizacao(double[][] serie) {
		double[][] suporte = new double[serie.length][serie[0].length];

		double minimo = min(serie);
		double maximo = max(serie);

		for (int i = 0; i < serie.length; i++) {
			for (int j = 0; j < serie[0].length; j++) {
			suporte[i][j] = (serie[i][j] - minimo) / (maximo - minimo);

		}}

		return suporte;
	}
	
	public void Atualizar_Particulas(int interacao) {

		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				this.particulas[i][j] = this.particulas[i][j] + this.velocidade[interacao][i][j];
				
			}
		}
	}
	
	public double Sphere_Fuction(double[] particulas) {

		double soma = 0;

		for (int j = 0; j < this.particulas[0].length; j++) {

			soma = soma + Math.pow(particulas[j], 2);

		}

		return soma;
	}

	public void DefinirPBest() {

		// se a posição atual de uma particula for melhor que a sua posição
		// anterior então ai sim o pbest é atualizado

		for (int i = 0; i < this.numero_Particulas; i++) {

			if (this.p_fitness[i] > this.fitness[i]) {
				this.p_fitness[i] = this.fitness[i];
				this.pBest[i] = this.particulas[i].clone();
			}
		}

	}

	public void DefinirGBest() {

		for (int i = 0; i < this.particulas.length; i++) {

			if (this.fitness[i] < this.bestFitness) {
				this.bestFitness = this.fitness[i];

				for (int j = 0; j < numero_Dimensoes; j++) {
					this.gBest[j] = this.particulas[i][j];
				}
			}
		}

	}

	public void Executar() throws InterruptedException {

		CriarParticula();
		int i = 1;
		int j = 1;
		double fitBest = 1;

		double[][] grafico = new double[2][numero_Iteracoes];

		JFrame frame_otimização = new JFrame("Plot Otimização");
		frame_otimização.setSize(300, 300);
		frame_otimização.setLocation(500, 0);

		while (i < numero_Iteracoes) {

			Fitness();
			DefinirGBest();
			DefinirPBest();
			Velocidade(i);
			Atualizar_Particulas(i);
			
			fitBest = this.bestFitness; // a cada iteração tem-se um melhor
										// fitness, esse fitness é salvo em
										// fitbest

			grafico[0][i] = i; // Grafico é uma matriz que recebe a quantidade
								// de iterações e os melhores fitness de cada
								// iteração para plotar o gráfico
			grafico[1][i] = fitBest;

			// print para imprimir as iterações e os valores de thetas a cada iteração.
			System.out.println(i + "- Melhor Fitness da iteração: " + fitBest + "\n");

			
			i++;
		}
		
		// plots da otimização do fitness 
		Plot2DPanel plot_fitness = new	 Plot2DPanel(); 
		plot_fitness.addLinePlot("Fitness", grafico);
		frame_otimização.setContentPane(plot_fitness);
		frame_otimização.setVisible(true);

	}

	public static void main(String[] args) throws InterruptedException {
		
		Leitor_txt leitor = new Leitor_txt("iris.txt");
		double[][] base_dados = leitor.Base_de_dados();

		// for para saber a quantidade de elementos para cada classe
		int set1 = 0;
		int set2 = 0;
		int set3 = 0;

		for (int i = 0; i < base_dados.length; i++) {

			if (base_dados[i][0] == 1) {

				set1++;

			} else if (base_dados[i][0] == 2) {

				set2++;

			} else if (base_dados[i][0] == 3) {

				set3++;

			}

		}

		// definir o tamanho que cada matriz vai ter de acordo com a quantidade
		// de elementos de cada classe
		double[][] conjunto1 = new double[set1][2];
		double[][] conjunto2 = new double[set2][2];
		double[][] conjunto3 = new double[set3][2];

		// passando os dados para suas matrizes
		set1 = 0;
		set2 = 0;
		set3 = 0;

		for (int i = 0; i < base_dados.length; i++) {

			if (base_dados[i][0] == 1) {

				for (int j = 0; j < conjunto1[0].length; j++) {
					conjunto1[set1][j] = base_dados[i][j + 1];
				}

				set1++;

			} else if (base_dados[i][0] == 2) {

				for (int j = 0; j < conjunto2[0].length; j++) {
					conjunto2[set2][j] = base_dados[i][j + 1];
				}

				set2++;

			} else if (base_dados[i][0] == 3) {

				for (int j = 0; j < conjunto3[0].length; j++) {
					conjunto3[set3][j] = base_dados[i][j + 1];
				}

				set3++;

			}

		}

		
		// plot do conjunto de dados
		Plot2DPanel plot = new Plot2DPanel();
		plot.addScatterPlot("Posições", Color.RED, conjunto1);
		plot.addScatterPlot("Posições", Color.BLUE, conjunto2);
		plot.addScatterPlot("Posições", Color.GREEN, conjunto3);

		JFrame frame = new JFrame("Plot dos dados");
		frame.setSize(500, 500);
		frame.setLocation(0, 0);
		frame.setContentPane(plot);
		frame.setVisible(true);
		
		
		CLUDIPSO p = new CLUDIPSO(10000, 20, 0.9);
		p.Executar();

	}
}
