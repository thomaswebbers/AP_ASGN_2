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
		statement = statement.replaceAll(" ","");
		if(charEqualsInput(statement.charAt(0), '/')){
			//skip line because its a comment
		}
		else if(charEqualsInput(statement.charAt(0), '?')){
			parsePrintStatement(statement);
		}else if(charIsLetter(statement.charAt(0))){
			parseAssigntment(statement);
		}else{
			System.out.printf("The input is: %s\n", statement);
			//throw new APException("Invalid statement found, please alter file\n");
		}
	}


	void parseAssigntment(String statement)throws  APException{
		Scanner assignment = new Scanner(statement);
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
		if(charIsLetter(factor.charAt(0))){
			Identifier id = parseIdentifier(factorScanner.next());
			if (variables.containsKey(id)) {
				set = variables.get(id);
			}else{
				throw new APException("Invalid set, set never initialized\n");
			}
		}else if(charEqualsInput(factor.charAt(0), '(')){
			set = parseComplexFactor(factorScanner.next());
		}else if(charEqualsInput(factor.charAt(0), '{')){
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
		if(charIsZero(number.charAt(0)) && number.length() == 1){
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
			if(!charIsNumber(nonZeroNumber.charAt(i))){
				throw new APException("Invalid number in set, set contains non number element\n");
			}
		}
		if(true){
			//throw new APException(nonZeroNumber + " NONZERONUMBER ");
		}

		BigInteger positiveNumber = new BigInteger(nonZeroNumber);
		return positiveNumber;
	}


	private boolean charIsNonZeroNumber(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("[1-9]");
	}


	private boolean charIsNumber(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("[0-9]");
	}


	private boolean charIsZero(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("[0]");
	}


	private boolean charIsLetter(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("[a-zA-Z]");
	}


	private boolean charEqualsInput(char character, char comparedChar) {
		//System.out.println((character == comparedChar) + " BOOLEAN " + comparedChar + " HO ");
		return character == comparedChar;
	}


	private boolean charIsAdditiveOperator(char character) {
		String characterString = String.valueOf(character);
		return  characterString.matches("\\+ | \\- | \\|");
	}


	private boolean charIsMultiplicativeOperator(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("\\*");
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
