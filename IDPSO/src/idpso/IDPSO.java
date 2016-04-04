package idpso;

import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.*;

public class IDPSO{

	public int numero_Particulas;
	public int iteracoes;
	public double[][] particulas;
	public double[][] pBest;
	public double[][][] velocidade;
	public double n1[], n2[];
	public double[] gBest;
	public double[] inercia;
	public double[] fitness;
	public double[] p_fitness; // Essa varavel vai armazenar o melhor fitness de cada particula.
	public double bestFitness;
	public double[] phi;
	public double inercia_Inicial;
	public double inercia_Final;
	public double mi = 100;
	public double n1_fixo;
	public double n2_fixo;
	public int numero_Dimensoes = 10;
	
	public double Xgmax = 4;
	public double Xgmin = -4;
	
	public double limite_inferior;
	public double limite_superior;
	public double minimo = 0;
	
	public int equacao;
	
	public double criterio_parada = Math.pow(10, -8);
	public int sem_mudanca;
	
	public IDPSO(int iteracoes, int n_particulas, double inercia_inicial, double inercia_final, double n1, double n2, int equacao, int s_mudanca) {
		
		this.n1_fixo = n1;
		this.n2_fixo = n2;
		
		this.sem_mudanca = s_mudanca;
		
		this.equacao = equacao;
		
		this.iteracoes = iteracoes;

		this.numero_Particulas = n_particulas;

		this.particulas = new double[numero_Particulas][numero_Dimensoes];
		this.velocidade = new double[iteracoes][numero_Particulas][numero_Dimensoes];

		this.phi = new double[numero_Particulas];
		this.n1 = new double[numero_Particulas];
		this.n2 = new double[numero_Particulas];
		this.inercia = new double[numero_Particulas];
		this.inercia_Inicial = inercia_inicial;
		this.inercia_Final = inercia_final;
		
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

		for (int i = 0; i < this.particulas.length; i++) {
			this.n1[i] = this.n1_fixo;
			this.n2[i] = this.n2_fixo;
			this.inercia[i] = this.inercia_Inicial;

		}
	}

	public void Fitness() {

		for (int i = 0; i < this.particulas.length; i++) {

			if(this.equacao == 0){
				this.fitness[i] = Math.abs(Sphere_Fuction(particulas[i]) - this.minimo);
			}else if(this.equacao == 1){
				this.fitness[i] = Math.abs(Schwefels_Fuction(particulas[i]) - this.minimo);
			}else if(this.equacao == 2){
				this.fitness[i] = Math.abs(Rosenbrock_function(particulas[i]) - this.minimo);
			}else if(this.equacao == 3){
				this.fitness[i] = Math.abs(Rastrigins_function(particulas[i]) - this.minimo);
			}else if(this.equacao == 4){
				this.fitness[i] = Math.abs(Scaffers_function(particulas[i]) - this.minimo);
			}
			
		}

	}

