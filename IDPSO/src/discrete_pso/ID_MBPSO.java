package discrete_pso;

import java.util.Random;

import javax.annotation.Generated;
import javax.swing.JFrame;

import org.math.plot.*;

public class ID_MBPSO {

	public int numero_Particulas;
	public int iteracoes;
	public double[][] Xp;
	public double[][] Xg;
	public double[][] pBest;
	public double[][][] velocidade;
	public double[] gBest;
	public double[] fitness;
	public double[] p_fitness; //Essa varavel vai armazenar o melhor fitness de cada particula.
	public double bestFitness;
	
	public double[] inercia;
	public double inercia_Inicial;
	public double inercia_Final;
	public double mi = 100;
	public double n1[], n2[];
	public double n1_fixo = 1.4;
	public double n2_fixo = 1.4;
	public double[] phi;
	
	public int numero_Dimensoes = 10;
	
	public double minimo = 0;
	
	public double Xgmax = 4;
	public double Xgmin = -4;
	
	public double Rmu = 0.2;
	
	public double limite_inferior;
	public double limite_superior;
	
	public double criterio_parada = Math.pow(10, -8);
	public int sem_mudanca = 200;
	
	public Random random = new Random();

	public ID_MBPSO(int iteracoes, int n_particulas, double inercia_inicial, double inercia_final) {
		

		this.iteracoes = iteracoes;
		this.numero_Particulas = n_particulas;

		this.inercia = new double[numero_Particulas];
		this.inercia_Inicial = inercia_inicial;
		this.inercia_Final = inercia_final;
		this.phi = new double[numero_Particulas];
		this.n1 = new double[numero_Particulas];
		this.n2 = new double[numero_Particulas];
		this.inercia = new double[numero_Particulas];

		this.Xp = new double[numero_Particulas][numero_Dimensoes];
		this.Xg = new double[numero_Particulas][numero_Dimensoes];
		this.velocidade = new double[iteracoes][numero_Particulas][numero_Dimensoes];

		this.gBest = new double[numero_Dimensoes];
		this.fitness = new double[numero_Particulas];
		this.p_fitness = new double[numero_Particulas];
		this.pBest = new double[numero_Particulas][numero_Dimensoes];

	}

	public void CriarParticula() {

		for (int i = 0; i < Xp.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				Xp[i][j] = this.random.nextInt(2);
				Xg[i][j] = Math.random();

			}
		}

		Fitness();
		this.gBest = Xp[0].clone();

		for (int i = 0; i < Xp.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {
				this.pBest[i][j] = Xp[i][j];
				this.p_fitness[i] = this.fitness[i];
			}
		}

		this.bestFitness = this.fitness[0];

		for (int i = 0; i < Xp.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {
				this.velocidade[0][i][j] = 0;
			}
		}
		
