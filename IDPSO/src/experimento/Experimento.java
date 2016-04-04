package experimento;

import idpso.IDPSO;
import pso.PSO;
import cs.CS;
import discrete_pso.BPSO;
import discrete_pso.MBPSO;

public class Experimento {
	
	public int testes = 50;
	public int iteracoes = 100000;
	public double inercia_inicial = 0.9;
	public double inercia_final = 0.4;
	public int particulas = 20;
	public double n1 = 2.8;
	public double n2 = 1.3;
	public int sem_mudanca = 1000;
	
	public int equacao = 4;
	
	public IDPSO idpso = new IDPSO(iteracoes, particulas, inercia_inicial, inercia_final, n1, n2, equacao, sem_mudanca);
	public double[] erro_IDPSO;
	public double media_erro_IDPSO;
	public double desvio_Padrao_IDPSO;
	
	public PSO pso = new PSO(iteracoes, particulas, inercia_inicial, n1, n2, equacao, sem_mudanca);
	public double[] erro_PSO;
	public double media_erro_PSO;
	public double desvio_Padrao_PSO;
	
	public BPSO bpso = new BPSO(iteracoes, particulas, inercia_inicial, n1, n2, equacao, sem_mudanca);
	public double[] erro_BPSO;
	public double media_erro_BPSO;
	public double desvio_Padrao_BPSO;
	
	public MBPSO mbpso = new MBPSO(iteracoes, particulas, inercia_inicial, n1, n2, equacao, sem_mudanca);
	public double[] erro_MBPSO;
	public double media_erro_MBPSO;
	public double desvio_Padrao_MBPSO;
	
	
	
	public Experimento(){

		this.erro_IDPSO = new double[this.testes];
		this.media_erro_IDPSO = 0;
		
		this.erro_PSO = new double[this.testes];
		this.media_erro_PSO = 0;
		
		this.erro_BPSO = new double[this.testes];
		this.media_erro_BPSO = 0;
		
		this.erro_MBPSO = new double[this.testes];
		this.media_erro_MBPSO = 0;
	}
	
	public double Desvio_Padrao(double[] erro, double media){
		
		double desvio_Padrao = 0;
		double soma_dos_quadrados = 0;
		double diferencas = 0;
		double quadradoDaDiferenca = 0;
		double variancia = 0;
		
		for (int i = 0; i < this.testes; i++) {
			diferencas = media - erro[i];
			quadradoDaDiferenca = Math.pow(diferencas, 2);
			soma_dos_quadrados = soma_dos_quadrados + quadradoDaDiferenca;
		}
		
		variancia = (soma_dos_quadrados / (this.testes - 1)); //ISso aqui é igual a variancia
		
		desvio_Padrao = Math.sqrt(variancia);
		
		return desvio_Padrao;
		
	}
	
	public void Experimento_Executar() throws InterruptedException{
		
		int i = 0;
		int qtd_experimentos = this.testes;
		
		
		while(i < qtd_experimentos){
			
			this.idpso.Executar();
			this.erro_IDPSO[i] = this.idpso.bestFitness;
			this.media_erro_IDPSO = this.media_erro_IDPSO + this.erro_IDPSO[i];
			
			this.pso.Executar();
			this.erro_PSO[i] = this.pso.bestFitness;
			this.media_erro_PSO = this.media_erro_PSO + this.erro_PSO[i];
			
			this.bpso.Executar();
			this.erro_BPSO[i] = this.bpso.bestFitness;
			this.media_erro_BPSO = this.media_erro_BPSO + this.erro_BPSO[i];
			
			this.mbpso.Executar();
			this.erro_MBPSO[i] = this.mbpso.bestFitness;
			this.media_erro_MBPSO = this.media_erro_MBPSO + this.erro_MBPSO[i];
			
			
			i++;
			
			System.out.println(i);
		}
		
		this.media_erro_IDPSO = (this.media_erro_IDPSO / (qtd_experimentos - 1));
		this.media_erro_PSO = (this.media_erro_PSO / (qtd_experimentos - 1));
		this.media_erro_BPSO = (this.media_erro_BPSO / (qtd_experimentos - 1));
		this.media_erro_MBPSO = (this.media_erro_MBPSO / (qtd_experimentos - 1));
		
		this.desvio_Padrao_IDPSO = Desvio_Padrao(this.erro_IDPSO, this.media_erro_IDPSO);
		this.desvio_Padrao_PSO = Desvio_Padrao(this.erro_PSO, this.media_erro_PSO);
		this.desvio_Padrao_BPSO = Desvio_Padrao(this.erro_BPSO, this.media_erro_BPSO);
		this.desvio_Padrao_MBPSO = Desvio_Padrao(this.erro_MBPSO, this.media_erro_MBPSO);
		
		
		System.out.println("Média IDPSO: " + this.media_erro_IDPSO);
		System.out.println("Desvio IDPSO: " + this.desvio_Padrao_IDPSO);
		
		System.out.println("Média PSO: " + this.media_erro_PSO);
		System.out.println("Desvio PSO: " + this.desvio_Padrao_PSO);
		
		System.out.println("Média BPSO: " + this.media_erro_BPSO);
		System.out.println("Desvio BPSO: " + this.desvio_Padrao_BPSO);
		
		System.out.println("Média MBPSO: " + this.media_erro_MBPSO);
		System.out.println("Desvio MBPSO: " + this.desvio_Padrao_MBPSO);
		
		
		
		
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		
		Experimento exp = new Experimento();
		
		exp.Experimento_Executar();
	}
	
	

}
