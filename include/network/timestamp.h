#ifndef NETWORK_TIMESTAMP_H
#define NETWORK_TIMESTAMP_H

#include <string>

class Timestamp {
public:
    static const Timestamp NOT_SET;

    Timestamp();
    Timestamp(long time);

    long getTime() const;
    void setTime(long time);

    bool operator==(const Timestamp& other) const;
    bool operator!=(const Timestamp& other) const;
    bool operator<(const Timestamp& other) const;
    bool operator>(const Timestamp& other) const;
    bool operator<=(const Timestamp& other) const;
    bool operator>=(const Timestamp& other) const;

    std::string toString() const;

private:
    long time;
};

#endif // NETWORK_TIMESTAMP_H 