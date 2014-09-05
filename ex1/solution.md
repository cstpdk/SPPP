# Exercise 1.1

1) I get the following output when running without synchronized:

```
Count is 19953484 and should be 20000000
Count is 19792435 and should be 20000000
Count is 19492924 and should be 20000000
Count is 19853500 and should be 20000000
Count is 19848901 and should be 20000000
```

2) No, I would not guarantee that this always gives the correct
result. The reason that this seems to work is that the problem size is
now small enough that each thread can finish before the next one
starts. Thus, no interleaving between the two threads can mess up the
results.

3) I would not expect any meaningful differences by using neither the
"++" or "+=" operator. These operators will still have to perform
several operations to actually do the increment, so the
synchronization errors will be the same.

4) I expect the value to be zero. To achieve this the methods must be
synchronized since the locking happens on the instance and the value
be in- and decremented is held on the object.

# Exercise 1.2
