package casinoroyale;

public class Card {

    private int cardNumber;

    public Card(int cardNumber){
        this.cardNumber = cardNumber;
    }

    public void setCardNumber(int cardNumber){
        this.cardNumber = cardNumber;
    }

    public int getCardNumber(){
        return this.cardNumber;
    }
}
