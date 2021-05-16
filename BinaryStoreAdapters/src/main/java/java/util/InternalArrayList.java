package java.util;

public class InternalArrayList<T> extends ArrayList<T> {

    public InternalArrayList(Object[] elementData) {
        this.elementData = elementData;
    }

    protected final Object[] getElementData() {
        return elementData;
    }

    @Override
    protected T elementData(int i) {
        return super.elementData(i);
    }
}
