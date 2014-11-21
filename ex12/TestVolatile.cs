// For week 12
// sestoft@itu.dk * 2014-11-20

// Example from Ostrovsky: The C# Memory Model in Theory and Practice,
// part 2, in MSDN Magazine, January 2013; URL
// http://msdn.microsoft.com/en-us/magazine/jj883956.aspx

using System.Threading;

public class TestVolatile {
  public static void Main() {
    StoreBufferExample sbe = new StoreBufferExample();
    Thread tA = new Thread(new ThreadStart(sbe.ThreadA)),
      tB = new Thread(new ThreadStart(sbe.ThreadB));
    tA.Start();
    tB.Start();
  }
}

class StoreBufferExample {
  volatile bool A = false;
  volatile bool B = false;
  volatile bool A_Won = false;
  volatile bool B_Won = false;
  public void ThreadA() {
    A = true;
    //    Thread.MemoryBarrier();
    if (!B) 
      A_Won = true;
  }
  public void ThreadB() {
    B = true;
    //     Thread.MemoryBarrier();
    if (!A)
      B_Won = true;
  }
}


/*

2014-11-20

Without Thread.MemoryBarrier there are no "lock" instructions in the
Mono 3.10 JIT-generated code.  

With the Thread.MemoryBarrier calls there are "lock" instructions as
shown below.  

Note that the Java JIT DOES INSERT the "lock" if fields A and B are
volatile; see file TestVolatile.java, and that Java has no analog to
.NET's Thread.MemoryBarrier.  In fact, it seems that Java inserts a
"lock" after any write to a volatile variable -- not entirely clear
from the description of "lock" that that is sufficient to achieve the
Java's extensive visibility guarantees.

The "lock" prefix precedes an instruction that reads and
writes memory (the memory location pointed to by the stack top);
according to the Intel instruction set reference [URL] vol 2A page
3-472 this has the following effect:

"Causes the processor's LOCK# signal to be asserted during execution
of the accompanying instruction (turns the instruction into an atomic
instruction). In a multiprocessor environment, the LOCK# signal
ensures that the processor has exclusive use of any shared memory
while the signal is asserted."

and 

"IA-32 Architecture Compatibility Beginning with the P6 family
processors, when the LOCK prefix is prefixed to an instruction and the
memory area being accessed is cached internally in the processor, the
LOCK# signal is generally not asserted. Instead, only the processor's
cache is locked. Here, the processor's cache coherency mechanism
ensures that the operation is carried out atomically with regards to
memory."


reBufferExample_ThreadA:
00000000        pushl   %ebp
00000001        movl    %esp, %ebp
00000003        subl    $0x8, %esp
00000006        movl    0x8(%ebp), %eax
00000009        movb    $0x1, 0x8(%eax)
0000000d        lock
0000000e        addl    $0x0, (%esp)
00000012        movzbl  0x9(%eax), %eax
00000016        testl   %eax, %eax
00000018        jne     0x21
0000001a        movl    0x8(%ebp), %eax
0000001d        movb    $0x1, 0xa(%eax)
00000021        leave
00000022        retl

reBufferExample_ThreadB:
00000000        pushl   %ebp
00000001        movl    %esp, %ebp
00000003        subl    $0x8, %esp
00000006        movl    0x8(%ebp), %eax
00000009        movb    $0x1, 0x9(%eax)
0000000d        lock
0000000e        addl    $0x0, (%esp)
00000012        movzbl  0x8(%eax), %eax
00000016        testl   %eax, %eax
00000018        jne     0x21
0000001a        movl    0x8(%ebp), %eax
0000001d        movb    $0x1, 0xb(%eax)
00000021        leave
00000022        retl

*/
