/*
 * Created on 25.5.2005
 *
 */
package core;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author tinodj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FiscalAccount {
    private LinkedList<ProductOnAccount> products;
    
    public FiscalAccount(){
        products = new LinkedList<ProductOnAccount>();
    }
    
    public void addProductOnAccount(ProductOnAccount productOnAccount){
        products.add(productOnAccount);
    }

    public LinkedList<ProductOnAccount> getProducts() {
        return products;
    }
    
    public void setProducts(LinkedList<ProductOnAccount> products) {
        this.products = products;
    }

    /**
     * @return
     */
    public double value() {
        Iterator it = products.iterator();
        double value = 0;
        while (it.hasNext()) {
            ProductOnAccount productOnAccount = (ProductOnAccount) it.next();
            value+=productOnAccount.getQty()*productOnAccount.getProduct().getPrice();
        }
        return value;
    }
}
