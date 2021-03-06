package util;

import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.TokenTypes;

public class FirstFollow {
	public final String CaseSensitive;
	public final String Name;
	public final String About;
	public final String Author;
	public final String Version;
	public final String StartSymbol;
	private HashMap<String, HashSet<String>> first;
	private HashMap<String, HashSet<String>> follow;
	private static FirstFollow instance;

	private FirstFollow(){
		this.CaseSensitive = "True";
		this.Name = "PBL Compiladores";
		this.About = "Grammar";
		this.Author = "Abel Galv�o, Caique Trindade, Emerson Souza, Felipe Damascena, Pedro Brand�o, Ramon Silva, Solenir Figueredo, Victor Souza";
		this.Version = "1.0";
		this.StartSymbol = "<Inicio>";
		this.first = new HashMap<>();

		
		this.first.put("Valor", new HashSet<>(Arrays.asList("String", "Boolean", "Id", "Numero")));
		this.first.put("ValorVetor", new HashSet<>(Arrays.asList("Id", "IntPos")));
		this.first.put("Tipo", new HashSet<>(Arrays.asList("string", "Id", "int", "real", "boolean")));
		this.first.put("Inicio", new HashSet<>(Arrays.asList("const", "typedef", "var", "function", "procedure", "start")));
		
		this.first.put("Condicional", new HashSet<>(Arrays.asList("if")));
		this.first.put("CondEnd", new HashSet<>(Arrays.asList("else", "")));
		this.first.put("Laco", new HashSet<>(Arrays.asList("while")));
		
		this.first.put("GeraFuncaoeProcedure", new HashSet<>(Arrays.asList("", "procedure", "function")));
		this.first.put("Funcao", new HashSet<>(Arrays.asList("function")));
		this.first.put("Procedimento", new HashSet<>(Arrays.asList("procedure")));
		this.first.put("Parametro", new HashSet<>(Arrays.asList("string", "int", "Id", "real", "boolean")));
		this.first.put("Para1", new HashSet<>(Arrays.asList(",", ")")));
		this.first.put("Para2", new HashSet<>(Arrays.asList("[", "")));
		this.first.put("Para3", new HashSet<>(Arrays.asList("[", "")));
		this.first.put("F2", new HashSet<>(Arrays.asList("{")));
		
		this.first.put("Const", new HashSet<>(Arrays.asList("const", "")));
		this.first.put("TipoConst", new HashSet<>(Arrays.asList("string", "int", "Id", "real", "boolean")));
		this.first.put("IdConst", new HashSet<>(Arrays.asList("Id")));
		this.first.put("Const2", new HashSet<>(Arrays.asList(",", ";")));
		this.first.put("Const3", new HashSet<>(Arrays.asList("string", "Id", "int", "real", "boolean", "}")));
		
		this.first.put("Struct", new HashSet<>(Arrays.asList("typedef", "")));
		this.first.put("Extends", new HashSet<>(Arrays.asList("{", "extends"))); 
		this.first.put("TipoStruct", new HashSet<>(Arrays.asList("string", "int", "Id", "real", "boolean")));
		this.first.put("IdStruct", new HashSet<>(Arrays.asList("Id")));
		this.first.put("Struct2", new HashSet<>(Arrays.asList(",", ";")));
		this.first.put("Struct3", new HashSet<>(Arrays.asList("string", "Id", "int", "real", "boolean", "}")));
		
		this.first.put("Var", new HashSet<>(Arrays.asList("", "var")));
		this.first.put("TipoVar", new HashSet<>(Arrays.asList("string", "int", "Id", "real", "boolean")));
		this.first.put("IdVar", new HashSet<>(Arrays.asList("Id")));
		this.first.put("Var2", new HashSet<>(Arrays.asList(";", "[", ",", "=")));
		this.first.put("Var3", new HashSet<>(Arrays.asList("string", "Id", "int", "real", "boolean", "}")));
		this.first.put("Var4", new HashSet<>(Arrays.asList(",", ";")));
		this.first.put("VetorDeclaracao", new HashSet<>(Arrays.asList("[")));
		this.first.put("Matriz", new HashSet<>(Arrays.asList("[", ",", ";")));
		
		
		this.first.put("ListaParametros", new HashSet<>(Arrays.asList("Numero", "String", "Id", "local", "global")));
		this.first.put("ContListaParametros", new HashSet<>(Arrays.asList(",", "")));
		this.first.put("ListaParametros2", new HashSet<>(Arrays.asList("Numero", "String", "Id", "global", "local")));
		
		
		this.first.put("Identificador", new HashSet<>(Arrays.asList("Id", "local", "global")));
		this.first.put("Identificador2", new HashSet<>(Arrays.asList("[", "", ".")));
		this.first.put("Identificador3", new HashSet<>(Arrays.asList(".", "[", "(")));
		this.first.put("Identificador4", new HashSet<>(Arrays.asList(".", "")));
		this.first.put("Vetor", new HashSet<>(Arrays.asList("[", ".")));
		this.first.put("Vetor2", new HashSet<>(Arrays.asList("[", "")));
		this.first.put("IndiceVetor", new HashSet<>(Arrays.asList("global", "local", "Id", "IntPos")));
		this.first.put("Escopo", new HashSet<>(Arrays.asList("global", "local")));
		
		this.first.put("IdentificadorSemFuncao", new HashSet<>(Arrays.asList("Id", "local", "global")));
		
		this.first.put("ExpressaoAritmetica", new HashSet<>(Arrays.asList("global", "local", "Numero", "Id", "(", "++", "--")));
		this.first.put("ExpressaoAritmetica2", new HashSet<>(Arrays.asList("/", "++", "*", "--")));
		this.first.put("E2", new HashSet<>(Arrays.asList("+", "", "-")));
		this.first.put("T", new HashSet<>(Arrays.asList("Numero", "(")));
		this.first.put("T2", new HashSet<>(Arrays.asList("*", "", "/")));
		this.first.put("F", new HashSet<>(Arrays.asList("Numero", "(")));
		this.first.put("IdentificadorAritmetico", new HashSet<>(Arrays.asList("Id", "local", "global")));
		this.first.put("IdentificadorAritmetico3", new HashSet<>(Arrays.asList(".", "[", "(")));
		
		this.first.put("ExpressaoLogicaRelacional", new HashSet<>(Arrays.asList("(", "global", "local", "Numero", "Id", "String", "true", "false", "!", "++", "--")));
		this.first.put("ExpressaoLR", new HashSet<>(Arrays.asList("(", "!", "local", "Numero", "global", "Id", "false", "--", "++", "String", "true")));
		this.first.put("ExpressaoLR2", new HashSet<>(Arrays.asList(">", "&&", "<", "||", "==", "!=", ">=", "<=")));
		this.first.put("ExpressaoLR3", new HashSet<>(Arrays.asList("||", "&&", "")));
		this.first.put("ArgumentoLR", new HashSet<>(Arrays.asList("(", "!", "local", "Numero", "global", "Id", "false", "--", "++", "String", "true")));
		this.first.put("ArgumentoLR2", new HashSet<>(Arrays.asList("true", "!", "false")));
		this.first.put("ArgumentoLR2_1", new HashSet<>(Arrays.asList("true", "Id", "false")));
		this.first.put("ArgumentoLR3", new HashSet<>(Arrays.asList("Numero", "local", "++", "(", "String", "--", "Id", "global")));	
		this.first.put("OperadorRelacional", new HashSet<>(Arrays.asList(">", "<", "==", "!=", ">=", "<=")));
		this.first.put("OperadorLogico", new HashSet<>(Arrays.asList("||", "&&")));
		
		this.first.put("Print", new HashSet<>(Arrays.asList("print")));
		this.first.put("Print1", new HashSet<>(Arrays.asList("Numero", "String", "Id", "global", "local")));
		this.first.put("AuxPrint", new HashSet<>(Arrays.asList(",", ")")));
		this.first.put("PrintFim", new HashSet<>(Arrays.asList(")")));
		
		this.first.put("Read", new HashSet<>(Arrays.asList("read")));
		this.first.put("Read1", new HashSet<>(Arrays.asList("Id", "global", "local")));
		this.first.put("AuxRead", new HashSet<>(Arrays.asList(",", ")")));
		this.first.put("ReadFim", new HashSet<>(Arrays.asList(")")));
		
		this.first.put("Corpo", new HashSet<>(Arrays.asList("print", "read", "local", "return", "if", "Id", "while", "global", "var", "}")));
		this.first.put("Corpo2", new HashSet<>(Arrays.asList("print", "read", "local", "", "return", "if", "while", "global", "Id")));
		this.first.put("Comandos", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "while", "Id", "global")));
		this.first.put("IdentificadorComandos", new HashSet<>(Arrays.asList("Id", "global", "local")));
		this.first.put("IdentificadorComandos2", new HashSet<>(Arrays.asList("(", "=")));
		this.first.put("IdentificadorComandos2_1", new HashSet<>(Arrays.asList("Boolean", "(", "String", "global", "Numero", "--", "Id", "local", "++")));
		this.first.put("ComandosReturn", new HashSet<>(Arrays.asList("return")));
		this.first.put("CodigosRetornos", new HashSet<>(Arrays.asList("Numero", "local", "++", "(", ";", "--", "Id", "global")));
		
		this.first.put("Start", new HashSet<>(Arrays.asList("start")));
		

		
		this.follow = new HashMap<>();
		
		this.follow.put("Valor", new HashSet<>(Arrays.asList("=", ";", "Id", ",", "[")));
		this.follow.put("ValorVetor", new HashSet<>(Arrays.asList("]")));
		this.follow.put("Tipo", new HashSet<>(Arrays.asList("function", "Id")));
		this.follow.put("Inicio", new HashSet<>(Arrays.asList("$")));
		
		this.follow.put("Condicional", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "Id", "while", "global")));
		this.follow.put("CondEnd", new HashSet<>(Arrays.asList("global", "print", "read", "if", "while", "Id", "return", "local")));
		this.follow.put("Laco", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "Id", "while", "global")));
		
		this.follow.put("GeraFuncaoeProcedure", new HashSet<>(Arrays.asList("start")));
		this.follow.put("Funcao", new HashSet<>(Arrays.asList("function", "procedure")));
		this.follow.put("Procedimento", new HashSet<>(Arrays.asList("function", "procedure")));
		this.follow.put("Parametro", new HashSet<>(Arrays.asList("procedure", "function")));
		this.follow.put("Para1", new HashSet<>(Arrays.asList("function", "procedure")));
		this.follow.put("Para2", new HashSet<>(Arrays.asList(",", ")")));		
		this.follow.put("Para3", new HashSet<>(Arrays.asList(",", ")")));
		this.follow.put("F2", new HashSet<>(Arrays.asList("var","}", "local", "print", "read", "if", "return", "Id", "while", "global")));
		
		this.follow.put("Const", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		this.follow.put("TipoConst", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		this.follow.put("IdConst", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		this.follow.put("Const2", new HashSet<>(Arrays.asList("typedef", "function", "const", "procedure", "var", "start")));
		this.follow.put("Const3", new HashSet<>(Arrays.asList("typedef", "function", "const", "procedure", "var", "start")));
		
		this.follow.put("Struct", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		this.follow.put("Extends", new HashSet<>(Arrays.asList("typedef", "function", "const", "procedure", "var", "start")));
		this.follow.put("TipoStruct", new HashSet<>(Arrays.asList("typedef", "function", "const", "procedure", "var", "start")));
		this.follow.put("IdStruct", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		this.follow.put("Struct2", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		this.follow.put("Struct3", new HashSet<>(Arrays.asList("typedef", "function", "const", "var", "procedure", "start")));
		
		this.follow.put("Var", new HashSet<>(Arrays.asList("print", "read", "const", "global", "local", "procedure", "function", "return", "if", "typedef", "while", "Id", "var", "start")));
		this.follow.put("TipoVar", new HashSet<>(Arrays.asList("print", "read", "const", "local", "global", "Id", "if", "return", "function", "typedef", "while", "procedure", "var", "}")));
		this.follow.put("IdVar", new HashSet<>(Arrays.asList("print", "read", "const", "global", "local", "procedure", "while", "return", "if", "typedef", "Id", "function", "var", "start")));
		this.follow.put("Var2", new HashSet<>(Arrays.asList("print", "read", "const", "local", "global", "function", "Id", "return", "if", "typedef", "while", "procedure", "var", "start")));
		this.follow.put("Var3", new HashSet<>(Arrays.asList("print", "read", "const", "global", "local", "procedure", "function", "return", "if", "typedef", "while", "Id", "var", "start")));
		this.follow.put("Var4", new HashSet<>(Arrays.asList("print", "read", "const", "global", "local", "procedure", "function", "return", "if", "typedef", "while", "Id", "var", "start")));
		this.follow.put("VetorDeclaracao", new HashSet<>(Arrays.asList(",", ";")));
		this.follow.put("Matriz", new HashSet<>(Arrays.asList(",", ";")));
		
		this.follow.put("ListaParametros", new HashSet<>(Arrays.asList("[", "=", ".", "(", ")")));
		this.follow.put("ContListaParametros", new HashSet<>(Arrays.asList("[", "(", ".", ")", "=")));
		this.follow.put("ListaParametros2", new HashSet<>(Arrays.asList(",", ")")));

		this.follow.put("Identificador", new HashSet<>(Arrays.asList("[", ".", ")", ",","]")));
		this.follow.put("Identificador2", new HashSet<>(Arrays.asList("*", ")", "(", ".", "++", "local", "Numero", "/", "--", "=", "[","]", "Id", ",", "global", ">", "<", "==", "!=", ">=", "<=", "+", "-")));
		this.follow.put("Identificador3", new HashSet<>(Arrays.asList(".", ",", ")", "[", "]")));
		this.follow.put("Identificador4", new HashSet<>(Arrays.asList("*", "global", "/", "--", "++", "Id", "local", "=", ")")));
		this.follow.put("Vetor", new HashSet<>(Arrays.asList("*", "local", "++", "Id", "--", "/", "global", ",", ")")));	
		this.follow.put("Vetor2", new HashSet<>(Arrays.asList("[", ".", "=", ")")));
		this.follow.put("IndiceVetor", new HashSet<>(Arrays.asList("[", ".")));	
		this.follow.put("Escopo", new HashSet<>(Arrays.asList("Id")));
		
		this.follow.put("IdentificadorSemFuncao", new HashSet<>(Arrays.asList(")", "(", "global", "Numero", "local", "=", "Id", "--", ",", "++")));

		this.follow.put("ExpressaoAritmetica", new HashSet<>(Arrays.asList("+", "*", "(", "String", "local", "!", "[", "!=", "/", "-", "||", "true", "&&", "++", "==", ">=", "--", "global", ">", ".", "<", "false", ";", "Id", "<=", "Numero")));
		this.follow.put("ExpressaoAritmetica2", new HashSet<>(Arrays.asList("(",")", ";", ">=", "<=", "Numero", "<", "!=", "==", ">")));
		this.follow.put("E2", new HashSet<>(Arrays.asList("(", ")", ";", ">=", "<=", ">", "<", "!=", "==", "Numero")));
		this.follow.put("T", new HashSet<>(Arrays.asList("+", "-")));
		this.follow.put("T2", new HashSet<>(Arrays.asList("+", "*", "(", ")","local", "global", "Numero", "Id", "[", "/", ".", "--", "++", "-",";")));
		this.follow.put("F", new HashSet<>(Arrays.asList("/", "*")));
		this.follow.put("IdentificadorAritmetico", new HashSet<>(Arrays.asList("+", "*", "(", "String", "local", "Id", "[", "!=", "||", "-", ".", "global", "--", ";", "==", ">=", "&&", "<=", "Numero", "true", "<", "false", "/", "++", ">", "!")));
		this.follow.put("IdentificadorAritmetico3", new HashSet<>(Arrays.asList("(",")", ";", ">=", "<=", ">", "<", "!=", "Numero", "==")));

		this.follow.put("ExpressaoLogicaRelacional", new HashSet<>(Arrays.asList("if", "while")));
		this.follow.put("ExpressaoLR", new HashSet<>(Arrays.asList("(", ")","!", "global", "--", "local", "Numero", "Id", "++", "if", "false", "while", "String", "true")));
		this.follow.put("ExpressaoLR2", new HashSet<>(Arrays.asList("(", ")","!", "true", "String", "global", "Numero", "while", "false", "if", "++", "--", "Id", "local")));
		this.follow.put("ExpressaoLR3", new HashSet<>(Arrays.asList("(", ")","!", "true", "String", "global", "Numero", "false", "++", "if", "Id", "while", "local", "--")));
		this.follow.put("ArgumentoLR", new HashSet<>(Arrays.asList("(", "String", "local", "!=", "true", "++", "--", "!", ">=", "global", "<=", "||", "==", "<", "false", "&&", "Id", ">", "Numero")));
		this.follow.put("ArgumentoLR2", new HashSet<>(Arrays.asList("(", "String", "local", "!=", "Numero", "Id", "--", "true", ">=", "global", "<=", "||", "!", "<", "false", "==", "++", "&&", ">")));
		this.follow.put("ArgumentoLR2_1", new HashSet<>(Arrays.asList("||", ">", "<", "!=", "==", ">=", "&&", "<=")));
		this.follow.put("ArgumentoLR3", new HashSet<>(Arrays.asList("(", "String", "local", "!=", "Numero", "Id", "&&", "true", ">=", "==", "<=", ">", "||", "<", "false", "global", "++", "!", "--")));
		this.follow.put("OperadorRelacional", new HashSet<>(Arrays.asList("(", "!", "local", "Numero", "true", "String", "false", "++", "Id", "--", "global")));
		this.follow.put("OperadorLogico", new HashSet<>(Arrays.asList("(", "true", "local", "Numero", "global", "--", "false", "Id", "++", "String", "!")));
		
		this.follow.put("Print", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "Id", "while", "global")));
		this.follow.put("Print1", new HashSet<>(Arrays.asList("global", "print", "read", "if", "while", "Id", "return", "local")));
		this.follow.put("AuxPrint", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "while", "Id", "global")));				
		this.follow.put("PrintFim", new HashSet<>(Arrays.asList()));
		
		this.follow.put("Read", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "Id", "while", "global")));
		this.follow.put("Read1", new HashSet<>(Arrays.asList("global", "print", "read", "if", "while", "Id", "return", "local")));
		this.follow.put("AuxRead", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "while", "Id", "global")));
		this.follow.put("ReadFim", new HashSet<>(Arrays.asList("global", "print", "read", "if", "Id", "while", "return", "local")));

		this.follow.put("Corpo", new HashSet<>(Arrays.asList("print", "read", "global","local", "return", "if", "Id", "while", "start", "else")));
		this.follow.put("Corpo2", new HashSet<>(Arrays.asList("print", "read", "local", "}", "return", "if", "global", "Id", "while", "var", "else")));
		this.follow.put("Comandos", new HashSet<>(Arrays.asList("Id", "print", "read", "if", "global", "while", "return", "local")));
		this.follow.put("IdentificadorComandos", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "Id", "while", "global")));
		this.follow.put("IdentificadorComandos2", new HashSet<>(Arrays.asList("Id", "local", "global")));
		this.follow.put("IdentificadorComandos2_1", new HashSet<>(Arrays.asList("Id", "global", "local")));
		this.follow.put("ComandosReturn", new HashSet<>(Arrays.asList("local", "print", "read", "if", "return", "Id", "while", "global")));
		this.follow.put("CodigosRetornos", new HashSet<>(Arrays.asList("global", "print", "read", "if", "while", "Id", "return", "local")));

		this.follow.put("Start", new HashSet<>(Arrays.asList("$")));	
}

	public HashMap<String, List<List<String>>> getProductions(){
		HashMap<String, List<List<String>>> productions = new HashMap<>();

		productions.put("<Valor>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("Numero")), new ArrayList<>(Arrays.asList("String")), new ArrayList<>(Arrays.asList("Boolean")), new ArrayList<>(Arrays.asList("Id")))));
		productions.put("<ValorVetor>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("IntPos")), new ArrayList<>(Arrays.asList("Id")))));
		productions.put("<Tipo>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'int'")), new ArrayList<>(Arrays.asList("'boolean'")), new ArrayList<>(Arrays.asList("'string'")), new ArrayList<>(Arrays.asList("'real'")), new ArrayList<>(Arrays.asList("Id")))));
		productions.put("<Inicio>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Const>", "<Struct>", "<Var>", "<GeraFuncaoeProcedure>", "<Start>")))));

		productions.put("<Condicional>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'if'", "'('", "<ExpressaoLogicaRelacional>", "')'", "'then'", "'{'", "<Corpo>", "<CondEnd>")))));
		productions.put("<CondEnd>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'else'", "'{'", "<Corpo>")), new ArrayList<>(Arrays.asList("")))));		
		productions.put("<Laco>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'while'", "'('", "<ExpressaoLogicaRelacional>", "')'", "'{'", "<Corpo>")))));

		productions.put("<GeraFuncaoeProcedure>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Funcao>", "<GeraFuncaoeProcedure>")), new ArrayList<>(Arrays.asList("<Procedimento>", "<GeraFuncaoeProcedure>")), new ArrayList<>(Arrays.asList("")))));
		
		productions.put("<Funcao>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'function'", "<Tipo>", "Id", "'('", "<Parametro>")))));
		productions.put("<Procedimento>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'procedure'", "Id", "'('", "<Parametro>")))));
		productions.put("<Parametro>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Tipo>", "Id", "<Para2>", "<Para1>")))));
		productions.put("<Para1>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<Parametro>")), new ArrayList<>(Arrays.asList("')'", "<F2>")))));
		productions.put("<Para2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'['", "']'", "<Para3>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<Para3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'['", "']'")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<F2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'{'", "<Corpo>")))));
		
		productions.put("<Const>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'const'", "' { '", "<TipoConst>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<TipoConst>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Tipo>", "<IdConst>")))));
		productions.put("<IdConst>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("Id", "<Valor>", "<Const2>")))));
		productions.put("<Const2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<IdConst>")), new ArrayList<>(Arrays.asList("' ; '", "<Const3>")))));
		productions.put("<Const3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'}'")), new ArrayList<>(Arrays.asList("<TipoConst>")))));

		productions.put("<Struct>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'typedef'", "'struct'", "Id", "<Extends>", "<TipoStruct>", "<Struct>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<Extends>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'extends'", "Id", "'{'")), new ArrayList<>(Arrays.asList("'{'")))));
		productions.put("<TipoStruct>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Tipo>", "<IdStruct>")))));
		productions.put("<IdStruct>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("Id", "<Struct2>")))));
		productions.put("<Struct2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<IdStruct>")), new ArrayList<>(Arrays.asList("';'", "<Struct3>")))));
		productions.put("<Struct3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'}'")), new ArrayList<>(Arrays.asList("<TipoStruct>")))));
		
		productions.put("<Var>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'var'", "'{'", "<TipoVar>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<TipoVar>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Tipo>", "<IdVar>")))));
		productions.put("<IdVar>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("Id", "<Var2>")))));
		productions.put("<Var2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<IdVar>")), new ArrayList<>(Arrays.asList("';'", "<Var3>")), new ArrayList<>(Arrays.asList("'='", "<Valor>", "<Var4>")), new ArrayList<>(Arrays.asList("<VetorDeclaracao>", "<Var4>")))));
		productions.put("<Var3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'}'")), new ArrayList<>(Arrays.asList("<TipoVar>")))));
		productions.put("<Var4>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<IdVar>")), new ArrayList<>(Arrays.asList("';'", "<Var3>")))));
		productions.put("<VetorDeclaracao>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'['", "<ValorVetor>", "']'", "<Matriz>")))));
		productions.put("<Matriz>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'['", "<ValorVetor>", "']'", "<Var4>")), new ArrayList<>(Arrays.asList("<Var4>")))));
		
		productions.put("<ListaParametros>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<ListaParametros2>", "<ContListaParametros>")))));
		productions.put("<ContListaParametros>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<ListaParametros>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<ListaParametros2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Identificador>")), new ArrayList<>(Arrays.asList("Numero")), new ArrayList<>(Arrays.asList("String")))));
		
		productions.put("<Identificador>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Escopo>", "Id", "<Identificador2>")), new ArrayList<>(Arrays.asList("Id", "<Identificador3>")))));
		productions.put("<Identificador2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'.'", "Id", "<Vetor>")), new ArrayList<>(Arrays.asList("<Vetor>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<Identificador3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Identificador2>")), new ArrayList<>(Arrays.asList("'('", "<ListaParametros>", "')'")))));
		productions.put("<Identificador4>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'.'", "Id", "<Vetor>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<Vetor>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'['", "<IndiceVetor>", "']'", "<Vetor2>", "<Identificador4>")), new ArrayList<>(Arrays.asList("<Identificador4>")))));
		productions.put("<Vetor2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'['", "<IndiceVetor>", "']'")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<IndiceVetor>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("IntPos")), new ArrayList<>(Arrays.asList("<Identificador>")))));
		productions.put("<Escopo>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'local'", "'.'")), new ArrayList<>(Arrays.asList("'global'", "'.'")))));
		
		productions.put("<IdentificadorSemFuncao>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Escopo>", "Id", "<Identificador2>")), new ArrayList<>(Arrays.asList("Id", "<Identificador2>")))));
		
		productions.put("<ExpressaoAritmetica>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<T>", "<E2>")), new ArrayList<>(Arrays.asList("<IdentificadorAritmetico>")), new ArrayList<>(Arrays.asList("'++'", "<IdentificadorSemFuncao>", "<T2>", "<E2>")), new ArrayList<>(Arrays.asList("'--'", "<IdentificadorSemFuncao>", "<T2>", "<E2>")))));
		productions.put("<ExpressaoAritmetica2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'++'", "<T2>", "<E2>")), new ArrayList<>(Arrays.asList("'--'", "<T2>", "<E2>")), new ArrayList<>(Arrays.asList("<T2>", "<E2>")))));
		productions.put("<E2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'+'", "<ExpressaoAritmetica>")), new ArrayList<>(Arrays.asList("'-'", "<ExpressaoAritmetica>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<T>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<F>", "<T2>")))));
		productions.put("<T2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'*'", "<ExpressaoAritmetica>")), new ArrayList<>(Arrays.asList("'/'", "<ExpressaoAritmetica>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<F>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'('", "<ExpressaoAritmetica>", "')'")), new ArrayList<>(Arrays.asList("Numero")))));
		productions.put("<IdentificadorAritmetico>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Escopo>", "Id", "<Identificador2>", "<ExpressaoAritmetica2>")), new ArrayList<>(Arrays.asList("Id", "<IdentificadorAritmetico3>")))));
		productions.put("<IdentificadorAritmetico3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Identificador2>", "<ExpressaoAritmetica2>")), new ArrayList<>(Arrays.asList("'('", "<ListaParametros>", "')'", "<T2>", "<E2>")))));
				
		productions.put("<ExpressaoLogicaRelacional>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<ExpressaoLR>")), new ArrayList<>(Arrays.asList("'('", "<ExpressaoLR>", "')'", "<ExpressaoLR3>")))));
		productions.put("<ExpressaoLR>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<ArgumentoLR2>", "<ExpressaoLR2>")), new ArrayList<>(Arrays.asList("<ArgumentoLR3>", "<OperadorRelacional>", "<ArgumentoLR>", "<ExpressaoLR3>")))));
		productions.put("<ExpressaoLR2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<OperadorRelacional>", "<ArgumentoLR>", "<ExpressaoLR3>")), new ArrayList<>(Arrays.asList("<ExpressaoLR3>")))));
		productions.put("<ExpressaoLR3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<OperadorLogico>", "<ExpressaoLogicaRelacional>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<ArgumentoLR>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<ArgumentoLR2>")), new ArrayList<>(Arrays.asList("<ArgumentoLR3>")))));
		productions.put("<ArgumentoLR2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'!'", "<ArgumentoLR2_1>")), new ArrayList<>(Arrays.asList("true")), new ArrayList<>(Arrays.asList("false")))));
		productions.put("<ArgumentoLR2_1>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("Id")), new ArrayList<>(Arrays.asList("true")), new ArrayList<>(Arrays.asList("false")))));
		productions.put("<ArgumentoLR3>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("String")), new ArrayList<>(Arrays.asList("<ExpressaoAritmetica>")))));
		productions.put("<OperadorRelacional>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'!='")), new ArrayList<>(Arrays.asList("'=='")), new ArrayList<>(Arrays.asList("'<'")), new ArrayList<>(Arrays.asList("'>'")), new ArrayList<>(Arrays.asList("'<='")), new ArrayList<>(Arrays.asList("'>='")))));
		productions.put("<OperadorLogico>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'&&'")), new ArrayList<>(Arrays.asList("'||'")))));		
		
		productions.put("<Print>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'print'", "'('", "<Print1>")))));
		productions.put("<Print1>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("String", "<AuxPrint>")), new ArrayList<>(Arrays.asList("<Identificador>", "<AuxPrint>")), new ArrayList<>(Arrays.asList("Numero", "<AuxPrint>")))));
		productions.put("<AuxPrint>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<Print1>")), new ArrayList<>(Arrays.asList("<PrintFim>")))));
		productions.put("<PrintFim>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("')'", "';'")))));
		
		productions.put("<Read>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'read'", "'('", "<Read1>")))));
		productions.put("<Read1>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<IdentificadorSemFuncao>", "<AuxRead>")))));
		productions.put("<AuxRead>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("','", "<Read1>")), new ArrayList<>(Arrays.asList("<ReadFim>")))));
		productions.put("<ReadFim>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("')'", "';'")))));
		
		productions.put("<Corpo>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Var>", "<Corpo2>", "'}'")), new ArrayList<>(Arrays.asList("'}'")))));
		productions.put("<Corpo2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Comandos>", "<Corpo2>")), new ArrayList<>(Arrays.asList("")))));
		productions.put("<Comandos>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<Condicional>")), new ArrayList<>(Arrays.asList("<Laco>")), new ArrayList<>(Arrays.asList("<Read>")), new ArrayList<>(Arrays.asList("<Print>")), new ArrayList<>(Arrays.asList("<ComandosReturn>")), new ArrayList<>(Arrays.asList("<IdentificadorComandos>")))));
		productions.put("<IdentificadorComandos>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<IdentificadorSemFuncao>", "<IdentificadorComandos2>", "';'")))));
		productions.put("<IdentificadorComandos2>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'='", "<IdentificadorComandos2_1>")), new ArrayList<>(Arrays.asList("'('", "<ListaParametros>", "')'")))));
		productions.put("<IdentificadorComandos2_1>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("<ExpressaoAritmetica>")), new ArrayList<>(Arrays.asList("String")), new ArrayList<>(Arrays.asList("Boolean")))));
		productions.put("<ComandosReturn>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'return'", "<CodigosRetornos>")))));
		productions.put("<CodigosRetornos>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("';'")), new ArrayList<>(Arrays.asList("<ExpressaoAritmetica>", "';'")))));
		
		productions.put("<Start>", new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList("'start'", "'('", "')'", "'{'", "<Corpo>", "'}'")))));
		
		return productions;
	}

	public HashMap<String, HashSet<String>> getFirst(){
		return this.first;
	}

	public HashMap<String, HashSet<String>> getFollow(){
		return this.follow;
	}

	public static FirstFollow getInstance(){
		if(instance == null){
			instance = new FirstFollow();
		}
		return instance;
	}
}