	public void Velocidade(int iteracao) {

		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				this.velocidade[iteracao][i][j] = (this.inercia[i] * this.velocidade[iteracao - 1][i][j])
						+ (this.n1[i] * Math.random() * (this.gBest[j] - this.particulas[i][j]))
						+ (this.n2[i] * Math.random() * (this.pBest[i][j] - this.particulas[i][j]));
			
				if (this.velocidade[iteracao][i][j] >= Xgmax) {
					
					this.velocidade[iteracao][i][j] = Xgmax;
					
				}else if(this.velocidade[iteracao][i][j] <= Xgmin){
					
					this.velocidade[iteracao][i][j] = Xgmin;
					
				}
			
			}
		}

	}

	public void Atualizar_Parametros(int interação) {

		// calculo da distancia euclidiana

		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				double parte1 = 0;
				double parte2 = 0;

				// condição de existencia para a distancia da particula para o
				// gbest e pbest
				if ((this.gBest[j] == this.particulas[i][j]) || (this.pBest[i][j] == this.particulas[i][j])) {

					parte1 = (this.gBest[j] - (this.particulas[i][j] + 0.000000000000001));
					parte2 = (this.pBest[i][j] - (this.particulas[i][j] + 0.0000000000000001));

				} else {

					parte1 = (this.gBest[j] - this.particulas[i][j]);
					parte2 = (this.pBest[i][j] - this.particulas[i][j]);

				}

				this.phi[i] = Math.abs(parte1 / parte2);

			}
		}

		// calculo para atualizar os valores dos parametros w, c1 e c2
		for (int i = 0; i < this.particulas.length; i++) {

			double ln = Math.log(this.phi[i]);

			double calculo = this.phi[i] * (interação - ((1 + ln) * this.iteracoes) / this.mi);

			this.inercia[i] = ((this.inercia_Inicial - this.inercia_Final) / (1 + Math.exp(calculo))) + this.inercia_Final;

			this.n1[i] = n1_fixo * Math.pow(this.phi[i], -1);
			this.n2[i] = n2_fixo * this.phi[i];
		}

	}

	public void Atualizar_Particulas(int interacao) {

		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				this.particulas[i][j] = this.particulas[i][j] + this.velocidade[interacao][i][j];

				if (this.particulas[i][j] > limite_superior) {
					this.particulas[i][j] = limite_superior;

				} else if (this.particulas[i][j] < limite_inferior) {
					this.particulas[i][j] = limite_inferior;
				}

			}
		}
	}

	public double Sphere_Fuction(double[] particulas) {

		this.limite_superior = 1;
		this.limite_inferior = -1;
		this.minimo = 0;
		
		double soma = 0;

		for (int j = 0; j < this.particulas[0].length; j++) {

			soma = soma + Math.pow(particulas[j], 2);

		}

		return soma;
	}

	public double Schwefels_Fuction(double[] particulas) {

		this.limite_superior = 500;
		this.limite_inferior = -500;
		
		double soma = 0;
		for (int j = 0; j < this.particulas[0].length; j++) {

			soma = soma + 420.9647 * Math.sin(Math.sqrt(Math.abs(420.9647)));

		}
		
		this.minimo = soma;
		
		soma = 0;

		for (int j = 0; j < this.particulas[0].length; j++) {

			soma = soma + particulas[j] * Math.sin(Math.sqrt(Math.abs(particulas[j])));

		}
		
		

		soma = (418.9829 * this.numero_Dimensoes) - soma;

		return soma;
	}
	
	public double Rosenbrock_function(double[] particulas) {
		
		this.limite_superior = 10;
		this.limite_inferior = -5;
		
		double soma = 0;
		double x = 0;
		double xnext = 0;
		
		for (int j = 0; j < this.particulas[0].length - 1; j++) {
			
			soma = soma + (100 *  (Math.pow((1 - Math.pow(x, 2)), 2)) +  Math.pow((1 - 1), 2) ); 

		}
		
		this.minimo = soma;
		soma = 0;
		
		for (int j = 0; j < this.particulas[0].length - 1; j++) {

			x = particulas[j];
			xnext = particulas[j + 1];
			
			soma = soma + (100 *  (Math.pow((xnext - Math.pow(x, 2)), 2)) +  Math.pow((x - 1), 2) ); 

		}

		return soma;

	}

	public double Rastrigins_function(double[] particulas) {
		
		this.limite_superior = 5.12;
		this.limite_inferior = -5.12;
		this.minimo = 0;
		
		double soma = 0;
		
		for (int j = 0; j < this.particulas[0].length - 1; j++) {

			
			soma = soma + (Math.pow(particulas[j], 2) - 10 * Math.cos(2 * Math.PI * particulas[j])); 

		}

		return (this.numero_Dimensoes*10) + soma;

	}
 
	public double Scaffers_function(double[] particulas) {
		
		this.limite_superior = 100;
		this.limite_inferior = -100;
		this.minimo = 0;
		
		double soma = 0;
		double parteCima = 0;
		double parteBaixo = 0;
		double x1 = particulas[0];
		double x2 = particulas[1];

			parteCima = (Math.pow(x1, 2))+(Math.pow(x2, 2));
			parteCima = Math.sqrt(parteCima);
			parteCima = Math.sin(parteCima);
			parteCima = Math.pow(parteCima, 2) - 0.5;
			
			parteBaixo = (Math.pow(x1, 2))+(Math.pow(x2, 2));
			parteBaixo = (0.001 * parteBaixo);
			parteBaixo = (1 + parteBaixo);
			parteBaixo = Math.pow(parteBaixo, 2);

		soma = (parteCima / parteBaixo) + 0.5;

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
		double fitBest = 1;

		double[][] grafico = new double[2][iteracoes];

		JFrame frame_particulas = new JFrame("Plot das particulas");
		frame_particulas.setSize(800, 800);
		frame_particulas.setLocation(600, 0);

		JFrame frame_otimização = new JFrame("Plot Otimização");
		frame_otimização.setSize(300, 300);
		frame_otimização.setLocation(0, 0);
		int j = 0;

		while (i < iteracoes) {

			Fitness();
			DefinirGBest();
			DefinirPBest();
			Velocidade(i);
			Atualizar_Parametros(i);
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
			System.out.println("\n   Inercia da particula 0: " + this.inercia[0] + "\n");

			
			/*// plots da otimização das particulas
			Plot2DPanel plot_particulas = new Plot2DPanel();
			plot_particulas.addScatterPlot("Particulas", this.particulas);
			frame_particulas.setContentPane(plot_particulas);
			frame_particulas.setVisible(true);
			*/
			
			/*// plots da otimização do fitness 
			Plot2DPanel plot_fitness = new	Plot2DPanel();
			plot_fitness.addLinePlot("Fitness", grafico);
			frame_otimização.setContentPane(plot_fitness);
			frame_otimização.setVisible(true);
			*/
			
			if(i >= 2){
				if (grafico[1][i] == grafico[1][i - 1]) {
					
					j++;
					
				} else if(grafico[1][i] != grafico[1][i - 1]){
					
					j = 0;
				}
			}
				
			
			if (fitBest <= this.criterio_parada || j == this.sem_mudanca) {
				break;
			}
			
			
			i++;
			}
		/*
		Plot2DPanel plot_fitness = new	Plot2DPanel();
		plot_fitness.addLinePlot("Fitness", grafico);
		frame_otimização.setContentPane(plot_fitness);
		frame_otimização.setVisible(true);
		*/
		}
		
		
	public static void main(String[] args) throws InterruptedException {

		IDPSO p1 = new IDPSO(100000, 20, 0.6, 0.2, 1.4, 1.4, 0, 1000);
		p1.Executar();

	}
}
