import java.io.File;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.InputMismatchException;

public class Main {
	/*
	*
	* TODO delete this message after implementing the whole interpreter
	*
	* 						!!!!!IMPORTANT!!!!
	*
	* 	Before implementing anything read the boolean methods like charIsLetter()
	*	Using these will make the  implementation much cleaner ;)
	*
	*
	* */
	PrintStream out;
	HashMap<Identifier, SetInterface> variables;


	Main() {
		out = new PrintStream(System.out);
		variables = new HashMap<Identifier, SetInterface>();
	}


	void parseStatement(String input) throws APException {
		Scanner statmentScanner = new Scanner (input);
		while(statmentScanner.hasNextLine()){
			String statement = statmentScanner.nextLine();
			if(nextCharEqualsInput(statmentScanner, '\\')){
				//skip line because its a comment
			}else if(nextCharEqualsInput(statmentScanner, '?')){
				parsePrintStatement(statement);
			}else if(nextCharIsLetter(statmentScanner)){
				parseAssigntment(statement);
			}else{
				throw new APException("Invalid statement found, please alter file\n");
			}
		}
		//while there are statements
		//If(comment) skip line
		//else if(printStatement) parsePrintStatement;
		//else if(assignment) do parseAssignment();
		//else EXCEPTION
	}


	void parseAssigntment(String statement){
		Scanner assignment = new Scanner(statement);
		assignment.useDelimiter("=");
		parseIdentifier(assignment.next());
		parseExpression(assignment.next());
	}
	
	//TODO Change return type
	void  parsePrintStatement(String statement){
		Identifier id = parseIdentifier(statement);
		statement = statement.substring(1);
		SetInterface<BigInteger> result = parseExpression(statement);
		variables.put(id, result);
		out.println(statement);

		//check if set is valid and then print it by creating a Set<BigInteger>
	}


	SetInterface<BigInteger> parseExpression(String expression){
		Scanner termChain = new Scanner(expression);
		termChain.useDelimiter("\\+|\\-|\\|");
		while(termChain.hasNext()){
			parseTerm(termChain.next());
		}

		//TODO logic on terms
		return null;
	}


	void parseTerm(String term) {
		Scanner factorChain = new Scanner(term);
		factorChain.useDelimiter("\\*");
		while(factorChain.hasNext()){
			parseFactor(factorChain.next());
		}

		//TODO logic on terms
		return;
	}


	void parseFactor(String factor) throws APException{
		Scanner factorScanner = new Scanner(factor);
		if(nextCharIsLetter(factorScanner)){
			parseIdentifier(factorScanner.next());
		}else if(nextCharEqualsInput(factorScanner, '(')){
			parseComplexFactor(factorScanner.next());
		}else if(nextCharEqualsInput(factorScanner, '{')){
			parseSet(factorScanner.next());
		}else{
			throw new APException("Invalid factor make sure all factors start with a letter, a \"(\" or a \"{\" \n");
		}
	}


	Identifier parseIdentifier(String statement) throws  APException{
		Identifier identifier = new Identifier();
		Scanner identifierScanner = new Scanner(statement);
		while(identifierScanner.hasNext()){
			String character = identifierScanner.next();
			boolean validChar = identifier.readValidChar(identifierScanner.next());
			if (!validChar){
				throw new APException("Invalid Identifier, Identifiers should start with a letter and only contain letters and numbers");
			}
			identifier.addChar(character);
		}
		return identifier;
	}


	void parseComplexFactor(String complexFactor){
		//parseExpression()
		//check if there is ')' at the end of expression else throw EXCEPTION
	}


	void parseSet(String set){
		//set is empty just return
		//else
		//check if first char is number and parseNaturalNumber if not throw EXCEPTION
		//usedelimeter(',')
		//while there are other natural numbers
		//if first char is number parseNaturalNumber if not throw EXCEPTION
	}


	void parseNaturalNumber(){
		//if charIsZero(char) and no chars after it return
		//else parsePositiveNumber()
	}


	void parsePositiveNumber(){
		//if charIsZero(char0) throw EXCEPTION
		//while the number still has characters to be read
		//if charIsNumber return else throw EXCEPTION
	}


	private boolean nextCharIsNonZeroNumber(Scanner in) { return in.hasNext("[1-9]"); }


	private boolean nextCharIsNumber(Scanner in) {
 		return in.hasNext("[0-9]");
	}


	private boolean nextCharIsZero(Scanner in) { return in.hasNext("0");}


	private boolean nextCharIsLetter(Scanner in) {
 		return in.hasNext("[a-zA-Z]");
	}


	private boolean nextCharEqualsInput(Scanner in, char c) { return in.hasNext(Pattern.quote(c + "")); }


	private boolean nextCharIsAdditiveOperator(Scanner in) { return  in.hasNext("\\+ | \\- | \\|"); }


	private boolean nextCharIsMultiplicativeOperator(Scanner in) { return  in.hasNext("\\*"); }

    private void start() {
        Scanner in = new Scanner(System.in);

        while(in.hasNextLine()) {
			String statement = in.nextLine();

			try {
				parseStatement(statement);
			}
			catch (APException e) {
				out.println(e);
			}
		}
        in.close();
    }


    public static void main(String[] argv) {
        new Main().start();
    }
}
