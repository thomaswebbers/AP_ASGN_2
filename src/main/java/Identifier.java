class Identifier {
	private String identifier;

	public Identifier(String name) {
		identifier = name;
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

	public String toString() {
		return identifier;
	}
}