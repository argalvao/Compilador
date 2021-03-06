package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

//import javax.swing.plaf.synth.SynthSeparatorUI;
import controller.SemanticAnalyzer;
import model.SynthaticNode;
import model.Token;
import model.TokenTypes;
import util.RecursiveCall;
import util.FirstFollow;

public class SynthaticAnalyzer extends RecursiveCall {

	private static SynthaticAnalyzer instance;
	public List<String> errors;
	public List<Token> id;
	public HashMap<String, Token> procedimentos;
	public HashMap<String, Token> funcoes;
	public boolean global = false;
	public String nomeBloco = "";
	public String tipoBloco = "";
	public String bloco = "";
	public String tipoVar = "";
	public String extend = "";
	public String nomeFuncao = "";
	public Token var;
	public List<Token> auxiliar;
	
	SemanticAnalyzer semantic = new SemanticAnalyzer();
	
	SynthaticAnalyzer() {
		this.auxiliar = new ArrayList<>();
		this.errors = new ArrayList<>();
		this.id = new ArrayList<>();
		
		// Certo
		this.functions.put("<Valor>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				semantic.verificStructExtends(tokens.peek());
				semantic.verificConstVarTypeValor(var, tokens.peek().getLexeme(), bloco, nomeBloco);
				var.setValorId(token.getLexeme());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (TokenTypes.NUMBER.equals(token.getType())) {
				if(token.getLexeme().matches("[+-]?[0-9][0-9]*")) {
					semantic.verificConstVarTypeValor(var, "int", bloco, nomeBloco);
				}else {
					semantic.verificConstVarTypeValor(var, "real", bloco, nomeBloco);
				}
				var.setValorId(token.getLexeme());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (TokenTypes.STRING.equals(token.getType())) {
				semantic.verificConstVarTypeValor(var, "string", bloco, nomeBloco);		
				var.setValorId(token.getLexeme());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "true".equals(token.getLexeme())
					|| token != null && "false".equals(token.getLexeme())) {
				semantic.verificConstVarTypeValor(var, "boolean", bloco, nomeBloco);
				var.setValorId(token.getLexeme());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um valor de variavel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ValorVetor>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				//semantic.verificStructExtendsType(tokens.peek());
				semantic.verificTypeVetor(tokens.peek(), bloco, nomeBloco);
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && !TokenTypes.DELIMITER.equals(token.getType())
					&& token.getLexeme().matches("[+]?[0-9][0-9]*")) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um valor v�lido para vetor.");
				if (!tokens.isEmpty()) {
					tokens.remove();
					token = tokens.peek();
				}

			}
			return null;
		});