		for (int i = 0; i < this.Xp.length; i++) {
			this.n1[i] = this.n1_fixo;
			this.n2[i] = this.n2_fixo;
			this.inercia[i] = this.inercia_Inicial;

		}

	}

	public void Fitness() {

		for (int i = 0; i < this.Xp.length; i++) {

			//this.fitness[i] = Math.abs(Sphere_Fuction(Xp[i]) - this.minimo);
			this.fitness[i] = Math.abs(Schwefels_Fuction(Xp[i]) - this.minimo);
			//this.fitness[i] = Math.abs(Rosenbrock_function(Xp[i]) - this.minimo);
			//this.fitness[i] = Math.abs(Rastrigins_function(Xp[i]) - this.minimo);
			//this.fitness[i] = Math.abs(Scaffers_function(Xp[i]) - this.minimo);
			
		}

	}

	public void Velocidade(int iteracao) {

		for (int i = 0; i < this.Xp.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				this.velocidade[iteracao][i][j] = (this.inercia[i] * this.velocidade[iteracao - 1][i][j])
						+ (this.n1[i] * Math.random() * (this.gBest[j] - this.Xp[i][j]))
						+ (this.n2[i] * Math.random() * (this.pBest[i][j] - this.Xp[i][j]));
			
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

		for (int i = 0; i < this.Xp.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				double parte1 = 0;
				double parte2 = 0;

				//questão a ser perguntada ao professor
				parte1 = (this.gBest[j] - this.Xp[i][j]);
				parte2 = (this.pBest[i][j] - this.Xp[i][j]);


				this.phi[i] = Math.abs(parte1 / parte2);

			}
		}

		// calculo para atualizar os valores dos parametros w, c1 e c2
		for (int i = 0; i < this.Xp.length; i++) {

			double ln = Math.log(this.phi[i]);

			double calculo = this.phi[i] * (interação - ((1 + ln) * this.iteracoes) / this.mi);

			this.inercia[i] = ((this.inercia_Inicial - this.inercia_Final) / (1 + Math.exp(calculo))) + this.inercia_Final;

			this.n1[i] = n1_fixo * Math.pow(this.phi[i], -1);
			this.n2[i] = n2_fixo * this.phi[i];
		}

	}
	
	public void Atualizar_Particulas(int interacao) {

		double sigmoide = 0;
		
		for (int i = 0; i < this.Xp.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				
				this.Xg[i][j] = this.Xg[i][j] + this.velocidade[interacao][i][j];
				
				double[] vetor_ordem = Ordenar(this.Xg[i]);
				
				sigmoide = 1/(1 + Math.exp(-vetor_ordem[j]));
				
				double rand = Math.random();
				
				if (rand >= sigmoide) {
					
					this.Xp[i][j] = 0;
							
				} else if(rand < sigmoide){
					
					this.Xp[i][j] = 1;
				}
				
				sigmoide = 0;
			}
		}
	}
	
	public void Mutacao(int iteracao){
		double rand;
		int jr;
		Random gerador = new Random(); 
				
		for (int i = 0; i < this.Xg.length; i++) {
			
			rand = Math.random();
			
			if(rand < this.Rmu){
				
				jr = gerador.nextInt(this.numero_Dimensoes);
				
				this.Xg[i][jr] = this.Xg[i][jr] - this.Xg[i][jr];
				
			}
			
		}
	}

	public void DefinirPBest() {

		// se a posição atual de uma particula for melhor que a sua posição
		// anterior então ai sim o pbest é atualizado
		
		for(int i = 0; i < this.numero_Particulas; i++){
			
			if(this.p_fitness[i] > this.fitness[i]){
				this.p_fitness[i] = this.fitness[i];
				this.pBest[i] = this.Xp[i].clone();
			}
		}

	}

	public void DefinirGBest() {

		for (int i = 0; i < this.Xp.length; i++) {

			if (this.fitness[i] < this.bestFitness) {
				this.bestFitness = this.fitness[i];

				for (int j = 0; j < numero_Dimensoes; j++) {
					this.gBest[j] = this.Xp[i][j];
				}
			}
		}

	}

	private double[] Ordenar(double[] par) {

		double aux = 0;

		for (int i = par.length - 1; i >= 1; i--) {

			for (int j = 0; j < par.length - 1; j++) {

				if (par[j] > par[j + 1]) {

					aux = par[j];

					par[j] = par[j + 1];

					par[j + 1] = aux;

				}
			}
		}

		return par;
	}
	
	public double Sphere_Fuction(double[] particulas) {

		this.limite_superior = 1;
		this.limite_inferior = -1;
		this.minimo = 0;
		
		double soma = 0;

		for (int j = 0; j < this.Xp[0].length; j++) {

			soma = soma + Math.pow(particulas[j], 2);

		}

		return soma;
	}
	//erro
	public double Schwefels_Fuction(double[] particulas) {

		this.limite_superior = 500;
		this.limite_inferior = -500;
		
		double soma = 0;
		for (int j = 0; j < this.Xp[0].length; j++) {

			soma = soma + 420.9647 * Math.sin(Math.sqrt(Math.abs(420.9647)));

		}
		
		this.minimo = soma;
		
		soma = 0;

		for (int j = 0; j < this.Xp[0].length; j++) {

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
		
		for (int j = 0; j < this.Xp[0].length - 1; j++) {
			
			soma = soma + (100 *  (Math.pow((1 - Math.pow(x, 2)), 2)) +  Math.pow((1 - 1), 2) ); 

		}
		
		this.minimo = soma;
		soma = 0;
		
		for (int j = 0; j < this.Xp[0].length - 1; j++) {

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
		
		for (int j = 0; j < this.Xp[0].length - 1; j++) {

			
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
	
	public void Executar() throws InterruptedException {

		CriarParticula();
		int i = 1;
		double fitBest = 1;

		double[][] grafico = new double[2][iteracoes];

		JFrame frame_otimização = new JFrame("Plot Otimização");
		frame_otimização.setSize(300, 300);
		frame_otimização.setLocation(0, 0);
		
		int j = 0;
		
		while (i < iteracoes) {

			Fitness();
			DefinirGBest();
			DefinirPBest();
			Velocidade(i);
			Mutacao(i);
			Atualizar_Parametros(i);
			Atualizar_Particulas(i);

			fitBest = this.bestFitness; // a cada iteração tem-se um melhor
										// fitness, esse fitness é salvo em
										// fitbest

			grafico[0][i] = i; // Grafico é uma matriz que recebe a quantidade
								// de iterações e os melhores fitness de cada
								// iteração para plotar o gráfico
			grafico[1][i] = fitBest;

			System.out.println(i + "- Melhor Fitness da iteração: " + fitBest + "\n");
			System.out.println(i + "- inercia: " + inercia[0] + "\n");
			
			
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
			
			// plots da otimização do fitness
			Plot2DPanel plot_fitness = new Plot2DPanel();
			plot_fitness.addLinePlot("Fitness", grafico); 
			frame_otimização.setContentPane(plot_fitness);
			frame_otimização.setVisible(true);
				
			i++;
		}
		

	}

	public static void main(String[] args) throws InterruptedException {

		ID_MBPSO p1 = new ID_MBPSO(1000, 20, 0.6, 0.2);
		p1.Executar();
		

	}
}
