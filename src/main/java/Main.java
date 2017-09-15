import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.InputMismatchException;

public class Main {
	PrintStream out;
	HashMap<Identifier, SetInterface> variables;


	Main() {
		out = new PrintStream(System.out);
		variables = new HashMap<Identifier, SetInterface>();
	}


	void parseStatement(String statement) throws APException {

	}


	void  parsePrintStatement(){

	}


	void parseEpression(){

	}


	void parseTerm(){

	}


	void parseFactor(){

	}


	void parseIdentifier(){

	}


	void parseComplexFactor(){

	}


	void parseSet(){

	}


	void parseNaturalNumber(){

	}


	void parsePositiveNumber(){

	}


	private boolean nextCharIsDigit(Scanner in) {
 		return in.hasNext("[0-9]");
	}


	private boolean nextCharIsLetter(Scanner in) {
 		return in.hasNext("[a-zA-Z]");
	}


	private boolean nextCharIs(Scanner in, char c) {
 		return in.hasNext(Pattern.quote(c + ""));
	}


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
