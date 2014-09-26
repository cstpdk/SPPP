# Exercise 2.1

1) I get example the same behaviour as in the lecture

2) Yes, now they terminate

3) No, that doesn't work. Synchronized is needed on both methods in
order to ensure memory visibility between threads

4) Yes, that works. This is because we only need to be able to read
the variable to reach that terminating state. This means that we only
require memory visibility, mutual exclusion is not needed.

# Exercise 2.2

1) Output from a timed execution is:

> make  0,02s user 0,01s system 0% cpu 6,862 total

2) 10 thread version is about twice as fast:

> make  0,01s user 0,01s system 0% cpu 3,317 total

3) No, the result is no longer correct, nor is it deterministic.
Example output:

> Parallel2  result:     664464

4) Lets start with the practice. Running the two threaded version
without synchronized on the get method computes the correct result.
Luckily this matches well with the theory, since we're not relying on
the value of get() during execution, but instead have the predefined
to variables, we are not at risk of computing a wrong result. Since
we're only read the result at the end, the value of get() is correct,
as it is guaranteed by the synchronized increment method.

# Exercise 2.3

1) That takes a while:

> make  0,03s user 0,00s system 0% cpu 7,345 total

2) I wrote it in TestCountFactors.java. For reference, this is it:

```java
public static class MyAtomicInteger{
	int value = 0;

	synchronized int addAndGet(int amount){
		value += amount;
		return get();
	}

	int get(){
		return value;
	}
}
```

3) Actually I do not get the correct answer. I get 18703701 (which is
off by 28). I assume that this is due to an off-by-one error or
something equally hard to track, so for now I will seek comfort in the
fact that I get a consistent result and assume that my parallelism is
correct. The time spent is:

> make  0,01s user 0,01s system 0% cpu 3,703 total

4) No, one could not do this only using volatile, as mutual exclusion
is needed to ensure that the value gets incremented correctly. If one
only used volatile the results would vary between runs

5) Using the final keyword my implementation is approximately the
speed of java atomic integer. However, without the final keyword
javas implementation is actually slower. This is due to the compiler
being able to optimize based on the knowledge that the object is
immutable.

# Exercise 2.4

1) The volatile keyword is needed because other threads may dependent
on an updated cache value for read requests. This is seen in the
getFactor method, where factors can falsely be null and thereby give
wrong results.

2) The final modifier gives a visibility guarantee which is, again,
needed in the getFactors method of the class.
