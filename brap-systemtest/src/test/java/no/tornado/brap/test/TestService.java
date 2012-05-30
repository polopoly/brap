package no.tornado.brap.test;

import java.io.InputStream;

public interface TestService {
    /**
     * @return "ok"
     */
    public String getString();
    
    /**
     * does nothing
     */
    public void doVoid();
    
    /**
     * Throws exception if input is not "hej"
     * @param input must be "hej"
     */
    public void setString(String input);
    
    /**
     * Returns INPUTInputinput
     * @param input
     * @return INPUTInputinput
     */
    
    
    public String echo(String input);
    /**
     * @return "getStream calling"
     */
    
    /**
     * 
     * @return an inputstream containing the string "getStream calling" 
     */
    public InputStream getStream();
    
    /**
     * Throws RTE if is is not a stream containing "hej"
     * @param is
     */
    public void setStream(InputStream is);
    
    /**
     * Throws RTE if params are not "hej"
     * @param is
     * @param str
     */
    public void setStreamAndString(InputStream is, String str);
    public void setStringAndStream(String str, InputStream is);
    
    /**
     * 
     * @param str needs to be hej
     * @param is needs to be hej
     * @param i needs to be 1
     */
    public void setStringAndStreamAndInt(String str, InputStream is, int i);
    
    /**
     * throws exception with message "exception!"
     * @throws Exception
     */
    public void throwException() throws Exception;
}
