package network.aika.queue;

public interface ProcessingPhase {

    int rank();

    boolean isDelayed();
}
