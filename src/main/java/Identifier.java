import java.util.Scanner;

class Identifier implements  IdentifierInterface {
	private StringBuffer identifier;

	Identifier(){
		identifier = new StringBuffer();
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Identifier) {
			Identifier that = (Identifier) obj;
			return this.identifier.equals(that.identifier);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean readValidChar(String readCharacter) {
		Scanner character = new Scanner(readCharacter);
		if(identifier.length() == 0){
			if(charIsLetter(character)){
				addChar(character.next());
			}else{
				return false;
			}
		}else {
			if(charIsNumber(character) || charIsLetter(character)){
				addChar(character.next());
			}else{
				return false;
			}
		}

		return true;
	}


	@Override
	public void addChar(String character) {
		identifier.append(character);
	}

	public String toString() {
		return identifier.toString();
	}

	private boolean charIsLetter(Scanner in) {
		return in.hasNext("[a-zA-Z]");
	}

	private boolean charIsNumber(Scanner in) {
		return in.hasNext("[0-9]");
	}
}