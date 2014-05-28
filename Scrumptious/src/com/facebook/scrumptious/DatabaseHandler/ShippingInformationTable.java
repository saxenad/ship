package com.facebook.scrumptious.DatabaseHandler;

public class ShippingInformationTable {

	   public String ImageFileName;
	   public String ShipOption;
       public String DestinationAddress;
       public String ContactName;
       public  String InsertDate;
       public int ShipInformationTableId;
	 // Empty constructor
    public ShippingInformationTable(){
    	
    }
    
    public void setShipInformationTableId(int shipInformationTableId){
        this.ShipInformationTableId =shipInformationTableId;
    }
    
    public void setImageFileName(String imageFileName)
    {
    	this.ImageFileName=imageFileName;
    }
    
    public void setShipOption(String shipOption){
        this.ShipOption =shipOption;
    }
    
    public void setDestinationAddress(String destinationAddress){
        this.DestinationAddress =destinationAddress;
    }
    
    public void setContactName(String contactName){
    	this.ContactName=contactName;
    }
    
    public void setInsertDate(String insertDate){
    	this.InsertDate=insertDate;
    }
    
    
    public int getShipInformationTableId(){
    	return this.ShipInformationTableId;
    }
    
    public String getImageFileName(){
    	return this.ImageFileName;
    }

    public String getShipOption(){
        return this.ShipOption;
    }
    
    public String getInsertDate(){
        return this.InsertDate;
    }
    
    public String getDestinationAddress(){
    	 return this.DestinationAddress;
    }
    
    public String getContactName(){
    	 return this.ContactName;
    }

    
}