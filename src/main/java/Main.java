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
			throw new APException("Invalid statement found, please alter file");
		}
	}


	void parseAssigntment(String statement) throws APException {
		Scanner assignment = new Scanner(statement);
		assignment.useDelimiter("=");
		Identifier id = parseIdentifier(assignment.next());
		if(assignment.hasNext()){
			SetInterface<BigInteger> value = parseExpression(assignment.next());
			variables.put(id, value);
		}else{
			throw new APException("Invalid statement found no value after identifier");
		}
	}


	void parsePrintStatement(String statement) throws APException {
		statement = statement.substring(1);
		SetInterface<BigInteger> result = parseExpression(statement);
		out.println(result.toString());
	}


	SetInterface<BigInteger> parseExpression(String expressionString) throws APException {
		int numberOfParsedTerms = 0;
		SetInterface<BigInteger> expression = null;
		String operatorString = computeOperatorString(expressionString);
		String found = "";
		int count = 0;
		while(expressionString.length() > 0) {
			if (expressionString.charAt(0) == '(') {
				//System.out.println("B ONE");
				for (int i = 0; i < expressionString.length(); i++) {
					if (expressionString.charAt(i) == '(') {
						count++;
					} else if (expressionString.charAt(i) == ')') {
						count--;
					}
					if(i == expressionString.length()-1){
						found = found + expressionString;
						expressionString = "";
						parseTerm(found);
						break;
					}


					if (count == 0) {
						found = found + expressionString.substring(0, i+1);
						expressionString = expressionString.substring(i+1);
						break;
					}
				}
			}

			if(expressionString.length() != 0 && expressionString.charAt(0) != '(') {
				//System.out.println("B TWO");
				for (int i = 0; i < expressionString.length(); i++) {
					if (expressionString.charAt(i) == '(') {
						count++;
					} else if (expressionString.charAt(i) == ')') {
						count--;
					}
					if(count == 0){
						if (expressionString.charAt(i) == '+' || expressionString.charAt(i) == '-' || expressionString.charAt(i) == '|') {
							found = found + expressionString.substring(0, i);
							expressionString = expressionString.substring(i);
							break;
						}
					}
					if(i == expressionString.length()-1){
						found = found + expressionString;
						expressionString = "";
					}
				}
			}

			//TODO seperate in two methods from this point
			if(expressionString.length() > 0 && expressionString.charAt(0) != '*') {
				expressionString = expressionString.substring(1);
			}
			if (numberOfParsedTerms == 0) {
				expression = parseTerm(found);
				found = "";
				numberOfParsedTerms++;
			} else {
				if(operatorString.length() > 0) {
					if (operatorString.charAt(0) == '+') {
						SetInterface<BigInteger> termToUnion = parseTerm(found);
						found = "";
						expression = expression.union(termToUnion);
						operatorString = operatorString.substring(1);
						numberOfParsedTerms++;
					} else if (operatorString.charAt(0) == '-') {
						SetInterface<BigInteger> termToComplement = parseTerm(found);
						found = "";
						expression = expression.complement(termToComplement);
						operatorString = operatorString.substring(1);
						numberOfParsedTerms++;
					} else if (operatorString.charAt(0) == '|') {
						SetInterface<BigInteger> termToSymDifference = parseTerm(found);
						found = "";
						expression = expression.symDifference(termToSymDifference);
						operatorString = operatorString.substring(1);
						numberOfParsedTerms++;
					}
				}else{
					throw new APException("Invalid expression, all terms in expression should be separated by addetive operator");
				}
			}
		}
		return expression;
	}

	private String computeOperatorString(String expressionString){
		Scanner termsAndOperators = new Scanner(expressionString);
		StringBuffer operatorStringBuffer =  new StringBuffer();
		termsAndOperators.useDelimiter("");
		int count = 0;
		while(termsAndOperators.hasNext()){
			String operator = termsAndOperators.next();
			if (operator.equals("(")) {
				count++;
			} else if (operator.equals(")")) {
				count--;
			}
			if(count == 0) {
				if (operator.equals("+") || operator.equals("-") || operator.equals("|")) {
					operatorStringBuffer.append(operator);
				}
			}
		}
		return operatorStringBuffer.toString();
	}


	SetInterface<BigInteger> parseTerm(String termString) throws APException{
		int numberOfParsedFactors = 0;
		SetInterface<BigInteger> term = null;
		String found = "";
		int count = 0;
		int numberOfMuliplicativeOperators = computMultiplicativeeOperator(termString);
		while(termString.length() > 0) {
			if (termString.charAt(0) == '(') {
				for (int i = 0; i < termString.length(); i++) {
					if (termString.charAt(i) == '(') {
						count++;
					} else if (termString.charAt(i) == ')') {
						count--;
					}
					if(i == termString.length()-1){
						found = found + termString;
						termString = "";
						parseFactor(found);
						break;
					}


					if (count == 0) {
						found = found + termString.substring(0, i+1);
						termString = termString.substring(i+1);
						break;
					}
				}
			}

			if(termString.length() != 0 && termString.charAt(0) != '(') {
				for (int i = 0; i < termString.length(); i++) {
					if (termString.charAt(i) == '*') {
						found = found + termString.substring(0, i);
						termString = termString.substring(i);
						break;
					}
					if(i == termString.length()-1){
						found = found + termString;
						termString = "";
					}
				}
			}

			//TODO seperate in two methods from this point
			if(termString.length() > 0 ) {
				termString = termString.substring(1);
			}

			if(numberOfParsedFactors == 0){
				term = parseFactor(found);
				found = "";
				numberOfParsedFactors++;
			}else{
				SetInterface<BigInteger> factorToIntersect = parseFactor(found);
				term = term.intersection(factorToIntersect);
				found = "";
				numberOfParsedFactors++;
			}
		}
		return term;
	}


	private int computMultiplicativeeOperator(String termString){
		Scanner factorsAndOperators = new Scanner(termString);
		int numberOfOperators = 0;
		factorsAndOperators.useDelimiter("");
		int count = 0;
		while(factorsAndOperators.hasNext()){
			String operator = factorsAndOperators.next();
			if (operator.equals("(")) {
				count++;
			} else if (operator.equals(")")) {
				count--;
			}
			if(count == 0) {
				if (operator.equals("*")) {
					numberOfOperators++;
				}
			}
		}
		return numberOfOperators;
	}


	SetInterface<BigInteger> parseFactor(String factor) throws APException{
		Scanner factorScanner = new Scanner(factor);
		SetInterface<BigInteger> set;
		if(nextCharIsLetter(factorScanner)){
			Identifier id = parseIdentifier(factor);
			if (variables.containsKey(id)) {
				set = variables.get(id);
			}else{
				throw new APException("Invalid set, set never initialized");
			}
		}else if(nextCharEqualsInput(factorScanner, '(')){
			set = parseComplexFactor(factor);
		}else if(nextCharEqualsInput(factorScanner, '{')){
			set = parseSet(factor);
		}else{
			throw new APException("Invalid factor make sure all factors start with a \"letter\", a \"(\" or a \"{\" ");
		}
		return set;
	}


	Identifier parseIdentifier(String statement) throws  APException{
		Identifier identifier = new Identifier();
		Scanner identifierScanner = new Scanner(statement);
		identifierScanner.useDelimiter("");
		while(identifierScanner.hasNext()){
			String character = identifierScanner.next();
			boolean validChar = identifier.readValidChar(character);
			if (!validChar){
				throw new APException("Invalid Identifier, Identifiers should start with a letter and only contain letters and numbers");
			}
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
			throw new APException("Invalid complex factor, complex factor never closed");
		}
	}


	SetInterface<BigInteger> parseSet(String set) throws APException{
		SetInterface<BigInteger> parsedSet = new Set<>();
		if(set.charAt(set.length() - 1) == '}'){
			String rowOfNaturalNumbers = set.substring(1, set.length() -1);
			//returns empty set
			if(rowOfNaturalNumbers.length() == 0){
				return parsedSet;
			}else{
				//checks if set doesn't contain null element at the start of the set
				if(rowOfNaturalNumbers.charAt(0) == ','){
					throw new APException("Invalid number in set, set starts with null element");
				}
				//checks if set doesn't contain null element in the middle
				if(set.contains(",,")){
					throw new APException("Invalid number in set, set contains null element");
				}

				if(rowOfNaturalNumbers.charAt(rowOfNaturalNumbers.length() -1) == ','){
					throw new APException("Invalid number in set, set ends with null element");
				}

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
			throw new APException("Invalid set, set never closed");
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

		if(nonZeroNumber.charAt(0) == '0'){
			throw new APException("Invalid number in set, set contains non zero number starting with 0");
		}

		if(nonZeroNumber.charAt(0) == ' '){
			throw new APException("Invalid number in set, set contains a null element");
		}

		for(int i = 1; i < nonZeroNumber.length(); i++){
			if( Character.isLetter(nonZeroNumber.charAt(i))){
				throw new APException("Invalid number in set, set contains non number element");
			}
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

	private void start()throws APException {
		Scanner in = new Scanner(System.in);

		while(in.hasNextLine()) {
			String statement = in.nextLine();

			if(statement.length() == 0){
				System.out.println("error: no statment");
				continue;
			}

			try{
				parseStatement(statement);
			}catch (APException e){
				System.out.println(e.getMessage());
			}
		}
		in.close();
	}


	public static void main(String[] argv)throws APException {
			new Main().start();
	}
}
