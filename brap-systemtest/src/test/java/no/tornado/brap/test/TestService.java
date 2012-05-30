package no.tornado.brap.test;

import java.io.InputStream;

public interface TestService {
    public String getString();
    public void doVoid();
    public void setString(String input);
    public String echo(String input);
    public InputStream getStream();
    public void setStream(InputStream is);
    public void setStreamAndString(InputStream is, String str);
    public void setStringAndStream(String str, InputStream is);
    public void setStringAndStreamAndInt(String str, InputStream is, int i);
    public void throwException() throws Exception;
}
