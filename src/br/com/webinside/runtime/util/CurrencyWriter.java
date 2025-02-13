package br.com.webinside.runtime.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CurrencyWriter {

	private static final BigInteger THOUSAND = new BigInteger("1000");
	private static final BigInteger HUNDRED = new BigInteger("100");
	private static final String CENTO = "cento";
	private static final String CEM = "cem";
	/**
	 * Nomes das grandezas num�ricas no plural. O mapa a chave do mapa � o
	 * expoente de dez do n�mero e o valor � o seu nome no plural. Por exemplo:
	 * para chave 3 (10^3) o valor � "mil", para a chave 6 (10^6) o valor �
	 * "milh�es", e assim por diante. Os nomes foram obtidos de um artigo
	 * publicado na se��o Dois Mais Dois na revista SuperInteressante n�. 15, de
	 * dezembro de 1988 (Editora Abril, S�o Paulo/SP), dispon�vel em
	 * http://www.novomilenio.inf.br/idioma/19881200.htm.
	 */
	private final Map<Integer, String> grandezasPlural = new HashMap<Integer, String>();
	private final Map<Integer, String> grandezasSingular = new HashMap<Integer, String>();
	
	/** Nomes dos n�meros. */
	private final Map<Integer, String> nomes = new HashMap<Integer, String>();
	
	private String MOEDA_SINGULAR = "real";
	private String MOEDA_PLURAL = "reais";	
	private String FRACAO_SINGULAR = "centavo";
	private String FRACAO_PLURAL = "centavos";
	
	private static String PARTICULA_ADITIVA = "e";
	private static String PARTICULA_DESCRITIVA = "de";
	
	private static final BigDecimal MAX_SUPPORTED_VALUE = new BigDecimal("999999999999999999999999999.99");
		
	public CurrencyWriter(boolean real) {
		if (!real) {
			MOEDA_SINGULAR = "";
			MOEDA_PLURAL = "";
			FRACAO_SINGULAR = "";
			FRACAO_PLURAL = "";
		}
		preencherGrandezasPlural();
		preencherGrandezasSingular();
		preencherNomes();
	}

	public String write(final BigDecimal amount) {
		if (null == amount) {throw new IllegalArgumentException();}
		
		/*
		 * TODO substituir o m�todo setScale, abaixo, pela vers�o cujo
		 * par�metro de arredondamento � um enum
		 */
		BigDecimal value = amount.setScale(2, RoundingMode.HALF_EVEN);
		
		if (value.compareTo(BigDecimal.ZERO) == 0) {return "zero";}
		if (value.compareTo(BigDecimal.ZERO) < 0) {return "";}
		
		if (MAX_SUPPORTED_VALUE.compareTo(value) < 0) {
			throw new IllegalArgumentException("Valor acima do limite suportado.");
		}

		Stack<Integer> decomposed = decompose(value);

		/* Se o n�mero estiver, digamos, na casa dos milh�es, a pilha 
		 * dever� conter 4 elementos sendo os dois �ltimos os das 
		 * centenas e dos centavos, respectivamente. Assim, o expoente de
		 * dez que representa a grandeza no topo da pilha � o n�mero de 
		 * (elementos - 2) * 3 */
		int expoente = 3 * (decomposed.size() - 2); // TODO usar um �ndice de grupos em vez do expoente 
		
		StringBuffer sb = new StringBuffer();
		int lastNonZeroExponent = -1;
		
		while (!decomposed.empty()) {
			int valor = decomposed.pop();
			
			if (valor > 0) {
				sb.append(" ").append(PARTICULA_ADITIVA).append(" ");
				sb.append(comporNomeGrupos(valor));
				String nomeGrandeza = obterNomeGrandeza(expoente, valor);
				if (nomeGrandeza.length() > 0) {
					sb.append(" ");	
				}
				sb.append(nomeGrandeza);
				
				lastNonZeroExponent = expoente;
			}
			
			switch (expoente) { // TODO ao inv�s desses switches e ifs, partir para a id�ia das "Pend�ncias"; talvez implement�-las com enum
			case 0:
				BigInteger parteInteira = value.toBigInteger();

				if (BigInteger.ONE.equals(parteInteira)) {
					sb.append(" ").append(MOEDA_SINGULAR);
				} else if (parteInteira.compareTo(BigInteger.ZERO) > 0) {
					if (lastNonZeroExponent >= 6) {
						sb.append(" ").append(PARTICULA_DESCRITIVA);
					}
					sb.append(" ").append(MOEDA_PLURAL);
				}
				break;
				
			case -3:
				if (1 == valor) {
					sb.append(" ").append(FRACAO_SINGULAR);
				} else if (valor > 1) {
					sb.append(" ").append(FRACAO_PLURAL);
				}
				break;
			}
			
			expoente -= 3;
		}
		String resp = sb.substring(3).trim();
		return resp.replaceAll("\\s+", " ");
	}
	
	private StringBuffer comporNomeGrupos(int valor) {
		StringBuffer nome = new StringBuffer();

		int centenas = valor - (valor % 100);
		int unidades = valor % 10;
		int dezenas = (valor - centenas) - unidades;
		int duasCasas = dezenas + unidades;
		
		if (centenas > 0) {
			nome.append(" ").append(PARTICULA_ADITIVA).append(" ");
			
			if (100 == centenas) {
				if (duasCasas > 0) {
					nome.append(CENTO);
				} else {
					nome.append(CEM);
				}
			} else {
				nome.append(nomes.get(centenas));
			}
		}
		
		if (duasCasas > 0) {
			nome.append(" ").append(PARTICULA_ADITIVA).append(" ");
			if (duasCasas < 20) {
				nome.append(nomes.get(duasCasas));
			} else {
				if (dezenas > 0) {
					nome.append(nomes.get(dezenas));
				}
				
				if (unidades > 0) {
					nome.append(" ").append(PARTICULA_ADITIVA).append(" ");
					nome.append(nomes.get(unidades));
				}
			}
		}
		
		return nome.delete(0, 3);
	}

	private String obterNomeGrandeza(int exponent, int value) {
		if (exponent < 3) {return "";}
		
		if (1 == value) {
			return grandezasSingular.get(exponent);
		} else {
			return grandezasPlural.get(exponent);
		}
	}

	private Stack<Integer> decompose(BigDecimal value) {
		BigInteger intermediate = value.multiply(new BigDecimal(100)).toBigInteger();
		Stack<Integer> decomposed = new Stack<Integer>();
		
		BigInteger[] result = intermediate.divideAndRemainder(HUNDRED);
		intermediate = result[0];
		decomposed.add(result[1].intValue());
		
		while (intermediate.compareTo(BigInteger.ZERO) > 0) {
			result = intermediate.divideAndRemainder(THOUSAND);
			intermediate = result[0];
			decomposed.add(result[1].intValue());
		}
		
		/*
		 * Se o valor for apenas em centavos, adicionar zero para a casa dos
		 * reais inteiros
		 */
		if (decomposed.size() == 1) {
			decomposed.add(0);
		}
		
		return decomposed;
	}

	private void preencherGrandezasPlural() {
		grandezasPlural.put(3, "mil");
		grandezasPlural.put(6, "milh�es");
		grandezasPlural.put(9, "bilh�es");
		grandezasPlural.put(12, "trilh�es");
		grandezasPlural.put(15, "quatrilh�es");
		grandezasPlural.put(18, "quintilh�es");
		grandezasPlural.put(21, "sextilh�es");
		grandezasPlural.put(24, "setilh�es");
	}

	private void preencherGrandezasSingular() {
		grandezasSingular.put(3, "mil");
		grandezasSingular.put(6, "milh�o");
		grandezasSingular.put(9, "bilh�o");
		grandezasSingular.put(12, "trilh�o");
		grandezasSingular.put(15, "quatrilh�o");
		grandezasSingular.put(18, "quintilh�o");
		grandezasSingular.put(21, "sextilh�o");
		grandezasSingular.put(24, "setilh�o");
	}

	private void preencherNomes() {
		nomes.put(1, "um");
		nomes.put(2, "dois");
		nomes.put(3, "tr�s");
		nomes.put(4, "quatro");
		nomes.put(5, "cinco");
		nomes.put(6, "seis");
		nomes.put(7, "sete");
		nomes.put(8, "oito");
		nomes.put(9, "nove");
		nomes.put(10, "dez");
		nomes.put(11, "onze");
		nomes.put(12, "doze");
		nomes.put(13, "treze");
		nomes.put(14, "quatorze");
		nomes.put(15, "quinze");
		nomes.put(16, "dezesseis");
		nomes.put(17, "dezessete");
		nomes.put(18, "dezoito");
		nomes.put(19, "dezenove");
		nomes.put(20, "vinte");
		nomes.put(30, "trinta");
		nomes.put(40, "quarenta");
		nomes.put(50, "cinquenta");
		nomes.put(60, "sessenta");
		nomes.put(70, "setenta");
		nomes.put(80, "oitenta");
		nomes.put(90, "noventa");
		nomes.put(200, "duzentos");
		nomes.put(300, "trezentos");
		nomes.put(400, "quatrocentos");
		nomes.put(500, "quinhentos");
		nomes.put(600, "seiscentos");
		nomes.put(700, "setecentos");
		nomes.put(800, "oitocentos");
		nomes.put(900, "novecentos");
	}
		
}