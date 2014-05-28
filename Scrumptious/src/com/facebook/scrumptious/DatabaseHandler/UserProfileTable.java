package com.facebook.scrumptious.DatabaseHandler;

public class UserProfileTable {

	public String Email;
	   public String PhoneNumber;
       public String FirstName;
       public String LastName;
       public int UserId;
       public  String Street_Address1;
       public  String StreetAddress2 ;
       public String HouseNumber ;
       public String City;
       public String State; 
       public String Country; 
       public String ZipCode ;
       public String FacebookUid; 
       public String LastFourCreditCard; 
       public String CreditCardType ;
       public String CardExpiryMonth;
       public String CardExpiryYear;
       public  String _insertDate;
	 // Empty constructor
    public UserProfileTable(){
    	
  
      
    }
    
    public UserProfileTable(int userId, String email,String insertDate,String firstName, String lastName){
        this.Email = email;
        this.FirstName=firstName;
        this.LastName=lastName;
        this.UserId=userId;
        this._insertDate=insertDate;
    }
    
    public void setFirstName(String firstName){
        this.FirstName =firstName;
    }
    
    public void setUserId(int userId){
        this.UserId =userId;
    }
    
    
    public void setEmail(String email){
        this.Email =email;
    }
    
    public void setLastName(String lastName){
        this.LastName =lastName;
    }
    
    public void setPhoneNumber(String phoneNumber){
    	this.PhoneNumber=phoneNumber;
    }
    
    public void setLastFourCreditCard(String lastFour){
    	this.LastFourCreditCard=lastFour;
    }
    
    public String getPhoneNumber(){
    	return this.PhoneNumber;
    }
    
    public String getLastFourCreditCard(){
    	return this.LastFourCreditCard;
    }
    // getting name
    public String getFirstName(){
        return this.FirstName;
    }
    
    public String getLastName(){
        return this.LastName;
    }

    public int getUserId(){
        return this.UserId;
    }
    
    public String getEmail(){
        return this.Email;
    }
}
