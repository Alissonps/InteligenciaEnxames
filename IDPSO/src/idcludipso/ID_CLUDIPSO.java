package idcludipso;

import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.*;

public class ID_CLUDIPSO {

	public int numero_Particulas;
	public int numero_Iteracoes;
	public double[][] particulas;
	public double[][] pBest;
	public double[][][] velocidade;
	public double[] gBest;
	public double[] inercia;
	public double[] fitness;
	public double[] p_fitness; // Essa varavel vai armazenar o melhor fitness de cada particula.
	public double bestFitness;
	double pm_Max = 0.9;
	double pm_Min = 0.4;
	public double[] n1;
	public double[] n2;
	public int numero_Dimensoes = 2;
	public double mi = 100;
	public double n1_fixo = 2.8;
	public double n2_fixo = 1.3;
	public double[] phi;
	public double inercia_Inicial;
	public double inercia_Final;

	public double limite_inferior;
	public double limite_superior;

	public double criterio_parada = Math.pow(10, -8);

	public ID_CLUDIPSO(int iteracoes, int n_particulas, double inercia_inicial, double inercia_final) {
		this.numero_Iteracoes = iteracoes;

		this.numero_Particulas = n_particulas;

		this.particulas = new double[numero_Particulas][numero_Dimensoes];
		this.velocidade = new double[iteracoes][numero_Particulas][numero_Dimensoes];

		this.gBest = new double[numero_Dimensoes];
		this.fitness = new double[numero_Particulas];
		this.p_fitness = new double[numero_Particulas];
		this.pBest = new double[numero_Particulas][numero_Dimensoes];

		this.phi = new double[numero_Particulas];
		this.n1 = new double[numero_Particulas];
		this.n2 = new double[numero_Particulas];
		this.inercia = new double[numero_Particulas];
		this.inercia_Inicial = inercia_inicial;
		this.inercia_Final = inercia_final;
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

			this.fitness[i] = Sphere_Fuction(particulas[i]);

		}

	}

	public void Velocidade(int iteracao) {
		
		double r = Math.random(); //vari�vel aleatoria para compara��o da velocidade

		//--------------------------------------------- Calcular Velocidade -------------------------------------------
		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				this.velocidade[iteracao][i][j] = (this.inercia[i] * this.velocidade[iteracao - 1][i][j])
						+ (this.n1[i] * Math.random() * (this.gBest[j] - this.particulas[i][j]))
						+ (this.n2[i] * Math.random() * (this.pBest[i][j] - this.particulas[i][j]));
				
			}
		}
		
		//normalizando a velocidade
		normalizacao(this.velocidade[iteracao]); 
		
		//--------------------------------------------------------------------------------------------------------------
		
		//--------------------------------------------- Atualizar Particulas -------------------------------------------
		
		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < this.numero_Dimensoes; j++) {

				//comparacao com a variavel r que � um n�mero aleatorio entre 0 e 1
				if(this.velocidade[iteracao][i][j] >= r)
				{
					
					//se a particula for igual ao seu pbest, utiliza-se o fator de muta��o
					if(this.particulas[i][j] == this.pBest[i][j])
					{
						
						Random num_random = new Random(); 
						
						//mudar o valor de duas dimens�es aleatoriamente 
						for (int k = 0; k < 2; k++) 
						{
							
							int num = num_random.nextInt(this.numero_Dimensoes);
							this.particulas[i][num] = Mutacao(iteracao);
							
						}
					
					//se a dimens�o da velocidade for maior que r ent�o usa-se a equa��o 1 para receber o valor do pbest
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
				
				if (this.particulas[i][j] > limite_superior) {
					this.particulas[i][j] = limite_superior;

				} else if (this.particulas[i][j] < limite_inferior) {
					this.particulas[i][j] = limite_inferior;
				}
				
			}
		}
	}
	
	public void Atualizar_Parametros(int intera��o) {

		// calculo da distancia euclidiana

		for (int i = 0; i < this.particulas.length; i++) {
			for (int j = 0; j < numero_Dimensoes; j++) {

				double parte1 = 0;
				double parte2 = 0;

				// condi��o de existencia para a distancia da particula para o
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

			double calculo = this.phi[i] * (intera��o - ((1 + ln) * this.numero_Iteracoes) / this.mi);

			this.inercia[i] = ((this.inercia_Inicial - this.inercia_Final) / (1 + Math.exp(calculo)))
					+ this.inercia_Final;

			this.n1[i] = n1_fixo * Math.pow(this.phi[i], -1);
			this.n2[i] = n2_fixo * this.phi[i];
		}

	}
	
	public double Sphere_Fuction(double[] particulas) {

		this.limite_superior = 1;
		this.limite_inferior = -1;

		double soma = 0;

		for (int j = 0; j < this.particulas[0].length; j++) {

			soma = soma + Math.pow(particulas[j], 2);

		}

		return soma;
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
				}
			}
		}

	}

	public void Executar() throws InterruptedException {

		CriarParticula();
		int i = 1;
		double fitBest = 1;

		double[][] grafico = new double[2][numero_Iteracoes];

		JFrame frame_particulas = new JFrame("Plot das particulas");
		frame_particulas.setSize(300, 300);
		frame_particulas.setLocation(600, 0);

		JFrame frame_otimiza��o = new JFrame("Plot Otimiza��o");
		frame_otimiza��o.setSize(300, 300);
		frame_otimiza��o.setLocation(0, 0);

		while (i < numero_Iteracoes) {

			Fitness();
			DefinirGBest();
			DefinirPBest();
			Velocidade(i);
			Atualizar_Parametros(i);
			Atualizar_Particulas(i);
			
			fitBest = this.bestFitness; // a cada itera��o tem-se um melhor
										// fitness, esse fitness � salvo em
										// fitbest

			grafico[0][i] = i; // Grafico � uma matriz que recebe a quantidade
								// de itera��es e os melhores fitness de cada
								// itera��o para plotar o gr�fico
			grafico[1][i] = fitBest;

			// print para imprimir as itera��es e os valores de thetas a cada itera��o.
			System.out.println(i + "- Melhor Fitness da itera��o: " + fitBest + "\n");
			
			/*
			  // plots da otimiza��o do fitness 
			 Plot2DPanel plot_fitness = new	 Plot2DPanel(); 
			 plot_fitness.addLinePlot("Fitness", grafico);
			 frame_otimiza��o.setContentPane(plot_fitness);
			 frame_otimiza��o.setVisible(true);
			 */
			
			/*
			// plots da otimiza��o das particulas
			Plot2DPanel plot_particulas = new Plot2DPanel();
			plot_particulas.addScatterPlot("Particulas", this.particulas);
			frame_particulas.setContentPane(plot_particulas);
			frame_particulas.setVisible(true);
			*/
			
			//Thread.sleep(500);

			if (fitBest <= this.criterio_parada) {
				break;
			}

			i++;
		}

	}

	public static void main(String[] args) throws InterruptedException {

		ID_CLUDIPSO p = new ID_CLUDIPSO(50, 20, 0.9, 0.4);
		p.Executar();

	}
}
