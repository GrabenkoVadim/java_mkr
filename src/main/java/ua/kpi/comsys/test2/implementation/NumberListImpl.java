/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import ua.kpi.comsys.test2.NumberList;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Custom implementation of INumberList interface.
 * Has to be implemented by each student independently.
 *
 * Author: Грабенко Вадим Валерійович, ІП-31, № у списку 0006
 */
public class NumberListImpl implements NumberList {

    /**
     * Основна система числення для мого варіанту:
     * C5 = 1 -> base 3
     */
    private static final int MAIN_BASE = 3;

    /**
     * Додаткова система числення:
     * index = (C5 + 1) mod 5 = (1 + 1) mod 5 = 2 -> base 8
     */
    private static final int ADDITIONAL_BASE = 8;

    /**
     * Внутрішній елемент двозв'язного списку.
     */
    private static final class Node {
        byte value;
        Node prev;
        Node next;

        Node(byte value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    /**
     * Основа системи числення для даного екземпляра списку.
     * Для мого варіанту за замовчуванням це 3.
     */
    private int base;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     * у основній системі числення (base = 3).
     */
    public NumberListImpl() {
        this(MAIN_BASE);
    }

    private NumberListImpl(int base) {
        this.base = base;
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.base = 10;

        if (file == null) {
            return;
        }

        if (!file.exists() || !file.isFile()) {
            return;
        }

        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8).trim();

            if (content.isEmpty()) {
                return;
            }

            initFromDecimalString(content);

        } catch (IOException e) {
            throw new RuntimeException("Unable to read number from file", e);
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this(MAIN_BASE);
        initFromDecimalString(value);
    }

    private void initFromDecimalString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        value = value.trim();
        if (value.isEmpty()) {
            add((byte) 0);
            return;
        }

        if (value.charAt(0) == '+') {
            value = value.substring(1);
        } else if (value.charAt(0) == '-') {
            clear();
            return;
        }

        final BigInteger num;
        try {
            num = new BigInteger(value);
        } catch (NumberFormatException ex) {
            clear();
            return;
        }

        fillFromBigInteger(num, this.base);
    }

    private void fillFromBigInteger(BigInteger num, int base) {
        clear();

        if (num.compareTo(BigInteger.ZERO) == 0) {
            add((byte) 0);
            return;
        }

        BigInteger b = BigInteger.valueOf(base);
        List<Byte> digitsReversed = new ArrayList<>();

        BigInteger current = num;
        while (current.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] dr = current.divideAndRemainder(b);
            int digit = dr[1].intValue();
            digitsReversed.add((byte) digit);
            current = dr[0];
        }

