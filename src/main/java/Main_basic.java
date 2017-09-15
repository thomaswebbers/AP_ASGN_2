/**
 * Created by thomaswebbers on 15/09/2017.
 */
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.InputMismatchException;

public class Main_basic {
    PrintStream out;
    HashMap<Identifier, SetInterface> variables;

    Main_basic() {
        out = new PrintStream(System.out);
        variables = new HashMap<Identifier, SetInterface>();
    }

    private void interpretStatement(String command) throws APException {
        //System.out.println("statement: " + command);

        Scanner in = new Scanner(command);
        in.useDelimiter("");
        in.skip("\\s*");
        if (nextCharIs(in, '/')) {
            return; //ignores comments
        }
        else if (nextCharIs(in, '?')) {
            interpretPrint(in.nextLine());
        }
        else if (nextCharIsLetter(in)) {
            interpretAssignment(in.nextLine());
        }
        else  {
            throw new APException("Invalid start of statement!");
        }
    }

    private void interpretAssignment(String assignment) throws APException {
        //System.out.println("assignment: " + assignment);

        Scanner in = new Scanner(assignment);
        Identifier id = new Identifier(in.next());

        if (!isValidIdentifier(id.toString())) {
            throw new APException("Invalid identifier syntax!");
        }

        if (!nextCharIs(in, '=')) {
            throw new APException("Invalid syntax, missing assignment operator!");
        }

        in.skip("\\s*=\\s*"); // to skip the '=' sign and unnecessary spaces

        SetInterface<BigInteger> result = interpretExpression(in.nextLine());
        variables.put(id, result);
    }

    private void interpretPrint(String printStatement) throws APException {
        //System.out.println("print: " + printStatement);

        Scanner in = new Scanner(printStatement);
        in.skip("\\s*\\?\\s*"); // to skip the '?' sign and spaces

        if (in.hasNext()) {
            SetInterface<BigInteger> result = interpretExpression(in.nextLine());
            out.println(result);
        }
        else {
            throw new APException("Invalid syntax, missing an expression!");
        }
    }

    private SetInterface<BigInteger> interpretExpression(String expression) throws APException {
        //System.out.println("expression: " + expression);

        expression = expression.replaceAll("\\s","");  // gets rid of all the spacing
        SetInterface<BigInteger> result;

        Scanner in = new Scanner(expression);
        in.useDelimiter("");

        if (nextCharIs(in, '(')) {
            result = interpretComplexTerm(in);
        }

        else {
            StringBuffer termBuffer = new StringBuffer();
            while (!nextCharIsAddOperator(in) && in.hasNext()) {
                termBuffer.append(in.next());
            }

            result = interpretTerm(termBuffer.toString());
        }

        //to check for operators and other terms.
        while (nextCharIsAddOperator(in)) {
            SetInterface<BigInteger> term;
            char operator = in.next().charAt(0);
            StringBuffer termBuffer = new StringBuffer();

            if (nextCharIs(in, '(')) {
                term = interpretComplexTerm(in);
            }
            else {
                int counter = 0;
                while (in.hasNext()) {
                    if (nextCharIsAddOperator(in) && counter <= 0) {
                        break;
                    }
                    else if (nextCharIs(in, '(')) {
                        termBuffer.append(in.next());
                        counter++;
                    }
                    else if (nextCharIs(in, ')')) {
                        termBuffer.append(in.next());
                        counter--;
                    }
                    else {
                        termBuffer.append(in.next());
                    }
                }
                term = interpretTerm(termBuffer.toString());
            }

            System.out.println("operator = " + operator);
            switch (operator) {
                case '+':
                    result = result.union(term);
                    break;

                case '-':
                    result = result.complement(term);
                    break;

                case '|':
                    result = result.symDifference(term);
                    break;

                default: throw new APException("Invalid syntax, missing an operator in the expression!");
            }
        }

        //Hacky way to catch the leftovers.
        if (nextCharIs(in, '*')) {
            in.next(); //skip the operator

            SetInterface<BigInteger> leftOvers = interpretTerm(in.nextLine());
            result = result.intersection(leftOvers);
        }

        return result;
    }

