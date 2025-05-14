#ifndef NETWORK_TIMESTAMP_H
#define NETWORK_TIMESTAMP_H

// This is a compatibility header.
// The Timestamp class has been replaced with direct use of long values.
// The constant Timestamp::NOT_SET has been replaced with the value -1.

// For backwards compatibility, define NOT_SET as -1
constexpr long NOT_SET = -1;

#endif // NETWORK_TIMESTAMP_H