        for (int i = digitsReversed.size() - 1; i >= 0; i--) {
            add(digitsReversed.get(i));
        }
    }

    private BigInteger toBigInteger() {
        if (size == 0) {
            return BigInteger.ZERO;
        }
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(this.base);
        Node current = head;
        while (current != null) {
            int d = current.value & 0xFF;
            result = result.multiply(b).add(BigInteger.valueOf(d));
            current = current.next;
        }
        return result;
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        try {
            String value = toDecimalString();
            Files.write(file.toPath(), value.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Unable to save number to file", e);
        }
    }

    /**
     * Returns student's record book number, which has 4 decimal digits.
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 6;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation (для мого варіанту base = 8).<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger value = toBigInteger();
        NumberListImpl result = new NumberListImpl(ADDITIONAL_BASE);
        result.fillFromBigInteger(value, ADDITIONAL_BASE);
        return result;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     * @param arg - second argument of additional operation
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }

        BigInteger a = this.toBigInteger();

        int argBase = (arg instanceof NumberListImpl)
            ? ((NumberListImpl) arg).base
            : this.base;

        BigInteger b = toBigIntegerFromList(arg, argBase);

        BigInteger r = a.or(b);

        NumberListImpl result = new NumberListImpl(this.base);
        result.fillFromBigInteger(r, this.base);
        return result;
    }

    private static BigInteger toBigIntegerFromList(List<Byte> list, int base) {
        if (list == null || list.isEmpty()) {
            return BigInteger.ZERO;
        }
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(base);
        for (Byte bv : list) {
            if (bv == null) {
                throw new IllegalArgumentException("Null digit is not allowed");
            }
            int d = bv & 0xFF;
            result = result.multiply(b).add(BigInteger.valueOf(d));
        }
        return result;
    }

    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        return toBigInteger().toString();
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(size);
        for (Node n = head; n != null; n = n.next) {
            int d = n.value & 0xFF;
            if (d < 10) {
                sb.append((char) ('0' + d));
            } else {
                sb.append((char) ('A' + (d - 10)));
            }
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List)) return false;
        List<?> other = (List<?>) o;
        if (other.size() != this.size) return false;

        Iterator<Byte> itThis = this.iterator();
        Iterator<?> itOther = other.iterator();

        while (itThis.hasNext() && itOther.hasNext()) {
            Byte a = itThis.next();
            Object b = itOther.next();
            if (!Objects.equals(a, b)) {
                return false;
            }
        }
        return !itThis.hasNext() && !itOther.hasNext();
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void checkDigitRange(byte value) {
        if (value < 0 || value >= base) {
            throw new IllegalArgumentException(
                "Digit " + value + " is out of range for base " + base);
        }
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    private final class Itr implements Iterator<Byte> {
        private Node next = head;
        private Node lastReturned = null;

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Byte next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            lastReturned = next;
            next = next.next;
            return lastReturned.value;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            Node node = lastReturned;
            lastReturned = null;
            unlink(node);
        }
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (Node n = head; n != null; n = n.next) {
            arr[i++] = n.value;
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Byte e) {
        if (e == null) {
            throw new NullPointerException("Digit cannot be null");
        }
        byte v = e;
        checkDigitRange(v);
        linkLast(v);
        return true;
    }

    private void linkLast(byte value) {
        Node newNode = new Node(value);
        Node oldTail = tail;
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
            newNode.prev = oldTail;
        }
        size++;
    }

    private void linkBefore(byte value, Node succ) {
        Node newNode = new Node(value);
        Node pred = succ.prev;
        newNode.next = succ;
        newNode.prev = pred;
        succ.prev = newNode;
        if (pred == null) {
            head = newNode;
        } else {
            pred.next = newNode;
        }
        size++;
    }

    private void unlink(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }

        size--;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        Byte target = (Byte) o;
        Node current = head;
        while (current != null) {
            if (Objects.equals(current.value, target)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte b : c) {
            add(b);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        Objects.checkIndex(index, size + 1);
        if (c.isEmpty()) {
            return false;
        }
        if (index == size) {
            return addAll(c);
        }
        Node succ = node(index);
        boolean modified = false;
        for (Byte b : c) {
            if (b == null) {
                throw new NullPointerException("Digit cannot be null");
            }
            byte v = b;
            checkDigitRange(v);
            linkBefore(v, succ);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            if (c.contains(current.value)) {
                unlink(current);
                modified = true;
            }
            current = next;
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            if (!c.contains(current.value)) {
                unlink(current);
                modified = true;
            }
            current = next;
        }
        return modified;
    }

    @Override
    public void clear() {
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.prev = null;
            current.next = null;
            current = next;
        }
        head = tail = null;
        size = 0;
    }

    private Node node(int index) {
        Objects.checkIndex(index, size);
        if (index < (size >> 1)) {
            Node x = head;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node x = tail;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }

    @Override
    public Byte get(int index) {
        return node(index).value;
    }

    @Override
    public Byte set(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("Digit cannot be null");
        }
        byte v = element;
        checkDigitRange(v);
        Node n = node(index);
        byte old = n.value;
        n.value = v;
        return old;
    }

    @Override
    public void add(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("Digit cannot be null");
        }
        byte v = element;
        checkDigitRange(v);
        if (index == size) {
            linkLast(v);
        } else {
            Node succ = node(index);
            linkBefore(v, succ);
        }
    }

    @Override
    public Byte remove(int index) {
        Node n = node(index);
        byte old = n.value;
        unlink(n);
        return old;
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        Byte target = (Byte) o;
        int idx = 0;
        for (Node n = head; n != null; n = n.next) {
            if (Objects.equals(n.value, target)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        Byte target = (Byte) o;
        int idx = size - 1;
        for (Node n = tail; n != null; n = n.prev) {
            if (Objects.equals(n.value, target)) {
                return idx;
            }
            idx--;
        }
        return -1;
    }

    private final class ListItr implements ListIterator<Byte> {
        private Node next;
        private Node lastReturned;
        private int nextIndex;

        ListItr(int index) {
            Objects.checkIndex(index, size + 1);
            if (index == size) {
                next = null;
            } else {
                next = node(index);
            }
            nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.value;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public Byte previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            if (next == null) {
                next = tail;
            } else {
                next = next.prev;
            }
            lastReturned = next;
            nextIndex--;
            return lastReturned.value;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            Node toRemove = lastReturned;
            if (toRemove == next) {
                next = next.next;
            } else {
                nextIndex--;
            }
            unlink(toRemove);
            lastReturned = null;
        }

        @Override
        public void set(Byte e) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            if (e == null) {
                throw new NullPointerException("Digit cannot be null");
            }
            byte v = e;
            checkDigitRange(v);
            lastReturned.value = v;
        }

        @Override
        public void add(Byte e) {
            if (e == null) {
                throw new NullPointerException("Digit cannot be null");
            }
            byte v = e;
            checkDigitRange(v);
            if (next == null) {
                linkLast(v);
            } else {
                linkBefore(v, next);
            }
            nextIndex++;
            lastReturned = null;
        }
    }

    @Override
    public ListIterator<Byte> listIterator() {
        return new ListItr(0);
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        return new ListItr(index);
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        NumberListImpl sub = new NumberListImpl(this.base);
        int i = 0;
        for (Node n = head; n != null && i < toIndex; n = n.next, i++) {
            if (i >= fromIndex) {
                sub.add(n.value);
            }
        }
        return sub;
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 == index2) {
            return true;
        }
        Node n1 = node(index1);
        Node n2 = node(index2);
        byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }

    @Override
    public void sortAscending() {
        if (size <= 1) {
            return;
        }
        byte[] arr = new byte[size];
        int i = 0;
        for (Node n = head; n != null; n = n.next) {
            arr[i++] = n.value;
        }
        Arrays.sort(arr);
        i = 0;
        for (Node n = head; n != null; n = n.next) {
            n.value = arr[i++];
        }
    }

    @Override
    public void sortDescending() {
        if (size <= 1) {
            return;
        }
        byte[] arr = new byte[size];
        int i = 0;
        for (Node n = head; n != null; n = n.next) {
            arr[i++] = n.value;
        }
        Arrays.sort(arr);
        i = size - 1;
        for (Node n = head; n != null; n = n.next) {
            n.value = arr[i--];
        }
    }

    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        Node oldHead = head;
        Node newHead = head.next;

        newHead.prev = null;
        head = newHead;

        tail.next = oldHead;
        oldHead.prev = tail;
        oldHead.next = null;
        tail = oldHead;
    }

    @Override
    public void shiftRight() {
        if (size <= 1) return;
        Node oldTail = tail;
        Node newTail = tail.prev;

        newTail.next = null;
        tail = newTail;

        oldTail.prev = null;
        oldTail.next = head;
        head.prev = oldTail;
        head = oldTail;
    }
}
