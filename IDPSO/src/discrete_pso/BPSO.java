package discrete_pso;

import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.*;

public class BPSO {

	public int numero_Particulas;
	public int iteracoes;
	public double[][] particula;
	public double[][] pBest;
	public double[][][] velocidade;
	public double[] gBest;
	public double inercia;
	public double[] fitness;
	public double[] p_fitness; //Essa varavel vai armazenar o melhor fitness de cada particula.
	public double bestFitness;
	public double n1;
	public double n2;
	public int numero_Dimensoes = 10;
	public double minimo = 0;
	
	public double Xgmax = 4;
	public double Xgmin = -4;
	
	public double Rmu = 0.7;
	
	public double limite_inferior;
	public double limite_superior;
	
	public double criterio_parada = Math.pow(10, -8);
	public int sem_mudanca;
	
	public int equacao;

	public Random random = new Random();
	
	public BPSO(int iteracoes, int n_particulas, double inercia, double n1, double n2, int equacao, int s_mudanca) {
		
		this.n1 = n1;
		this.n2 = n2;
		
		this.sem_mudanca = s_mudanca;
		
		this.equacao = equacao;
		
		this.inercia = inercia;
		this.iteracoes = iteracoes;
		this.numero_Particulas = n_particulas;

		this.particula = new double[numero_Particulas][numero_Dimensoes];
		this.velocidade = new double[iteracoes][numero_Particulas][numero_Dimensoes];

		this.gBest = new double[numero_Dimensoes];
		this.fitness = new double[numero_Particulas];
		this.p_fitness = new double[numero_Particulas];
		this.pBest = new double[numero_Particulas][numero_Dimensoes];

	}

	public void CriarParticula() {

		for (int i = 0; i < particula.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				particula[i][j] = this.random.nextInt(2);

			}
		}

		Fitness();
		this.gBest = particula[0].clone();

		for (int i = 0; i < particula.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {
				this.pBest[i][j] = particula[i][j];
				this.p_fitness[i] = this.fitness[i];
			}
		}

		this.bestFitness = this.fitness[0];

		for (int i = 0; i < particula.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {
				this.velocidade[0][i][j] = 0;
			}
		}

	}

	public void Fitness() {

		for (int i = 0; i < this.particula.length; i++) {
			
			if(this.equacao == 0){
				this.fitness[i] = Math.abs(Sphere_Fuction(particula[i]) - this.minimo);
			}else if(this.equacao == 1){
				this.fitness[i] = Math.abs(Schwefels_Fuction(particula[i]) - this.minimo);
			}else if(this.equacao == 2){
				this.fitness[i] = Math.abs(Rosenbrock_function(particula[i]) - this.minimo);
			}else if(this.equacao == 3){
				this.fitness[i] = Math.abs(Rastrigins_function(particula[i]) - this.minimo);
			}else if(this.equacao == 4){
				this.fitness[i] = Math.abs(Scaffers_function(particula[i]) - this.minimo);
			}
			
		}

	}

	public void Velocidade(int iteracao) {

		for (int i = 0; i < this.particula.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				this.velocidade[iteracao][i][j] = (this.inercia * this.velocidade[iteracao - 1][i][j])
						+ (this.n1 * Math.random() * (this.gBest[j] - this.particula[i][j]))
						+ (this.n2 * Math.random() * (this.pBest[i][j] - this.particula[i][j]));
			
				if (this.velocidade[iteracao][i][j] >= Xgmax) {
					
					this.velocidade[iteracao][i][j] = Xgmax;
					
				}else if(this.velocidade[iteracao][i][j] <= Xgmin){
					
					this.velocidade[iteracao][i][j] = Xgmin;
					
				}
			}
		}

	}

	public void Atualizar_Particulas(int interacao) {

		double sigmoide = 0;
		
		for (int i = 0; i < this.particula.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				sigmoide = 1/(1 + Math.exp(-this.velocidade[interacao][i][j]));
				
				double rand = Math.random();
				
				if (rand >= sigmoide) {
					
					this.particula[i][j] = 0;
							
				} else if(rand < sigmoide){
					
					this.particula[i][j] = 1;
				}
				
				sigmoide = 0;
			}
		}
	}
	
	public void Mutacao(int iteracao){
		double rand;
		int jr;
		Random gerador = new Random(); 
				
		for (int i = 0; i < velocidade[0].length; i++) {
			
			rand = Math.random();
			
			if(rand < this.Rmu){
				
				jr = gerador.nextInt(this.numero_Dimensoes);
				
				this.velocidade[iteracao][i][jr] = this.velocidade[iteracao][i][jr] - this.velocidade[iteracao][i][jr];
				
			}
			
		}
	}

	public void DefinirPBest() {

		for(int i = 0; i < this.numero_Particulas; i++){
			
			if(this.p_fitness[i] > this.fitness[i]){
				this.p_fitness[i] = this.fitness[i];
				this.pBest[i] = this.particula[i].clone();
			}
		}

	}

	public void DefinirGBest() {

		for (int i = 0; i < this.particula.length; i++) {

			if (this.fitness[i] < this.bestFitness) {
				this.bestFitness = this.fitness[i];

				for (int j = 0; j < numero_Dimensoes; j++) {
					this.gBest[j] = this.particula[i][j];
				}
			}
		}

	}

	public double Sphere_Fuction(double[] particulas) {

		this.limite_superior = 1;
		this.limite_inferior = -1;
		this.minimo = 0;
		
		double soma = 0;

		for (int j = 0; j < this.particula[0].length; j++) {

			soma = soma + Math.pow(particulas[j], 2);

		}

		return soma;
	}
	
	public double Schwefels_Fuction(double[] particulas) {

		this.limite_superior = 500;
		this.limite_inferior = -500;
		
		double soma = 0;
		for (int j = 0; j < this.particula[0].length; j++) {

			soma = soma + 420.9647 * Math.sin(Math.sqrt(Math.abs(420.9647)));

		}
		
		this.minimo = soma;
		
		soma = 0;

		for (int j = 0; j < this.particula[0].length; j++) {

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
		
		for (int j = 0; j < this.particula[0].length - 1; j++) {
			
			soma = soma + (100 *  (Math.pow((1 - Math.pow(x, 2)), 2)) +  Math.pow((1 - 1), 2) ); 

		}
		
		this.minimo = soma;
		soma = 0;
		
		for (int j = 0; j < this.particula[0].length - 1; j++) {

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
		
		for (int j = 0; j < this.particula[0].length - 1; j++) {

			
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
			Atualizar_Particulas(i);

			fitBest = this.bestFitness; // a cada iteração tem-se um melhor
										// fitness, esse fitness é salvo em
										// fitbest

			grafico[0][i] = i; // Grafico é uma matriz que recebe a quantidade
								// de iterações e os melhores fitness de cada
								// iteração para plotar o gráfico
			grafico[1][i] = fitBest;

			System.out.println(i + "- Melhor Fitness da iteração: " + fitBest + "\n");
			
			
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
			
			/*
			// plots da otimização do fitness
			Plot2DPanel plot_fitness = new Plot2DPanel();
			plot_fitness.addLinePlot("Fitness", grafico); 
			frame_otimização.setContentPane(plot_fitness);
			frame_otimização.setVisible(true);
			*/
			
			i++;
		}
		

	}

	public static void main(String[] args) throws InterruptedException {

		BPSO p1 = new BPSO(1000, 20, 0.6, 1.4, 1.4, 0, 1000);
		p1.Executar();
		

	}
}
