package no.tornado.brap.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestServiceImpl implements TestService {

    public String getString() {
        return "ok";
    }

    public void doVoid() {
    }

    public void setString(String input) {
        System.out.println("got" + input);
    }

    public String echo(String input) {
        return input;
    }

    public InputStream getStream() {
        byte[] buf = "getStream calling".getBytes();
        InputStream is = new ByteArrayInputStream(buf);
        return is;
    }

    public void setStream(InputStream is) {
        try {
            printInputStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setStreamAndString(InputStream is, String str) {
        try {
            printInputStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(str);
    }

    public void setStringAndStream(String str, InputStream is) {
        System.out.println(str);
        try {
            printInputStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setStringAndStreamAndInt(String str, InputStream is, int i) {
        System.out.println(str);
        try {
            printInputStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println(i);
    }

    private void printInputStream(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String read = br.readLine();

        while(read != null) {
            System.out.println(read);
            read = br.readLine();
        }
    }

    public void thowException() throws Exception {
        throw new Exception("exception!");
    }

    
}
