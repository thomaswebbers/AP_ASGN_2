public class List<E extends Comparable> implements ListInterface<E>{
    private class Node {
        private E data;
        private Node prior, next;

        public Node(E d) {
            this(d, null, null);
        }

        Node(E data, Node prior, Node next) {
            this.data = data == null ? null : data;
            this.prior = null;
            this.next = null;
        }

        public String toString() {
            return data.toString();
        }
    }

    private Node current;
    private Node head;
    private Node tail;
    private int numberOfElements = 0;


    public List(){
        this.init();
    }

    @Override
    public boolean isEmpty() {
        return numberOfElements == 0;
    }

    @Override
    public ListInterface<E> init() {
        this.current = new Node(null);
        head = current;
        tail = current;
        numberOfElements = 0;

        return this;
    }

    @Override
    public int size() {
        return numberOfElements;
    }

    @Override
    public ListInterface<E> insert(E d) {
        if(isEmpty()) {
            current.data = d;
            numberOfElements++;

            return this;
        }

        Node newNode = new Node(d);
        find(d); //automatically sets the list at the correct position so that the node gets inserted in the right position.

        //if the list has only 1 element
        if(numberOfElements == 1) {
            if (current.data.compareTo(newNode.data) > 0) {
                current.prior = newNode;
                newNode.next = current;
                current = newNode;
                head = current;
            }
            else {
                current.next = newNode;
                newNode.prior = current;
                current = newNode;
                tail = current;
            }
        }

        //front
        else if(current == head && current.next != null) {
            current.prior = newNode;
            newNode.next = current;

            current = newNode;
            head = current;
        }
        //middle
        else if(current.prior != null && current.next != null) {
            /*
            current.prior.next = newNode;
        	current.prior = newNode;
        	current.prior.next = current;
     
        	current = newNode;
        	*/
            newNode.next = current;
            newNode.prior = current.prior;
            newNode.prior.next = newNode;
            current.prior = newNode;
            current = newNode;
        }
        //end
        else if(current.prior != null && current.next == null) {
            if (current.data.compareTo(newNode.data) < 0) {
                current.next = newNode;
                newNode.next = null;
                newNode.prior = current;

                current = newNode;
                tail = current;
            }
            else {
        		/*current.prior.next = newNode;
            	current.prior = newNode;
            	current.prior.next = current;

        		current = newNode;
        		*/
                newNode.next = current;
                newNode.prior = current.prior;
                newNode.prior.next = newNode;
                current.prior = newNode;
                current = newNode;

            }
        }
        //current = newNode;
        numberOfElements++;

        return this;
    }

    @Override
    public E retrieve() {
        if(isEmpty()){
            return null;
        }

        return (E) current.data;
    }

    @Override
    public ListInterface<E> remove() {
        if(!isEmpty()){
            //if the node is alone
            if (numberOfElements == 1) {
                return this.init();
            }
            //if at the start
            else if (current == head) {
                current.next.prior = null;
                current = current.next;
                head = current;
            }
            //if at the end
            else if(current == tail){
                current.prior.next = null;
                current = current.prior;
                tail = current;
            }
            //if in the middle
            else {
                current.next.prior = current.prior;
                current.prior.next = current.next;
                current = current.next;
            }
            numberOfElements--;
        }
        return this;
    }

    @Override
    public boolean find(E d) {
        if(isEmpty()){
            return false;
        }

        goToFirst();

        while(d.compareTo(current.data) > 0 && current.next != null){
            goToNext();
        }

        if(d.compareTo(current.data) == 0){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean goToFirst() {
        if(isEmpty()){
            return false;
        }

        current = head;
        return true;
    }

    @Override
    public boolean goToLast() {
        if(isEmpty()){
            return false;
        }

        current = tail;
        return true;
    }

    @Override
    public boolean goToNext() {
        if(isEmpty() || current.next == null){
            return false;
        }

        current = current.next;
        return true;
    }

    @Override
    public boolean goToPrevious() {
        if(isEmpty() || current.prior == null){
            return false;
        }

        current = current.prior;
        return true;
    }

    @Override
    public ListInterface<E> copy() {
        List<E> result = new List<E>();

        if(this.isEmpty()){
            return result;
        }

        this.goToFirst();

        do {
            E data = this.retrieve();

            result.insert(data);
        } while(this.goToNext() == true);

        return result;
    }
}
