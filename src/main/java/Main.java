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
		//System.out.println();
		//System.out.println("START OF STATEMENT--------------------------------------------------------------");
		statement = statement.replaceAll(" ", "");
		Scanner statementScanner = new Scanner(statement);
		if (nextCharEqualsInput(statementScanner, '/')) {
			//skip line because its a comment
		} else if (nextCharEqualsInput(statementScanner, '?')) {
			parsePrintStatement(statement);
		} else if (nextCharIsLetter(statementScanner)) {
			parseAssigntment(statement);
		} else {
			//System.out.printf("The input is: %s\n", statement);
			throw new APException("Invalid statement found, please alter file\n");
		}
	}


	void parseAssigntment(String statement) throws APException {
		Scanner assignment = new Scanner(statement);
		assignment.useDelimiter("=");
		Identifier id = parseIdentifier(assignment.next());
		SetInterface<BigInteger> value = parseExpression(assignment.next());
		variables.put(id, value);
		//System.out.println("END OF STATEMENT--------------------------------------------------------------");
		//System.out.println();
	}


	void parsePrintStatement(String statement) throws APException {
		statement = statement.substring(1);
		SetInterface<BigInteger> result = parseExpression(statement);
		out.println(result.toString());
		//System.out.println("END OF STATEMENT--------------------------------------------------------------");
		//System.out.println();
	}


	SetInterface<BigInteger> parseExpression(String expressionString) throws APException {
		//System.out.printf(expressionString + " expressionstring IS NOW\n");
		int numberOfParsedTerms = 0;
		SetInterface<BigInteger> expression = null;
		String operatorString = computeOperatorString(expressionString);
		String found = "";
		int count = 0;
		while(expressionString.length() > 0) {
			//System.out.printf(expressionString + " EXPRESSION IS NOW\n");
			if (expressionString.charAt(0) == '(') {
				//System.out.printf(expressionString + " TWICE\n");
				//System.out.println(" PARENTHESIS BRANCH");
				for (int i = 0; i < expressionString.length(); i++) {
					if (expressionString.charAt(i) == '(') {
						count++;
					} else if (expressionString.charAt(i) == ')') {
						count--;
					}else if(i == expressionString.length()-1){
						//System.out.println("THIS");
						found = found + expressionString;
						expressionString = "";
						parseTerm(found);
					}


					if (count == 0) {
						//System.out.println("THAT");
						found = found + expressionString.substring(0, i+1);
						expressionString = expressionString.substring(i+1);
						//System.out.printf(expressionString + " what do you contain??\n");
						break;
					}
				}
			}

			if(expressionString.length() != 0 && expressionString.charAt(0) != '(') {
				//System.out.println("BRANCH");
				//System.out.printf(expressionString + " should start with *\n");
				for (int i = 0; i < expressionString.length(); i++) {
					if (expressionString.charAt(i) == '+' || expressionString.charAt(i) == '-' || expressionString.charAt(i) == '|') {
						found = found + expressionString.substring(0, i);
						//System.out.printf(found + " Wwe should not see this\n");
						expressionString = expressionString.substring(i);
						break;
					}
					if(i == expressionString.length()-1){
						found = found + expressionString;
						//System.out.printf(found + " WHATS THIS FOUND\n");
						expressionString = "";
					}
				}
			}

			//TODO seperate in two methods from this point
			if(expressionString.length() > 0 && expressionString.charAt(0) != '*') {
				expressionString = expressionString.substring(1);
			}
			//System.out.println();
			//System.out.println( "FOUND" + found + " FOUND");
			//System.out.printf(expressionString + " EXPRESSION STRING ++++++\n");
			//System.out.println(numberOfParsedTerms + " NUMBER");
			//System.out.println(numberOfParsedTerms + "PARSED");
			String foundDelete = found;
			//System.out.println(operatorString + "OPERATORS");
			if (numberOfParsedTerms == 0) {
				//System.out.println("expression TADA ");
				//System.out.println( "MAGIC" + found + " MAGIC");
				expression = parseTerm(found);
				found = "";
				//System.out.println("expression contains: " + expression.toString());
				numberOfParsedTerms++;
			} else {
				if(operatorString.length() > 0) {
					if (operatorString.charAt(0) == '+') {
						//System.out.println("UNION");
						SetInterface<BigInteger> termToUnion = parseTerm(found);
						found = "";
						expression = expression.union(termToUnion);
						//System.out.println(expression.toString() + " expression");
						operatorString = operatorString.substring(1);
						numberOfParsedTerms++;
					} else if (operatorString.charAt(0) == '-') {
						//System.out.println("COMPLEMENT");
						SetInterface<BigInteger> termToComplement = parseTerm(found);
						found = "";
						expression = expression.complement(termToComplement);
						operatorString = operatorString.substring(1);
						numberOfParsedTerms++;
					} else if (operatorString.charAt(0) == '|') {
						//System.out.println("SYMDIFFERNCE");
						SetInterface<BigInteger> termToSymDifference = parseTerm(found);
						found = "";
						expression = expression.symDifference(termToSymDifference);
						operatorString = operatorString.substring(1);
						numberOfParsedTerms++;
					}
				}else{
					throw new APException("Invalid expression, all terms in expression should be seperated by addetive operator and complex terms should be closed");
				}
			}
			//System.out.println(expression.toString() + " elements of term of "  + foundDelete + "\n");
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
		//System.out.println(termString + " TermString FOR PARSETERM!!!!");
		//Scanner factorChain = new Scanner(termString);
		//TODO fix complex factors containing *
		//factorChain.useDelimiter("\\*");
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
					}else if(i == termString.length()-1){
						found = found + termString;
						termString = "";
						parseFactor(found);
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


			//System.out.println("THE TERMSTRING IS: " + termString);
			//System.out.println( "FOUND" + found + " FOUND");
			String foundDelete = found;
			if(numberOfParsedFactors == 0){
				term = parseFactor(found);
				found = "";
				numberOfParsedFactors++;
			}else{
				//System.out.println("INTERSECT");
				SetInterface<BigInteger> factorToIntersect = parseFactor(found);
				term = term.intersection(factorToIntersect);
				found = "";
				numberOfParsedFactors++;
			}
			//System.out.println(term.toString() + " elements of factor  of "  + foundDelete + "\n");
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
		//System.out.println(factor + "FACTOR");
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
			//System.out.println("THIS ONE??");
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
		//System.out.println(complexFactor + " COMPLEX");
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
