#ifndef NETWORK_TRANSITION_H
#define NETWORK_TRANSITION_H


class Transition {
public:
    Transition(int from, int to);

    static Transition* of(int from, int to);

    int from() const;
    int to() const;

private:
    int fromType;
    int toType;
};

#endif // NETWORK_TRANSITION_H 