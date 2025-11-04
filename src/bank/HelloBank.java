package bank ;
public class HelloBank {
private String bankName ;
private String city ;
private Integer yearFounded;
// Constructeur
public HelloBank ( String name , String city, Integer yearFounded ) {
this.bankName = name ;
this.city = city ;
this.yearFounded = yearFounded;
}


// Mé thode d’instance
public void greetCustomer ( String customerName ) {
System.out.println("Bienvenue à " + bankName +"("+city+")"+" Depuis "+yearFounded+",cher "+customerName+"!");
}
// Mé thode statique d’information
public static void info () {
System.out.println("UPPA Bank-Système de demonstration Java.");
							}
}