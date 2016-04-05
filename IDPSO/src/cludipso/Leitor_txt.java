package cludipso;
import java.io.BufferedReader;
import java.io.FileReader;


public class Leitor_txt {

	private String Txt;
	public int qtd;
	public int colunas = 4;
	
	public Leitor_txt(String txt){
		
		this.Txt = txt;
		
	}
	
	public void Tamanho_txt() {
		try {
			FileReader fr = new FileReader(this.Txt);
			BufferedReader br = new BufferedReader(fr);

			String linha = br.readLine(); // Le a primeira linha do arquivo, a
											// variavel linha recebe valor null
											// quando o processo de repetição
											// chega ao final do arquivo

			int count = 0;
			while (linha != null) {
				linha = br.readLine(); // le da segunda linha até a ultima linha
				
				count++;
			}
			this.qtd = count;

			fr.close();

		} catch (Exception e) {
			System.out.println("Erro na abertura do arquivo!");
		}
	}

	
	public String[][] GuardarTxt() {
		String[][] matriz_str = new String[this.qtd - 1][colunas];

		try {
			FileReader fr = new FileReader(this.Txt);
			BufferedReader br = new BufferedReader(fr);

			String linha = br.readLine(); // Le a primeira linha do arquivo, a
											// variavel linha recebe valor null
											// quando o processo de repetição
											// chega ao final do arquivo

			
			for (int j = 0; j < this.qtd - 1; j++) {
				linha = br.readLine(); 			// le da segunda linha até a ultima linha
				
				matriz_str[j] = linha.split("	");  // adiciona cada linha a uma posição da matriz de string
		
			}

			fr.close();

		} catch (Exception e) {
			System.out.println("Erro na abertura do arquivo!");
		}
		return matriz_str;
	}
	

	public double[][] ConversaoDb(String[][] vetor_str) {
		double[][] matriz_db = new double[this.qtd - 1][colunas];

		for (int j = 0; j < qtd - 1; j++) 
		{
			
			for (int i = 0; i < matriz_db[0].length; i++) 
			{
				matriz_db[j][i] = Double.parseDouble(vetor_str[j][i]);
			}
		
		}
		return matriz_db;
	}
	
	

	public double[][] Base_de_dados() {
		Tamanho_txt();
		return ConversaoDb(GuardarTxt());
	}
	
	public static void main(String[] args) {
		
		Leitor_txt leitor = new Leitor_txt("iris.txt");
		
		double[][] entrada = leitor.Base_de_dados();
		
		System.out.println("PARE");
		
		
		
	}
}

