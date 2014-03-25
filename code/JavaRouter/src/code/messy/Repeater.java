package code.messy;

import java.util.ArrayList;
import java.util.List;

public class Repeater<T> implements Receiver<T> {
    List<Receiver<T>> receivers = new ArrayList<Receiver<T>>();

    public Repeater() {
    }

    public void add(Receiver<T> receiver) {
        receivers.add(receiver);
    }

    @Override
    public void receive(T item) {
        for (Receiver<T> handler : receivers) {
            handler.receive(item);
        }
    }
}
