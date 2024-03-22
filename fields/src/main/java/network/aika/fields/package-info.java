package network.aika.fields;

/**
 * This package contains an implementation of an event driven fields and functions concept. Each field and function can
 * either act as an event source or an event sink and can propagate changes to its value to all the registered listeners.
 * The package recognizes the close relation between differential algebra and event-driven programming. For example to
 * implement the conjunction of two events requires the implementation of a rule like: If A was triggered the check
 * if B is already true and if B is triggered than check if A is already true. This is essentially the product rule.
 * Implementing this rule by hand without this more explicit and descriptive field concept requires the programmer to
 * take care of these two cases A and B by hand which are usually scattered across the entire code base, easily leading
 * to unwanted bugs.
 */