/*
 * Created on 25.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package core;

/**
 * @author tinodj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Product {
    private String ID;
    private String name;
    private String plu;
    private Double price;
    private String bar;
    private int vat;
    private Double currQty;
    
    /**
     * @param string
     * @param string2
     * @param string3
     * @param string4
     */
    public Product(String plu, String name, Double price, String bar, String ID, int vat, Double currQty) throws Exception{
        this.plu=plu;
        this.name=name;
        this.price=price;
        this.bar=bar;
        this.ID = ID;
        this.currQty = currQty;
        setVat(vat);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
    public String getBar() {
        return bar;
    }
    public void setBar(String bar) {
        this.bar = bar;
    }
    public String getPlu() {
        return plu;
    }
    public void setPlu(String plu) {
        this.plu = plu;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    
    
    public String getID() {
        return ID;
    }
    public void setID(String id) {
        ID = id;
    }
    
    
    public Double getCurrQty() {
        return currQty;
    }
    public int getVat() {
        return vat;
    }
    public void setVat(int vat) throws Exception {
        if (vat>2 || vat<1) throw new Exception("Лошо е избран данокот!!!");
        this.vat = vat;
    }
}
