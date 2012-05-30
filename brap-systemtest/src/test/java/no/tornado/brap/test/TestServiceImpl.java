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
        if (!"hej".equals(input))
            throw new RuntimeException("expected hej as input");
    }

    public String echo(String input) {
        return input.toUpperCase() + input + input.toLowerCase();
    }

    public InputStream getStream() {
        byte[] buf = "getStream calling".getBytes();
        InputStream is = new ByteArrayInputStream(buf);
        return is;
    }

    public void setStream(InputStream is) {
        try {
            if (!"hej".equals(getInputStream(is)))
                throw new RuntimeException("expected hej");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setStreamAndString(InputStream is, String str) {
        try {
            if (!"hej".equals(getInputStream(is)))
                throw new RuntimeException("expected hej in IS arg");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!"hej".equals(str))
            throw new RuntimeException("expected hej in string arg");
    }

    public void setStringAndStream(String str, InputStream is) {
        if (!"hej".equals(str))
            throw new RuntimeException("expected hej in string arg");
        try {
            if (!"hej".equals(getInputStream(is)))
                throw new RuntimeException("expected hej in IS arg");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setStringAndStreamAndInt(String str, InputStream is, int i) {
        if (!"hej".equals(str))
            throw new RuntimeException("expected hej in string arg");
        try {
            if (!"hej".equals(getInputStream(is)))
                throw new RuntimeException("expected hej in IS arg");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(i != 1)
            throw new RuntimeException("expected 1 as int arg");
    }

    private String getInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }

    public void throwException() throws Exception {
        throw new Exception("exception!");
    }

}
