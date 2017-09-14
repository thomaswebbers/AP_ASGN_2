/* DISCLAIMER
*	There is a bug in our Set that does not allow us to use our copy method, thus variables will get overwritten unintentionally.
*	The code that would've been used for the copy has been commented out so that the program does at least something.
*/


public class Set <E extends Comparable> implements SetInterface<E> {
    private ListInterface<E> list;

	public Set() {
        list = new List<E>();
        init();
	}

    public SetInterface<E> union(SetInterface<E> set) {
        if (set.isEmpty()) {
            return this.copy();
        }

        SetInterface<E> result = set.copy();

        if(this.isEmpty()) {
            return result;
        }

        this.list.goToFirst();

        do {
        	result.add(this.list.retrieve());
        } while (this.list.goToNext() == true);

        return result;
    }

    public SetInterface<E> intersection(SetInterface<E> set){
        SetInterface<E> result = new Set<E>();

        if (this.isEmpty() || set.isEmpty()) {
        	return result;
        }

        this.list.goToFirst();

        do {
        	E current = this.list.retrieve();

            if (set.contains(current)) {
            	result.add(current);
            }
        } while (this.list.goToNext() == true);

        return result;
    }

    public SetInterface<E> complement(SetInterface<E> set) {
        SetInterface<E> result = this.copy();

        if (this.isEmpty() || set.isEmpty()) {
            return result;
        }

        this.list.goToFirst();

        do {
            E current = this.list.retrieve();

            if (set.contains(current)) {
                result.remove(current);
            }
        } while (this.list.goToNext() == true);

        return result;
    }

    public SetInterface<E> symDifference(SetInterface<E> set) {
        SetInterface<E> a = this.union(set);
        SetInterface<E> b = set.intersection(this);

        return a.complement(b);
    }

    public SetInterface<E> remove(E e){
    	if (this.list.find(e)) {
    		this.list.remove();
    	}
    	return this;
    }

    public boolean contains(E e){
    	return this.list.find(e);
    }

    public void init(){
        this.list.init();
    }

    public SetInterface<E> add(E e) {
    	if (!this.list.find(e)) {
    	    System.out.println("adding: " + e.toString());
    		this.list.insert(e);
    		System.out.println("current list: " + this.toString() + "\n");
    	}
        return this;
    }

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public SetInterface<E>copy() {
        Set<E> copySet = new Set<E>();
        copySet.list = this.list.copy();
        return copySet;
    }

    public String toString() {
        if(this.isEmpty()){
            return "";
        }
        
        StringBuilder setString = new StringBuilder(20);
        
        this.list.goToFirst();
        setString.append(this.list.retrieve().toString());
        
        if(this.list.goToNext()) {
        	do {
        		setString.append(" " + this.list.retrieve().toString());
        	}
        	while(!this.list.goToNext() == false);
        }   
        
        //setString.append("}");
        
        return setString.toString();
	}
}
