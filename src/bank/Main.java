package bank;
import bank.HelloBank;

public class Main {

	public static void main(String[] args) {
		HelloBank.info();
		HelloBank b = new HelloBank("Ecobank","Pau",2003);
		b.greetCustomer("Alice");
		
		HelloBank c = new HelloBank("MyBank","Biarritz",2003);
		c.greetCustomer("Mahdi");

	}
	
	

}
