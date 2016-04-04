package cs;

import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

public class CS {

	public double[][] ninhos;
	public double[] fitness;
	public double Pa;
	public int iteracoes;
	public int cuckoo;
	public int ninho_aleatorio;
	public double alfa;
	public double lambda;
	public double[] rank;
	public double[][] rank_ordenado;

	public Random gerador = new Random();

	public CS(int quantidade_ninhos, int quantidade_ovos, double pa, int iteracoes, double alfa, double lambda) {

		this.ninhos = new double[quantidade_ninhos][quantidade_ovos];
		this.fitness = new double[quantidade_ninhos];
		this.rank = new double[quantidade_ninhos];
		this.Pa = pa;
		this.iteracoes = iteracoes;
		this.rank_ordenado = new double[2][quantidade_ninhos];
		this.alfa = alfa;
		this.lambda = lambda;

	}

	private void Gerar_ninhos() {

		for (int i = 0; i < this.ninhos.length; i++) {
			for (int j = 0; j < this.ninhos[0].length; j++) {
				this.ninhos[i][j] = Math.random();
			}
		}

	}

	private double Funcao_fitness(int ninho) {

		this.fitness[ninho] = (this.ninhos[ninho][0] * this.ninhos[ninho][0])
				+ (this.ninhos[ninho][1] * this.ninhos[ninho][1]);

		return this.fitness[ninho];

	}

	private void Voo_levy(int iteracao_atual) {
		
		this.cuckoo = 0;
		
		while (this.cuckoo == 0) {
			this.cuckoo = this.gerador.nextInt(this.ninhos.length);			
		}

		for (int j = 0; j < ninhos[0].length; j++) {

			// System.out.println(" Ovo " + j + " : " + this.ninhos[cuckoo][j]);

			double elevacao = Math.pow(iteracao_atual, (-lambda));
			this.ninhos[cuckoo][j] = this.ninhos[cuckoo][j] + this.alfa * elevacao;

			if (this.ninhos[cuckoo][j] > 1) {
				this.ninhos[cuckoo][j] = 1;

			} else if (this.ninhos[cuckoo][j] < -1) {
				this.ninhos[cuckoo][j] = -1;
			}

		}

		// System.out.println(" Voo de levy: " + this.alfa *
		// Math.pow(iteracao_atual, (-lambda)));

	}

	private void Comparar_fitness() {

		this.ninho_aleatorio = this.gerador.nextInt(this.ninhos.length);

		double Fi = Funcao_fitness(this.cuckoo);
		double Fj = fitness[this.ninho_aleatorio];

		if (Fi > Fj) {
			for (int j = 0; j < this.ninhos[0].length; j++) {
				this.ninhos[this.cuckoo] = this.ninhos[this.ninho_aleatorio].clone();
			}

		}

	}

	private void Rankear_fitness() {
		
		for (int j = 0; j < this.fitness.length; j++) {
			this.rank_ordenado[0][j] = this.fitness[j];
			this.rank_ordenado[1][j] = j;
		}
		
		this.rank_ordenado = Ordenar(this.rank_ordenado);
		
		for (int j = 0; j < this.fitness.length; j++) {
			this.fitness[j] = this.rank_ordenado[0][j];
		}
		
	}

	private double[][] Ordenar(double[][] par) {
		double aux = 0;
		double aux_indice = 0;

		for (int i = par[0].length - 1; i >= 1; i--) {

			for (int j = 0; j < par[0].length - 1; j++) {

				if (par[0][j] > par[0][j + 1]) {

					aux = par[0][j];
					aux_indice = par[1][j];

					par[0][j] = par[0][j + 1];
					par[1][j] = par[1][j + 1];

					par[0][j + 1] = aux;
					par[1][j + 1] = aux_indice;

				}
			}
		}

		return par;
	}

	private void Abandonar_ninhos() {

		double abandono_ninhos = this.Pa * this.ninhos.length;
		abandono_ninhos = (int) Math.floor(abandono_ninhos);

		for (int i = this.ninhos.length - 1; i >= (this.ninhos.length - abandono_ninhos); i--) {

			int indice = (int) this.rank_ordenado[1][i];

			for (int j = 0; j < this.ninhos[0].length; j++) {

				this.ninhos[indice][j] = Math.random();

			}

		}

	}

	public void Executar() throws InterruptedException {

		double fitBest = 1;
		double[][] grafico = new double[2][iteracoes];

		Gerar_ninhos();

		for (int i = 0; i < this.ninhos.length; i++) {
			Funcao_fitness(i);
		}
		
		/*
		JFrame frame_particulas = new JFrame("Plot das particulas");
		frame_particulas.setSize(500, 500);
		frame_particulas.setLocation(600, 0);
		
		JFrame frame_otimização = new JFrame("Plot Otimização");
		frame_otimização.setSize(500, 500);
		frame_otimização.setLocation(0, 0);
		*/
		
		for (int i = 1; i < this.iteracoes; i++) {

			System.out.println("\n-----------------------------------------Inicio da Iteração " + i
					+ "---------------------------------------- \n");

			Voo_levy(i);
			Comparar_fitness();
			Rankear_fitness();
			Abandonar_ninhos();

			fitBest = this.rank_ordenado[0][0];
			grafico[0][i] = i;
			grafico[1][i] = fitBest;
			
			
			//Thread.sleep(1000);
/*			
			
			Plot2DPanel plot_particulas = new Plot2DPanel();
			plot_particulas.addScatterPlot("Particulas", this.ninhos);
			frame_particulas.setContentPane(plot_particulas);
			frame_particulas.setVisible(true);
	
			Plot2DPanel plot_fitness = new Plot2DPanel();
			plot_fitness.addLinePlot("Fitness", grafico);
			frame_otimização.setContentPane(plot_fitness);
			frame_otimização.setVisible(true);
	*/		
			
			
			System.out.println(" Melhor Fitness: " + this.rank_ordenado[0][0]);
			
			/*
			for (int j = 0; j < this.rank_ordenado[0].length; j++) {
				System.out.println(j + " " + this.rank_ordenado[0][j]);
			}*/
		}
	}

	public static void main(String[] args) throws InterruptedException {

		CS cs = new CS(20, 2, 0.5, 1000, 1, 1.5);

		cs.Executar();
	}

}
