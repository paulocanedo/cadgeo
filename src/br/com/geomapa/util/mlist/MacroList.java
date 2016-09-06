/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.mlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author paulocanedo
 */
public class MacroList<T> implements Iterable<T> {

    private List<MacroListListener> listeners = new ArrayList<MacroListListener>();
    private List<List<T>> delegate = new ArrayList<List<T>>();

    public MacroList() {
    }

    public void addList(List<T> list) {
        delegate.add(list);
        fireSizeChangedListener();
        fireListAdded(list);
    }

    public void removeList(List<T> list) {
        delegate.remove(list);
        fireSizeChangedListener();
        fireListRemoved(list);
    }

    public void addAll(T... elems) {
        List<T> asList = Arrays.asList(elems);
        delegate.add(asList);
        fireSizeChangedListener();
        fireListAdded(asList);
    }

    public boolean addElement(T elem) {
        if (delegate.isEmpty()) {
            delegate.add(new ArrayList<T>());
        }
        lastList().add(elem);
        fireSizeChangedListener();
        fireElementAdded(elem);
        return true;
    }

    public boolean removeElement(T elem) {
        for (List<T> list : delegate) {
            boolean removed = list.remove(elem);
            if (removed) {
                fireSizeChangedListener();
                fireElementRemoved(removed);
                return true;
            }
        }
        return false;
    }

    public List<T> lastList() {
        int index = delegate.size() - 1;
        return index < 0 ? null : delegate.get(index);
    }

    public T get(int index) {
        int sum = 0;
        for (List<T> list : delegate) {
            if (index < (list.size() + sum)) {
                return list.get(index - sum);
            }
            sum += list.size();
        }
        return null;
    }

    public int indexOf(T elem) {
        int sum = 0;
        for (List<T> list : delegate) {
            int indexOf = list.indexOf(elem);
            if (indexOf >= 0) {
                return sum + indexOf;
            }
            sum += list.size() - 1;
        }
        return -1;
    }

    public boolean contains(T elem) {
        return indexOf(elem) >= 0;
    }

    public int size() {
        int sum = 0;
        for (List<T> list : delegate) {
            sum += list.size();
        }
        return sum;
    }

    public void clear() {
        delegate.clear();
        fireSizeChangedListener();

        for (Iterator it = iterator(); it.hasNext();) {
            fireElementRemoved(it.next());
        }
    }

    private void fireSizeChangedListener() {
        for (MacroListListener listener : listeners) {
            listener.sizeChanged(size());
        }
    }

    private void fireElementAdded(Object o) {
        for (MacroListListener listener : listeners) {
            listener.elementAdded(o);
        }
    }

    private void fireElementRemoved(Object o) {
        for (MacroListListener listener : listeners) {
            listener.elementRemoved(o);
        }
    }

    private void fireListAdded(List l) {
        for (MacroListListener listener : listeners) {
            listener.listAdded(l);
        }
    }

    private void fireListRemoved(List l) {
        for (MacroListListener listener : listeners) {
            listener.listRemoved(l);
        }
    }

    public void addMacroListListener(MacroListListener listener) {
        listeners.add(listener);
    }

    public void removeMacroListListener(MacroListListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Iterator<T> iterator() {
        return new MacroListIterator<T>(this);
    }

    public List<T> toList() {
        auxlist.clear();

        for (T t : this) {
            auxlist.add(t);
        }
        return auxlist;
    }

    public T find(T temp) {
        for (List<T> list : delegate) {
            int indexOf = list.indexOf(temp);
            if (indexOf >= 0) {
                return list.get(indexOf);
            }
        }
        return null;
    }
    private List<T> auxlist = new ArrayList<T>();

    private class MacroListIterator<T> implements Iterator<T> {

        private MacroList<T> macroList;
        private int size;
        private int position = 0;

        public MacroListIterator(MacroList<T> macroList) {
            this.macroList = macroList;
            this.size = macroList.size();
        }

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public void remove() {
        }

        @Override
        public T next() {
            if (hasNext()) {
                return macroList.get(position++);
            } else {
                return null;
            }
        }
    }
}
