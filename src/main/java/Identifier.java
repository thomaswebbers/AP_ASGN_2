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
		if(identifier.length() == 0){
			if(charIsLetter(readCharacter.charAt(0))){
				addChar(readCharacter);
			}else{
				return false;
			}
		}else {
			if(charIsNumber(readCharacter.charAt(0)) || charIsLetter(readCharacter.charAt(0))){
				addChar(readCharacter);
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

	private boolean charIsLetter(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("[a-zA-Z]");
	}

	private boolean charIsNumber(char character) {
		String characterString = String.valueOf(character);
		return characterString.matches("[0-9]");
	}
}