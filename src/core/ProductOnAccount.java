/*
 * Created on 26.5.2005
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
public class ProductOnAccount {
    private Product product;
    private double qty;
    
    public ProductOnAccount(Product product, double qty){
        this.product = product;
        this.qty = qty;
        
        
    }
    

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public double getQty() {
        return qty;
    }
    public void setQty(double qty) {
        this.qty = qty;
    }
}
