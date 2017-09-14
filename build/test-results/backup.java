public class Set <E extends Comparable> implements SetInterface<E> {
    ListInterface<E> list;

	public Set() {
        list = new List<E>();
        init();
	}

    public SetInterface<E> union(SetInterface<E> set) {
        Set<E> that = (Set<E>) set;
        Set<E> result = (Set<E>) this.copy();

        if (this.list.isEmpty()) {
            return that.copy();
        }
        else if (that.list.isEmpty()) {
            return this.copy();
        }

        that.list.goToFirst();

        do {
            result.add(that.list.retrieve());
        } while (that.list.goToNext() == true);

        return result;
    }

    public SetInterface<E> intersection(SetInterface<E> set){
        Set<E> that = (Set<E>) set;
        Set<E> result = (Set<E>) this.copy();

        if (this.list.isEmpty()) {
            return this.copy();
        }
        else if (that.list.isEmpty()) {
            return that.copy();
        }

        this.list.goToFirst();

        do {
            E current = result.list.retrieve();

            if (!that.list.find(current)) {
                result.remove(current);
            }
        } while (result.list.goToNext() == true);

        return result;
    }

    public SetInterface<E> complement(SetInterface<E> set){
        Set<E> that = (Set<E>) set;
        Set<E> result = (Set<E>) this.copy();

        if (this.list.isEmpty()) {
            return this;
        }
        else if (that.list.isEmpty()) {
            return that;
        }

        that.list.goToFirst();

        do {
            E current = result.list.retrieve();

            if (this.list.find(current)) {
                result.remove(current);
            }
        } while (that.list.goToNext() == true);
        return result;
    }

    public SetInterface<E> symDifference(SetInterface<E> set) {
        SetInterface<E> setA = (Set<E>)this.copy();
        SetInterface<E> setB = (Set<E>)set.copy();
        SetInterface<E> result = new Set<E>();

        if (this.list.isEmpty()) {
            return this;
        }

        else if (setB.list.isEmpty()) {
            return setB;
        }

        setA = setA.union(set);
        setB = setB.intersection(this);
        result = setA.complement(setB);

        return result;
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
    		this.list.insert(e);
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
        Set<E> copySet = new Set();
        copySet.list = this.list.copy();
        return copySet;
    }

    public String toString() {
        if(this.isEmpty()){
            return "{ }";
        }
        
        StringBuilder setString = new StringBuilder(20);
        
        this.list.goToFirst();
        setString.append("{" + this.list.retrieve().toString());
        
        if(this.list.goToNext()) {
        	do {
        		setString.append("," + this.list.retrieve().toString());
        	}
        	while(!this.list.goToNext() == false);
        }        
        setString.append("}");
        
        return setString.toString();
	}
}
