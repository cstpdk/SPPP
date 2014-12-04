import java.util.Random;
import java.io.*;
import akka.actor.*;

// -- MESSAGES --------------------------------------------------$
class StartTransferMessage implements Serializable {
	public final ActorRef bank, from, to;
	public StartTransferMessage(ActorRef bank, ActorRef from, ActorRef to){
		this.bank = bank;
		this.from = from;
		this.to = to;
	}
}
class TransferMessage implements Serializable {
	public final ActorRef from,to;
	public final int amount;

	public TransferMessage(ActorRef from, ActorRef to, int amount){
		this.from = from;
		this.to = to;
		this.amount = amount;
	}
}

class DepositMessage implements Serializable {
	public final int amount;

	public DepositMessage(int amount){
		this.amount = amount;
	}
}
class PrintBalanceMessage implements Serializable {}

// -- ACTORS --------------------------------------------------
class AccountActor extends UntypedActor {
	int balance = 0;
	public void onReceive(Object o){
		if (o instanceof DepositMessage)
			balance += ((DepositMessage) o).amount;
		if (o instanceof PrintBalanceMessage)
			System.out.println(balance);
	}
}

class BankActor extends UntypedActor {
	public void onReceive(Object o){
		if (o instanceof TransferMessage) {
			TransferMessage message = (TransferMessage) o;

			DepositMessage fromDeposit = new DepositMessage(-1 * message.amount);
			DepositMessage toDeposit = new DepositMessage(message.amount);

			message.from.tell(fromDeposit,getSelf());
			message.to.tell(toDeposit,getSelf());
		}
	}
}

class ClerkActor extends UntypedActor {
	Random random = new Random();
	public void onReceive(Object o){

		System.out.println("Wat");
		if (o instanceof StartTransferMessage){
			StartTransferMessage message = (StartTransferMessage) o;

			for(int i=0; i< 100; i++){
				TransferMessage outMessage = new TransferMessage(
					message.from, message.to,random.nextInt());

				message.bank.tell(outMessage,getSelf());
			}
		}
	}
}
// -- MAIN --------------------------------------------------
public class ABC { // Demo showing how things work:
	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("ABCSystem");

		final ActorRef A1 = system.actorOf(Props.create(AccountActor.class),"A1");
		final ActorRef A2 = system.actorOf(Props.create(AccountActor.class),"A2");

		final ActorRef B1 = system.actorOf(Props.create(BankActor.class),"B1");
		final ActorRef B2 = system.actorOf(Props.create(BankActor.class),"B2");

		final ActorRef C1 = system.actorOf(Props.create(ClerkActor.class),"C1");
		final ActorRef C2 = system.actorOf(Props.create(ClerkActor.class),"C2");

		try {
			StartTransferMessage t1 = new StartTransferMessage(B1,A1,A2);
			StartTransferMessage t2 = new StartTransferMessage(B2,A2,A1);

			C1.tell(t1,ActorRef.noSender());
			C2.tell(t2,ActorRef.noSender());

			System.out.println("Press return to inspect...");
			System.in.read();

			A1.tell(new PrintBalanceMessage(),ActorRef.noSender());
			A2.tell(new PrintBalanceMessage(),ActorRef.noSender());

			System.out.println("Press return to terminate...");
			System.in.read();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			system.shutdown();
		}
	}
}
