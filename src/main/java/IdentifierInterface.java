/**	@elements : char value
 *	@structure : singular
 *	@domain : 	any combination of characters
 *	@constructor - Identifier()
 *	<dl>
 *		<dt><b>PRE-conditie</b><dd>		
 *		<dt><b>POST-conditie</b><dd> 	An Identifier has been created that contains a String element
 * </dl>
 **/

public interface IdentifierInterface {
    /** @precondition -
     *  @postcondition -
     *  @success - char is valid call addChar() and return true
     *  @failure - char is nonvalid return false
     */
    boolean readValidChar(String character);


    /** @precondition -
     *  @postcondition - char is added to identifier
     */
    void addChar(String character);


	/**	@precondition -
     *  @postcondition - returns the String value that the Identifier stores.
     **/
    String toString();
}


