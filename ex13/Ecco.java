// COMPILE:
// javac -cp scala.jar:akka-actor.jar Ecco.java 
// RUN:
// java -cp scala.jar:akka-actor.jar:akka-config.jar:. Ecco

import java.io.*;
import akka.actor.*;

// -- MESSAGES --------------------------------------------------

class StartMessage implements Serializable {
    public final ActorRef ecco;
    public StartMessage(ActorRef ecco) {
	this.ecco = ecco;
    }
}

class Message implements Serializable {
    public final String s;
    public Message(String s) {
	this.s = s;
    }
}

// -- ACTORS --------------------------------------------------

class PersonActor extends UntypedActor {
    public void onReceive(Object o) throws Exception {
	if (o instanceof StartMessage) {
	    StartMessage start = (StartMessage) o;
	    ActorRef ecco = start.ecco;
	    String s = "hvad drikker moller";
	    System.out.println("[says]:  " + s);
	    ecco.tell(new Message(s), getSelf());
	} else if (o instanceof Message) {
	    Message m = (Message) o;
	    System.out.println("[hears]: " + m.s);
	}
    }
}

class EccoActor extends UntypedActor {
    public void onReceive(Object o) throws Exception {
	if (o instanceof Message) {
	    Message m = (Message) o;
	    String s = m.s;
	    Message reply;
	    if (s.length()>5) reply = new Message("..." + s.substring(s.length()-5));
	    else reply = new Message("...");
	    getSender().tell(reply, getSelf());
	    getSender().tell(reply, getSelf());
	    getSender().tell(reply, getSelf());
	}
    }
}

// -- MAIN --------------------------------------------------

public class Ecco {
    public static void main(String[] args) {
	final ActorSystem system = ActorSystem.create("EccoSystem");
	final ActorRef person = system.actorOf(Props.create(PersonActor.class), "person");
	final ActorRef ecco = system.actorOf(Props.create(EccoActor.class), "ecco");
	person.tell(new StartMessage(ecco), ActorRef.noSender());
	try {
	    System.out.println("Press return to terminate...");
	    System.in.read();
	} catch(IOException e) {
	    e.printStackTrace();
	} finally {
	    system.shutdown();
	}
    }
}
