import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
	/*
	*
	* TODO delete this message after implementing the whole interpreter
	*
	* 						!!!!!IMPORTANT!!!!
	*
	* 	Before implementing anything read the boolean methods like nextCharIsLetter()
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
		statement = statement.replaceAll(" ","");
		if(nextCharEqualsInput(statement.charAt(0), '/')){
			//skip line because its a comment
		}
		else if(nextCharEqualsInput(statement.charAt(0), '?')){
			parsePrintStatement(statement);
		}else if(nextCharIsLetter(statement.charAt(0))){
			parseAssigntment(statement);
		}else{
			System.out.printf("The input is: %s\n", statement);
			//throw new APException("Invalid statement found, please alter file\n");
		}
	}


	void parseAssigntment(String statement)throws  APException{
		Scanner assignment = new Scanner(statement);
		assignment.nextCharIs();
		assignment.useDelimiter("=");
		Identifier id = parseIdentifier(assignment.next());
		SetInterface<BigInteger> value = parseExpression(assignment.next());
		variables.put(id, value);
	}
	

	void parsePrintStatement(String statement) throws  APException{
		statement = statement.substring(1);
		SetInterface<BigInteger> result = parseExpression(statement);
		out.println(result.toString());
	}


	SetInterface<BigInteger> parseExpression(String expressionString) throws  APException{
		Scanner termChain = new Scanner(expressionString);
		termChain.useDelimiter("\\+|\\-|\\|");
		int numberOfParsedTerms = 0;
		SetInterface<BigInteger> expression = null;
		String operatorString = computeOperatorString(expressionString);
		while(termChain.hasNext()){
			if(numberOfParsedTerms == 0){
				expression = parseFactor(termChain.next());
				numberOfParsedTerms++;
			}else{
				if(operatorString.charAt(0) == '+'){
					SetInterface<BigInteger> termToUnion = parseFactor(termChain.next());
					expression.union(termToUnion);
					operatorString.substring(1);
					numberOfParsedTerms++;
				}else if(operatorString.charAt(0) == '-'){
					SetInterface<BigInteger> termToComplement = parseFactor(termChain.next());
					expression.complement(termToComplement);
					operatorString.substring(1);
					numberOfParsedTerms++;
				}else if(operatorString.charAt(0) == '|'){
					SetInterface<BigInteger> termToSymDifference = parseFactor(termChain.next());
					expression.symDifference(termToSymDifference);
					operatorString.substring(1);
					numberOfParsedTerms++;
				}
			}
		}
		return expression;
	}


	private String computeOperatorString(String expressionString){
		Scanner termsAndOperators = new Scanner(expressionString);
		StringBuffer operatorStringBuffer =  new StringBuffer();
		while(termsAndOperators.hasNext()){
			String operator = termsAndOperators.next();
			if(operator == "+" || operator == "-" || operator == "|"){
				operatorStringBuffer.append(operator);
			}
		}
		return operatorStringBuffer.toString();
	}


	SetInterface<BigInteger> parseTerm(String termString) throws APException{
		Scanner factorChain = new Scanner(termString);
		factorChain.useDelimiter("\\*");
		int numberOfParsedFactors = 0;
		SetInterface<BigInteger> term = null;
		while(factorChain.hasNext()){
			if(numberOfParsedFactors == 0){
				term = parseFactor(factorChain.next());
				numberOfParsedFactors++;
			}else{
				SetInterface<BigInteger> factorToIntersect = parseFactor(factorChain.next());
				term.intersection(factorToIntersect);
				numberOfParsedFactors++;
			}
		}
		return term;
	}


	SetInterface<BigInteger> parseFactor(String factor) throws APException{
		if(factor.charAt(0) == ' '){
			factor = factor.substring(1);
		}
		Scanner factorScanner = new Scanner(factor);
		SetInterface<BigInteger> set;
		if(nextCharIsLetter(factor.charAt(0))){
			Identifier id = parseIdentifier(factorScanner.next());
			if (variables.containsKey(id)) {
				set = variables.get(id);
			}else{
				throw new APException("Invalid set, set never initialized\n");
			}
		}else if(nextCharEqualsInput(factor.charAt(0), '(')){
			set = parseComplexFactor(factorScanner.next());
		}else if(nextCharEqualsInput(factor.charAt(0), '{')){
			set = parseSet(factorScanner.next());
		}else{
			throw new APException("Invalid factor make sure all factors start with a \"letter\", a \"(\" or a \"{\" \n");
		}
		return set;
	}


	Identifier parseIdentifier(String statement) throws  APException{
		Identifier identifier = new Identifier();
		Scanner identifierScanner = new Scanner(statement);
		while(identifierScanner.hasNext()){
			String character = identifierScanner.next();

			boolean validChar = identifier.readValidChar(character);
			if (!validChar){
				throw new APException("Invalid Identifier, Identifiers should start with a letter and only contain letters and numbers\n");
			}
			identifier.addChar(character);
		}
		return identifier;
	}


	SetInterface<BigInteger> parseComplexFactor(String complexFactor) throws  APException{
		SetInterface<BigInteger> set;
		if(complexFactor.charAt(complexFactor.length() - 1) == ')'){
			String expression = complexFactor.substring(1, complexFactor.length() -1);
			set = parseExpression(expression);
			return set;
		}else{
			throw new APException(" Invalid complex factor, complex factor never closed\n");
		}
	}


	SetInterface<BigInteger> parseSet(String set) throws APException{
		SetInterface<BigInteger> parsedSet = new Set<>();
		if(set.charAt(set.length() - 1) == '}'){
			//TODO the { should be removed here but oddly it is not....
			String rowOfNaturalNumbers = set.substring(1, set.length() -1);
			//String rowOfNaturalNumbers = set;
			//returns empty set
			if(rowOfNaturalNumbers.length() == 0){
				return parsedSet;
			}else{
				Scanner setScanner = new Scanner(rowOfNaturalNumbers);
				setScanner.useDelimiter(",");
				//fills set and then returns it
				do {
					BigInteger naturalNumber = parseNaturalNumber(setScanner.next());
					parsedSet.add(naturalNumber);
				}while(setScanner.hasNext());
				return parsedSet;
			}
		}else{
			throw new APException("Invalid set, set never closed\n");
		}
	}


	BigInteger parseNaturalNumber(String number) throws  APException{
		BigInteger naturalNumber;
		if(nextCharIsZero(number.charAt(0)) && number.length() == 1){
			naturalNumber = BigInteger.ZERO;
		}else{
			naturalNumber = parsePositiveNumber(number);
		}

		return naturalNumber;
	}


	BigInteger parsePositiveNumber(String nonZeroNumber) throws  APException{
		if(nonZeroNumber.charAt(0) == 0){
			throw new APException("Invalid number in set, set contains non number starting with 0\n");
		}
		//Scanner positiveNumberScanner = new Scanner(nonZeroNumber);
		for(int i = 1; i < nonZeroNumber.length(); i++){
			if(!nextCharIsNumber(nonZeroNumber.charAt(i))){
				throw new APException("Invalid number in set, set contains non number element\n");
			}
		}
		if(true){
			//throw new APException(nonZeroNumber + " NONZERONUMBER ");
		}

		BigInteger positiveNumber = new BigInteger(nonZeroNumber);
		return positiveNumber;
	}



	private boolean nextCharIsNonZeroNumber(Scanner in) {
		in.useDelimiter("");
		return in.hasNext("[1-9]");
	}


	private boolean nextCharIsNumber(Scanner in) {
		in.useDelimiter("");
		return in.hasNext("[0-9]");
	}


	private boolean nextCharIsZero(Scanner in) {
		in.useDelimiter("");
		return in.hasNext("0");
	}


	private boolean nextCharIsLetter(Scanner in) {
		in.useDelimiter("");
		return in.hasNext("[a-zA-Z]");
	}


	private boolean nextCharEqualsInput(Scanner in, char c) {
		in.useDelimiter("");
		return in.hasNext(Pattern.quote(c + ""));
	}


	private boolean charIsAdditiveOperator(Scanner in) {
		in.useDelimiter("");
		return  in.hasNext("\\+ | \\- | \\|");
	}


	private boolean charIsMultiplicativeOperator(Scanner in) {
		in.useDelimiter("");
		return  in.hasNext("\\*");
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
