package fprn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import core.FiscalAccount;
import core.Product;
import core.ProductOnAccount;

/**
 * @author tinodj
 *
 */
public class FiscalPrinter {
    private String portName="COM2"; //just default
    private OutputStream os;
    private CommPortIdentifier portId;
    private SerialPort serialPort;
    private static final long sleepingTime = 500;
    
    public void flush() throws Exception{
        os.flush();
    }
    
    
    public void sendPackage(int seq,int cmd, int[] data) throws Exception{
        int len = (36+data.length);
        int[] sum = new int[4];
        int decSum=calculateSum(len,seq,cmd,data);
        sum[0] = (int)((decSum/4096)%16 + 48);
        sum[1] = (int)((decSum/256)%16 + 48);
        sum[2] = (int)((decSum/16)%16 + 48);
        sum[3] = (int)(decSum %16 + 48);
        sendInt(1);
        sendInt(len);
        sendInt(seq);
        sendInt(cmd);
        sendInts(data);
        sendInt(5);
        sendInts(sum);
        sendInt(3);
        Thread.sleep(sleepingTime);
        byte[] output = this.readPackage();
        if (output==null || (output.length==1 && output[0]==(byte)21)){
            return false;
        }else{
            return true;
        }
    }

    public void sendCommand(int cmd, int[] data) throws Exception{
        byte seq = SequenceGenerator.getNewSeq();
        int i=0;
        while (i<5 && !this.sendCommand(cmd,data)){
            i++;
        }
        if (i==5){
            throw new Exception("Printer not working");
        }
    }

    public void printBytes(int[] bytes){
        for (int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]+" ");
        }
    }
    
    public int calculateSum(int len, int seq, int cmd, int[] data){
        int sum = len+seq+cmd;
        for (int i=0;i<data.length;i++){
            sum+=data[i];
        }
        sum+=5;
        return sum;
        
    }
    
    
    public void sendInts(int[] data)throws Exception{
        for (int i=0;i<data.length;i++){
            sendInt(data[i]);
        }
    }

    
    public FiscalPrinter() throws Exception{
        Enumeration portList;
        portList = CommPortIdentifier.getPortIdentifiers();
        if (!portList.hasMoreElements()){
            System.err.println("Listata na portovi e prazna");
        }
        CommPortIdentifier port = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("comm.ini"));
            portName=br.readLine();
        } catch (Exception e) {
            System.err.println("Ne mozam da najdam comm.ini ke zemam default port COM2");
        }
        System.err.println("Najdeni se slednive portovi:");
        while (portList.hasMoreElements()) {
            port = (CommPortIdentifier) portList.nextElement();
            System.err.println(port.getName());
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL
                && port.getName().equalsIgnoreCase(portName)) {
                    break;
            }else{
                port=null;
            }
        }
        if (port == null){
            throw new NullPointerException("Ne e najden port koj moze da se otvori");
        }
        this.portId = port;
        openPort();
    }

    public void openPort() throws Exception {
        serialPort =
            (SerialPort) portId.open(
                "Fiscal" + System.currentTimeMillis(),
                4000);
        try {
            serialPort.setSerialPortParams(9600,8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        } catch (Exception e) {}
        os = serialPort.getOutputStream();
    }

    public void closePort() {
        if (serialPort == null)
            return;
        try {
            os.flush();
            os.close();
        } catch (IOException e) {}
        serialPort.close();
        
        serialPort = null;
    }

    private void checkOpen() throws Exception {
        if (serialPort == null)
            openPort();
    }

    public void sendByte(byte byteToSend) throws Exception {
        checkOpen();
        os.write(byteToSend);
    }
    
    public void sendInt(int intToSend) throws Exception{
        checkOpen();
        os.write(intToSend);
    }

   
    public static int[] getInts(String s){
        int[] ints= new int[s.length()];
        for (int i=0;i<s.length();i++){
            ints[i]=s.charAt(i);
        }
        return ints;
    }
    
    public void print(FiscalAccount fiscalAccount) throws Exception{
        sendCommand(48, getInts("1,0000,1"));
        char vat=(char)((int)193);

        Iterator it = fiscalAccount.getProducts().iterator();
        while (it.hasNext()){
            ProductOnAccount productOnAccount = (ProductOnAccount)it.next();
            Product product = productOnAccount.getProduct();
            if (product.getVat()==1){
                vat=(char)((int)192);    
            }else{
                vat=(char)((int)193);
            }
            
            String name1 = product.getName();
            String name = new String(name1.getBytes("cp1251"),"iso8859-1");
            
            String price = product.getPrice().toString();
            double qty = productOnAccount.getQty();
            sendCommand(49, getInts(name+"\t"+vat+""+price+"*"+qty));
        }
        sendCommand(53, getInts("\t"));
        sendCommand(56, getInts(""));
    }
    
    public void closeFiscal(byte seq) throws Exception{
        int sum = seq+69+5+36;
        int prv = (sum % 16) + 48;
        int vtor = (sum/16)%16 + 48;
        int tret = (sum/256)%16 + 48;
        int cet = (sum/4096)%16 + 48;
        
        sendByte((byte)1);
        sendByte((byte)36);
        sendByte((byte)seq);
        sendByte((byte)69);
        sendByte((byte)5);
        
        sendByte((byte)cet);//sum
        sendByte((byte)tret);//sum
        sendByte((byte)vtor);//sum
        sendByte((byte)prv);//sum
        sendByte((byte)3);
        
    }
    
    public void longReportInPeriod(byte seq, Date dateFrom, Date dateTo) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String fromDate = sdf.format(dateFrom);
        String toDate = sdf.format(dateTo);
        sendCommand(94,getInts(fromDate+","+toDate));
    }

    public void shortReportInPeriod(byte seq, Date dateFrom, Date dateTo) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String fromDate = sdf.format(dateFrom);
        String toDate = sdf.format(dateTo);
        sendCommand(95,getInts(fromDate+","+toDate));
    }
    
}
