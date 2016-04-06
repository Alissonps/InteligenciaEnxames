package cludipso;

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.*;

public class CLUDIPSO {

	public int numero_Iteracoes;
	public double[][] particulas;
	public double[][] pBest;
	public double[][] velocidade;
	public double[] gBest;
	public double[][] gPosicoes;
	public double inercia;
	public double[] fitness;
	public double[] p_fitness; // Essa varavel vai armazenar o melhor fitness de
								// cada particula.
	public double bestFitness;
	double pm_Max = 0.9;
	double pm_Min = 0.4;
	public double n1 = 2.8;
	public double n2 = 1.3;
	public int numero_Dimensoes = 2;

	public double[][] base_entrada;
	public int[] classes = { 1, 2, 3 };
	public int K = this.classes.length;
	public int numero_Particulas = this.K;

	public double limite_superior = 1;
	public double limite_inferior = -1;
	public double Xgmax = 1;
	public double Xgmin = -1;

	public CLUDIPSO(double[][] b_entrada, int iteracoes, double inercia) {
		this.base_entrada = b_entrada;

		this.inercia = inercia;
		this.numero_Iteracoes = iteracoes;

		// a dimens�o est� menos 3 porque nesse momento s� vou utilizar duas
		// dimens�es.
		this.particulas = new double[this.K][numero_Dimensoes];
		this.velocidade = new double[numero_Particulas][numero_Dimensoes];

		this.gBest = new double[numero_Dimensoes];
		this.gPosicoes = new double[numero_Particulas][numero_Dimensoes];
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
				this.velocidade[i][j] = 0;
			}
		}

	}

	public void Fitness() {
		double[] a = new double[this.particulas.length];
		double[] b = new double[this.particulas.length];
		double[] s = new double[this.particulas.length];
		double[] max = new double[this.particulas.length];

		// calculando a(i) e b(i)
		for (int i = 0; i < this.particulas.length; i++) {
			a[i] = Coesao(this.particulas[i], this.classes[i]);
			b[i] = Separacao(this.particulas[i], this.classes[i]);
		}

		// calculando o max dos 3 s(i)
		for (int i = 0; i < this.particulas.length; i++) {

			if (a[i] > b[i]) {

				max[i] = a[i];

			} else if ((a[i] < b[i])) {

				max[i] = b[i];
			}

		}

		// calculando o s(i)
		for (int i = 0; i < this.particulas.length; i++) {

			s[i] = (b[i] - a[i]) / max[i];

		}

		// calculando o fitness, o fitness � dado pela m�dia dos s(i)
		for (int i = 0; i < this.particulas.length; i++) {

			for (int j = 0; j < s.length; j++) {

				this.fitness[i] = this.fitness[i] + s[j];

			}

			double media = (this.fitness[i] / this.K);

			this.fitness[i] = 1 - media;
		}

	}

	public void Velocidade(int iteracao) {

		// --------------------------------------------- Calcular Velocidade
		// -------------------------------------------
		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				double parte1 = (this.inercia * this.velocidade[i][j]);
				double parte2 = (this.n1 * Math.random() * (this.gBest[j] - this.particulas[i][j]));
				
				
				double r = Math.random();
				
				if (this.velocidade[i][j] >= r) {
					this.particulas[i][j] = this.pBest[i][j];
				}
				
				if (this.particulas[i][j] == this.pBest[i][j]) {
	
					Random num_random = new Random();
					int num = num_random.nextInt(this.numero_Dimensoes);
					this.particulas[i][num] = Mutacao(iteracao);
						
				}
				
				double parte3 = (this.n2 * Math.random() * (this.pBest[i][j] - this.particulas[i][j]));

				double teste = parte1 + parte2 + parte3;
				this.velocidade[i][j] = teste;

				if (this.velocidade[i][j] >= Xgmax) {

					this.velocidade[i][j] = Xgmax;

				} else if (this.velocidade[i][j] <= Xgmin) {

					this.velocidade[i][j] = Xgmin;

				}

			}
		}

		// --------------------------------------------------------------------------------------------------------------
	}

	public double Mutacao(int iteracao) {
		double pm = 0;

		pm = this.pm_Max - ((this.pm_Max - this.pm_Min) / this.numero_Iteracoes) * iteracao;

		return pm;
	}

	public void Atualizar_Particulas(int iteracao) {

		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				this.particulas[i][j] = this.particulas[i][j] + this.velocidade[i][j];

				if (this.particulas[i][j] > limite_superior) {
					this.particulas[i][j] = limite_superior;

				} else if (this.particulas[i][j] < limite_inferior) {
					this.particulas[i][j] = limite_inferior;
				}

			}
		}
	}

	public void DefinirPBest() {

		// se a posi��o atual de uma particula for melhor que a sua posi��o
		// anterior ent�o ai sim o pbest � atualizado

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

					for (int k = 0; k < this.particulas.length; k++) {

						this.gPosicoes[k] = this.particulas[k].clone();

					}

				}
			}
		}

	}

	private double Coesao(double[] particula, double classe) {

		double media = 0;
		double qtd = 0;

		for (int i = 0; i < this.base_entrada.length; i++) {

			if (this.base_entrada[i][0] == classe) {

				for (int j = 0; j < numero_Dimensoes; j++) {

					media = media + Math.abs(particula[j] - this.base_entrada[i][j + 1]);
				}

				qtd++;
			}

		}

		media = media / qtd;

		return media;

	}

	private double Separacao(double[] particula, double classe) {

		double[] media = new double[this.K - 1];
		double[] qtd = new double[this.K - 1];
		double[] classe_amedir = new double[this.K - 1];

		int z = 0;

		for (int i = 0; i < this.classes.length; i++) {

			if (this.classes[i] != classe) {

				classe_amedir[z] = this.classes[i];

				z++;
			}

		}

		// ----------------------------Calculando a distancia para a primeira
		// classe
		// ----------------------------------------------------------------------------------------
		for (int j = 0; j < this.base_entrada.length; j++) {

			if (this.base_entrada[j][0] != classe && this.base_entrada[j][0] != classe_amedir[0]) {

				for (int k = 0; k < numero_Dimensoes; k++) {

					media[0] = media[0] + Math.abs(particula[k] - this.base_entrada[j][k + 1]);
				}

				qtd[0]++;

			}
		}

		media[0] = media[0] / qtd[0];

		// ----------------------------Calculando a distancia para a segunda
		// classe-----------------------------------------------------------------------------------------

		for (int j = 0; j < this.base_entrada.length; j++) {

			if (this.base_entrada[j][0] != classe && this.base_entrada[j][0] != classe_amedir[1]) {

				for (int k = 0; k < numero_Dimensoes; k++) {

					media[1] = media[1] + Math.abs(particula[k] - this.base_entrada[j][k + 1]);
				}

				qtd[1]++;

			}
		}

		media[1] = media[1] / qtd[1];

		// ---------------------------------------------------------------------------------------------------------------------

		double retornavel = 0;

		if (media[0] < media[1]) {

			retornavel = media[0];

		} else
			retornavel = media[1];

		return retornavel;

	}

	public void Executar() throws InterruptedException {

		// for para saber a quantidade de elementos para cada classe
		int set1 = 0;
		int set2 = 0;
		int set3 = 0;

		for (int i = 0; i < this.base_entrada.length; i++) {

			if (this.base_entrada[i][0] == 1) {

				set1++;

			} else if (this.base_entrada[i][0] == 2) {

				set2++;

			} else if (this.base_entrada[i][0] == 3) {

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

		for (int i = 0; i < this.base_entrada.length; i++) {

			if (this.base_entrada[i][0] == 1) {

				for (int j = 0; j < conjunto1[0].length; j++) {
					conjunto1[set1][j] = this.base_entrada[i][j + 1];
				}

				set1++;

			} else if (this.base_entrada[i][0] == 2) {

				for (int j = 0; j < conjunto2[0].length; j++) {
					conjunto2[set2][j] = this.base_entrada[i][j + 1];
				}

				set2++;

			} else if (this.base_entrada[i][0] == 3) {

				for (int j = 0; j < conjunto3[0].length; j++) {
					conjunto3[set3][j] = this.base_entrada[i][j + 1];
				}

				set3++;

			}

		}

		// plot do conjunto de dados
		Plot2DPanel plot = new Plot2DPanel();
		plot.addScatterPlot("Posi��es", Color.ORANGE, conjunto1);
		plot.addScatterPlot("Posi��es", Color.MAGENTA, conjunto2);
		plot.addScatterPlot("Posi��es", Color.BLUE, conjunto3);

		CriarParticula();
		int i = 1;
		double fitBest = 1;

		double[][] grafico = new double[2][numero_Iteracoes];

		JFrame frame_otimiza��o = new JFrame("Plot Otimiza��o");
		frame_otimiza��o.setSize(600, 600);
		frame_otimiza��o.setLocation(800, 0);

		Plot2DPanel plot_fitness = new Plot2DPanel();
		JFrame frame = new JFrame("Plot dos dados");

		while (i < numero_Iteracoes) {

			Fitness();
			DefinirGBest();
			DefinirPBest();
			Velocidade(i);
			Atualizar_Particulas(i);

			fitBest = this.bestFitness; // a cada itera��o tem-se um melhor
										// fitness, esse fitness � salvo em
										// fitbest

			grafico[0][i] = i; // Grafico � uma matriz que recebe a quantidade
								// de itera��es e os melhores fitness de cada
								// itera��o para plotar o gr�fico
			grafico[1][i] = fitBest;

			// print para imprimir as itera��es e os valores de thetas a cada
			// itera��o.
			System.out.println(i + "- Melhor Fitness da itera��o: " + fitBest + "\n");

			plot.addScatterPlot("Particulas", Color.RED, this.gPosicoes);
			//plot.addScatterPlot("Particulas", Color.BLACK, this.particulas);
			frame.setContentPane(plot);
			frame.setSize(700, 700);
			frame.setLocation(0, 0);
			frame.setVisible(true);
			
			Thread.sleep(200);

			i++;
		}

		// plots da otimiza��o do fitness

		plot_fitness.addLinePlot("Fitness", grafico);
		frame_otimiza��o.setContentPane(plot_fitness);
		frame_otimiza��o.setVisible(true);

	}

	public static void main(String[] args) throws InterruptedException {

		Leitor_txt leitor = new Leitor_txt("iris.txt");
		double[][] base_dados = leitor.Base_de_dados();

		CLUDIPSO p = new CLUDIPSO(base_dados, 1000, 0.9);
		p.Executar();

	}
}