		// Certo
		this.functions.put("<Tipo>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "int".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "boolean".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "string".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "real".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				semantic.verificStructExtendsType(tokens.peek());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um tipo.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Inicio>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			while (!tokens.isEmpty()) {
				Token token = tokens.peek();
				if (this.predict("Const", tokens.peek())) {
					if (tokens.peek() != null) {
						tokenMap.add(this.call("<Const>", tokens).getTokenNode());
						token = tokens.peek();
					}
				}
				if (this.predict("Struct", tokens.peek())) {
					if (tokens.peek() != null) {
						tokenMap.add(this.call("<Struct>", tokens).getTokenNode());
						token = tokens.peek();
					}
				}
				if (this.predict("Var", tokens.peek())) {
					if (tokens.peek() != null) {
						global = true;
						tokenMap.add(this.call("<Var>", tokens).getTokenNode());
						token = tokens.peek();
					}
				}
				if (this.predict("GeraFuncaoeProcedure", tokens.peek())) {
					if (tokens.peek() != null) {
						tokenMap.add(this.call("<GeraFuncaoeProcedure>", tokens).getTokenNode());
						token = tokens.peek();
					}
				}
				if (this.predict("Start", tokens.peek())) {
					if (tokens.peek() != null) {
						tokenMap.add(this.call("<Start>", tokens).getTokenNode());
						token = tokens.peek();
					}
				}
				if (tokens.peek() != null && TokenTypes.COMMENT.equals(tokens.peek().getType())){
					tokens.remove();
				} else if (!tokens.isEmpty() && token != null && !this.first.get("Inicio").contains(tokens.peek().getLexeme())){
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia nenhum bloco algoritmico.");
					//System.out.println(tokens.peek().getLexeme());
					while (token != null) {
						tokens.remove();
						token = tokens.peek();
					}
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}	
			}
			return null;
		});

		// Certo
		this.functions.put("<Condicional>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "if".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "(".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					tokenMap.add(this.call("<ExpressaoLogicaRelacional>", tokens).getTokenNode());
					token = tokens.peek();
					if (token != null && ")".equals(token.getLexeme())) {
						tokenMap.add(new SynthaticNode(tokens.remove()));
						token = tokens.peek();
						if (token != null && "then".equals(token.getLexeme())) {
							tokenMap.add(new SynthaticNode(tokens.remove()));
							token = tokens.peek();
							if (token != null && "{".equals(token.getLexeme())) {
								tokenMap.add(new SynthaticNode(tokens.remove()));
								token = tokens.peek();
								tokenMap.add(this.call("<Corpo>", tokens).getTokenNode());
								tokenMap.add(this.call("<CondEnd>", tokens).getTokenNode());
								return tokenMap;
							} else {
								int line = token.getLine() + 1;
								this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia o corpo da funcao if/then.");
								if (!tokens.isEmpty()) {
									tokens.remove();
								}
							}
						} else {
							int line = token.getLine() + 1;
							this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao faz parte da declaracao para funcao if/then.");
							if (!tokens.isEmpty()) {
								tokens.remove();
							}
						}
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de parametros para funcao if/then.");
						if (!tokens.isEmpty()) {
							tokens.remove();
						}
					}
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao de parametros para funcao if/then.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a funcao if/then.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// VAZIO
		this.functions.put("<CondEnd>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "else".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "{".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					tokenMap.add(this.call("<Corpo>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia corpo da funcao else.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.follow.get("CondEnd").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e continua��o da funcao if/then.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});
		
		// Certo
		this.functions.put("<Laco>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "while".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "(".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					tokenMap.add(this.call("<ExpressaoLogicaRelacional>", tokens).getTokenNode());
					token = tokens.peek();
					//System.out.println(tokens.peek().getLexeme() + tokens.peek().getLine());
					if (token != null && ")".equals(token.getLexeme())) {
						tokenMap.add(new SynthaticNode(tokens.remove()));
						token = tokens.peek();
						if (token != null && "{".equals(token.getLexeme())) {
							tokenMap.add(new SynthaticNode(tokens.remove()));
							token = tokens.peek();
							tokenMap.add(this.call("<Corpo>", tokens).getTokenNode());
							return tokenMap;
						} else {
							int line = token.getLine() + 1;
							this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia corpo da funcao while");
							if (!tokens.isEmpty()) {
								tokens.remove();
							}
						}
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de parametros para funcao while.");
						if (!tokens.isEmpty()) {
							tokens.remove();
						}
					}
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao de parametros para funcao while.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a funcao while.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});
		
		// Vazio
		this.functions.put("<GeraFuncaoeProcedure>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Funcao", tokens.peek())) {
				tokenMap.add(this.call("<Funcao>", tokens).getTokenNode());
				tokenMap.add(this.call("<GeraFuncaoeProcedure>", tokens).getTokenNode());
			} else if (this.predict("Procedimento", tokens.peek())) {
				tokenMap.add(this.call("<Procedimento>", tokens).getTokenNode());
				tokenMap.add(this.call("<GeraFuncaoeProcedure>", tokens).getTokenNode());
			} else if (token != null && this.follow.get("GeraFuncaoeProcedure").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				if (tokens.peek() != null && !this.follow.get("GeraFuncaoeProcedure").contains(token.getLexeme())) {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao de um bloco de funcao ou procedimento.");
					while (tokens.peek() != null
							&& !this.follow.get("GeraFuncaoeProcedure").contains(token.getLexeme())) {
						if (!tokens.isEmpty()) {
							tokens.remove();
							token = tokens.peek();
						}
					}
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<Funcao>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "function".equals(token.getLexeme())) {
				bloco = token.getLexeme();
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tipoBloco = tokens.peek().getLexeme();
				tokenMap.add(this.call("<Tipo>", tokens).getTokenNode());
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					// Verificacao Semantica de nomes iguais de funcoes
					nomeBloco = token.getLexeme();
					tokens.peek().setTipoId(tipoBloco);
					semantic.funtionsEqualNames(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					if (token != null && "(".equals(token.getLexeme())) {
						tokenMap.add(new SynthaticNode(tokens.remove()));
						tokenMap.add(this.call("<Parametro>", tokens).getTokenNode());
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia declaracao de parametro do bloco function.");
						while (tokens.peek() != null && !this.follow.get("Funcao").contains(token.getLexeme())) {
							tokens.remove();
							token = tokens.peek();
						}
					}
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador do bloco function.");
					while (tokens.peek() != null && !this.follow.get("Funcao").contains(token.getLexeme())) {
						//System.out.println(tokens.peek().getLexeme());
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao do bloco function.");
				while (tokens.peek() != null && !this.follow.get("Funcao").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Procedimento>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "procedure".equals(token.getLexeme())) {
				bloco = token.getLexeme();
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					nomeBloco = token.getLexeme();
					// Verificacao Semantica de nomes iguais de procedimentos
					semantic.procedureEqualNames(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					if (token != null && "(".equals(token.getLexeme())) {
						tokenMap.add(new SynthaticNode(tokens.remove()));
						tokenMap.add(this.call("<Parametro>", tokens).getTokenNode());
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia declaracao de parametro do bloco procedure.");
						while (tokens.peek() != null && !this.follow.get("Procedimento").contains(token.getLexeme())) {
							tokens.remove();
							token = tokens.peek();
						}
					}
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador do bloco procedure.");
					while (tokens.peek() != null && !this.follow.get("Procedimento").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add(
						"Linha: " + line + " | (" + token.getLexeme() + ") inicia a declaracao do bloco procedure.");
				while (tokens.peek() != null && !this.follow.get("Procedimento").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Parametro>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Tipo", tokens.peek())) {
				tipoVar = tokens.peek().getLexeme();
				tokenMap.add(this.call("<Tipo>", tokens).getTokenNode());
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					tokens.peek().setTipoId(tipoVar);
					//System.out.println(tokens.peek().getTipoId()+"  "+tokens.peek().getLexeme());
					semantic.fuctionsProcedureAddPara(tokens.peek(), bloco, nomeBloco);
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Para2>", tokens).getTokenNode());
					tokenMap.add(this.call("<Para1>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador do parametro de function/procedure.");
					while (tokens.peek() != null && !this.follow.get("Parametro").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors
						.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e tipo do parametro de function/procedure.");
				while (tokens.peek() != null && !this.follow.get("Parametro").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Para1>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ")".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<F2>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Parametro>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza ou continua a declaracao do parametro do bloco function/procedure.");
				while (tokens.peek() != null && !this.follow.get("Para1").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Para2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "[".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "]".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Para3>", tokens).getTokenNode());
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza declaracao de vetor no parametro do bloco function/procedure.");
					while (tokens.peek() != null && !this.follow.get("Para2").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else if (this.follow.get("Para2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia declaracao de vetor no parametro do bloco function/procedure.");
				while (tokens.peek() != null && !this.follow.get("Para2").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return tokenMap;
		});

		// Vazio
		this.functions.put("<Para3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "[".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "]".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza declaracao de matriz no parametro do bloco function/procedure.");
					while (tokens.peek() != null && !this.follow.get("Para3").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else if (this.follow.get("Para3").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia declaracao de matriz no parametro do bloco function/procedure.");
				while (tokens.peek() != null && !this.follow.get("Para3").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<F2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "{".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Corpo>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia o escopo do bloco function/procedure.");
				while (tokens.peek() != null && !this.follow.get("F2").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Const>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "const".equals(token.getLexeme())) {
				bloco = "";
				tipoBloco = "";
				nomeBloco = "";
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "{".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<TipoConst>", tokens).getTokenNode());
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia o escopo do bloco const.");
					while (tokens.peek() != null && !this.follow.get("Const").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else if (this.follow.get("Const").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao do bloco const.");
				while (tokens.peek() != null && !this.follow.get("Const").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<TipoConst>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Tipo", tokens.peek())) {
				tipoVar = tokens.peek().getLexeme();
				tokenMap.add(this.call("<Tipo>", tokens).getTokenNode());
				tokenMap.add(this.call("<IdConst>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um tipo de vari�vel no bloco const.");
				while (tokens.peek() != null && !this.follow.get("TipoConst").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdConst>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				var = tokens.peek();
				tokens.peek().setTipoId(tipoVar);
				semantic.constVarEqualNames(tokens.peek());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Valor>", tokens).getTokenNode());
				tokenMap.add(this.call("<Const2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de vari�vel no bloco const.");
				while (tokens.peek() != null && !this.follow.get("IdConst").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Const2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ";".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Const3>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdConst>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza ou continua a declaracao de uma vari�vel no bloco const.");
				while (tokens.peek() != null && !this.follow.get("Const2").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Const3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("TipoConst", tokens.peek())) {
				tokenMap.add(this.call("<TipoConst>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "}".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza o escopo do bloco const ou continua a declaracao de uma vari�vel no bloco const.");
				while (tokens.peek() != null && !this.follow.get("Const3").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Struct>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "typedef".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "struct".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					if (TokenTypes.IDENTIFIER.equals(token.getType())) {
						this.id.add(tokens.peek());
						nomeBloco = token.getLexeme();
						semantic.structOnly(tokens.peek());
						tokenMap.add(new SynthaticNode(tokens.remove()));
						tokenMap.add(this.call("<Extends>", tokens).getTokenNode());
						tokenMap.add(this.call("<TipoStruct>", tokens).getTokenNode());
						tokenMap.add(this.call("<Struct>", tokens).getTokenNode());
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador do bloco struct");
						while (tokens.peek() != null && !this.follow.get("Struct").contains(token.getLexeme())) {
							tokens.remove();
							token = tokens.peek();
						}
					}
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e parte da declaracao do bloco struct");
					while (tokens.peek() != null && !this.follow.get("Struct").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else if (this.follow.get("Struct").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				if (!this.follow.get("Struct").contains(token.getLexeme())) {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao do bloco struct");
				}
				while (tokens.peek() != null && !this.follow.get("Struct").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<Extends>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "{".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "extends".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					extend = tokens.peek().getLexeme();
					semantic.verificStructExtends(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					if (token != null && "{".equals(token.getLexeme())) {
						tokenMap.add(new SynthaticNode(tokens.remove()));
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia o escopo do bloco struct.");
						while (tokens.peek() != null && !this.follow.get("Extends").contains(token.getLexeme())) {
							tokens.remove();
							token = tokens.peek();
						}
					}
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add(
							"Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador do bloco struct.");
					while (tokens.peek() != null && !this.follow.get("Extends").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao estende ou inicia o escopo do bloco struct.");
				while (tokens.peek() != null && !this.follow.get("Extends").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<TipoStruct>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Tipo", tokens.peek())) {
				tipoVar = tokens.peek().getLexeme();
				tokenMap.add(this.call("<Tipo>", tokens).getTokenNode());
				tokenMap.add(this.call("<IdStruct>", tokens).getTokenNode());
				return tokenMap;
			} else {
				if (!this.follow.get("TipoStruct").contains(token.getLexeme())) {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um tipo de struct.");
				}
				while (tokens.peek() != null && !this.follow.get("TipoStruct").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdStruct>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				tokens.peek().setTipoId(tipoVar);
				if(extend.equals("")) {
					semantic.structVarEqualNames(tokens.peek(), nomeBloco);
				}else {
					semantic.structExtendsOnly(tokens.peek(), extend);
					semantic.structVarEqualNames(tokens.peek(), nomeBloco);
				}
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Struct2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de vari�vel do bloco struct.");
				while (tokens.peek() != null && !this.follow.get("IdStruct").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Struct2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ";".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Struct3>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdStruct>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua ou finaliza a declaracao de variaveis do bloco struct.");
				while (tokens.peek() != null && !this.follow.get("Struct2").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Struct3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("TipoStruct", tokens.peek())) {
				tokenMap.add(this.call("<TipoStruct>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "}".equals(token.getLexeme())) {
				extend = "";
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua a declaracao de vari�veis ou finaliza o escopo do bloco struct.");
				while (tokens.peek() != null && !this.follow.get("Struct3").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Var>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "var".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "{".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<TipoVar>", tokens).getTokenNode());
					// System.out.println(this.call("<Var>", tokens).getTokenNode().);
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | " + token.getLexeme() + " nao inicia o escopo do bloco var.");
					while (tokens.peek() != null && !this.follow.get("Var").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else if (this.follow.get("Var").contains(token.getLexeme()) || TokenTypes.IDENTIFIER.equals(tokens.peek().getType())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | " + token.getLexeme() + "  nao inicia a declaracao do bloco var.");
				while (tokens.peek() != null && !this.follow.get("Var").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<TipoVar>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			// System.out.println(tokens.peek().getLexeme());
			if (this.predict("Tipo", tokens.peek())) {
				tipoVar = tokens.peek().getLexeme();
				tokenMap.add(this.call("<Tipo>", tokens).getTokenNode());
				tokenMap.add(this.call("<IdVar>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um tipo de vari�vel no bloco var.");
				while (tokens.peek() != null && !this.follow.get("TipoVar").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdVar>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				var = tokens.peek();
				tokens.peek().setTipoId(tipoVar);
				// Semantica de variaveis globais
				if(global) {
					semantic.globalVarEqualNames(tokens.peek());
				} else {
					// Semantica de variaveis Locais
					semantic.localVarEqualNames(tokens.peek(), bloco, nomeBloco);
				}
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Var2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de vari�vel no bloco var.");
				while (tokens.peek() != null && !this.follow.get("IdVar").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Var2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("VetorDeclaracao", tokens.peek())) {
				tokenMap.add(this.call("<VetorDeclaracao>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdVar>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ";".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Var3>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "=".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Valor>", tokens).getTokenNode());
				tokenMap.add(this.call("<Var4>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza, continua, dar valor ou come�a um vetor no bloco var.");
				while (tokens.peek() != null && !this.follow.get("Var2").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Var3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("TipoVar", tokens.peek())) {
				tokenMap.add(this.call("<TipoVar>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "}".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua as declara��es de vari�veis, ou finaliza o bloco var.");
				while (tokens.peek() != null && !this.follow.get("Var3").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Var4>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ";".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Var3>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdVar>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua ou finaliza as declara��es de variaveis no bloco var.");
				while (tokens.peek() != null && !this.follow.get("Var4").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<VetorDeclaracao>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "[".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ValorVetor>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && "]".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Matriz>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de um vetor no bloco var.");
					while (tokens.peek() != null && !this.follow.get("VetorDeclaracao").contains(token.getLexeme())) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			} else if (this.follow.get("VetorDeclaracao").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao de um vetor no bloco var.");
				while (tokens.peek() != null && !this.follow.get("VetorDeclaracao").contains(token.getLexeme())) {
					tokens.remove();
					token = tokens.peek();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Matriz>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Var4", tokens.peek())) {
				tokenMap.add(this.call("<Var4>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "[".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ValorVetor>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && "]".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Var4>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de uma matriz no bloco var.");
					while (tokens.peek() != null && !this.follow.get("Matriz").contains(token.getLexeme())) {
						if (!tokens.isEmpty()) {
							tokens.remove();
							token = tokens.peek();
						}
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao de uma matriz no bloco var.");
				while (tokens.peek() != null && !this.follow.get("Matriz").contains(token.getLexeme())) {
					if (!tokens.isEmpty()) {
						tokens.remove();
						token = tokens.peek();
					}
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ListaParametros>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ListaParametros2", tokens.peek()) || this.follow.get("ListaParametros2").contains(tokens.peek().getLexeme())) {
				//semantic.verificFuncProcDeclaration(tokens.peek(), nomeFuncao, bloco, nomeBloco);
				tokenMap.add(this.call("<ListaParametros2>", tokens).getTokenNode());
				tokenMap.add(this.call("<ContListaParametros>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia um parametro de uma express�o.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<ContListaParametros>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				SynthaticNode temporary = this.call("<ListaParametros>", tokens).getTokenNode();
				if (temporary != null && !temporary.isEmpty()) {
					tokenMap.add(temporary);
				}
			} else if (this.follow.get("ContListaParametros").contains(token.getLexeme())) {
				return tokenMap;
			} else { 
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua a declaracao de outro elemento no parametro de uma express�o.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<ListaParametros2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			// Lista auxiliar para ajudar na quantidade de parametros
			auxiliar.add(token);
			if (this.predict("Identificador", tokens.peek())) {
				tokenMap.add(this.call("<Identificador>", tokens).getTokenNode());
				return tokenMap;
			} else if (TokenTypes.NUMBER.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (TokenTypes.STRING.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (this.follow.get("ListaParametros2").contains(token.getLexeme())) {
				auxiliar.remove(auxiliar.size()-1);
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um tipo de vari�vel no parametro.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		//
		this.functions.put("<Identificador>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				// pegando variaveis dentro da funcao
				this.id.add(tokens.peek());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Identificador3>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Escopo", tokens.peek())) {
				tokenMap.add(this.call("<Escopo>", tokens).getTokenNode());
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Identificador2>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de acesso, apos o global/local.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de acesso.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Identificador2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ".".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					semantic.readVarExists(tokens.peek(), bloco);
					this.id.add(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Vetor>", tokens).getTokenNode());
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de acesso a vetor.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.predict("Vetor", tokens.peek())) {
				tokenMap.add(this.call("<Vetor>", tokens).getTokenNode());
			} else if (this.follow.get("Identificador2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um acesso a vetor.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<Identificador3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "(".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ListaParametros>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && ")".equals(token.getLexeme())) {
					auxiliar.clear();
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um acesso a vetor.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.predict("Identificador2", tokens.peek())) {
				tokenMap.add(this.call("<Identificador2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.follow.get("Identificador3").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um acesso a vetor ou inicia uma entrada de parametros.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Aqui escrever os erros
		// Vazio
		this.functions.put("<Identificador4>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ".".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Vetor>", tokens).getTokenNode());
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de acesso.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.follow.get("Identificador4").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um acesso a vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<Vetor>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Identificador4", tokens.peek())) {
				tokenMap.add(this.call("<Identificador4>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "[".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<IndiceVetor>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && "]".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Vetor2>", tokens).getTokenNode());
					token = tokens.peek();
					tokenMap.add(this.call("<Identificador4>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de um vetor.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			}  else if (this.follow.get("Vetor").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma declaracao de um vetor ou nao acessa uma vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Vetor2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "[".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<IndiceVetor>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && "]".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza uma declaracao de um vetor ou nao acessa uma vari�vel.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.follow.get("Vetor2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma declaracao de um vetor ou nao acessa uma vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<IndiceVetor>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Identificador", tokens.peek())) {
				tokenMap.add(this.call("<Identificador>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "IntPos".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um valor v�lido para o tamanho de um vetor.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Escopo>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "global".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && ".".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um delimitador de acesso a uma vari�vel.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (token != null && "local".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && ".".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um delimitador de acesso a uma vari�vel.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador de escopo de vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdentificadorSemFuncao>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				// Passou acesso de funcao e atribuicao de valor a variavel, retorno = a + i;
				nomeFuncao = tokens.peek().getLexeme();
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Identificador2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Escopo", tokens.peek())) {
				tokenMap.add(this.call("<Escopo>", tokens).getTokenNode());
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Identificador2>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de vari�vel.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador de escopo de vari�vel ou identificador de vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ExpressaoAritmetica>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			//System.out.println(tokens.peek().getLexeme());
			if (token != null && "--".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdentificadorSemFuncao>", tokens).getTokenNode());
				tokenMap.add(this.call("<T2>", tokens).getTokenNode());
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("T", tokens.peek())) {
				tokenMap.add(this.call("<T>", tokens).getTokenNode());
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("IdentificadorAritmetico", tokens.peek())) {
				// System.out.println(tokens.peek().getLexeme());
				tokenMap.add(this.call("<IdentificadorAritmetico>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "++".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdentificadorSemFuncao>", tokens).getTokenNode());
				tokenMap.add(this.call("<T2>", tokens).getTokenNode());
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma express�o aritm�tica.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio 
		this.functions.put("<ExpressaoAritmetica2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "++".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<T2>", tokens).getTokenNode());
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "--".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<T2>", tokens).getTokenNode());
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("T2", tokens.peek())) {
				tokenMap.add(this.call("<T2>", tokens).getTokenNode());
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
				return tokenMap;
				//DUVIDA se pode ser ter vazio em expressao com produ��es vaziass
			} else if(this.predict("E2", tokens.peek())){
				tokenMap.add(this.call("<E2>", tokens).getTokenNode());
			} else if (this.follow.get("ExpressaoAritmetica2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma express�o aritm�tica(1).");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<E2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "+".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
			} else if (token != null && "-".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
			} else if (this.follow.get("E2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um delimitar aritm�tico de somar ou subtrair.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<T>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("F", tokens.peek())) {
				tokenMap.add(this.call("<F>", tokens).getTokenNode());
				tokenMap.add(this.call("<T2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um delimitador aritm�tico .");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<T2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "*".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
			}
			if (token != null && "/".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
			} else if (this.follow.get("T2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um delimitar aritm�tico de multiplicar ou dividir.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<F>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.NUMBER.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "(".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && ")".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza uma declaracao aritm�tica.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um n�mero ou inicia uma declaracao aritm�tica.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdentificadorAritmetico>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				// Inicia acesso a funcao cadastrarPessoas
				nomeFuncao = tokens.peek().getLexeme();
				this.id.add(tokens.peek());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdentificadorAritmetico3>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Escopo", tokens.peek())) {
				tokenMap.add(this.call("<Escopo>", tokens).getTokenNode());
				token = tokens.peek();
				if (TokenTypes.IDENTIFIER.equals(token.getType())) {
					this.id.add(tokens.peek());
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Identificador2>", tokens).getTokenNode());
					tokenMap.add(this.call("<ExpressaoAritmetica2>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de vari�vel.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e identificador de escopo de vari�vel ou identificador de vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdentificadorAritmetico3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "(".equals(token.getLexeme())) {
				semantic.verificFuncProcDeclaration(tokens.peek(), nomeFuncao, bloco, nomeBloco, auxiliar.size(), auxiliar);
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ListaParametros>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && ")".equals(token.getLexeme())) {
					auxiliar.clear();
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<T2>", tokens).getTokenNode());
					tokenMap.add(this.call("<E2>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza uma lista de parametros aritm�ticos.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.predict("Identificador2", tokens.peek()) || this.follow.get("Identificador2").contains(token.getLexeme())) {
				tokenMap.add(this.call("<Identificador2>", tokens).getTokenNode());
				tokenMap.add(this.call("<ExpressaoAritmetica2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.follow.get("IdentificadorAritmetico3").contains(token.getLexeme())) {
				//System.out.println(tokens.peek().getLexeme());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma lista de parametros aritm�ticos ou um acesso a vari�vel.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ExpressaoLogicaRelacional>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "(".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ExpressaoLR>", tokens).getTokenNode());
				if (token != null && ")".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<ExpressaoLR3>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza uma express�o l�gica ou relacional.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (this.predict("ExpressaoLR", tokens.peek())) {
				tokenMap.add(this.call("<ExpressaoLR>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma express�o l�gica ou relacional.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ExpressaoLR>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ArgumentoLR3", tokens.peek())) {
				tokenMap.add(this.call("<ArgumentoLR3>", tokens).getTokenNode());
				tokenMap.add(this.call("<OperadorRelacional>", tokens).getTokenNode());
				tokenMap.add(this.call("<ArgumentoLR>", tokens).getTokenNode());
				tokenMap.add(this.call("<ExpressaoLR3>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("ArgumentoLR2", tokens.peek())) {
				tokenMap.add(this.call("<ArgumentoLR2>", tokens).getTokenNode());
				tokenMap.add(this.call("<ExpressaoLR2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma express�o l�gica ou relacional.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ExpressaoLR2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ExpressaoLR3", tokens.peek())) {
				tokenMap.add(this.call("<ExpressaoLR3>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("OperadorRelacional", tokens.peek())) {
				tokenMap.add(this.call("<OperadorRelacional>", tokens).getTokenNode());
				tokenMap.add(this.call("<ArgumentoLR>", tokens).getTokenNode());
				tokenMap.add(this.call("<ExpressaoLR3>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.follow.get("ExpressaoLR2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia uma express�o l�gica/relacional ou um operador relacional.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<ExpressaoLR3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("OperadorLogico", tokens.peek())) {
				tokenMap.add(this.call("<OperadorLogico>", tokens).getTokenNode());
				tokenMap.add(this.call("<ExpressaoLogicaRelacional>", tokens).getTokenNode());
			} else if (this.follow.get("ExpressaoLR3").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um operador l�gico para iniciar uma express�o l�gica ou relacional.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});
		// Certo
		this.functions.put("<ArgumentoLR>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ArgumentoLR3", tokens.peek())) {
				tokenMap.add(this.call("<ArgumentoLR3>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("ArgumentoLR2", tokens.peek())) {
				tokenMap.add(this.call("<ArgumentoLR2>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um argumento para express�es relacional ou l�gicas.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ArgumentoLR2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "!".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				semantic.verificTipoVarNegative(tokens.peek(), bloco, tipoBloco, nomeBloco);
				tokenMap.add(this.call("<ArgumentoLR2_1>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && "true".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "false".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um delimitador booleano ou negado.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ArgumentoLR2_1>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.IDENTIFIER.equals(token.getType())) {
				this.id.add(tokens.peek());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "true".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "false".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um identificador de v�riavel ou operador booleano");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ArgumentoLR3>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ExpressaoAritmetica", tokens.peek())) {
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
				return tokenMap;
			} else if (TokenTypes.STRING.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e uma express�o aritm�tica ou uma string.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<OperadorRelacional>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "!=".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "==".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "<".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && ">".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "<=".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && ">=".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um operador relacional.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<OperadorLogico>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "||".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "&&".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um operador l�gico.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Print>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "print".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "(".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Print1>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia o parametro para funcao print.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a funcao print.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Print1>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (TokenTypes.STRING.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<AuxPrint>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Identificador", tokens.peek())) {
				tokenMap.add(this.call("<Identificador>", tokens).getTokenNode());
				tokenMap.add(this.call("<AuxPrint>", tokens).getTokenNode());
				return tokenMap;
			} else if (TokenTypes.NUMBER.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<AuxPrint>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um parametro v�lido para funcao print.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		/// Certo
		this.functions.put("<AuxPrint>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("PrintFim", tokens.peek())) {
				tokenMap.add(this.call("<PrintFim>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Print1>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua a declaracao de parametros para funcao print.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<PrintFim>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ")".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && ";".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a funcao print.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de parametros para funcao print.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Read>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "read".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "(".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					tokenMap.add(this.call("<Read1>", tokens).getTokenNode());
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao de parametros para funcao read.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a funcao read.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Read1>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("IdentificadorSemFuncao", tokens.peek())) {
				semantic.readVarExists(tokens.peek(), bloco);
				tokenMap.add(this.call("<IdentificadorSemFuncao>", tokens).getTokenNode());
				tokenMap.add(this.call("<AuxRead>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e um parametro v�lido para a funcao read.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<AuxRead>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ReadFim", tokens.peek())) {
				tokenMap.add(this.call("<ReadFim>", tokens).getTokenNode());
				return tokenMap;
			} else if (token != null && ",".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<Read1>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao continua a delara��o de parametro da funcao read.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ReadFim>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && ")".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && ";".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a funcao read.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza a declaracao de parametros para funcao read.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});
		
		// Certo
		this.functions.put("<Corpo>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "}".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (this.predict("Var", tokens.peek()) || this.follow.get("Var").contains(token.getLexeme()) || TokenTypes.IDENTIFIER.equals(tokens.peek().getType())) {
				global = false;
				tokenMap.add(this.call("<Var>", tokens).getTokenNode());
				tokenMap.add(this.call("<Corpo2>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && "}".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza o corpo de blocos e fun��es.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia ou finaliza o corpo de blocos e fun��es.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Vazio
		this.functions.put("<Corpo2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Comandos", tokens.peek())) {
				tokenMap.add(this.call("<Comandos>", tokens).getTokenNode());
				tokenMap.add(this.call("<Corpo2>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.follow.get("Corpo2").contains(token.getLexeme())) {
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao faz parte do corpo de fun��es.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return tokenMap;
		});

		// Certo
		this.functions.put("<Comandos>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("Condicional", tokens.peek())) {
				tokenMap.add(this.call("<Condicional>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Laco", tokens.peek())) {
				tokenMap.add(this.call("<Laco>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Read", tokens.peek())) {
				tokenMap.add(this.call("<Read>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("Print", tokens.peek())) {
				tokenMap.add(this.call("<Print>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("ComandosReturn", tokens.peek())) {
				tokenMap.add(this.call("<ComandosReturn>", tokens).getTokenNode());
				return tokenMap;
			} else if (this.predict("IdentificadorComandos", tokens.peek())) {
				tokenMap.add(this.call("<IdentificadorComandos>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e comando dentro de um bloco.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdentificadorComandos>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("IdentificadorSemFuncao", tokens.peek())) {
				nomeFuncao = tokens.peek().getLexeme();
				tokenMap.add(this.call("<IdentificadorSemFuncao>", tokens).getTokenNode());
				token = tokens.peek();
				tokenMap.add(this.call("<IdentificadorComandos2>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && ";".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza comandos dentro do corpo de funcao/bloco.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e comando dentro de um bloco.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdentificadorComandos2>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "(".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				tokenMap.add(this.call("<ListaParametros>", tokens).getTokenNode());
				token = tokens.peek();
				semantic.verificFuncProcDeclaration(tokens.peek(), nomeFuncao, bloco, nomeBloco, auxiliar.size(), auxiliar);
				nomeFuncao = "";
				auxiliar.clear();
				if (token != null && ")".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza uma lista de paraemtro na funcao/bloco.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (token != null && "=".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				tokenMap.add(this.call("<IdentificadorComandos2_1>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao atribui ou inicia uma lista de parametro na funcao/bloco.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<IdentificadorComandos2_1>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ExpressaoAritmetica", tokens.peek())) {
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
				return tokenMap;
			} else if (TokenTypes.STRING.equals(token.getType())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else if (token != null && "true".equals(token.getLexeme())
					|| token != null && "false".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e uma express�o aritim�tica, string ou boolean.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<ComandosReturn>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "return".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				semantic.verificTipoVarReturn(tokens.peek(), bloco, tipoBloco, nomeBloco);
				tokenMap.add(this.call("<CodigosRetornos>", tokens).getTokenNode());
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia um retorno.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<CodigosRetornos>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (this.predict("ExpressaoAritmetica", tokens.peek())) {
				tokenMap.add(this.call("<ExpressaoAritmetica>", tokens).getTokenNode());
				token = tokens.peek();
				if (token != null && ";".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					return tokenMap;
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza um retorno de uma funcao/bloco.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else if (token != null && ";".equals(token.getLexeme())) {
				tokenMap.add(new SynthaticNode(tokens.remove()));
				return tokenMap;
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao finaliza ou continua um retorno de uma funcao/bloco.");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

		// Certo
		this.functions.put("<Start>", tokens -> {
			SynthaticNode tokenMap = new SynthaticNode();
			Token token = tokens.peek();
			if (token != null && "start".equals(token.getLexeme())) {
				bloco = token.getLexeme();
				nomeBloco = "";
				semantic.startOnly(tokens.peek());
				tokenMap.add(new SynthaticNode(tokens.remove()));
				token = tokens.peek();
				if (token != null && "(".equals(token.getLexeme())) {
					tokenMap.add(new SynthaticNode(tokens.remove()));
					token = tokens.peek();
					if (token != null && ")".equals(token.getLexeme())) {
						tokenMap.add(new SynthaticNode(tokens.remove()));
						token = tokens.peek();
						if (token != null && "{".equals(token.getLexeme())) {
							tokenMap.add(new SynthaticNode(tokens.remove()));
							token = tokens.peek();
							tokenMap.add(this.call("<Corpo>", tokens).getTokenNode());
							return tokenMap;
						} else {
							int line = token.getLine() + 1;
							this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia o corpo do bloco start.");
							if (!tokens.isEmpty()) {
								tokens.remove();
							}
						}
					} else {
						int line = token.getLine() + 1;
						this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e faz parte da declaracao do bloco start.");
						if (!tokens.isEmpty()) {
							tokens.remove();
						}
					}
				} else {
					int line = token.getLine() + 1;
					this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao e faz parte da declaracao do bloco start.");
					if (!tokens.isEmpty()) {
						tokens.remove();
					}
				}
			} else {
				int line = token.getLine() + 1;
				this.errors.add("Linha: " + line + " | (" + token.getLexeme() + ") nao inicia a declaracao do bloco start");
				if (!tokens.isEmpty()) {
					tokens.remove();
				}
			}
			return null;
		});

	}

	public static SynthaticAnalyzer getInstance() {
		if (instance == null) {
			instance = new SynthaticAnalyzer();
		}
		return instance;
	}

	public SynthaticNode start(Queue<Token> queue) {
		return this.functions.get(FirstFollow.getInstance().StartSymbol).run(queue);
	}

	public void showDerivation(SynthaticNode node) {
		if (node != null) {
			if (node.getNodeList().isEmpty()) {
				System.out.println(node.getToken() != null ? node.getToken() : "Sucesso!");
			} else {
				for (SynthaticNode synthaticNode : node.getNodeList()) {
					this.showDerivation(synthaticNode);
				}
			}
		}
	}

	public List<String> getErros() {
		return errors;
	}
	
	public List<Token> getId() {
		return id;
	}
}