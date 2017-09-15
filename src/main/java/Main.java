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


	void parseStatement(String statement) throws APException {
		Scanner statmentScanner = new Scanner (statement);
		//while there are statements
		//If(comment) skip line
		//else if(printStatemnt) parsePrintStatement;
		//else if(assignment) do parseAssignment();
		//else EXCEPTION
	}


	void parseAssigntment(){
		//check if starts with valid Identifier
		//check if identifer is followed by "="
		//check if expresion is valid with parseExpression()
	}

	void  parsePrintStatement(){
		//skip first character(already checked that it is ? in parseStatement
		//check if set is valid and then print it by creating a Set<BigInteger>
	}


	void parseEpression(){
		//use additive operator as delimeter to get individual terms
		//parse individual terms with parseTerm()
		//apply logic to terms if terms are valid
	}


	void parseTerm(){
		//use multiplicative operator as delimeter to get individual factors
		//parse individual factors with parseFactor()
		//apply logic to factors if factors are valid
	}


	void parseFactor(){
		//if (Identifier) do parseIdentifier()
		//else if (complexFactor) do parseComplexFactor()
		//else if (set) do parseSet()
		//else EXCEPTION
	}


	void parseIdentifier(){
		//check if first character is letter else throw EXCEPTION
		//while there are characters
		//check if character is number or letter if not throw exception
		//return if entire identifier is correct
	}


	void parseComplexFactor(){
		//parseExpression()
		//check if there is ')' at the end of expression else throw EXCEPTION
	}


	void parseSet(){
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


	private boolean nextCharIsAdditiveOperator(Scanner in) { return  in.hasNext("\\+ | \\- | \\|");}


	private boolean nextCharIsMultiplicativeOperator(Scanner in) {}

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
