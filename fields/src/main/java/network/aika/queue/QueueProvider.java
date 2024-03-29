package network.aika.queue;

public interface QueueProvider {

    Queue getQueue();

    boolean isNextRound();
}
