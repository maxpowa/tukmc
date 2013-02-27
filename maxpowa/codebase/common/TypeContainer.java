package maxpowa.codebase.common;

/**
 * Class that holds a type, used to be able to change it around.
 */
public class TypeContainer<T> {

	public TypeContainer(T obj) {
		this.obj = obj;
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	private T obj;
}
