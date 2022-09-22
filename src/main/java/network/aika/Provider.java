package network.aika;


import network.aika.neuron.INeuron;
import network.aika.neuron.Neuron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Lukas Molzberger
 */
public class Provider<T extends AbstractNode> implements Comparable<Provider<?>> {

    private static final Logger log = LoggerFactory.getLogger(Provider.class);

    public static boolean ENABLE_COMPRESSION = true;

    protected Model model;
    protected Integer id;

    private volatile T n;

    private boolean markedDeleted;


    public enum SuspensionMode {
        SAVE,
        DISCARD
    }


    public Provider(Model model, int id) {
        this.model = model;
        this.id = id;

        if(model != null) {
            synchronized (model.providers) {
                model.providers.put(this.id, new WeakReference<>(this));
            }
        }
    }


    public Provider(Model model, T n) {
        this.model = model;
        this.n = n;

        id = model.suspensionHook != null ? model.suspensionHook.getNewId() : model.currentId.addAndGet(1);
        synchronized (model.providers) {
            model.providers.put(id, new WeakReference<>(this));

            if(n != null) {
                model.register(this);
            }
        }
    }


    public Integer getId() {
        return id;
    }


    public Model getModel() {
        return model;
    }


    public boolean isSuspended() {
        return n == null;
    }


    public T getIfNotSuspended() {
        return n;
    }


    public synchronized T get() {
        if (n == null) {
            reactivate();
        }
        return n;
    }


    public synchronized T get(int lastUsedDocumentId) {
        T n = get();

        if(n != null) {
            n.lastUsedDocumentId = Math.max(n.lastUsedDocumentId, lastUsedDocumentId);
        }
        return n;
    }


    /**
     *
     * @param doc The document is used to remember when this node has been used last.
     * @return
     */
    public T get(Document doc) {
        return doc != null ? get(doc.getId()) : get();
    }


    public synchronized void suspend(SuspensionMode sm) {
        if(n == null)
            return;

        assert model.suspensionHook != null;

        n.suspend();

        model.unregister(this);

        if(sm == SuspensionMode.SAVE)
            save();

        n = null;
    }


    public void save() {
        if (n.modified) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(ENABLE_COMPRESSION) {
                try (
                        GZIPOutputStream gzipos = new GZIPOutputStream(baos);
                        DataOutputStream dos = new DataOutputStream(gzipos)) {
                    n.write(dos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try (DataOutputStream dos = new DataOutputStream(baos)) {
                    n.write(dos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                model.suspensionHook.store(id, baos.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        n.modified = false;
    }


    private void reactivate() {
        assert model.suspensionHook != null;

        byte[] data = new byte[0];
        try {
            data = model.suspensionHook.retrieve(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(data == null) {
            log.warn("Tried to reactivate deleted node!");
            markedDeleted = true;
            return;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        if(ENABLE_COMPRESSION) {
            try (
                    GZIPInputStream gzipis = new GZIPInputStream(bais);
                    DataInputStream dis = new DataInputStream(gzipis)) {
                n = (T) AbstractNode.read(dis, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (DataInputStream dis = new DataInputStream(bais)) {
                n = (T) AbstractNode.read(dis, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        n.reactivate();

        model.register(this);
    }

    public boolean isMarkedDeleted() {
        return markedDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;

        if(o instanceof Provider<?>) {
            return ((Provider<?>) o).id.equals(id);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return id;
    }


    public String toString() {
        return "p(" + id + ":" + (n != null ? n.toString() : "SUSPENDED") + ")";
    }

    public int compareTo(Provider<?> n) {
        return id.compareTo(n.id);
    }
}