    private SetInterface<BigInteger> interpretComplexTerm(Scanner in) throws APException {
        //System.out.println("complexterm called!");

        StringBuffer complexTermBuffer = new StringBuffer();
        int counter = 1;
        boolean closed = false;
        in.next(); //skip the first '(' sign

        while (in.hasNext() && !closed) {
            if (nextCharIs(in, '(')) {
                complexTermBuffer.append(in.next());
                counter++;
            }
            else if (nextCharIs(in, ')')) {
                if (counter == 1) {
                    in.next(); //skip the found ')' sign
                    closed = true;
                }
                else {
                    complexTermBuffer.append(in.next());
                }
                counter--;
            }
            else {
                complexTermBuffer.append(in.next());
            }
        }

        //System.out.println("~~~~Complexterm eventually ended up with: " + complexTermBuffer.toString());

        if (counter != 0) { //then we are missing a closing parenthesis
            throw new APException("Invalid syntax, missing a closing parenthesis!");
        }

        return interpretExpression(complexTermBuffer.toString());
    }

    private SetInterface<BigInteger> interpretTerm(String term) throws APException {
        //System.out.println("term: " + term);

        SetInterface<BigInteger> result;
        Scanner in = new Scanner(term);
        in.useDelimiter("");

        if (nextCharIs(in, '(')) {
            result = interpretComplexTerm(in);
        }
        else {
            StringBuffer factorBuffer = new StringBuffer();

            while (!nextCharIs(in, '*') && in.hasNext()) {
                factorBuffer.append(in.next());
            }

            result = interpretFactor(factorBuffer.toString());
        }

        //to check for operators and other terms.
        while (nextCharIs(in, '*')) {
            char operator = in.next().charAt(0);
            SetInterface<BigInteger> factor;

            if (nextCharIs(in, '(')) {
                factor = interpretComplexTerm(in);
            }
            else {
                StringBuffer factorBuffer = new StringBuffer();

                while (!nextCharIs(in, '*') && in.hasNext()) {
                    factorBuffer.append(in.next());
                }
                factor = interpretFactor(factorBuffer.toString());
            }

            result = result.intersection(factor);
        }

        return result;
    }

    private SetInterface<BigInteger> interpretFactor(String factor) throws APException {
        System.out.println("factor: " + factor + "\n");

        Scanner in = new Scanner(factor);
        in.useDelimiter("");

        if (nextCharIs(in, '{')) {
            Set<BigInteger> result = new Set<BigInteger>();
            in.next(); //to skip the { sign.

            StringBuffer setBuffer = new StringBuffer();
            while (!nextCharIs(in, '}') && in.hasNext()) {
                setBuffer.append(in.next());
            }

            if (!nextCharIs(in, '}')) {
                throw new APException("Missing closing bracket of set!");
            }

            Scanner set = new Scanner(setBuffer.toString());
            set.useDelimiter(",");

            while (set.hasNext()) {
                BigInteger number = readBigInteger(set);
                result.add(number);
            }

            in.next(); //gets rid of the closing bracket.
            if (!in.hasNext()) {
                return result;
            }
            else {
                throw new APException("Invalid syntax, only operators are allowed after a factor!");
            }
        }

        else if (isValidIdentifier(factor)) {
            Identifier id = new Identifier(factor);

            if (variables.containsKey(id)) {
                return variables.get(id);
            }
            else {
                throw new APException("The variable " + id.toString() + " does not exist.");
            }
        }

        else {
            throw new APException("Invalid factor syntax!");
        }
    }

    private BigInteger readBigInteger(Scanner in) throws APException {
        try {
            return in.nextBigInteger();
        }
        catch (InputMismatchException e) {
            throw new APException("Invalid syntax, only natural numbers are allowed in sets!");
        }
    }

    private boolean nextCharIsAddOperator(Scanner in) {
        return (nextCharIs(in, '+') | nextCharIs(in, '-') | nextCharIs(in, '|'));
    }

    private boolean isValidIdentifier(String id) {
        Scanner in = new Scanner(id);
        in.useDelimiter(""); // set delimiter to empty to get only 1 char at a time.

        if (!nextCharIsLetter(in)) {
            return false;
        }

        in.next(); // skip the first character that has already been checked.
        while (in.hasNext()) {
            if (!nextCharIsLetter(in) && !nextCharIsDigit(in)) {
                return false;
            }
            in.next();
        }

        return true;
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
                interpretStatement(statement);
            }
            catch (APException e) {
                out.println(e);
            }
        }
        in.close();
    }

    public static void main(String[] argv) {
        new Main_basic().start();
    }
}

