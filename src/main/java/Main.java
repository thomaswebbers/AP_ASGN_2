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
		//Removes all the spaces
		statement = statement.replaceAll(" ", "");
		Scanner statementScanner = new Scanner(statement);
		if (nextCharEqualsInput(statementScanner, '/')) {
			//skip line because its a comment
		} else if (nextCharEqualsInput(statementScanner, '?')) {
			parsePrintStatement(statement);
		} else if (nextCharIsLetter(statementScanner)) {
			parseAssigntment(statement);
		} else {
			System.out.printf("The input is: %s\n", statement);
			//throw new APException("Invalid statement found, please alter file\n");
		}
	}


	void parseAssigntment(String statement) throws APException {
		Scanner assignment = new Scanner(statement);
		assignment.useDelimiter("=");
		Identifier id = parseIdentifier(assignment.next());
		SetInterface<BigInteger> value = parseExpression(assignment.next());
		variables.put(id, value);
	}


	void parsePrintStatement(String statement) throws APException {
		statement = statement.substring(1);
		SetInterface<BigInteger> result = parseExpression(statement);
		out.println(result.toString());
	}

	/*
	//TODO APEXCEPTION
	SetInterface<BigInteger> parseExpression(String expressionString) throws  APException{
		System.out.println(expressionString + " EXPRESSION");
		Scanner termChain = new Scanner(expressionString);
		termChain.useDelimiter("\\+|\\-|\\|");
		int numberOfParsedTerms = 0;
		SetInterface<BigInteger> expression = null;
		String operatorString = computeOperatorString(expressionString);
		//System.out.println("Operatorstring contains?" + operatorString);
		while(termChain.hasNext()){
			if(numberOfParsedTerms == 0){
				expression = parseTerm(termChain.next());
				numberOfParsedTerms++;
			}else{
				if(operatorString.charAt(0) == '+'){
					SetInterface<BigInteger> termToUnion = parseTerm(termChain.next());
					expression = expression.union(termToUnion);
					System.out.println(expression.toString() + " expression");
					operatorString.substring(1);
					numberOfParsedTerms++;
				}else if(operatorString.charAt(0) == '-'){
					SetInterface<BigInteger> termToComplement = parseTerm(termChain.next());
					expression = expression.complement(termToComplement);
					operatorString.substring(1);
					numberOfParsedTerms++;
				}else if(operatorString.charAt(0) == '|'){
					SetInterface<BigInteger> termToSymDifference = parseTerm(termChain.next());
					expression = expression.symDifference(termToSymDifference);
					operatorString.substring(1);
					numberOfParsedTerms++;
				}
			}
		}
		return expression;
	}
	*/

	//TODO APEXCEPTION
	SetInterface<BigInteger> parseExpression(String expressionString) throws APException {
		int numberOfParsedTerms = 0;
		SetInterface<BigInteger> expression = null;
		String operatorString = computeOperatorString(expressionString);
		String found = null;
		int count = 0;
		while(expressionString.length() > 0) {
			if (expressionString.charAt(0) == '(') {
				for (int i = 0; i < expressionString.length(); i++) {
					if (expressionString.charAt(i) == '(') {
						count++;
					} else if (expressionString.charAt(i) == ')') {
						count--;
					}
					if (count == 0) {
						found = expressionString.substring(0, i-1);
						expressionString = expressionString.substring(i);
						break;
					}
				}
			} else {
				for (int i = 0; i < expressionString.length(); i++) {
					if (expressionString.charAt(i) == '+' || expressionString.charAt(i) == '-' || expressionString.charAt(i) == '|') {
						found = expressionString.substring(0, i-1);
						expressionString = expressionString.substring(i);
						break;
					}
				}
			}


			if (numberOfParsedTerms == 0) {
				expression = parseTerm(found);
				numberOfParsedTerms++;
			} else {
				//TODO seperate in two methods from this point
				if(expressionString.length() > 0) {
					expressionString = expressionString.substring(1);
				}
				if (operatorString.charAt(0) == '+') {
					SetInterface<BigInteger> termToUnion = parseTerm(found);
					expression = expression.union(termToUnion);
					System.out.println(expression.toString() + " expression");
					operatorString.substring(1);
					numberOfParsedTerms++;
				} else if (operatorString.charAt(0) == '-') {
					SetInterface<BigInteger> termToComplement = parseTerm(found);
					expression = expression.complement(termToComplement);
					operatorString.substring(1);
					numberOfParsedTerms++;
				} else if (operatorString.charAt(0) == '|') {
					SetInterface<BigInteger> termToSymDifference = parseTerm(found);
					expression = expression.symDifference(termToSymDifference);
					operatorString.substring(1);
					numberOfParsedTerms++;
				}
			}
		}
		return expression;
	}






	/*
	//TODO APEXCEPTION
	SetInterface<BigInteger> parseExpression(String expressionString) throws  APException {
		System.out.println(expressionString + " EXPRESSION");
		int numberOfParsedTerms = 0;
		SetInterface<BigInteger> expression = null;
		int count = 0;
		for (int i = 0; i < expressionString.length(); i++) {
			String found = null;
			if (expressionString.charAt(i) == '(') {
				count++;
			} else if (expressionString.charAt(i) == ')') {
				count++;
			}
			if (count == 0) {
				found = expressionString.substring(i);
				char operator = ' ';
				if(i == expressionString.length() - 1) {
					operator = expressionString.charAt(i - 1);
				}
				expression = singleTerm(found, numberOfParsedTerms, expression, operator);
			}
		}
		return expression;
	}
*/






	SetInterface<BigInteger> singleTerm(String term, int numberOfParsedTerms, SetInterface<BigInteger> expression, char operator) throws APException{
		//String operatorString = computeOperatorString(term);
			if(numberOfParsedTerms == 0){
				expression = parseTerm(term);
				numberOfParsedTerms++;
			}else {
				if (operator == '+') {
					SetInterface<BigInteger> termToUnion = parseTerm(term);
					expression = expression.union(termToUnion);
					System.out.println(expression.toString() + " expression");
					numberOfParsedTerms++;
				} else if (operator == '-') {
					SetInterface<BigInteger> termToComplement = parseTerm(term);
					expression = expression.complement(termToComplement);
					numberOfParsedTerms++;
				} else if (operator == '|') {
					SetInterface<BigInteger> termToSymDifference = parseTerm(term);
					expression = expression.symDifference(termToSymDifference);
					numberOfParsedTerms++;
				}
			}
		return expression;
	}


	private String computeOperatorString(String expressionString){
		Scanner termsAndOperators = new Scanner(expressionString);
		StringBuffer operatorStringBuffer =  new StringBuffer();
		termsAndOperators.useDelimiter("");
		while(termsAndOperators.hasNext()){
			String operator = termsAndOperators.next();
			if(operator.equals("+") || operator.equals("-") || operator.equals("|")){
				operatorStringBuffer.append(operator);
			}
		}
		return operatorStringBuffer.toString();
	}


	SetInterface<BigInteger> parseTerm(String termString) throws APException{
		System.out.println(termString + " TermString");
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
				term = term.intersection(factorToIntersect);
				numberOfParsedFactors++;
			}
		}
		return term;
	}


	SetInterface<BigInteger> parseFactor(String factor) throws APException{
		System.out.println(factor + "FACTOR");
		Scanner factorScanner = new Scanner(factor);
		SetInterface<BigInteger> set;
		if(nextCharIsLetter(factorScanner)){
			Identifier id = parseIdentifier(factor);
			//System.out.println("boolean" + variables.containsKey(id));
			if (variables.containsKey(id)) {
				set = variables.get(id);
			}else{
				throw new APException("Invalid set, set never initialized\n");
			}
		}else if(nextCharEqualsInput(factorScanner, '(')){
			//TODO use correct delimeter
			set = parseComplexFactor(factor);
		}else if(nextCharEqualsInput(factorScanner, '{')){
			set = parseSet(factor);
		}else{
			throw new APException("Invalid factor make sure all factors start with a \"letter\", a \"(\" or a \"{\" \n");
		}
		return set;
	}


	Identifier parseIdentifier(String statement) throws  APException{
		Identifier identifier = new Identifier();
		Scanner identifierScanner = new Scanner(statement);
		identifierScanner.useDelimiter("");
		while(identifierScanner.hasNext()){
			String character = identifierScanner.next();
			//System.out.println(character + "CHARACTER");
			boolean validChar = identifier.readValidChar(character);
			if (!validChar){
				throw new APException("Invalid Identifier, Identifiers should start with a letter and only contain letters and numbers\n");
			}
		}
		return identifier;
	}


	SetInterface<BigInteger> parseComplexFactor(String complexFactor) throws  APException{
		System.out.println(complexFactor + " COMPLEX");
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
		//System.out.println(set + " SET");
		SetInterface<BigInteger> parsedSet = new Set<>();
		if(set.charAt(set.length() - 1) == '}'){
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
			System.out.println(set);
			throw new APException("Invalid set, set never closed\n");
		}
	}


	BigInteger parseNaturalNumber(String number) throws  APException{
		BigInteger naturalNumber;
		Scanner numberScanner = new Scanner(number);
		if(nextCharIsZero(numberScanner) && number.length() == 1){
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

		Scanner positiveNumberScanner = new Scanner(nonZeroNumber);
		for(int i = 1; i < nonZeroNumber.length(); i++){
			if(!nextCharIsNumber(positiveNumberScanner)){
				throw new APException("Invalid number in set, set contains non number element\n");
			}
			positiveNumberScanner.next();
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
