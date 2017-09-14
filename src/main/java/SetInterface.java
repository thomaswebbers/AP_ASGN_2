/**	@elements : a List of elements of type E
 *	@structure : sequential
 *	@domain : 	a sequential list of elements that extend Comparable
 *	@constructor - Set();
 *	<dl>
 *		<dt><b>PRE-conditie</b><dd>		
 *		<dt><b>POST-conditie</b><dd> 	A set has been created containing a List() with elements of type e
 * </dl>
 **/

public interface SetInterface<E extends Comparable> {
	//ListInterface list = null;

	/** @precondition  - 
     *	@postcondition - returns a new Set that is the union of both Sets
     **/
    SetInterface<E> union(SetInterface<E> e);

	/** @precondition  - 
     *	@postcondition - returns a new Set that is the intersection of both Sets
     **/
    SetInterface<E> intersection(SetInterface<E> e);

    /** @precondition  - 
     *	@postcondition - returns a new Set that is the complement of both Sets
     **/
    SetInterface<E> complement(SetInterface<E> e);

    /** @precondition  - 
     *	@postcondition - returns a new Set that is the symmetric difference of both Sets
     **/
    SetInterface<E> symDifference(SetInterface<E> e);

    /** @precondition  - 
     *  @postcondition - returns a new Set that has the element added to it
     **/
    SetInterface<E> add(E e);

    /** @precondition  - the element exists in the set
     *  @postcondition - TRUE:	returns a new Set that has the element removed from it
     *  				 FALSE: the set is unchanged.
     **/
    SetInterface<E> remove(E e);

    /** @precondition  - 
     *  @postcondition - FALSE: the Set does not contain the element.
     *                   TRUE:  the Set does contain the element.
     **/
    boolean contains(E e);

    /** @precondition  - 
     *  @postcondition - the list has been emptied
     **/
    void init();

    /**	@precondition  -
     *	@postcondition - The number of elements the List contains has been returned.
     **/
    int size();

    /**	@precondition -
     *  @postcondition - FALSE: Set is not empty.
     *  				 TRUE:  Set is empty.
     **/
    boolean isEmpty();

    /**	@precondition -
     *  @postcondition - returns a deep copy of the Set object
     **/
    SetInterface<E> copy();

    /**	@precondition -
     *  @postcondition - returns a String that contains the contents of the List
     *  				 seperated by spaces
     **/
    String toString();

}