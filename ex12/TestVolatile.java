// For week 12
// sestoft@itu.dk * 2014-11-20

// To obtain JIT-generated machine code, run with 
//   java -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly TestVolatile | less
// then search for "threadA" or "threadB".

// Example adapted from Ostrovsky: The C# Memory Model in Theory and
// Practice, part 2, in MSDN Magazine, January 2013; URL
// http://msdn.microsoft.com/en-us/magazine/jj883956.aspx

public class TestVolatile {
  public static void main(String[] args) {
    final StoreBufferExample sbe1 = new StoreBufferExample();
    // Seems necessary to call threadA and threadB many times to make
    // sure they are compiled (JITted) to machine code:
    for (int i = 0; i<100000; i++) {
      sbe1.threadA();
      sbe1.threadB();
    }
    final StoreBufferExample sbe2 = new StoreBufferExample();
    Thread tA = new Thread(new Runnable() { public void run() { 
      sbe2.threadA();
    }}),
      tB = new Thread(new Runnable() { public void run() { 
      sbe2.threadB();
      }});
    tA.start();
    tB.start();
  }
}

class StoreBufferExample {
  volatile boolean A = false;
  volatile boolean B = false;
  boolean A_Won = false;
  boolean B_Won = false;
  public void threadA() {
    A = true;
    if (!B) 
      A_Won = true;
  }
  public void threadB() {
    B = true;
    if (!A)
      B_Won = true;
  }
}


/*

2014-11-20

The JVM JIT inserts a "lock"-prefixed instruction after the writes to
volatile field A and volatile field B, as shown below.  

Note that C# DOES NOT insert the "lock" thing just because the fields
are volatile; see file TestVolatile.cs.

The "lock" prefix precedes an instruction that reads and writes memory
(the memory location pointed to by the stack top); according to the
Intel instruction set reference [URL] vol 2A page 3-472 this has the
following effect:

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


  # {method} {0x00000001dbec47e8} 'threadA' '()V' in 'StoreBufferExample'
[Verified Entry Point]
  0x0000000107325720: sub    $0x18,%rsp
  0x0000000107325727: mov    %rbp,0x10(%rsp)
  0x000000010732572c: movb   $0x1,0xc(%rsi)	;*putfield A = true
  0x0000000107325730: lock addl $0x0,(%rsp)     ; A is volatile, so lock rw (%rsp)
  0x0000000107325735: movzbl 0xd(%rsi),%r10d    ;*getfield B
  0x000000010732573a: test   %r10d,%r10d
  0x000000010732573d: je     0x000000010732574b ;*synchronization entry

  0x000000010732573f: add    $0x10,%rsp
  0x0000000107325743: pop    %rbp
  0x0000000107325744: test   %eax,-0x212774a(%rip) ;   {poll_return}
  0x000000010732574a: retq   

  0x000000010732574b: movb   $0x1,0xe(%rsi)     ;*putfield A_Won = true
  0x000000010732574f: jmp    0x000000010732573f
  0x0000000107325751: hlt  


  # {method} {0x00000001dbec48a8} 'threadB' '()V' in 'StoreBufferExample'
  #           [sp+0x20]  (sp of caller)
[Verified Entry Point]
  0x0000000107322820: sub    $0x18,%rsp
  0x0000000107322827: mov    %rbp,0x10(%rsp)
  0x000000010732282c: movb   $0x1,0xd(%rsi)	;*putfield B = 1
  0x0000000107322830: lock addl $0x0,(%rsp)     ; B is volatile, so lock rw (%rsp)
  0x0000000107322835: movzbl 0xc(%rsi),%r10d    ;*getfield A
  0x000000010732283a: test   %r10d,%r10d
  0x000000010732283d: je     0x000000010732284b  ;*synchronization entry

  0x000000010732283f: add    $0x10,%rsp
  0x0000000107322843: pop    %rbp
  0x0000000107322844: test   %eax,-0x212484a(%rip) ;   {poll_return}
  0x000000010732284a: retq   

  0x000000010732284b: movb   $0x1,0xf(%rsi)     ;*putfield B_Won = true
  0x000000010732284f: jmp    0x000000010732283f

*/
